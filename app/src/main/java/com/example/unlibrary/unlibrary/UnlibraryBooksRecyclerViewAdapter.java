package com.example.unlibrary.unlibrary;

import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentUnlibraryBookBinding;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BooksRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Book} that a user has requested, waiting
 * for handoff or borrowing. Can be specialized to attach a custom listener to the ViewHolder.
 */
public class UnlibraryBooksRecyclerViewAdapter extends BooksRecyclerViewAdapter<FragmentUnlibraryBookBinding> {
    /**
     * Constructs the UnlibraryBookRecyclerViewAdapter
     *
     * @param books initial list of books to be displayed
     */
    public UnlibraryBooksRecyclerViewAdapter(ArrayList<Book> books) {
        super(books, R.layout.fragment_unlibrary_book);
    }
}
