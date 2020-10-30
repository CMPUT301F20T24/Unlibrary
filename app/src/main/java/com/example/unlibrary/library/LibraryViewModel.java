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
import com.example.unlibrary.util.BarcodeScanner;

import java.util.ArrayList;

/**
 * Manages the Library flow business logic. Connects the library fragments to the repository.
 */
public class LibraryViewModel extends ViewModel implements BarcodeScanner.OnFinishedScanListener {

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
    @Override
    public void onFinishedScanSuccess(String isbn) {
        Book book = mCurrentBook.getValue();
        book.setIsbn(isbn);
        mCurrentBook.setValue(book);
        System.out.println(isbn);
    }

    // TODO
    @Override
    public void onFinishedScanFailure(Throwable e) {
        // TODO
        System.out.println("IT FAILED");
    }
}
