package com.example.unlibrary.exchange;

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

public class ExchangeRepository {

    FirebaseFirestore db;
    Query query;
    ListenerRegistration registration;

    MutableLiveData<ArrayList<Book>> books;

    public ExchangeRepository () {
        db = FirebaseFirestore.getInstance();
        query = db.collection("Books");
        books = new MutableLiveData<>(new ArrayList<Book>());
    }

    public void attachListener () {
        registration = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("listen:error", error);
                return;
            }
            ArrayList<Book> dbBooks = new ArrayList<>();
            for (DocumentSnapshot doc: value.getDocuments()) {
                if (doc.getData().get("mStatus").equals("AVAILABLE") || doc.getData().get("mStatus").equals("REQUESTED") ) {
                    dbBooks.add(new Book(doc.getId(), (String) doc.getData().get("mTitle"), (String) doc.getData().get("mAuthor"), (String) doc.getData().get("mStatus")));
                }
            }
            books.setValue(dbBooks);
        });
    }

    public void detachListener () {
        registration.remove();
    }

    public LiveData<ArrayList<Book>> getBooks () {
        return this.books;
    }

}

