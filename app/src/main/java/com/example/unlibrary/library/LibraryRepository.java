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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.unlibrary.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Manages all the database interaction for the Library ViewModel.
 */
public class LibraryRepository {

    private static final String ISBN_FETCH_TAG = "isbn fetch";
    private static final String BOOKS_COLLECTION = "books";
    private static final String TAG = LibraryRepository.class.getSimpleName();

    FirebaseFirestore mDb;
    FirebaseAuth mAuth;
    ListenerRegistration listenerRegistration;
    MutableLiveData<ArrayList<Book>> mBooks;

    /**
     * Constructor for the Library Repository.
     */
    public LibraryRepository() {
        // TODO
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mBooks = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     */
    // TODO listener not working
    public void attachListener() {
        // TODO
        listenerRegistration = mDb.collection(BOOKS_COLLECTION).addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Error listening", error);
                return;
            }

            // TODO only use getDocumentChanges
            // Rebuild the list
            ArrayList<Book> newBooks = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                newBooks.add(doc.toObject(Book.class));
            }
            mBooks.setValue(newBooks);
        });
    }

    /**
     * Save new Book into the database. Assumes book is valid.
     *
     * @param book book object to be saved in the database.
     */
    public void createBook(Book book) {
        // TODO check null case
        String uid = mAuth.getCurrentUser().getUid();
        book.setOwner(uid);
        mDb.collection(BOOKS_COLLECTION).add(book)
                .addOnSuccessListener(documentReference -> {
                    // TODO
                    System.out.println("Success");
                })
                .addOnFailureListener(e -> {
                    // TODO
                    System.out.println("Failure");
                });
    }

    /**
     * Update a book document.
     *
     * @param book book object to be updated in the database.
     */
    public void updateBook(Book book) {
        mDb.collection(BOOKS_COLLECTION).document(book.getId())
                .set(book)
                .addOnSuccessListener(documentReference -> {
                    // TODO
                })
                .addOnFailureListener(e -> {
                    // TODO
                });
    }

    /**
     * Delete book from the database.
     *
     * @param book book object to be deleted from the database.
     */
    public void deleteObject(Book book) {
        mDb.collection(BOOKS_COLLECTION).document(book.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // TODO
                })
                .addOnFailureListener(e -> {
                    // TODO
                });
    }

    // TODO
    public void fetchBookDataFromIsbn(String isbn, JSONObjectRequestListener listener) {
        AndroidNetworking.get("https://www.googleapis.com/books/v1/volumes")
                .addQueryParameter("q", "ISBN:" + isbn)
                .addQueryParameter("key", "AIzaSyAD0VElKl_qWGbjeDSzLKR9PcKuRqbFu6M") // This probably shouldn't be embedded here but ya know...
                .setTag(ISBN_FETCH_TAG)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    /**
     * Detach listener when fragment is no longer being viewed.
     */
    public void detachListener() {
        // TODO
        listenerRegistration.remove();
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
