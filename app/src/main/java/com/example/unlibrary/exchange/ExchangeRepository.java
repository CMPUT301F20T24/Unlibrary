/*
 * ExchangeRepository
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.exchange;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages all the database interaction for the ExchangeViewModel.
 */
public class ExchangeRepository {
    private static final String BOOKS_COLLECTION = "Books";
    private static final String TAG = ExchangeRepository.class.getSimpleName();

    private final FirebaseFirestore mDb;
    private final MutableLiveData<ArrayList<Book>> mBooks;
    private ListenerRegistration mListenerRegistration;

    /**
     * Constructor for the Exchange Repository.
     */
    public ExchangeRepository() {
        mDb = FirebaseFirestore.getInstance();
        mBooks = new MutableLiveData<>(new ArrayList<>());
        attachListener();
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listens to any changes in the database
     * and updates books object
     */
    public void attachListener() {
        mListenerRegistration = mDb.collection(BOOKS_COLLECTION)
                .whereIn("status", Arrays.asList(Book.Status.AVAILABLE, Book.Status.REQUESTED))
                .whereNotEqualTo("owner", FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, error);
                        return;
                    }

                    // Update the list to reflect changes in the database
                    // TODO: use getDocumentChanges instead to minimize payload from Firestore
                    ArrayList<Book> dbBooks = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        // Only show the book with AVAILABLE or REQUESTED status for exchange
                        dbBooks.add(doc.toObject(Book.class));
                    }

                    mBooks.setValue(dbBooks);
                });
    }

    /**
     * Getter for the books object.
     *
     * @return LiveData<ArrayList < Book>> This returns the books object.
     */
    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Removes snapshot listeners. Should be called just before the owning ViewModel is destroyed.
     */
    public void detachListener() {
        mListenerRegistration.remove();
    }
}

