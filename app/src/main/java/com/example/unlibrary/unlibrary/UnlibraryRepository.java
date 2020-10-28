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

import java.util.ArrayList;

/**
 * Manages all the database interaction for {@link UnlibraryViewModel}
 */
public class UnlibraryRepository {
    private final static String TAG = "UNLIBRARY_REPOSITORY";
    private final static String BOOK_COLLECTION = "Books";

    private final FirebaseFirestore mDb;
    private final MutableLiveData<ArrayList<Book>> mBooks = new MutableLiveData<>(new ArrayList<>());

    /**
     * Constructor for UnlibraryRepository. Sets up Firestore
     */
    public UnlibraryRepository() {
        mDb = FirebaseFirestore.getInstance();
        mDb.collection(BOOK_COLLECTION).addSnapshotListener((snapshot, error) -> {
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
    public MutableLiveData<ArrayList<Book>> getBooks() {
        return mBooks;
    }

    /**
     * Adds a new book entry in Firestore. This book entry does not need to have an id, this id
     * will be automatically generated in Firestore backend.
     *
     * @param book new book to add
     */
    public void addBook(Book book) {
        mDb.collection(BOOK_COLLECTION).add(book).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "Success uploading book " + book.getTitle());
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Unable to upload book " + book.getTitle(), e);
        });

    }

    /**
     * Removes a book entry from Firestore.
     *
     * @param book book to delete
     */
    public void removeBook(Book book) {
        mDb.collection(BOOK_COLLECTION).document(book.getId()).delete().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Successfully removed book " + book.getTitle());
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Unable to remove book " + book.getTitle());
        });
    }
}
