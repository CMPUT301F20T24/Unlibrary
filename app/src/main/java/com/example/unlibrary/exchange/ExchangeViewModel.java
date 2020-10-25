package com.example.unlibrary.exchange;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

public class ExchangeViewModel extends ViewModel {
    private LiveData<ArrayList<Book>> mBooks;
    private ExchangeRepository mExchangeRepository;

    public ExchangeViewModel() {
        this.mExchangeRepository = new ExchangeRepository();
        this.mExchangeRepository.attachListener();
        this.mBooks = this.mExchangeRepository.getBooks();
    }

    public LiveData<ArrayList<Book>> getBooks() {
        return this.mBooks;
    }

    public void detachListeners() {
        this.mExchangeRepository.detachListener();
    }
}
