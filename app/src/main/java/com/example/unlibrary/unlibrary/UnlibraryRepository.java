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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Manages all the database interaction for {@link UnlibraryViewModel}
 */
public class UnlibraryRepository {
    private final static String TAG = UnlibraryRepository.class.getSimpleName();
    private final static String BOOK_COLLECTION = "books";
    private final static String REQUEST_COLLECTION = "requests";
    private final static String REQUESTER_FIELD = "requester";

    private final FirebaseFirestore mDb;
    private final FirebaseAuth mAuth;
    private final MutableLiveData<List<Book>> mBooks = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration mListenerRegistration;

    /**
     * Constructor for UnlibraryRepository. Sets up Firestore
     * <p>
     * TODO: Add querying logic to return only books that have been requested or borrowed by the user
     */
    @Inject
    public UnlibraryRepository(FirebaseFirestore db, FirebaseAuth auth) {
        mDb = db;
        mAuth = auth;
       attachListeners();
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     *
     * TODO: Get document changes only to minimize payload from Firestore
     * TODO: Find a way so we can wait until all books are loaded before updating the view
     */
    public void attachListeners() {
        Query query = mDb.collection(REQUEST_COLLECTION).whereEqualTo(REQUESTER_FIELD, mAuth.getUid());
        mListenerRegistration = query.addSnapshotListener((snapshot, error) -> {
            List<Request> requests = snapshot.toObjects(Request.class);

            ArrayList<Book> books = new ArrayList<>();
            for (Request request : requests) {
                mDb.collection(BOOK_COLLECTION).document(request.getBook()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Book book = documentSnapshot.toObject(Book.class);
                            books.add(book);
                            mBooks.setValue(books);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "UnlibraryRepository: Unable to get book", e);
                        });
            }
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
