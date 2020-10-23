package com.example.unlibrary.unlibrary;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.databinding.FragmentLibraryBookBinding;
import com.example.unlibrary.databinding.FragmentUnlibraryBookBinding;
import com.example.unlibrary.library.LibraryBooksRecyclerViewAdapter;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BookViewHolder;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Book}.
 * TODO: Generalize this
 */
public class UnlibraryBooksRecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder<FragmentUnlibraryBookBinding>> {
    private ArrayList<Book> mValues;

    public UnlibraryBooksRecyclerViewAdapter(ArrayList<Book> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public BookViewHolder<FragmentUnlibraryBookBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentUnlibraryBookBinding libraryBookBinding = FragmentUnlibraryBookBinding.inflate(layoutInflater, parent, false);
        return new BookViewHolder<>(libraryBookBinding);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder<FragmentUnlibraryBookBinding> holder, int position) {
        Book book = mValues.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
