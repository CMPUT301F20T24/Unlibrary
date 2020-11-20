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

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.example.unlibrary.models.User;
import com.example.unlibrary.util.FilterMap;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.firebase.firestore.Transaction;

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
    private static final String BOOK = "book";
    private static final String STATE = "state";
    private static final String STATUS = "status";
    private static final String IS_READY_FOR_HANDOFF = "isReadyForHandoff";
    private static final String REQUESTER_FIELD = "requester";
    private static final String BOOK_FIELD = "book";
    private static final String STATUS_FIELD = "status";
    private static final String STATE_FIELD = "state";

    private static final String TAG = LibraryRepository.class.getSimpleName();
    private static final String ALGOLIA_INDEX_NAME = "books";

    // Algolia field names
    // TODO: Consider using POJOs for algolia
    private static final String ALGOLIA_TITLE_FIELD = "title";
    private static final String ALGOLIA_AUTHOR_FIELD = "author";
    private static final String ALGOLIA_ID_FIELD = "id";
    private final Client mAlgoliaClient;
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
    public LibraryRepository(FirebaseFirestore db, FirebaseAuth auth, Client algoliaClient) {
        mDb = db;
        mAuth = auth;
        mBooks = new MutableLiveData<>(new ArrayList<>());
        mAlgoliaClient = algoliaClient;
        mCurrentBookRequesters = new MutableLiveData<>(new ArrayList<>());
        this.mFilter = new FilterMap(true);
        attachListener();
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     */
    public void attachListener() {
        mDb.collection(BOOKS_COLLECTION).addSnapshotListener((value, error) -> Log.d(TAG, "onEvent: "));
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
                .addOnSuccessListener(documentReference -> {
                    // Also add book to algolia with Firestore ID reference
                    book.setId(documentReference.getId());
                    addAlgoliaIndex(book);

                    onSuccessListener.onSuccess(documentReference);
                })
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
     * Adds book to Algolia search index so it can be searched in {@link com.example.unlibrary.exchange.ExchangeFragment}.
     * Book's id, title, and author are assumed to be non null.
     *
     * @param book new book to add to search index
     */
    public void addAlgoliaIndex(Book book) {
        Index index = mAlgoliaClient.getIndex(ALGOLIA_INDEX_NAME);
        try {
            index.addObjectAsync(new JSONObject()
                            .put(ALGOLIA_TITLE_FIELD, book.getTitle())
                            .put(ALGOLIA_AUTHOR_FIELD, book.getAuthor())
                            .put(ALGOLIA_ID_FIELD, book.getId()),
                    (jsonObject, e) -> {
                        if (e != null) {
                            Log.e(TAG, "createBook: Unable to push to algolia", e);
                            return;
                        }
                        Log.d(TAG, "createBook: Success adding index to algolia! " + book.getId());
                    });
        } catch (JSONException e) {
            Log.e(TAG, "createBook: Unable to push to algolia", e);
        }
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
        // Clear the previous book's requesters in time before requesters list gets displayed
        mCurrentBookRequesters.setValue(new ArrayList<>());
        // Attach snapshot listener for requesters on current book
        attachRequestsListener(currentBookID);
    }

    /**
     * Attaches snapshot listener for requests on a given book
     *
     * @param bookID requests on this book will be listened to
     */
    public void attachRequestsListener(String bookID) {
        // Get all requests associated with current book that are in REQUESTED state
        Query query = mDb.collection(REQUESTS_COLLECTION)
                .whereEqualTo(BOOK_FIELD, bookID)
                .whereEqualTo(STATE_FIELD, Request.State.REQUESTED.name());


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

    /**
     * Get the borrowed request associated with the current book.
     *
     * @param book              book request is associated with
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void getBorrowedRequest(Book book, OnSuccessListener<? super QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        Query query = mDb.collection(REQUESTS_COLLECTION).whereEqualTo(BOOK, book.getId()).whereEqualTo(STATE, Request.State.BORROWED.toString());
        query.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    /**
     * Update state and status of request and book
     *
     * @param request           request object to be updated in the database
     * @param book              book object to update
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void completeExchange(Request request, Book book, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        WriteBatch batch = mDb.batch();
        DocumentReference requestCol = mDb.collection(REQUESTS_COLLECTION).document(request.getId());
        DocumentReference bookCol = mDb.collection(BOOKS_COLLECTION).document(book.getId());

        requestCol.update(STATE, request.getState());
        bookCol.update(STATUS, book.getStatus());
        bookCol.update(IS_READY_FOR_HANDOFF, book.getIsReadyForHandoff());

        batch.commit()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Delete book from the database.
     * Make the required changes in FireBase to decline a request
     *
     * @param requestedUID     User ID of requester who made the request
     * @param bookRequestedID  Book ID of book that was requested
     * @param onDeclineSuccess code to call on successfully declining request
     * @param onDeclineFailure code to call on failure to decline request
     */
    public void declineRequester(String requestedUID, String bookRequestedID, OnSuccessListener<? super Void> onDeclineSuccess, OnFailureListener onDeclineFailure) {
        // Query to find all Request documents associated with the given book
        mDb.collection(REQUESTS_COLLECTION).whereEqualTo(BOOK_FIELD, bookRequestedID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Request> requestsOnBook = queryDocumentSnapshots.toObjects(Request.class);

                    // From these queried requests, find the request made by given requester that is non-archived; this is the one we should decline
                    // Note there should only be one such requests; we use an ArrayList to find all such requests and log error if the number of such requests is not 1
                    ArrayList<Request> matchingRequests = new ArrayList<>();
                    for (Request request : requestsOnBook) {
                        if (request.getRequester().equals(requestedUID) && !request.getState().equals(Request.State.ARCHIVED)) {
                            matchingRequests.add(request);
                            //Sanity Check
                            if (request.getState().equals(Request.State.BORROWED) || request.getState().equals(Request.State.ACCEPTED )) {
                                Log.e(TAG, "Unexpected requests were found for book ID " + bookRequestedID + " and requesterID " + requestedUID);
                                return;
                            }
                        }
                    }
                    // Check to make sure we actually found the request and there was only one such request
                    if (matchingRequests.size() == 0) {
                        Log.e(TAG, "The request was not found for book ID " + bookRequestedID + " and requesterID " + requestedUID);
                        return;  // We don't show toast here to user. Can we do that (we don't have access to mFailureMsgEvent of ViewModel)?
                    } else if (matchingRequests.size() != 1) {
                        Log.e(TAG, "More than one request was returned for book ID " + bookRequestedID + " and requesterID " + requestedUID);
                        return;  // We don't show toast here to user. Can we do that (we don't have access to mFailureMsgEvent of ViewModel)?
                    }
                    Request requestToUpdate = matchingRequests.get(0);

                    // Figure out if there are any other non-archived requests on this book (made by other users)
                    boolean allOtherRequestsAreArchived = true;
                    for (Request request : requestsOnBook) {
                        if (!request.getState().toString().equals(Request.State.ARCHIVED.name()) && !request.getId().equals(requestToUpdate.getId())) {
                            allOtherRequestsAreArchived = false;
                            break;
                        }
                    }

                    // Get DocumentReferences required for transaction
                    DocumentReference requestToUpdateDocRef = mDb.collection(REQUESTS_COLLECTION).document(requestToUpdate.getId());
                    DocumentReference currentBookDocRef = mDb.collection(BOOKS_COLLECTION).document(bookRequestedID);
                    // Convert to a final variable so it can be referenced later in transaction
                    final boolean updateBookStatus = allOtherRequestsAreArchived;

                    // Transaction to do both updates at once
                    mDb.runTransaction((Transaction.Function<Void>) transaction -> {
                        transaction.update(requestToUpdateDocRef, STATE_FIELD, Request.State.ARCHIVED.name());
                        if (updateBookStatus) {
                            transaction.update(currentBookDocRef, STATUS_FIELD, Book.Status.AVAILABLE.name());
                        }
                        // Success
                        return null;
                    }).addOnSuccessListener(onDeclineSuccess).addOnFailureListener(onDeclineFailure);
                    // TODO: Need to change mCurrentBook locally in viewmodel
                })
                .addOnFailureListener(onDeclineFailure);
    }
}
