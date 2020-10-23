package com.example.unlibrary.unlibrary;


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
import com.example.unlibrary.models.Book;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class UnlibraryBooksFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private UnlibraryViewModel mViewModel;
    private Observer<ArrayList<Book>> mBookListObserver;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UnlibraryBooksFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static com.example.unlibrary.library.LibraryBooksFragment newInstance(int columnCount) {
        com.example.unlibrary.library.LibraryBooksFragment fragment = new com.example.unlibrary.library.LibraryBooksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(UnlibraryViewModel.class);
    }

    /**
     * Access Library viewModel and setup data-binding and observer for changes to books.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_book_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            UnlibraryBooksRecyclerViewAdapter adapter = new UnlibraryBooksRecyclerViewAdapter(mViewModel.getBooks().getValue());

            // Set the adapter
            recyclerView.setAdapter(adapter);

            // observe LiveData from ViewModel
            mViewModel.getBooks().observe(getViewLifecycleOwner(), books -> adapter.notifyDataSetChanged());
        }

        return view;
    }
}
