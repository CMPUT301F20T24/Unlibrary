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

import java.util.ArrayList;
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
        mListenerRegistration = mDb.collection(BOOK_COLLECTION).addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, error);
            }

            ArrayList<Book> books = new ArrayList<>();
            if (snapshot != null) {
                for (DocumentSnapshot book : snapshot.getDocuments()) {
                    Book _book = book.toObject(Book.class);
                    books.add(_book);
                }

                mBooks.setValue(books);
            }
        });

        // TODO: make sure user is authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUID = user.getUid();
    }

    /**
     * Save new Request into the database. Assumes Request is valid.
     *
     * @param book              book object to get request from
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void getRequest(Book book, OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {

        mDb.collection(REQUEST_COLLECTION)
                .whereEqualTo(BOOK, book.getId())
                .whereEqualTo(REQUESTER, mUID)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Update sate and status of request and book
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
}
