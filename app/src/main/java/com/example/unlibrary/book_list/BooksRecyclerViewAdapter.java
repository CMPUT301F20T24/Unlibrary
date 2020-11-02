/*
 * BooksRecyclerViewAdapter
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.book_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.BR;
import com.example.unlibrary.databinding.FragmentBookCardBinding;
import com.example.unlibrary.models.Book;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Book}
 */
public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.BookViewHolder> {
    protected List<Book> mBooks;
    private final OnItemClickListener mOnItemClickListener;

    /**
     * Constructs the RecyclerAdapter with an initial list of books (may be null).
     *
     * @param books initial list of books
     */
    public BooksRecyclerViewAdapter(List<Book> books, OnItemClickListener onItemCLickListener) {
        mOnItemClickListener = onItemCLickListener;
        mBooks = books;
    }

    /**
     * Set the new data for the recycler view and notify the Ui of changes
     *
     * @param books new books to be displayed
     */
    public void setData(List<Book> books) {
        mBooks = books;
        notifyDataSetChanged();
    }

    /**
     * Inflate fragment_book_card and bind it with the {@link BookViewHolder} class. OnClickListeners
     * can be passed here as well.
     *
     * @param parent   ViewGroup of which this View will be added after binding
     * @param viewType layout ID
     * @return a new ViewHolder that holds a View for a {@link Book}
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentBookCardBinding bookBinding = FragmentBookCardBinding.inflate(layoutInflater, parent, false);
        return new BookViewHolder(bookBinding);
    }

    /**
     * Binds the {@link BookViewHolder} to its {@link Book} item.
     *
     * @param holder   view holder instance
     * @param position index of the view holder in the list
     */
    @Override
    public void onBindViewHolder(final BookViewHolder holder, int position) {
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

    /**
     * Interface that must be implemented by anywhere that uses a book list.
     */
    public interface OnItemClickListener {
        /**
         * Called when a book list card is clicked.
         *
         * @param v        Card view
         * @param position Position of card in the list
         */
        void onItemClicked(View v, int position);
    }

    /**
     * ViewHolder for the book list recycler view. Contains a book card. Handles on click interactions.
     * Defined inline as a closure and non-generically because it is already assuming that it will be
     * binding to a book.
     */
    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final FragmentBookCardBinding mBinding;

        /**
         * Build the holder and setup the onClickListener.
         *
         * @param binding Binding for the holder
         */
        public BookViewHolder(FragmentBookCardBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.getRoot().setOnClickListener(this);
        }

        /**
         * Bind a book to the card.
         *
         * @param book Book to bind to the card.
         */
        public void bind(Book book) {
            mBinding.setVariable(BR.book, book);
            mBinding.executePendingBindings();
        }

        /**
         * To be called when a card is clicked.
         *
         * @param v View of the card
         */
        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClicked(v, this.getLayoutPosition());
        }
    }
}
