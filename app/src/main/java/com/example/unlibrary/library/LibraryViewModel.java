package com.example.unlibrary.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

public class LibraryViewModel extends ViewModel {
    private LiveData<ArrayList<Book>> mBooks;
    private LibraryRepository libraryRepository;

    public LibraryViewModel() {
        this.libraryRepository = new LibraryRepository();
        this.libraryRepository.attachListener();
        this.mBooks = this.libraryRepository.getBooks();
    }

    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }


    public void detachListeners() {
        this.libraryRepository.detachListener();
    }
}
