/*
 * LibraryViewModel
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BarcodeScanner;
import com.example.unlibrary.util.SingleLiveEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Manages the Library flow business logic. Connects the library fragments to the repository.
 */
public class LibraryViewModel extends ViewModel implements BarcodeScanner.OnFinishedScanListener {

    private static final String TAG = LibraryViewModel.class.getSimpleName();
    private MutableLiveData<Book> mCurrentBook = new MutableLiveData<>();
    private SingleLiveEvent<String> mFailureMsgEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Pair<InputKey, String>> mInvalidInputEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<NavDirections> mNavigationEvent = new SingleLiveEvent<>();
    private LiveData<ArrayList<Book>> mBooks;
    private LibraryRepository mLibraryRepository;

    public enum InputKey {
        TITLE,
        AUTHOR,
        ISBN
    }

    /**
     * Constructor for the Library ViewModel. Instantiates listener to Firestore.
     */
    public LibraryViewModel() {
        this.mLibraryRepository = new LibraryRepository();
        this.mLibraryRepository.attachListener();
        this.mBooks = this.mLibraryRepository.getBooks();
    }

    /**
     * Getter for the mCurrentBook object.
     *
     * @return CurrentBook MutableLiveData
     */
    public MutableLiveData<Book> getCurrentBook() {
        return this.mCurrentBook;
    }

    /**
     * NavigationEvent getter for activity observers.
     *
     * @return Event of which fragment to navigate to
     */
    public SingleLiveEvent<NavDirections> getNavigationEvent() {
        if (mNavigationEvent == null) {
            mNavigationEvent = new SingleLiveEvent<>();
        }
        return mNavigationEvent;
    }

    /**
     * FailureMsgEvent getter for activity observers.
     *
     * @return Event of failure message to display
     */
    public SingleLiveEvent<String> getFailureMsgEvent() {
        if (mFailureMsgEvent == null) {
            mFailureMsgEvent = new SingleLiveEvent<>();
        }
        return mFailureMsgEvent;
    }

    /**
     * InvalidInputEvent getter for activity observers.
     *
     * @return Event of failure message to display
     */
    public SingleLiveEvent<Pair<InputKey, String>> getInvalidInputEvent() {
        if (mInvalidInputEvent == null) {
            mInvalidInputEvent = new SingleLiveEvent<>();
        }
        return mInvalidInputEvent;
    }

    /**
     * Getter for the mBooks object.
     *
     * @return LiveData<ArrayList < Book>> This returns the mBooks object
     */
    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Detach listener when fragment is no longer being viewed.
     */
    public void detachListeners() {
        this.mLibraryRepository.detachListener();
    }

    /**
     * Prepare model to create a new book.
     */
    public void createBook() {
        // Current book becomes empty
        mCurrentBook.setValue(new Book());
        mNavigationEvent.setValue(LibraryFragmentDirections.actionLibraryFragmentToLibraryEditBookFragment());
    }

    /**
     * Filter displayed books.
     */
    public void filter() {
        // TODO
    }

    /**
     * Save the book that is currently being created or edited
     */
    public void saveBook() {
        // TODO validate data
        Book book = mCurrentBook.getValue();
        if (book.getId() == null) {
            // Book is new
            book.setStatus(Book.Status.AVAILABLE); // A book is by default available
            mLibraryRepository.createBook(book,
                    o -> {
                        mNavigationEvent.setValue(LibraryEditBookFragmentDirections.actionLibraryEditBookFragmentToLibraryBookDetailsFragment());
                    },
                    e -> {
                        mFailureMsgEvent.setValue("Failed to create new book.");
                        Log.e(TAG, "Failed to create new book.", e);
                    });
        } else {
            // Pre-existing book
            mLibraryRepository.updateBook(book,
                    o -> {
                        mNavigationEvent.setValue(LibraryEditBookFragmentDirections.actionLibraryEditBookFragmentToLibraryBookDetailsFragment());
                    },
                    e -> {
                        mFailureMsgEvent.setValue("Failed to update book.");
                        Log.e(TAG, "Failed to update book.", e);
                    });
        }
    }

    /**
     * Prepare to edit the current book.
     */
    public void editCurrentBook() {
        mNavigationEvent.setValue(LibraryBookDetailsFragmentDirections.actionLibraryBookDetailsFragmentToLibraryEditBookFragment());
    }

    /**
     * Delete the current book.
     */
    public void deleteCurrentBook() {
        // TODO
    }

    /**
     * Code to be called when a barcode scan is finished successfully.
     *
     * @param isbn isbn from successful scan
     */
    @Override
    public void onFinishedScanSuccess(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            mFailureMsgEvent.setValue("Invalid ISBN found");
            return;
        }

        // Use the ISBN to search up book details
        mLibraryRepository.fetchBookDataFromIsbn(isbn, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                String title = "";
                String author = "";
                JSONArray items;
                try {
                    // Grunge through JSON and grab the first valid values we find.
                    items = response.getJSONArray("items");
                    JSONObject firstItem = items.getJSONObject(0);
                    JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");
                    title = volumeInfo.getString("title");
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    author = authors.getString(0);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to grab values from json.", e);
                } finally {
                    Book book = mCurrentBook.getValue();
                    book.setIsbn(isbn);
                    book.setTitle(title);
                    book.setAuthor(author);
                    mCurrentBook.setValue(book);
                }
            }

            @Override
            public void onError(ANError error) {
                mFailureMsgEvent.setValue("Failed to find title or author from ISBN.");
                Log.e(TAG, "Failed to get title or author.", error);
                Book book = mCurrentBook.getValue();
                book.setIsbn(isbn);
                mCurrentBook.setValue(book);
            }
        });
    }

    /**
     * Code to be called when a barcode scan fails.
     *
     * @param e Throwable
     */
    @Override
    public void onFinishedScanFailure(Throwable e) {
        mFailureMsgEvent.setValue("Failed to scan barcode.");
        Log.e(TAG, "Failed to scan barcode.", e);
    }

}
