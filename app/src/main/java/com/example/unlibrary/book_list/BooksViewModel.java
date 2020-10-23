package com.example.unlibrary.book_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Represents a basic book list view model that can retrieve a list of observable books.
 * Extensible to allow adding extra functionality such as holding the form state of a new book to
 * add or sending a request of a book to a specified repository as this view model will likely be
 * used on the main fragments (e.g. LibraryFragment).
 */
public abstract class BooksViewModel extends ViewModel {
    /**
     * Gets a list of observable books. Typically from a repository.
     *
     * @return list of observable books
     */
    public abstract LiveData<ArrayList<Book>> getBooks();
}
