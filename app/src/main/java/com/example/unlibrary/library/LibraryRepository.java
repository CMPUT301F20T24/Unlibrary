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

public class LibraryRepository {
    FirebaseFirestore db;
    Query query;
    ListenerRegistration registration;
    MutableLiveData<ArrayList<Book>> books;

    public LibraryRepository () {
        db = FirebaseFirestore.getInstance();
        query = db.collection("testRepo");
        books = new MutableLiveData<>(new ArrayList<Book>());
    }

    public void attachListener () {
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("listen:error", error);
                    return;
                }

                ArrayList<Book> temp = new ArrayList<>();
                for (DocumentSnapshot doc: value.getDocuments()) {
                    temp.add(new Book (doc.getId(), (String)doc.getData().get("Title")));
                }

                books.setValue(temp);
            }
        });
    }

    public void createObject (String book) {
        db.collection("books").document(book)
                .set(book)
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

    public void detachListener () {
        registration.remove();
    }

    public LiveData<ArrayList<Book>> getBooks () {
        return this.books;
    }
}
