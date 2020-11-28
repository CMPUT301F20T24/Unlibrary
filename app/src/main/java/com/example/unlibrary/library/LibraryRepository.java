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
import com.example.unlibrary.models.Book.Status;
import com.example.unlibrary.models.Request;
import com.example.unlibrary.models.User;
import com.example.unlibrary.util.FilterMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.example.unlibrary.models.Request.State.ARCHIVED;

/**
 * Manages all the database interaction for the Library ViewModel.
 */
public class LibraryRepository {
    private static final String ISBN_FETCH_TAG = "isbn fetch";
    private static final String BOOKS_COLLECTION = "books";
    private static final String REQUESTS_COLLECTION = "requests";
    private static final String USERS_COLLECTION = "users";
    private static final String IS_READY_FOR_HANDOFF = "isReadyForHandoff";
    private static final String BOOK_FIELD = "book";
    private static final String STATUS_FIELD = "status";
    private static final String STATE_FIELD = "state";
    private static final String OWNER_FIELD = "owner";

    private static final String TAG = LibraryRepository.class.getSimpleName();
    private static final String ALGOLIA_INDEX_NAME = "books";
    private static final String REQUESTER = "requester";
    private static final String LOCATION = "location";

    // Algolia field names
    // TODO: Consider using POJOs for algolia
    private static final String ALGOLIA_TITLE_FIELD = "title";
    private static final String ALGOLIA_AUTHOR_FIELD = "author";
    private static final String ALGOLIA_ID_FIELD = "objectID";
    private final Client mAlgoliaClient;

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ListenerRegistration mBookListenerRegistration;
    private ListenerRegistration mBooksListenerRegistration;
    private ListenerRegistration mRequestsListenerRegistration;
    private ListenerRegistration mHandoffLocationListenerRegistration;

