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

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Manages the Exchange flow business logic. Connects the exchange fragment to the repository.
 */
public class ExchangeViewModel extends ViewModel {
    private LiveData<ArrayList<Book>> mBooks;
    private ExchangeRepository mExchangeRepository;

    /**
     * Constructor for the ExchangeViewModel. Instantiates listener to Firestore.
     */
    public ExchangeViewModel() {
        this.mExchangeRepository = new ExchangeRepository();
        this.mExchangeRepository.attachListener();
        this.mBooks = this.mExchangeRepository.getBooks();
    }

    /**
     * Getter for the mBooks live data object
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
        this.mExchangeRepository.detachListener();
    }
}
