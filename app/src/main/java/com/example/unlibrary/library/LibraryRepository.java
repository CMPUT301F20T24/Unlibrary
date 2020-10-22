package com.example.unlibrary.library;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * LibraryRepository
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

/**
 * Manages all the database interaction for the Library ViewModel.
 */
public class LibraryRepository {
    FirebaseFirestore db;
    Query query;
    ListenerRegistration registration;
    MutableLiveData<ArrayList<Book>> books;

    /**
     * Constructor for the Library Repository.
     */
    public LibraryRepository () {
        db = FirebaseFirestore.getInstance();
        query = db.collection("testRepo");
        
        ArrayList<Book> aBooks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            aBooks.add(new Book("abcd-1234", "Crafting the interpreter", "https://craftinginterpreters.com/", "me", null));
        }

        books = new MutableLiveData<>(aBooks);
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     */
    public void attachListener () {
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // error handling
                if (error != null) {
                    Log.w("listen:error", error);
                    return;
                }

                //update the list to reflect changes in the database
                ArrayList<Book> dbBooks = new ArrayList<>();
                for (DocumentSnapshot doc: value.getDocuments()) {
                    dbBooks.add(new Book (doc.getId(), (String)doc.getData().get("Title"), null, null, null));
                }

                books.setValue(dbBooks);
            }
        });
    }

    /**
     * Save new Book object into the database.
     * @param book book object to be saved in the database.
     */
    public void createObject (Book book) {
        HashMap<String, String> data = new HashMap<>();
        if (book.getIsbn().length()>0 && book.getTitle().length()>0) {
            data.put("Title", book.getTitle());
            db.collection("books").document(book.getIsbn())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Create", "Document succesfully written");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Create", "DocumentSnapshot not written", e);
                        }
                    });
        }
    }

    /**
     * Update the title field of the object in the document.
     * @param book book object to be saved in the database.
     */
    public void updateObjectField (Book book) {
        db.collection("books").document(book.getIsbn())
                .update("Title", book.getTitle())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Create", "Document succesfully Updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Create", "DocumentSnapshot not updated", e);
                    }
                });
    }

    /**
     * Delete book object from the database.
     * @param book book object to be deleted from the database.
     */
    public void deleteObject (Book book) {
        db.collection("books")
                .document(book.getIsbn())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    // These are a method which gets executed when the task is succeeded
                        Log.d("Delete", "Data has been deleted successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d("Delete", "Data could not be deleted!" + e.toString());
                    }
                });
    }

    /**
     * Detach listener when fragment is no longer being viewed.
     */
    public void detachListener () {
        registration.remove();
    }

    /**
     * Getter for the books object.
     * @return LiveData<ArrayList<Book>> This returns the books object.
     */
    public LiveData<ArrayList<Book>> getBooks () {
        return this.books;
    }
}
