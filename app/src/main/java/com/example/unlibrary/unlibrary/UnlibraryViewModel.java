package com.example.unlibrary.unlibrary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Manages the Unlibrary flow business logic. Connects the  fragments to the repository.
 */
public class UnlibraryViewModel extends ViewModel {
    private LiveData<ArrayList<Book>> mBooks;
    private UnlibraryRepository mUnlibraryRepository;

    /**
     * Constructor for the Library ViewModel. Instantiates listener to Firestore.
     */
    public UnlibraryViewModel() {
        mUnlibraryRepository = new UnlibraryRepository();
        mBooks = mUnlibraryRepository.getBooks();
    }

    /**
     * Getter for the mBooks object.
     *
     * @return LiveData<ArrayList < Book>> This returns the mBooks object
     */
    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }
}
