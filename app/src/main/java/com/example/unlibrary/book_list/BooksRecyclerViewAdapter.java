/*
 * BooksRecyclerViewAdapter
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.book_list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.databinding.FragmentBookCardBinding;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BookViewHolder;

import java.util.ArrayList;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Book}
 */
public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder<FragmentBookCardBinding>> {
    protected ArrayList<Book> mBooks;

    /**
     * Constructs the RecyclerAdapter with an initial list of books (may be null).
     *
     * @param books initial list of books
     */
    public BooksRecyclerViewAdapter(ArrayList<Book> books) {
        mBooks = books;
    }

    /**
     * Set the new data for the recycler view and notify the Ui of changes
     *
     * @param books new books to be displayed
     */
    public void setData(ArrayList<Book> books) {
        mBooks = books;
        notifyDataSetChanged();
    }

    /**
     * Inflate fragment_book_card and bind it with the {@link BookViewHolder} class. OnClickListeners
     * can be passed here as well.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public BookViewHolder<FragmentBookCardBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentBookCardBinding bookBinding = FragmentBookCardBinding.inflate(layoutInflater, parent, false);
        return new BookViewHolder<>(bookBinding);
    }

    /**
     * Binds the {@link BookViewHolder} to its {@link Book} item.
     *
     * @param holder   view holder instance
     * @param position index of the view holder in the list
     */
    @Override
    public void onBindViewHolder(final BookViewHolder<FragmentBookCardBinding> holder, int position) {
        Book book = mBooks.get(position);
        holder.bind(book);
    }

    /**
     * Gets the number of books displayed.
     *
     * @return number of books displayed
     */
    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}
