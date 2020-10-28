/*
 * LibraryRepository
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
 * Manages all the database interaction for the Library ViewModel.
 */
public class LibraryRepository {
    private static final String BOOK_COLLECTION = "Books";
    private static final String TAG = "LIBRARY_REPOSITORY";

    private FirebaseFirestore mDb;
    private ListenerRegistration mListenerRegistration;
    private MutableLiveData<ArrayList<Book>> mBooks;

    /**
     * Constructor for the Library Repository. Sets up the database snapshot listener.
     */
    public LibraryRepository() {
        mDb = FirebaseFirestore.getInstance();
        mBooks = new MutableLiveData<>(new ArrayList<>());
        attachListener();
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     */
    public void attachListener() {
        mListenerRegistration = mDb.collection(BOOK_COLLECTION).addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.w("LIBRARY_REPOSITORY", error);
            }

            ArrayList<Book> books = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot book : snapshots.getDocuments()) {
                    books.add(book.toObject(Book.class));
                }

                mBooks.setValue(books);
            }
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
     * Save new Book object into the database.
     *
     * @param book book object to be saved in the database.
     */
    public void addBook(Book book) {
        mDb.collection(BOOK_COLLECTION).add(book)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Create", "Document succesfully written");
                })
                .addOnFailureListener(e -> {
                    Log.w("Create", "DocumentSnapshot not written", e);
                });
    }

    /**
     * Update the title field of the object in the document.
     *
     * @param book book object to be saved in the database.
     */
    public void updateBook(Book book) {
        mDb.collection(BOOK_COLLECTION).document(book.getId()).set(book)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully updated book " + book.getTitle());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Unable to update book " + book.getTitle());
                });
    }

    /**
     * Delete book object from the database.
     *
     * @param book book object to be deleted from the database.
     */
    public void deleteBook(Book book) {
        mDb.collection(BOOK_COLLECTION)
                .document(book.getId())
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("Delete", "Data has been deleted successfully!")
                )
                .addOnFailureListener(e ->
                        Log.d("Delete", "Data could not be deleted!" + e.toString())
                );
    }

    /**
     * Removes snapshot listeners. Should be called just before the owning ViewModel is destroyed.
     */
    public void detachListener() {
        mListenerRegistration.remove();
    }

}
