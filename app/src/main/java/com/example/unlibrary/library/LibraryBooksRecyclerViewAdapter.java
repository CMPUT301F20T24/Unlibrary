package com.example.unlibrary.library;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.databinding.FragmentLibraryBookBinding;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BookViewHolder;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Book}.
 * TODO: Replace the implementation with code for your data type.
 */
public class LibraryBooksRecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder<FragmentLibraryBookBinding>> {

    private final List<Book> mValues;

    public LibraryBooksRecyclerViewAdapter(List<Book> items) {
        mValues = items;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflaytor = LayoutInflater.from(parent.getContext());
        FragmentLibraryBookBinding libraryBookBinding = FragmentLibraryBookBinding.inflate(layoutInflaytor, parent,false);
        return new BookViewHolder<>(libraryBookBinding);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder<FragmentLibraryBookBinding> holder, int position) {
        Book book = mValues.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}