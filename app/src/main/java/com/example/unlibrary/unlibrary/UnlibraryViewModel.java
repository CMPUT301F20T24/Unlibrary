/*
 * UnlibraryViewModel
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.unlibrary;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.unlibrary.book_list.BooksSource;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.example.unlibrary.util.BarcodeScanner;
import com.example.unlibrary.util.SingleLiveEvent;

import java.util.List;

/**
 * Manages the Unlibrary flow business logic. Connects the  fragments to the repository.
 */
public class UnlibraryViewModel extends ViewModel implements BooksSource, BarcodeScanner.OnFinishedScanListener {
    private static final String TAG = UnlibraryViewModel.class.getSimpleName();
    private final LiveData<List<Book>> mBooks;
    private final UnlibraryRepository mUnlibraryRepository;
    private final MutableLiveData<Book> mCurrentBook = new MutableLiveData<>();
    private final MutableLiveData<Request> mCurrentRequest = new MutableLiveData<>();
    private final SingleLiveEvent<NavDirections> mNavigationEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> mFailureMsgEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> mSuccessMsgEvent = new SingleLiveEvent<>();

    /**
     * Constructor for the UnLibrary ViewModel. Binds the list of books to return from the
     * repository.
     */
    @ViewModelInject
    public UnlibraryViewModel(UnlibraryRepository unlibraryRepository) {
        mUnlibraryRepository = unlibraryRepository;
        mBooks = mUnlibraryRepository.getBooks();
    }

    /**
     * FailureMsgEvent getter for activity observers.
     *
     * @return Event of failure message to display
     */
    public SingleLiveEvent<String> getFailureMsgEvent() {
        return mFailureMsgEvent;
    }

    /**
     * NavigationEvent getter for activity observers.
     *
     * @return Event of which fragment to navigate to
     */
    public SingleLiveEvent<NavDirections> getNavigationEvent() {
        return mNavigationEvent;
    }


    /**
     * SuccessMsgEvent getter for activity observers.
     *
     * @return Event of which fragment to navigate to
     */
    public SingleLiveEvent<String> getSuccessMsgEvent() {
        return mSuccessMsgEvent;
    }

    /**
     * Should handoff button be shown.
     *
     * @return ShowHandoffButton LiveData
     */
    public LiveData<Boolean> showHandoffButton() {
        return Transformations.map(mCurrentBook, input -> {
            if (input.getStatus() == Book.Status.ACCEPTED && input.getIsReadyForHandoff()) {
                return true;
            } else if (input.getStatus() == Book.Status.BORROWED) {
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Getter for mCurrentBook live data object
     *
     * @return LiveData<Book> This returns the mBooks object
     */
    public LiveData<Book> getCurrentBook() {
        return mCurrentBook;
    }

    /**
     * Gets a list of observable books. Typically from a repository.
     *
     * @return list of observable books
     */
    public LiveData<List<Book>> getBooks() {
        return mBooks;
    }

    /**
     * Sets mCurrentBook, get request and navigates to detailed book view.
     *
     * @param view     view to navigate from.
     * @param position list position of selected book.
     */
    public void selectCurrentBook(View view, int position) {
        if (mBooks.getValue() == null) {
            mFailureMsgEvent.setValue("Failed to show details for book");
            return;
        }

        Book book = mBooks.getValue().get(position);
        Toast toast = Toast.makeText(view.getContext(), "Failed to get request", Toast.LENGTH_SHORT);
        Request request = new Request();
        mUnlibraryRepository.getRequest(book,
                r -> {
                    mCurrentBook.setValue(book);
                    mCurrentRequest.setValue(r);
                    NavDirections direction = UnlibraryFragmentDirections.actionUnlibraryFragmentToUnlibraryBookDetailsFragment();
                    Navigation.findNavController(view).navigate(direction);
                },

                () -> {
                    toast.show();
                    Log.e(TAG, "Failed to get request.");
                });
    }

    // TODO
    public void handoff(String isbn) {
        // TODO handle getting back from ready to handoff state
        // TODO do I need to worry about state of request at all?
        Request request = mCurrentRequest.getValue();
        Book book = mCurrentBook.getValue();
        if (!book.getIsbn().equals(isbn)) {
            mFailureMsgEvent.setValue("Isbn does not match current book.");
            return;
        }

        if (book.getStatus() == Book.Status.ACCEPTED && book.getIsReadyForHandoff()) {
            // Borrowing book
            book.setIsReadyForHandoff(false);
            book.setStatus(Book.Status.BORROWED);
            request.setState(Request.State.BORROWED);
            mUnlibraryRepository.completeExchange(request, book,
                    o -> {
                        mNavigationEvent.setValue(UnlibraryBookDetailsFragmentDirections.actionUnlibraryBookDetailsFragmentToUnlibraryFragment());
                        mSuccessMsgEvent.setValue("Successfully Accepted Book");
                    },
                    e -> {
                        mFailureMsgEvent.setValue("Could not change status of book");
                    });
        } else if (book.getStatus() == Book.Status.BORROWED) {
            // Return the book
            book.setIsReadyForHandoff(true);
            mUnlibraryRepository.updateBook(book, aVoid -> mSuccessMsgEvent.setValue("Book has been handed off."), e -> mFailureMsgEvent.setValue("Failed to handover book."));

        } else {
            Log.w(TAG, "Handoff called for an invalid book status.");
        }
    }

    /**
     * Cleans up resources, removes the snapshot listener from the repository.
     */
    @Override
    protected void onCleared() {
        mUnlibraryRepository.detachListeners();
    }

    /**
     * Code to be called when a barcode scan is finished successfully.
     *
     * @param tag  Identifier for what is using scan barcode.
     * @param isbn isbn from successful scan
     */
    @Override
    public void onFinishedScanSuccess(String tag, String isbn) {
        if (tag.equals(UnlibraryBookDetailsFragment.SCAN_TAG)) {
            handoff(isbn);
        } else {
            Log.w(TAG, "Invalid scan_tag encountered.");
        }
    }

    /**
     * Code to be called when a barcode scan fails.
     *
     * @param tag Identifier for what is using barcode scan.
     * @param e   Throwable
     */
    @Override
    public void onFinishedScanFailure(String tag, Throwable e) {
        mFailureMsgEvent.setValue("Failed to scan barcode.");
        Log.e(TAG, "Failed to scan barcode.", e);
    }
}
