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

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Manages the Library flow business logic. Connects the library fragments to the repository.
 */
public class LibraryViewModel extends ViewModel {

    private MutableLiveData<String> mTitle = new MutableLiveData<>();
    private MutableLiveData<String> mAuthor = new MutableLiveData<>();
    private MutableLiveData<String> mISBN = new MutableLiveData<>();
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
     * Getter for the mBooks object.
     *
     * @return LiveData<ArrayList < Book>> This returns the mBooks object
     */
    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Title getter for data binding. Exposes data in a mutable format so 2-way binding works.
     *
     * @return Title MutableLiveData
     */
    public MutableLiveData<String> getTitle() {
        if (mTitle == null) {
            mTitle = new MutableLiveData<>();
        }
        return mTitle;
    }

    /**
     * Author getter for data binding. Exposes data in a mutable format so 2-way binding works.
     *
     * @return Author MutableLiveData
     */
    public MutableLiveData<String> getAuthor() {
        if (mAuthor == null) {
            mAuthor = new MutableLiveData<>();
        }
        return mAuthor;
    }

    /**
     * ISBN getter for data binding. Exposes data in a mutable format so 2-way binding works.
     *
     * @return ISBN MutableLiveData
     */
    public MutableLiveData<String> getISBN() {
        if (mISBN == null) {
            mISBN = new MutableLiveData<>();
        }
        return mISBN;
    }

    /**
     * Detach listener when fragment is no longer being viewed.
     */
    public void detachListeners() {
        this.mLibraryRepository.detachListener();
    }
}
