package com.example.unlibrary.unlibrary;

import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

public class UnlibraryRepository {
    private MutableLiveData<ArrayList<Book>> mBooks;

    public UnlibraryRepository() {
        // TODO: Unmock data
        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            books.add(new Book("abcd-1234", "Crafting the interpreter", "me", "me", null));
        }
        mBooks = new MutableLiveData<>(books);
    }

    public MutableLiveData<ArrayList<Book>> getBooks() {
        return mBooks;
    }
}
