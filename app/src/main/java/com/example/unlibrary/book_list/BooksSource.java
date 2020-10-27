/*
 * BooksSource
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.book_list;

import androidx.lifecycle.LiveData;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Interface that must be implemented by book source passed to BooksFragment
 */
public interface BooksSource {
    /**
     * Gets a list of observable books. Typically from a repository.
     *
     * @return list of observable books
     */
    LiveData<ArrayList<Book>> getBooks();
}
