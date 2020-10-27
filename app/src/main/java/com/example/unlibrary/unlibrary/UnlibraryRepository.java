package com.example.unlibrary.unlibrary;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UnlibraryRepository {
    private final String mTag = "UNLIBRARY_REPOSITORY";
    private final String mDevCollection = "devBooks";

    private FirebaseFirestore mDb;
    private MutableLiveData<ArrayList<Book>> mBooks = new MutableLiveData<>(new ArrayList<>());

    public UnlibraryRepository() {
        mDb = FirebaseFirestore.getInstance();
        mDb.collection(mDevCollection).addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w(mTag, error);
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

    public MutableLiveData<ArrayList<Book>> getBooks() {
        return mBooks;
    }

    public void addBook(Book book) {
        mDb.collection(mDevCollection).add(book).addOnSuccessListener(documentReference -> {
            Log.d(mTag, "Success uploading book " + book.getTitle());
        }).addOnFailureListener(e -> {
            Log.w(mTag, "Unable to upload book " + book.getTitle(), e);
        });

    }

    public void removeBook(Book book) {
        mDb.collection(mDevCollection).document(book.getId()).delete().addOnSuccessListener(aVoid -> {
            Log.d(mTag, "Successfully removed book " + book.getTitle());
        }).addOnFailureListener(e -> {
            Log.w(mTag, "Unable to remove book " + book.getTitle());
        });
    }
}
