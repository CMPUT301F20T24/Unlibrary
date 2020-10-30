/*
 * LibraryViewModel
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.unlibrary.models.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Manages the Library flow business logic. Connects the library fragments to the repository.
 */
public class LibraryViewModel extends ViewModel {

    private MutableLiveData<Book> mCurrentBook = new MutableLiveData<>();
    private LiveData<ArrayList<Book>> mBooks;
    private LibraryRepository mLibraryRepository;

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

    // TODO
    public void createNewBook() {
        // Current book becomes empty
        mCurrentBook.setValue(new Book());
    }

    // TODO
    public void saveNewBook() {
        // TODO validate data
        Book book = mCurrentBook.getValue();
        book.setStatus(Book.Status.AVAILABLE);
        mLibraryRepository.createBook(mCurrentBook.getValue());
    }

    // TODO
    public void editCurrentBook() {
        // TODO
    }

    public void deleteCurrentBook() {
        // TODO
    }

    // TODO
    public void autoFill(String isbn) {
        mLibraryRepository.fetchBookDataFromIsbn(isbn, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO parse out author and title
                String title = "";
                String author = "";
                JSONArray items = null;
                try {
                    items = response.getJSONArray("items");
                    JSONObject firstItem = items.getJSONObject(0);
                    JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");
                    title = volumeInfo.getString("title");
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    author = authors.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO handle
                }
                Book book = mCurrentBook.getValue();
                book.setIsbn(isbn);
                book.setTitle(title);
                book.setAuthor(author);
                mCurrentBook.setValue(book);
            }

            @Override
            public void onError(ANError error) {
                // TODO share error that it failed to get author or title
                Book book = mCurrentBook.getValue();
                book.setIsbn(isbn);
                mCurrentBook.setValue(book);
            }
        });
    }
}