    private MutableLiveData<List<Book>> mBooks;
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
        this.mFilter = new FilterMap(true);
        mAuth.addAuthStateListener((a) -> {
            mUser = a.getCurrentUser();
            if (mUser != null) {
                attachListener();
            }
        });
    }

    /**
     * Attaches listener to any changes to requests related to bookRequestedID to get the handoff location
     *
     * @param bookRequestedID id of requested book
     * @param listener        Callback to set the handoff location of the selected book
     */
    public void addHandoffLocationListener(String bookRequestedID, OnSuccessListener<GeoPoint> listener) {
        if (mHandoffLocationListenerRegistration != null) {
            mHandoffLocationListenerRegistration.remove();
        }

        // Query returns all requests for this book that are not archived
        mHandoffLocationListenerRegistration = mDb.collection(REQUESTS_COLLECTION)
                .whereEqualTo(BOOK_FIELD, bookRequestedID)
                .whereNotEqualTo(STATE_FIELD, ARCHIVED)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error getting handoff location", error);
                        return;
                    }

                    // If the request is not in either Accepted or Borrowed state, location is null
                    // null location is handled in viewmodel / view (map is not initiated and location
                    // pointer is not accessed)
                    List<Request> requests = value.toObjects(Request.class);
                    listener.onSuccess(requests.get(0).getLocation());
                });
    }

    /**
     * Removes listener to handoff location for currently selected book
     */
    public void detachHandoffLocationListener() {
        if (mHandoffLocationListenerRegistration != null) {
            mHandoffLocationListenerRegistration.remove();
        }
    }

    /**
     * Listen to any changes in the database and update the books list.
     */
    public void attachListener() {
        mDb.collection(BOOKS_COLLECTION).addSnapshotListener((value, error) -> Log.d(TAG, "onEvent: "));
        Query query = mDb.collection(BOOKS_COLLECTION).whereEqualTo(OWNER_FIELD, mUser.getUid());

        // Filter according to status in UI if any
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

        mAlgoliaClient.getIndex(ALGOLIA_INDEX_NAME)
                .deleteObjectAsync(book.getId(), (jsonObject, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Unable to delete book " + book.getId(), e);
                    }
                });
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
        if (mBookListenerRegistration != null) {
            mBooksListenerRegistration.remove();
        }
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
     * Listens to requesters on given book.
     *
     * @param bookId   Firestore assigned bookId
     * @param listener to give back the list of users actively requesting for the given book
     */
    public void addBookRequestersListener(String bookId, OnSuccessListener<List<User>> listener) {
        if (mRequestsListenerRegistration != null) {
            mRequestsListenerRegistration.remove();
        }

        Query query = mDb.collection(REQUESTS_COLLECTION)
                .whereEqualTo(BOOK_FIELD, bookId)
                .whereNotEqualTo(STATE_FIELD, ARCHIVED);

        // TODO only use getDocumentChanges instead of rebuilding the entire list
        mRequestsListenerRegistration = query.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Error fetching requests for book" + bookId, error);
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

            // There are no more active requests on this book
            if (addRequesterTasks.size() == 0) {
                listener.onSuccess(new ArrayList<>());
                return;
            }

            // No failure listener added because this task will never fail
            Tasks.whenAllComplete(addRequesterTasks)
                    .addOnSuccessListener(aVoid -> {
                        listener.onSuccess(requesters);
                    });
        });
    }

    /**
     * Sets up a listener to callback to for whenever book details are updated (e.g. status)
     *
     * @param bookId Firestore assigned bookId (use Book::getId())
     */
    public void addBookListener(String bookId, OnSuccessListener<Book> listener) {
        if (mBookListenerRegistration != null) {
            mBookListenerRegistration.remove();
        }

        mBookListenerRegistration = mDb.collection(BOOKS_COLLECTION).document(bookId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error updating book details", error);
                        return;
                    }

                    listener.onSuccess(value.toObject(Book.class));
                });
    }

    /**
     * Removes the current snapshot listener for requesters
     */
    public void detachRequestersListener() {
        try {
            mRequestsListenerRegistration.remove();
        } catch (Exception e) {
            Log.d(TAG, "Failed to remove listener registration. Probably was null.", e);
        }
    }

    /**
     * Get the borrowed request associated with the current book.
     *
     * @param book              book request is associated with
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void getBorrowedRequest(Book book, OnSuccessListener<? super QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        Query query = mDb.collection(REQUESTS_COLLECTION).whereEqualTo(BOOK_FIELD, book.getId()).whereEqualTo(STATE_FIELD, Request.State.BORROWED.toString());
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

        requestCol.update(STATE_FIELD, request.getState());
        bookCol.update(STATUS_FIELD, book.getStatus());
        bookCol.update(IS_READY_FOR_HANDOFF, book.getIsReadyForHandoff());

        batch.commit()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    /**
     * Make the required changes in FireBase to decline a request
     *
     * @param requestedUID          User ID of requester who made the request
     * @param bookRequestedID       Book ID of book that was requested
     * @param onDeclineSuccess      code to call on successfully declining request
     * @param onDeclineFailure      code to call on failure to decline request
     * @param onRequestNotFoundInDB code to call on failure of fetching requests necessary to potentially change status of book to ACCEPTED
     */
    public void declineRequester(String requestedUID, String bookRequestedID, OnSuccessListener<? super Void> onDeclineSuccess, OnFailureListener onDeclineFailure, Runnable onRequestNotFoundInDB) {
        // Query to find all documents in Requests collection associated with the given book
        mDb.collection(REQUESTS_COLLECTION).whereEqualTo(BOOK_FIELD, bookRequestedID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Request> requestsOnBook = queryDocumentSnapshots.toObjects(Request.class);
                    // Find the request associated to given requester that is in state "REQUESTED"
                    Request requestToUpdate = null;
                    for (Request request : requestsOnBook) {
                        if (request.getRequester().equals(requestedUID) && request.getState().equals(Request.State.REQUESTED)) {
                            requestToUpdate = request;
                            break;
                        }
                    }
                    // Check to make sure requestToUpdate is non-null
                    if (requestToUpdate == null) {
                        onRequestNotFoundInDB.run();
                        return;
                    }

                    // Figure out if there are any other non-archived requests on this book (made by other users)
                    boolean allOtherRequestsAreArchived = true;
                    for (Request request : requestsOnBook) {
                        if (!request.getState().toString().equals(ARCHIVED.toString()) && !request.getId().equals(requestToUpdate.getId())) {
                            allOtherRequestsAreArchived = false;
                            break;
                        }
                    }

                    // Get DocumentReferences required for transaction
                    DocumentReference requestToUpdateDocRef = mDb.collection(REQUESTS_COLLECTION).document(requestToUpdate.getId());
                    DocumentReference currentBookDocRef = mDb.collection(BOOKS_COLLECTION).document(bookRequestedID);

                    // Convert to a final variable; only then it can be referenced later in transaction
                    final boolean finalAllOtherRequestsAreArchived = allOtherRequestsAreArchived;

                    // Transaction to do both updates at once
                    mDb.runTransaction((Transaction.Function<Void>) transaction -> {
                        transaction.update(requestToUpdateDocRef, STATE_FIELD, ARCHIVED.toString());
                        if (finalAllOtherRequestsAreArchived) {
                            transaction.update(currentBookDocRef, STATUS_FIELD, Book.Status.AVAILABLE.toString());
                        }
                        // Success
                        return null;
                    }).addOnSuccessListener(onDeclineSuccess).addOnFailureListener(onDeclineFailure);
                })
                .addOnFailureListener(onDeclineFailure);
    }

    /**
     * Accepts the selected requester, sets the handoff location, and clears the other requesters
     *
     * @param requestedUID      accepted requester user ID
     * @param bookRequestedID   book ID request is associated with
     * @param handoffLocation   LatLng of handoff location
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void acceptRequester(String requestedUID, String bookRequestedID, LatLng handoffLocation, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        mDb.collection(REQUESTS_COLLECTION)
                .whereEqualTo(BOOK_FIELD, bookRequestedID)
                .whereNotEqualTo(STATE_FIELD, ARCHIVED)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Request> requests = queryDocumentSnapshots.toObjects(Request.class);
                    mDb.runTransaction(transaction -> {

                        // Firestore transactions do not allow reads to occur after a write
                        DocumentReference acceptedRequestDocument = null;
                        ArrayList<DocumentReference> declinedRequestDocuments = new ArrayList<>();
                        for (Request r : requests) {
                            String id = r.getId();
                            DocumentReference requestDocument = mDb.collection(REQUESTS_COLLECTION).document(id);
                            DocumentSnapshot snapshot = transaction.get(requestDocument);

                            if (snapshot.get(REQUESTER).equals(requestedUID)) {
                                acceptedRequestDocument = requestDocument;

                            } else {
                                declinedRequestDocuments.add(requestDocument);
                            }
                        }

                        if (acceptedRequestDocument == null) {
                            onFailureListener.onFailure(new Exception("Failed to accept requester"));
                            return null;
                        }

                        transaction.update(acceptedRequestDocument, LOCATION, new GeoPoint(handoffLocation.latitude, handoffLocation.longitude));
                        transaction.update(acceptedRequestDocument, STATE_FIELD, Request.State.ACCEPTED.toString());

                        for (DocumentReference doc : declinedRequestDocuments) {
                            transaction.update(doc, STATE_FIELD, ARCHIVED.toString());
                        }

                        final DocumentReference bookDocument = mDb.collection(BOOKS_COLLECTION).document(bookRequestedID);
                        transaction.update(bookDocument, STATUS_FIELD, Status.ACCEPTED.toString());

                        // Success
                        return null;
                    })
                            .addOnSuccessListener(onSuccessListener)
                            .addOnFailureListener(onFailureListener);
                })
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Updates the handoff location for the book
     *
     * @param requestedUID      accepted requester user ID
     * @param bookRequestedID   book ID request is associated with
     * @param handoffLocation   LatLng of handoff location
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void updateHandoffLocation(String requestedUID, String bookRequestedID, LatLng handoffLocation, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        mDb.collection(REQUESTS_COLLECTION)
                .whereEqualTo(REQUESTER, requestedUID)
                .whereEqualTo(BOOK_FIELD, bookRequestedID)
                .whereNotEqualTo(STATE_FIELD, ARCHIVED)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Request> requests = queryDocumentSnapshots.toObjects(Request.class);
                    if (requests.size() != 1) {
                        onFailureListener.onFailure(new Exception("Unexpected number of requests returned during update location.  " + requests.size() + " requests found."));
                        return;
                    }
                    Request request = requests.get(0);
                    mDb.collection(REQUESTS_COLLECTION).document(request.getId())
                            .update(LOCATION, new GeoPoint(handoffLocation.latitude, handoffLocation.longitude))
                            .addOnSuccessListener(onSuccessListener)
                            .addOnFailureListener(onFailureListener);
                })
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Callback for fetching handoff location
     */
    public interface OnFinishedHandoffLocationListener {
        void onFinished(GeoPoint geoPoint);
    }
}
