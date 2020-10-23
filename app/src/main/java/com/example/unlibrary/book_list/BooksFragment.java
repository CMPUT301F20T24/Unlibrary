package com.example.unlibrary.book_list;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.databinding.FragmentBookListBinding;

import java.util.ArrayList;

/**
 * A fragment representing a list of books. A book source should be specified before the fragment
 * is displayed. Book source must implement {@link BooksSource}. Refer to
 * {@link com.example.unlibrary.unlibrary.UnlibraryFragment} for example.
 */
public class BooksFragment extends Fragment {
    private BooksSource mBooksSource;
    private FragmentBookListBinding mBinding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BooksFragment() {
    }

    public void setBooksSource(BooksSource booksSource) {
        mBooksSource = booksSource;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentBookListBinding.inflate(inflater, container, false);
        RecyclerView view = mBinding.getRoot();

        Context context = view.getContext();

        view.setLayoutManager(new LinearLayoutManager(context));

        BooksRecyclerViewAdapter adapter = new BooksRecyclerViewAdapter(mBooksSource == null ? new ArrayList<>() : mBooksSource.getBooks().getValue());

        // Bind ViewModel books to RecyclerViewAdapter
        view.setAdapter(adapter);

        // Watch changes in bookSource and update the view accordingly
        if (mBooksSource != null) {
            mBooksSource.getBooks().observe(getViewLifecycleOwner(), adapter::setData);
        }

        return view;
    }
}
