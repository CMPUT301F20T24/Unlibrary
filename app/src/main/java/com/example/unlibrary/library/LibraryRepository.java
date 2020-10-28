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
import java.util.HashMap;

/**
 * Manages all the database interaction for the Library ViewModel.
 */
public class LibraryRepository {
    private static final String BOOK_COLLECTION = "Books";

    FirebaseFirestore db;
    ListenerRegistration mListenerRegistration;
    MutableLiveData<ArrayList<Book>> mBooks;

    /**
     * Constructor for the Library Repository. Sets up the database snapshot listener.
     */
    public LibraryRepository() {
        db = FirebaseFirestore.getInstance();
        mBooks = new MutableLiveData<>(new ArrayList<>());
        attachListener();
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     */
    public void attachListener() {
        mListenerRegistration = db.collection(BOOK_COLLECTION).addSnapshotListener((snapshots, error) -> {
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
     * Save new Book object into the database.
     *
     * @param book book object to be saved in the database.
     */
    public void createObject(Book book) {
        HashMap<String, String> data = new HashMap<>();
        if (book.getIsbn().length() > 0 && book.getTitle().length() > 0) {
            data.put("Title", book.getTitle());
            db.collection(BOOK_COLLECTION).document(book.getIsbn())
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Create", "Document succesfully written");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Create", "DocumentSnapshot not written", e);
                    });
        }
    }

    /**
     * Update the title field of the object in the document.
     *
     * @param book book object to be saved in the database.
     */
    public void updateObjectField(Book book) {
        db.collection(BOOK_COLLECTION).document(book.getIsbn())
                .update("Title", book.getTitle())
                .addOnSuccessListener(aVoid ->
                        Log.d("Create", "Document succesfully Updated")
                )
                .addOnFailureListener(e ->
                        Log.w("Create", "DocumentSnapshot not updated", e)
                );
    }

    /**
     * Delete book object from the database.
     *
     * @param book book object to be deleted from the database.
     */
    public void deleteObject(Book book) {
        db.collection("books")
                .document(book.getIsbn())
                .delete().addOnSuccessListener(aVoid ->
                Log.d("Delete", "Data has been deleted successfully!")
        )
                .addOnFailureListener(e ->
                        Log.d("Delete", "Data could not be deleted!" + e.toString())
                );
    }

    /**
     * Detach listener when fragment is no longer being viewed.
     */
    public void detachListener() {
        mListenerRegistration.remove();
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
