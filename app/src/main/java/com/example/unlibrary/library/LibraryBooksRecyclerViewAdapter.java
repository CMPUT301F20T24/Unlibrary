package com.example.unlibrary.library;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.databinding.FragmentLibraryBookBinding;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BookViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Book}.
 * TODO: Generalize this
 */
public class LibraryBooksRecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder<FragmentLibraryBookBinding>> {

    private final LiveData<ArrayList<Book>> mValues;

    public LibraryBooksRecyclerViewAdapter(LiveData<ArrayList<Book>> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public BookViewHolder<FragmentLibraryBookBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentLibraryBookBinding libraryBookBinding = FragmentLibraryBookBinding.inflate(layoutInflater, parent,false);
        return new BookViewHolder<>(libraryBookBinding);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder<FragmentLibraryBookBinding> holder, int position) {
        Book book = mValues.getValue().get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return mValues.getValue().size();
    }
}
