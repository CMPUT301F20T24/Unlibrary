/*
 * LibraryViewModel
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.book_list.BooksSource;
import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Manages the Library flow business logic. Connects the library fragments to the repository.
 */
public class LibraryViewModel extends ViewModel implements BooksSource {
    private final LiveData<ArrayList<Book>> mBooks;
    private final LibraryRepository mLibraryRepository;

    /**
     * Constructor for the Library ViewModel. Instantiates listener to Firestore.
     */
    public LibraryViewModel() {
        this.mLibraryRepository = new LibraryRepository();
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
     * Cleans up resources, removes the snapshot listener from the repository.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        mLibraryRepository.detachListener();
    }
}
