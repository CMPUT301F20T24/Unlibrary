/*
 * BookViewHolder
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.BR;
import com.example.unlibrary.models.Book;

public class BookViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private final T mBinding;

    public BookViewHolder(T binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(Book book) {
        mBinding.setVariable(BR.book, book);
        mBinding.executePendingBindings();
    }
}
