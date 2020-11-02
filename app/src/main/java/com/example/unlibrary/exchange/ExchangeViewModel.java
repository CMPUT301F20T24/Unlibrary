/*
 * ExchangeViewModel
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.exchange;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
    private final MutableLiveData<Book> mCurrentBook = new MutableLiveData<>();
    private final SingleLiveEvent<NavDirections> mNavigationEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> mFailureMsgEvent = new SingleLiveEvent<>();

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
        return mFailureMsgEvent;
    }

    /**
     * NavigationEvent getter for activity observers.
     *
     * @return Event of which fragment to navigate to
     */
    public SingleLiveEvent<NavDirections> getNavigationEvent() {
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
     * Getter for mCurrentBook live data object
     *
     * @return LiveData<Book> This returns the mBooks object
     */
    public LiveData<Book> getCurrentBook() {
        return this.mCurrentBook;
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
     * Sets mCurrentBook and navigates to detailed book view.
     *
     * @param view     view to navigate from.
     * @param position list position of selected book.
     */
    public void selectCurrentBook(View view, int position) {
        if (mBooks.getValue() == null) {
            mFailureMsgEvent.setValue("Failed show details for book");
            return;
        }

        Book book = mBooks.getValue().get(position);
        mCurrentBook.setValue(book);
        NavDirections direction = ExchangeFragmentDirections.actionExchangeFragmentToExchangeBookDetailsFragment();
        Navigation.findNavController(view).navigate(direction);

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
