package com.example.unlibrary.exchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentLibraryBookBinding;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BookViewHolder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ExchangeBookRecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder<FragmentLibraryBookBinding>>{
    private ArrayList<Book> mValues;
    public ExchangeBookRecyclerViewAdapter(ArrayList<Book> items) {
        mValues = items;
    }

    public void setData(ArrayList<Book> items) {
        mValues = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder<FragmentLibraryBookBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentLibraryBookBinding libraryBookBinding = FragmentLibraryBookBinding.inflate(layoutInflater, parent, false);
        return new BookViewHolder<>(libraryBookBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder<FragmentLibraryBookBinding> holder, int position) {
        Book book = mValues.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
