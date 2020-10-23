package com.example.unlibrary.unlibrary;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.R;

/**
 * A fragment representing a list of books that a user requests and borrows but not owns.
 */
public class UnlibraryBooksFragment extends Fragment {

    private UnlibraryViewModel mViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UnlibraryBooksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(UnlibraryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_book_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            UnlibraryBooksRecyclerViewAdapter adapter = new UnlibraryBooksRecyclerViewAdapter(mViewModel.getBooks().getValue());

            // Bind ViewModel books to RecyclerViewAdapter
            recyclerView.setAdapter(adapter);
            mViewModel.getBooks().observe(getViewLifecycleOwner(), adapter::setData);
        }

        return view;
    }
}
