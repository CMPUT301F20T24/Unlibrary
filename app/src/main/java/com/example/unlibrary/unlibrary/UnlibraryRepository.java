/*
 * UnlibraryRepository
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.unlibrary;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

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
    private final static String REQUEST_COLLECTION = "requests";
    private final static String REQUESTER_FIELD = "requester";


    private final FirebaseFirestore mDb;
    private final MutableLiveData<List<Book>> mBooks = new MutableLiveData<>(new ArrayList<>());
    private final ListenerRegistration mListenerRegistration;

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
                    .addOnSuccessListener( aVoid -> { mBooks.setValue(books); })
                    .addOnFailureListener( e -> {
                        Log.e("TAG", "Failed to update book list", e);
                    });
        });
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
}
