/*
 * ExchangeViewModel
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.exchange;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.book_list.BooksSource;
import com.example.unlibrary.models.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Exchange flow business logic. Connects the exchange fragment to the repository.
 */
public class ExchangeViewModel extends ViewModel implements BooksSource {
    private final LiveData<List<Book>> mBooks;
    private final ExchangeRepository mExchangeRepository;

    /**
     * Constructor for the ExchangeViewModel. Instantiates listener to Firestore.
     */
    public ExchangeViewModel() {
        mExchangeRepository = new ExchangeRepository();
        mBooks = mExchangeRepository.getBooks();
    }

    /**
     * Getter for the mBooks live data object
     *
     * @return LiveData<ArrayList < Book>> This returns the mBooks object
     */
    public LiveData<List<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Cleans up resources, removes the snapshot listener from the repository.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        mExchangeRepository.detachListener();
    }
}
