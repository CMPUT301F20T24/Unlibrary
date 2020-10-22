package com.example.unlibrary.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/*
 * LibraryViewModel
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

/**
 * Manages the Library flow business logic. Connects the library fragments to the repository.
 */
public class LibraryViewModel extends ViewModel {
    private LiveData<ArrayList<Book>> mBooks;
    private LibraryRepository libraryRepository;

    /**
     * Constructor for the Library ViewModel. Instantiates listener to Firestore.
     */
    public LibraryViewModel() {
        this.libraryRepository = new LibraryRepository();
        this.libraryRepository.attachListener();
        this.mBooks = this.libraryRepository.getBooks();
    }

    /**
     * Getter for the mBooks object.
     * @return LiveData<ArrayList<Book>> This returns the mBooks object
     */
    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Detach listener when fragment is no longer being viewed.
     */
    public void detachListeners() {
        this.libraryRepository.detachListener();
    }
}
