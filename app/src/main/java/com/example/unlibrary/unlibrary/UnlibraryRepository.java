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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Manages all the database interaction for {@link UnlibraryViewModel}
 */
public class UnlibraryRepository {
    private final static String TAG = UnlibraryRepository.class.getSimpleName();
    private final static String BOOK_COLLECTION = "books";

    private final FirebaseFirestore mDb;
    private final MutableLiveData<List<Book>> mBooks = new MutableLiveData<>(new ArrayList<>());
    private final ListenerRegistration mListenerRegistration;

    /**
     * Constructor for UnlibraryRepository. Sets up Firestore
     * <p>
     * TODO: Add querying logic to return only books that have been requested or borrowed by the user
     */
    @Inject
    public UnlibraryRepository(FirebaseFirestore db) {
        mDb = db;
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
