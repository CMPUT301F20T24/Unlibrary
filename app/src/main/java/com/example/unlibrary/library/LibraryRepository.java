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
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.example.unlibrary.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Manages all the database interaction for the Library ViewModel.
 */
public class LibraryRepository {

    private static final String ISBN_FETCH_TAG = "isbn fetch";
    private static final String BOOKS_COLLECTION = "books";
    private static final String REQUESTS_COLLECTION = "requests";
    private static final String USERS_COLLECTION = "users";
    private static final String TAG = LibraryRepository.class.getSimpleName();

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private ListenerRegistration mBooksListenerRegistration;
    private ListenerRegistration mRequestsListenerRegistration;
    private MutableLiveData<List<Book>> mBooks;
    private MutableLiveData<List<User>> mCurrentBookRequesters;
    private FilterMap mFilter;

    /**
     * Constructor for the Library Repository. Sets up the database snapshot listener.
     */
    @Inject
    public LibraryRepository(FirebaseFirestore db, FirebaseAuth auth) {
        mDb = db;
        mAuth = auth;
        mBooks = new MutableLiveData<>(new ArrayList<>());
        mCurrentBookRequesters = new MutableLiveData<>(new ArrayList<>());
        this.mFilter = new FilterMap();
        attachListener();
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     */
    public void attachListener() {
        Query query = mDb.collection(BOOKS_COLLECTION).whereEqualTo("owner", FirebaseAuth.getInstance().getUid());
        List<String> statusValues = new ArrayList<>();
        for (Map.Entry<Book.Status, Boolean> f : mFilter.getMap().entrySet()) {
            if (f.getValue()) {
                statusValues.add(f.getKey().toString());
            }
        }
        if (!statusValues.isEmpty()) {
            query = query.whereIn("status", statusValues);
        }

        mBooksListenerRegistration = query.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Error listening", error);
                return;
            }

            // TODO only use getDocumentChanges instead of rebuilding the entire list
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
     * @param book              book object to be saved in the database
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void createBook(Book book, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        String uid = mAuth.getCurrentUser().getUid();
        if (uid == null) {
            onFailureListener.onFailure(new NullPointerException("Null user id"));
        }
        book.setOwner(uid);
        mDb.collection(BOOKS_COLLECTION).add(book)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Update a book document.
     *
     * @param book              book object to be updated in the database.
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void updateBook(Book book, OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener) {
        mDb.collection(BOOKS_COLLECTION).document(book.getId())
                .set(book)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Delete book from the database.
     *
     * @param book              book object to be deleted from the database.
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void deleteBook(Book book, OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener) {
        mDb.collection(BOOKS_COLLECTION).document(book.getId())
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Fetch the title and author of a book with Google Books API via ISBN.
     *
     * @param isbn     ISBN of book
     * @param listener code to call on completion
     */
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
     * Removes snapshot listeners. Should be called just before the owning ViewModel is destroyed.
     */
    public void detachListener() {
        mBooksListenerRegistration.remove();
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
     * Filter the results of the books query.
     *
     * @param filter What to filter for
     */
    public void setFilter(FilterMap filter) {
        mFilter = filter;
        detachListener();
        attachListener();
    }

    /**
     * Getter for the LiveData List of requesters on a selected book.
     *
     * @return LiveData<ArrayList < String>> This returns the books object.
     */
    public LiveData<List<User>> getRequesters() {
        return this.mCurrentBookRequesters;
    }

    /**
     * Fetches the list of requesters for a newly selected book by clearing the previous book's requesters and
     * adding a snapshot listener for the new book's requesters
     *
     * @param currentBookID
     */
    public void fetchRequestersForCurrentBook(String currentBookID) {
        mCurrentBookRequesters.setValue(new ArrayList<>()); //Is this required to ensure we clear the previous book's requesters in time before requesters get displayed?
        attachRequestsListener(currentBookID);
    }

    /**
     * Attaches snapshot listener for requests on a given book
     *
     * @param bookID requests on this book will be listened to
     */
    public void attachRequestsListener(String bookID) {
        Query query = mDb.collection(REQUESTS_COLLECTION).whereEqualTo("book", bookID);

        // TODO only use getDocumentChanges instead of rebuilding the entire list
        mRequestsListenerRegistration = query.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Error fetching requests for book" + bookID, error);
                return;
            }

            List<Request> requests = snapshot.toObjects(Request.class);

            ArrayList<Task<DocumentSnapshot>> addRequesterTasks = new ArrayList<>();
            ArrayList<User> requesters = new ArrayList<>();

            for (Request r : requests) {
                addRequesterTasks.add(
                        mDb.collection(USERS_COLLECTION).document(r.getRequester()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    User requester = documentSnapshot.toObject(User.class);
                                    requesters.add(requester);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Unable to get requester " + r.getRequester() + "from database", e);
                                })
                );
            }


            Tasks.whenAllComplete(addRequesterTasks)    //This task will never fail
                    .addOnSuccessListener(aVoid -> {
                        mCurrentBookRequesters.setValue(requesters);
                    });
        });
    }

    /**
     * Removes the current snapshot listener for requesters
     */
    public void detachRequestersListener() {
        mRequestsListenerRegistration.remove();
    }
}
