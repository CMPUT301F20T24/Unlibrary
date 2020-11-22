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
import com.example.unlibrary.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONArray;

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
    private static final String USER_COLLECTION = "users";
    private static final String OWNER = "owner";
    private static final String STATUS = "status";

    // Algolia fields
    private static final String ALGOLIA_INDEX_NAME = "books";
    private static final String ALGOLIA_ID_FIELD = "objectID";
    private String previousSearch;

    private static final String TAG = ExchangeRepository.class.getSimpleName();

    private final FirebaseFirestore mDb;
    private final Client mAlgoliaClient;

    private final List<Book.Status> mAllowedStatus;
    private final MutableLiveData<List<Book>> mBooks;
    private final MutableLiveData<User> mCurrentBookOwner;
    private final MutableLiveData<Request> mCurrentRequest;
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
        mCurrentBookOwner = new MutableLiveData<>(new User());
        mCurrentRequest = new MutableLiveData<>(new Request());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mAllowedStatus = Arrays.asList(Book.Status.AVAILABLE, Book.Status.REQUESTED);

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
                .whereIn(STATUS, mAllowedStatus)
                .whereNotEqualTo(OWNER, FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((value, error) -> {
                    if (previousSearch != null) {
                        search(previousSearch);
                        return;
                    }

                    if (error != null) {
                        Log.w(TAG, error);
                        return;
                    }

                    // Update the list to reflect changes in the database
                    // TODO: use getDocumentChanges instead to minimize payload from Firestore
                    ArrayList<Book> dbBooks = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        // Only show the book with AVAILABLE or REQUESTED status for exchange
                        Book book = doc.toObject(Book.class);
                        book.setStatus(Book.Status.AVAILABLE);
                        dbBooks.add(doc.toObject(Book.class));
                    }

                    mBooks.setValue(dbBooks);
                });
    }

    /**
     * Gets the observable list of books that are available to be requested for. This includes all
     * books that are not currently being borrowed or has been accepted. Status is always set to
     * AVAILABLE.
     *
     * @return observable list of requestable books
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

    /**
     * Perform full search in title and author using Algolia. Full book details is fetched from
     * Firestore. Updates list of books at the end.
     * <p>
     * TODO: Decide what to do when keywords is empty and what happens when someone adds a new book (go with listener above or do re-search)
     *
     * @param keywords space separated words to search for
     */
    public void search(String keywords) {
        // Store search so that in the event a collection change happens, repository can proactively
        // update query
        previousSearch = keywords;

        Index index = mAlgoliaClient.getIndex(ALGOLIA_INDEX_NAME);
        Query query = new Query(keywords).setAttributesToRetrieve(ALGOLIA_ID_FIELD);

        index.searchAsync(query, (content, error) -> {
            if (error != null) {
                Log.e(TAG, "Algolia error: ", error);
                return;
            }

            // Get results array
            JSONArray hits = content.optJSONArray("hits");

            // Update books list
            ArrayList<Book> dbBooks = new ArrayList<>();
            ArrayList<Task<DocumentSnapshot>> tasks = new ArrayList<>();

            for (int i = 0; i < hits.length(); i++) {
                String bookId = hits.optJSONObject(i).optString(ALGOLIA_ID_FIELD);
                tasks.add(mDb.collection(BOOK_COLLECTION)
                        .document(bookId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Book book = documentSnapshot.toObject(Book.class);
                            if (!book.getOwner().equals(FirebaseAuth.getInstance().getUid()) &&
                                    mAllowedStatus.contains(book.getStatus())) {
                                book.setStatus(Book.Status.AVAILABLE);
                                dbBooks.add(book);
                            }
                        }));
            }

            Tasks.whenAll(tasks).addOnSuccessListener(aVoid -> {
                mBooks.setValue(dbBooks);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Add searched books error", e);
            });
        });
    }

    /**
     * Getter for the owner of the book.
     *
     * @return LiveData<ArrayList < String>> This returns the books object.
     */
    public LiveData<User> getOwner() {
        return this.mCurrentBookOwner;
    }

    /**
     * Fetches the owner for a newly selected book by clearing the previous book's owner information and
     * adding a snapshot listener for the book's owner
     *
     * @param currentBookOwnerID
     */
    public void fetchOwnerForCurrentBook(String currentBookOwnerID) {
        mCurrentBookOwner.setValue(new User());

        mDb.collection(USER_COLLECTION).document(currentBookOwnerID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    mCurrentBookOwner.setValue(documentSnapshot.toObject(User.class));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Unable to get owner " + currentBookOwnerID + "from database", e);
                });
    }

    /**
     * Get the request associated with the current book.
     *
     * @return request
     */
    public LiveData<Request> getCurrentRequest() {
        return this.mCurrentRequest;
    }

    /**
     * Fetch the request associated with the current book.
     *
     * @param book current book
     */
    public void fetchCurrentRequest(Book book) {
        // Set request so that UI thinks that it shouldn't display request button right away
        // This prevents flashing button which could be bad b/c then double requests could be made
        mCurrentRequest.setValue(new Request());

        com.google.firebase.firestore.Query query = mDb.collection(REQUEST_COLLECTION)
                .whereEqualTo("book", book.getId())
                .whereEqualTo("requester", mUID)
                .whereNotEqualTo("state", Request.State.DECLINED.toString());

        query.get().addOnSuccessListener(qds -> {
            List<DocumentSnapshot> documents = qds.getDocuments();
            if (documents.size() == 1) {
                mCurrentRequest.setValue(documents.get(0).toObject(Request.class));
            } else if (documents.size() == 0) {
                mCurrentRequest.setValue(null);
            } else {
                Log.e(TAG, "Only up to one request should be associated with book.");
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Unable to get current request", e));
    }

    /**
     * Request a book.
     *
     * @param request           request to be created
     * @param book              book to associate request with
     * @param onSuccessListener code to call on success
     * @param onFailureListener code to call on failure
     */
    public void sendRequest(Request request, Book book, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        WriteBatch batch = mDb.batch();

        DocumentReference newRequest = mDb.collection(REQUEST_COLLECTION).document();
        batch.set(newRequest, request);

        DocumentReference updatedBook = mDb.collection(BOOK_COLLECTION).document(book.getId());
        batch.update(updatedBook, STATUS, book.getStatus());

        batch.commit()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
