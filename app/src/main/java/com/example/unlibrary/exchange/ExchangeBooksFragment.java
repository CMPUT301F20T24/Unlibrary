/*
 * ExchangeBooksFragment
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.exchange;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.R;
import com.example.unlibrary.library.LibraryBooksRecyclerViewAdapter;
import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * Fragment representing a list of books for exchange
 */
public class ExchangeBooksFragment extends Fragment {

    private ExchangeViewModel mViewModel;
    private Observer<ArrayList<Book>> mBookListObserver;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExchangeBooksFragment() {
    }

    /**
     * Called when exchange activity is started
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(ExchangeViewModel.class);
    }

    /**
     * Access ExchangeViewModel and setup data-binding and observer for changes to books.
     *
     * @return instance of recyclerview for book exchange
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_book_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            LibraryBooksRecyclerViewAdapter adapter = new LibraryBooksRecyclerViewAdapter(this.mViewModel.getBooks().getValue());
            // Set the adapter
            recyclerView.setAdapter(adapter);
            // observe LiveData from ViewModel
            mViewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
                adapter.setData(books);
            });
        }
        return view;
    }

    /**
     * Performs a final cleanup before exchange activity is destroyed
     */
    @Override
    public void onDestroy() {
        mViewModel.detachListeners();
        super.onDestroy();
    }

}
