/*
 * UnlibraryViewModel
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.unlibrary;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.unlibrary.book_list.BooksSource;
import com.example.unlibrary.exchange.ExchangeFragmentDirections;
import com.example.unlibrary.models.Book;

import java.util.List;

/**
 * Manages the Unlibrary flow business logic. Connects the  fragments to the repository.
 */
public class UnlibraryViewModel extends ViewModel implements BooksSource {
    private final LiveData<List<Book>> mBooks;
    private final UnlibraryRepository mUnlibraryRepository;

    /**
     * Constructor for the UnLibrary ViewModel. Binds the list of books to return from the
     * repository.
     */
    public UnlibraryViewModel() {
        mUnlibraryRepository = new UnlibraryRepository();
        mBooks = mUnlibraryRepository.getBooks();
    }

    /**
     * Gets a list of observable books. Typically from a repository.
     *
     * @return list of observable books
     */
    public LiveData<List<Book>> getBooks() {
        return mBooks;
    }

    /**
     * Cleans up resources, removes the snapshot listener from the repository.
     */
    @Override
    protected void onCleared() {
        mUnlibraryRepository.detachListeners();
    }

    /**
     * Sets mCurrentBook and navigates to detailed book view.
     *
     * @param view     view to navigate from.
     * @param position list position of selected book.
     */
    public void selectCurrentBook(View view, int position) {
        //TODO
    }
}
