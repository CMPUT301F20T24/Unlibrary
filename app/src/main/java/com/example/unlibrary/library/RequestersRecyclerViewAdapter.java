/*
 * RequesterRecyclerViewAdapter
 *
 * November 17, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.BR;
import com.example.unlibrary.databinding.FragmentLibraryRequesterItemBinding;
import com.example.unlibrary.models.User;

import java.util.List;

public class RequestersRecyclerViewAdapter extends RecyclerView.Adapter<RequestersRecyclerViewAdapter.RequesterViewHolder> {

    protected List<User> mRequesters;
    private RequestersRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;

    /**
     * Constructs the RecyclerAdapter with an initial list of requesters (may be null).
     *
     * @param requesters initial list of requesters
     */
    public RequestersRecyclerViewAdapter(List<User> requesters, RequestersRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        mRequesters = requesters;
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Set the new data for the recycler view and notify the UI of changes
     *
     * @param newRequesters new requesters to be displayed
     */
    public void setData(List<User> newRequesters) {
        mRequesters = newRequesters;
        notifyDataSetChanged();
    }

    /**
     * Inflate fragment_library_requester_item and bind it with the {@link RequestersRecyclerViewAdapter.RequesterViewHolder} class.
     *
     * @param parent   ViewGroup of which this View will be added after binding
     * @param viewType layout ID
     * @return a new ViewHolder that holds a View for a {@link User}
     */
    @NonNull
    @Override
    public RequestersRecyclerViewAdapter.RequesterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentLibraryRequesterItemBinding requesterBinding = FragmentLibraryRequesterItemBinding.inflate(layoutInflater, parent, false);
        return new RequestersRecyclerViewAdapter.RequesterViewHolder(requesterBinding);
    }

    /**
     * Binds the {@link RequestersRecyclerViewAdapter.RequesterViewHolder} to its {@link User} item.
     *
     * @param holder   view holder instance
     * @param position index of the view holder in the list
     */
    @Override
    public void onBindViewHolder(final RequestersRecyclerViewAdapter.RequesterViewHolder holder, int position) {
        User requester = mRequesters.get(position);
        holder.bind(requester);
    }

    /**
     * Gets the number of requesters displayed.
     *
     * @return number of requesters displayed
     */
    @Override
    public int getItemCount() {
        return mRequesters.size();
    }

    /**
     * One parameter to the constructor of this adapter must implement this interface
     * This ensures that
     */
    public interface OnItemClickListener {
        /**
         * Called when a requester in the requesters list is clicked.
         *
         * @param v        Card view
         * @param position Position of card in the list
         */
        void onItemClicked(View v, int position);
    }

    /**
     * ViewHolder for the requester list recycler view.
     */
    public class RequesterViewHolder extends RecyclerView.ViewHolder {
        private final FragmentLibraryRequesterItemBinding mBinding;

        /**
         * Build the holder and setup the onClickListener.
         *
         * @param binding Binding for the holder
         */
        public RequesterViewHolder(FragmentLibraryRequesterItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClicked(v, getLayoutPosition());
                }
            });
        }

        /**
         * Bind a book to the card.
         *
         * @param requester User to bind to the card.
         */
        public void bind(User requester) {
            mBinding.setVariable(BR.requester, requester);
            mBinding.executePendingBindings();
        }

    }
}

