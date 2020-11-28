/*
 * UnlibraryRepository
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.unlibrary;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.library.LibraryRepository;
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
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.example.unlibrary.models.Request.State.ARCHIVED;

/**
 * Manages all the database interaction for {@link UnlibraryViewModel}
 */
public class UnlibraryRepository {
    private final static String TAG = UnlibraryRepository.class.getSimpleName();
    private final static String BOOKS_COLLECTION = "books";
    private static final String REQUEST_COLLECTION = "requests";
    private static final String USER_COLLECTION = "users";
    private static final String REQUESTER = "requester";
    private static final String BOOK = "book";
    private static final String STATE = "state";
    private static final String STATUS = "status";
    private static final String IS_READY_FOR_HANDOFF = "isReadyForHandoff";

    private final FirebaseFirestore mDb;
    private final MutableLiveData<List<Book>> mBooks = new MutableLiveData<>(new ArrayList<>());
    private List<Book> mAllBooks;
    private ListenerRegistration mListenerRegistration;
    private ListenerRegistration mBookListenerRegistration;

    private final String mUID;
    private FilterMap mFilter;

    /**
     * Constructor for UnlibraryRepository. Sets up Firestore
     * <p>
     * TODO: Add querying logic to return only books that have been requested or borrowed by the user
     */
    @Inject
    public UnlibraryRepository(FirebaseFirestore db, FirebaseAuth auth) {
        mDb = db;

        mUID = auth.getCurrentUser().getUid();
        mAllBooks = new ArrayList<>();
        mFilter = new FilterMap(true);

        attachListeners();
    }

    /**
     * Attach a listener to a QuerySnapshot from Firestore. Listen to any changes in the database
     * and update the books object.
     */
    public void attachListeners() {
        // TODO: Get document changes only to minimize payload from Firestore
        mListenerRegistration = mDb.collection(REQUEST_COLLECTION)
                .whereEqualTo(REQUESTER, mUID)
                .whereNotEqualTo(STATE, Request.State.ARCHIVED.toString())
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Unable to get requests from database", error);
                        return;
                    }

                    List<Request> requests = snapshot.toObjects(Request.class);

                    ArrayList<Task<DocumentSnapshot>> addBookTasks = new ArrayList<>();
                    mAllBooks.clear();

                    for (Request r : requests) {
                        addBookTasks.add(
                                mDb.collection(BOOKS_COLLECTION).document(r.getBook()).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            Book book = documentSnapshot.toObject(Book.class);
                                            mAllBooks.add(book);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Unable to get book " + r.getBook() + "from database", e);
                                        })
                        );
                    }

                    Tasks.whenAllComplete(addBookTasks)
                            .addOnSuccessListener(aVoid -> {
                                filter();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("TAG", "Failed to update book list", e);
                            });
                });

    }

    /**
     * Filter the results of the books query.
     *
     * @param filter What to filter for
     */
    public void setFilter(FilterMap filter) {
        mFilter = filter;
        filter();
    }

    public void filter() {
        List<Book> filtered = new ArrayList<>();
        List<String> statusValues = new ArrayList<>();
        for (Map.Entry<Book.Status, Boolean> f : mFilter.getMap().entrySet()) {
            if (f.getValue()) {
                statusValues.add(f.getKey().toString());
            }
        }

        for (Book book : mAllBooks) {
            if (statusValues.isEmpty() || statusValues.contains(book.getStatus().toString())) {
                filtered.add(book);
            }
        }

        mBooks.setValue(filtered);
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
     * Save new Request into the database. Assumes Request is valid.
     *
     * @param book            book object to get request from
     * @param onFinished      code to call on success
     * @param onErrorListener code to call on failure
     */
    public void getRequest(Book book, OnSuccessListener<Request> onFinished, OnFailureListener onErrorListener) {
        mDb.collection(REQUEST_COLLECTION)
                .whereEqualTo(BOOK, book.getId())
                .whereEqualTo(REQUESTER, mUID)
                .whereNotEqualTo(STATE, ARCHIVED)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Request> request = snapshots.toObjects(Request.class);
                    if (request.size() != 1) {
                        onErrorListener.onFailure(new IllegalStateException("Request not found in database"));
                        return;
                    }
                    onFinished.onSuccess(request.get(0));
                })
                .addOnFailureListener(onErrorListener::onFailure);
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
        DocumentReference requestCol = mDb.collection(REQUEST_COLLECTION).document(request.getId());
        DocumentReference bookCol = mDb.collection(BOOKS_COLLECTION).document(book.getId());

        requestCol.update(STATE, request.getState());
        bookCol.update(STATUS, book.getStatus());
        bookCol.update(IS_READY_FOR_HANDOFF, book.getIsReadyForHandoff());

        batch.commit()
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
     * Gets an observable list of books that are requested or borrowed by the current user.
     *
     * @return observable list of books
     */
    public MutableLiveData<List<Book>> getBooks() {
        return mBooks;
    }

    /**
     * Removes snapshot listeners. Should be called just before the owning ViewModel is destroyed.
     */
    public void detachListeners() {
        mListenerRegistration.remove();
    }

    /**
     * Fetches the owner for a newly selected book by clearing the previous book's owner information and
     * adding a snapshot listener for the book's owner
     *
     * @param book
     */
    public void fetchOwner(Book book, OnSuccessListener<User> onSuccessListener) {

        mDb.collection(USER_COLLECTION).document(book.getOwner()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    onSuccessListener.onSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Unable to get owner " + book + "from database", e);
                });
    }

    /**
     * Fetches the handoff location for the selected book
     *
     * @param bookRequestedID   book ID request is associated with
     * @param onFinished        code to call on success
     * @param onFailureListener code to call on failure
     */
    public void fetchHandoffLocation(String bookRequestedID, LibraryRepository.OnFinishedHandoffLocationListener onFinished, OnFailureListener onFailureListener) {
        mDb.collection(REQUEST_COLLECTION)
                .whereEqualTo(BOOK, bookRequestedID)
                .whereEqualTo(REQUESTER, mUID)
                .whereNotEqualTo(STATE, ARCHIVED)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Request> requests = queryDocumentSnapshots.toObjects(Request.class);
                    if (requests.size() != 1) {
                        onFailureListener.onFailure(new Exception("Unexpected number of requests returned when fetching location.  " + requests.size() + " requests found."));
                        return;
                    }
                    onFinished.onFinished(requests.get(0).getLocation());
                })
                .addOnFailureListener(onFailureListener);
    }
}
