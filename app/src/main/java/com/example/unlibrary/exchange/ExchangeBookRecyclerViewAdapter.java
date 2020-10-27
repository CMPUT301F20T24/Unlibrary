/*
 * ExchangeBookRecyclerViewAdapter
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.exchange;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.databinding.FragmentLibraryBookBinding;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BookViewHolder;

import java.util.ArrayList;

/**
 * Binds collection of books for exchange to recyclerview of exchange fragment
 */
public class ExchangeBookRecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder<FragmentLibraryBookBinding>> {
    private ArrayList<Book> mValues;

    /**
     * Constructor for the exchange recyclerview adapter
     *
     * @param items
     */
    public ExchangeBookRecyclerViewAdapter(ArrayList<Book> items) {
        mValues = items;
    }

    /**
     * sets the collection of books to recyclerview
     */
    public void setData(ArrayList<Book> items) {
        mValues = items;
        notifyDataSetChanged();
    }

    /**
     * Called for creating a new BookViewHolder representing the books for exchange
     *
     * @return a new BookViewHolder for exchange booklist
     */
    @NonNull
    @Override
    public BookViewHolder<FragmentLibraryBookBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentLibraryBookBinding libraryBookBinding = FragmentLibraryBookBinding.inflate(layoutInflater, parent, false);
        return new BookViewHolder<>(libraryBookBinding);
    }

    /**
     * Called by the recyclerview to show and update the books in the exhange adapter's data
     *
     * @param holder   the content of each book at any position
     * @param position the position of each book in the exchange adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder<FragmentLibraryBookBinding> holder, int position) {
        Book book = mValues.get(position);
        holder.bind(book);
    }

    /**
     * Counts the number of books in the exchange adapter bound to the recyclerview
     *
     * @return the number of books
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
