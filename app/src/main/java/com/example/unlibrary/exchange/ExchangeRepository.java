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

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Manages all the database interaction for the ExchangeViewModel.
 */
public class ExchangeRepository {
    private static final String REQUEST_COLLECTION = "requests";
    private static final String BOOK_COLLECTION = "books";
    private static final String OWNER = "owner";
    private static final String STATUS = "status";
    private static final String ALGOLIA_INDEX_NAME = "books";

    private static final String TAG = ExchangeRepository.class.getSimpleName();

    private final FirebaseFirestore mDb;
    private final Client mAlgoliaClient;

    private final MutableLiveData<List<Book>> mBooks;
    private ListenerRegistration mListenerRegistration;
    private String mUID;

    /**
     * Constructor for the Exchange Repository.
     */
    @Inject
    public ExchangeRepository(FirebaseFirestore db, Client algoliaClient) {
        mDb = db;
        mAlgoliaClient = algoliaClient;
        mBooks = new MutableLiveData<>(new ArrayList<>());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // TODO: make sure user is authenticated
        mUID = user.getUid();
        attachListener();
    }

    /**
     * Returns id of currently logged on user.
     *
     * @return unique identifier of user
     */
    public String getUid() {
        return mUID;
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listens to any changes in the database
     * and updates books object
     */
    public void attachListener() {
        mListenerRegistration = mDb.collection(BOOK_COLLECTION)
                .whereIn(STATUS, Arrays.asList(Book.Status.AVAILABLE, Book.Status.REQUESTED))
                .whereNotEqualTo(OWNER, FirebaseAuth.getInstance().getUid())
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
     * Save new Request into the database. Assumes Request is valid.
     *
     * @param request           request object to be saved in the database
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void createRequest(Request request, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        mDb.collection(REQUEST_COLLECTION).add(request)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Getter for the books object.
     *
     * @return LiveData<ArrayList < Book>> This returns the books object.
     */
    public LiveData<List<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Removes snapshot listeners. Should be called just before the owning ViewModel is destroyed.
     */
    public void detachListener() {
        mListenerRegistration.remove();
    }

    public void search(String keywords) {
        Index index = mAlgoliaClient.getIndex(ALGOLIA_INDEX_NAME);
        index.searchAsync(new Query(keywords), (content, error) -> {
            if (error != null) {
                Log.e(TAG, "search: algolia error", error);
            }

            Log.d(TAG, "search: " + content.toString());
        });
    }
}

