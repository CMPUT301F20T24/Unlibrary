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
