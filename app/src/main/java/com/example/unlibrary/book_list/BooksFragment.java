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

/**
 * A fragment representing a list of books. Extensible to allow navigating to custom fragments in
 * different context (e.g. in LibraryFragment, we want to navigate to DetailedLibraryBookFragment)
 */
public abstract class BooksFragment extends Fragment {
    protected BooksViewModel mViewModel;
    private FragmentBookListBinding mBinding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BooksFragment() {
    }

    /**
     * Initialize essential components of the fragment that is retained when the fragment is paused
     * or stopped, then resumed.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBooksViewModel();
    }

    public abstract void setBooksViewModel();

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

        BooksRecyclerViewAdapter adapter = new BooksRecyclerViewAdapter(mViewModel.getBooks().getValue());

        // Bind ViewModel books to RecyclerViewAdapter
        view.setAdapter(adapter);
        mViewModel.getBooks().observe(getViewLifecycleOwner(), adapter::setData);

        return view;
    }
}
