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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * Manages all the database interaction for the ExchangeViewModel.
 */
public class ExchangeRepository {
    private static final String BOOKS_COLLECTION = "Books";
    private static final String STATUS = "mStatus";
    private static final String TITLE = "mTitle";
    private static final String AUTHOR = "mAuthor";

    FirebaseFirestore mdb;
    Query mQuery;
    ListenerRegistration registration;
    MutableLiveData<ArrayList<Book>> mBooks;

    /**
     * Constructor for the Exchange Repository.
     */
    public ExchangeRepository() {
        mdb = FirebaseFirestore.getInstance();
        mQuery = mdb.collection(BOOKS_COLLECTION);
        mBooks = new MutableLiveData<>(new ArrayList<Book>());
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listens to any changes in the database
     * and updates books object
     */
    public void attachListener() {
        registration = mQuery.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("LISTEN:ERROR", error);
                return;
            }

            // Update the list to reflect changes in the database
            ArrayList<Book> dbBooks = new ArrayList<>();
            for (DocumentSnapshot doc : value.getDocuments()) {
                // Only show the book with AVAILABLE or REQUESTED status for exchange
                Book book = doc.toObject(Book.class);
                if ((book.getStatus() == Book.Status.AVAILABLE || book.getStatus() == Book.Status.REQUESTED)
                        && !book.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    dbBooks.add(book);
                }
            }
            mBooks.setValue(dbBooks);
        });
    }

    /**
     * Detach listener when fragment is no longer being viewed.
     */
    public void detachListener() {
        registration.remove();
    }

    /**
     * Getter for the books object.
     *
     * @return LiveData<ArrayList < Book>> This returns the books object.
     */
    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }
}

