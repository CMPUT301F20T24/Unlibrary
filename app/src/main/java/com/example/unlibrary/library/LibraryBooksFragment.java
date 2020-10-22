package com.example.unlibrary.library;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.R;
import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/*
 * LibraryBooksFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

/**
 * A fragment representing a list of Items.
 */
public class LibraryBooksFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private LibraryViewModel mViewModel;
    private Observer<ArrayList<Book>> bookListObserver;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LibraryBooksFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LibraryBooksFragment newInstance(int columnCount) {
        LibraryBooksFragment fragment = new LibraryBooksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Bind ViewModel to fragment when fragment is created.
     * @param savedInstanceState saved instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
    }

    /**
     * Access Library viewModel and setup data-binding and observer for changes to books.
     * @param inflater inflater for the fragment.
     * @param container ViewGroup for fragment.
     * @param savedInstanceState saved instance.
     * @return View returns view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_book_list, container, false);

        // observe LiveData from ViewModel
        mViewModel.getBooks().observe(getViewLifecycleOwner(), new Observer<ArrayList<Book>>() {
            @Override
            public void onChanged(ArrayList<Book> books) {
                if (view instanceof RecyclerView) {
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    // Set the adapter
                    recyclerView.setAdapter(new LibraryBooksRecyclerViewAdapter(books));
                }
            }
        });

        return view;
    }

    /**
     * Detach listener when fragment is no longer being viewed when fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        mViewModel.detachListeners();
        super.onDestroy();
    }
}

