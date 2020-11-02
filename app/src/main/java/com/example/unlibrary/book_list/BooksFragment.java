/*
 * BooksFragment
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.book_list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.databinding.FragmentBookListBinding;

/**
 * A fragment representing a list of books. A book source should be specified before the fragment
 * is displayed. Book source must implement {@link BooksSource}. Refer to
 * {@link com.example.unlibrary.unlibrary.UnlibraryFragment} for example.
 */
public class BooksFragment extends Fragment {
    private BooksSource mBooksSource;
    private BooksRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;
    private FragmentBookListBinding mBinding;
    private BooksRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BooksFragment() {
    }

    /**
     * Sets the book source for the current fragment instantiation. Should be called during
     * {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)} in the parent or
     * containing fragment.
     *
     * @param booksSource object that implements {@link BooksSource#getBooks()} usually a view model
     */
    public void setBooksSource(BooksSource booksSource) {
        mBooksSource = booksSource;
    }

    /**
     * Sets the OnItemClickListener for the current fragment instantiation. Should be called during
     * {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)} in the parent or
     * containing fragment.
     *
     * @param onItemClickListener implementation of BooksFragment.OnItemClickListener
     */
    public void setOnItemClickListener(BooksRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Draws the fragment UI
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View root of the fragment's layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentBookListBinding.inflate(inflater, container, false);
        RecyclerView view = mBinding.getRoot();

        Context context = view.getContext();

        view.setLayoutManager(new LinearLayoutManager(context));

        BooksRecyclerViewAdapter adapter = new BooksRecyclerViewAdapter(mBooksSource.getBooks().getValue(), mOnItemClickListener);

        // Bind ViewModel books to RecyclerViewAdapter
        view.setAdapter(adapter);

        // Watch changes in bookSource and update the view accordingly
        mBooksSource.getBooks().observe(getViewLifecycleOwner(), adapter::setData);

        return view;
    }
}
