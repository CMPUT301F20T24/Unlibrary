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

public class ExchangeBooksFragment extends Fragment {
    //    private static final String ARG_COLUMN_COUNT = "column-count";
    private ExchangeViewModel mViewModel;
    private Observer<ArrayList<Book>> mBookListObserver;
    public ExchangeBooksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(ExchangeViewModel.class);
    }

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

    @Override
    public void onDestroy() {
        mViewModel.detachListeners();
        super.onDestroy();
    }

}
