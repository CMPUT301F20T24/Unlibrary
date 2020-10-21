package com.example.unlibrary.library;

import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

public class LibraryViewModel extends ViewModel {
    private ArrayList<Book> mBooks;

    public LibraryViewModel() {
        mBooks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            mBooks.add(new Book("abcd-1234", "Crafting the interpreter", "https://craftinginterpreters.com/", null));
        }
    }

    public ArrayList<Book> getBooks() {
        return this.mBooks;
    }

}
