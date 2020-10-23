package com.example.unlibrary.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Base class for a RecyclerView representing a list of books.
 *
 * @param <T> Fragment*Binding to be bound to the ViewHolders (list child)
 */
public abstract class BooksRecyclerViewAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<BookViewHolder<T>> {
    protected ArrayList<Book> mBooks;
    protected int mFragmentLayoutId;

    /**
     * Constructs the RecyclerAdapter with an initial list of books (may be null).
     *
     * @param books            initial list of books
     * @param fragmentLayoutId card layout resource ID to be displayed by the ViewHolder
     */
    public BooksRecyclerViewAdapter(ArrayList<Book> books, int fragmentLayoutId) {
        mBooks = books;
        mFragmentLayoutId = fragmentLayoutId;
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

    @NonNull
    @Override
    public BookViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        T bookBinding = DataBindingUtil.inflate(layoutInflater, mFragmentLayoutId, parent, false);
        return new BookViewHolder<>(bookBinding);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder<T> holder, int position) {
        Book book = mBooks.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}
