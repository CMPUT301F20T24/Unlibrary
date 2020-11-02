/*
 * ExchangeViewModel
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.exchange;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;

import com.example.unlibrary.book_list.BooksSource;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.example.unlibrary.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Exchange flow business logic. Connects the exchange fragment to the repository.
 */
public class ExchangeViewModel extends ViewModel implements BooksSource {
    private static final String TAG = ExchangeViewModel.class.getSimpleName();

    private final LiveData<List<Book>> mBooks;
    private final ExchangeRepository mExchangeRepository;
    private MutableLiveData<Book> mCurrentBook = new MutableLiveData<>();
    private SingleLiveEvent<NavDirections> mNavigationEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mFailureMsgEvent = new SingleLiveEvent<>();

    /**
     * Constructor for the ExchangeViewModel. Instantiates listener to Firestore.
     */
    public ExchangeViewModel() {
        mExchangeRepository = new ExchangeRepository();
        mBooks = mExchangeRepository.getBooks();
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
     * Getter for the mBooks live data object
     *
     * @return LiveData<ArrayList < Book>> This returns the mBooks object
     */
    public LiveData<List<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Getter for themCurrentBook live data object
     *
     * @return LiveData<Book> This returns the mBooks object
     */
    public LiveData<Book> getCurrentBook() {
        return this.mCurrentBook;
    }

    /**
     * Setter for the mCurrentBook live data object
     *
     */
    public void setCurrentBook(Book book) {
        mCurrentBook.setValue(book);
    }

    /**
     * Getter for the description of the book.
     *
     * @return String This returns book description
     */
    public String getDescription() {
        Book currentBook = mCurrentBook.getValue();
        return String.format("%s %s %s", currentBook.getIsbn(), currentBook.getTitle(), currentBook.getAuthor());
    }

    /**
     * Generates and saves the request into firestore.
     */
    public void sendRequest() {
        Request request = new Request(mExchangeRepository.getUid(), mCurrentBook.getValue().getId());
        mExchangeRepository.createRequest(request,
                o -> {
                    mNavigationEvent.setValue(ExchangeBookDetailsFragmentDirections.actionExchangeBookDetailsFragmentToExchangeFragment());
                },
                e -> {
                    mFailureMsgEvent.setValue("Failed to send request.");
                    Log.e(TAG, "Failed to send request.", e);
                });
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
