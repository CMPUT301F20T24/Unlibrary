/*
 * UnlibraryRepository
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.unlibrary;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.example.unlibrary.models.User;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages all the database interaction for {@link UnlibraryViewModel}
 */
public class UnlibraryRepository {
    private final static String TAG = UnlibraryRepository.class.getSimpleName();
    private final static String BOOK_COLLECTION = "books";
    private static final String REQUEST_COLLECTION = "requests";
    private static final String REQUESTER = "requester";
    private static final String BOOK = "book";
    private static final String STATE = "state";
    private static final String STATUS = "status";


    private final FirebaseFirestore mDb;
    private final MutableLiveData<List<Book>> mBooks = new MutableLiveData<>(new ArrayList<>());
    private final ListenerRegistration mListenerRegistration;
    private String mUID;

    /**
     * Constructor for UnlibraryRepository. Sets up Firestore
     * <p>
     * TODO: Add querying logic to return only books that have been requested or borrowed by the user
     */
    public UnlibraryRepository() {
        mDb = FirebaseFirestore.getInstance();
        // TODO: Get document changes only to minimize payload from Firestore
        mListenerRegistration = mDb.collection(REQUEST_COLLECTION)
                .whereEqualTo(REQUESTER_FIELD, FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Unable to get requests from database", error);
                        return;
                    }

                    List<Request> requests = snapshot.toObjects(Request.class);

                    ArrayList<Task<DocumentSnapshot>> addBookTasks = new ArrayList<>();
                    ArrayList<Book> books = new ArrayList<>();

                    for (Request r : requests) {
                        addBookTasks.add(
                                mDb.collection(BOOK_COLLECTION).document(r.getBook()).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            Book book = documentSnapshot.toObject(Book.class);
                                            books.add(book);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Unable to get book " + r.getBook() + "from database", e);
                                        })
                        );
                    }


                    Tasks.whenAllComplete(addBookTasks)
                            .addOnSuccessListener(aVoid -> {
                                mBooks.setValue(books);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("TAG", "Failed to update book list", e);
                            });
                });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUID = user.getUid();
    }

    /**
     * Save new Request into the database. Assumes Request is valid.
     *
     * @param book            book object to get request from
     * @param onFinished      code to call on success
     * @param onErrorListener code to call on failure
     */
    public void getRequest(Book book, OnFinishedListener onFinished, OnErrorListener onErrorListener) {

        mDb.collection(REQUEST_COLLECTION)
                .whereEqualTo(BOOK, book.getId())
                .whereEqualTo(REQUESTER, mUID)
                .get()
                .addOnCompleteListener(querySnapShot -> {
                    List<DocumentSnapshot> documents = querySnapShot.getResult().getDocuments();

                    if (querySnapShot.getResult().isEmpty()) {
                        onErrorListener.error();
                        return;
                    }
                    if (documents.size() != 1) {
                        onErrorListener.error();
                        return;
                    }

                    onFinished.finished(documents.get(0).toObject(Request.class));
                });
    }

    /**
     * Update state and status of request and book
     *
     * @param request           request object to be updated in the database
     * @param book              book object to update
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void completeExchange(Request request, Book book, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        WriteBatch batch = mDb.batch();
        DocumentReference requestCol = mDb.collection(REQUEST_COLLECTION).document(request.getId());
        DocumentReference bookCol = mDb.collection(BOOK_COLLECTION).document(book.getId());

        requestCol.update(STATE, request.getState());
        bookCol.update(STATUS, book.getStatus());

        batch.commit()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);

    }

    /**
     * Gets an observable list of books that are requested or borrowed by the current user.
     *
     * @return observable list of books
     */
    public MutableLiveData<List<Book>> getBooks() {
        return mBooks;
    }

    /**
     * Removes snapshot listeners. Should be called just before the owning ViewModel is destroyed.
     */
    public void detachListeners() {
        mListenerRegistration.remove();
    }


    /**
     * Simple callback interface for asynchronous events
     */
    public interface OnFinishedListener {
        void finished(Request request);
    }

    /**
     * Callback interface for errors with asynchronous events
     */
    public interface OnErrorListener {
        void error();
    }
}
