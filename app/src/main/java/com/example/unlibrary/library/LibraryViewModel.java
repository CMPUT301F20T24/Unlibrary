/*
 * LibraryViewModel
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.unlibrary.book_list.BooksSource;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.BarcodeScanner;
import com.example.unlibrary.util.SingleLiveEvent;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

/**
 * Manages the Library flow business logic. Connects the library fragments to the repository.
 */
public class LibraryViewModel extends ViewModel implements BarcodeScanner.OnFinishedScanListener, BooksSource {

    private static final String TAG = LibraryViewModel.class.getSimpleName();
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_AUTHOR_LENGTH = 100;
    private static final int ISBN_LENGTH = 13;
    private static final String BOOK_PHOTO_STORE = "book_photos/";
    private final MutableLiveData<Book> mCurrentBook = new MutableLiveData<>();
    private final MutableLiveData<Uri> mTakenPhoto = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private SingleLiveEvent<String> mFailureMsgEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Pair<InputKey, String>> mInvalidInputEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<NavDirections> mNavigationEvent = new SingleLiveEvent<>();
    private final LiveData<List<Book>> mBooks;
    private final LibraryRepository mLibraryRepository;

    public enum InputKey {
        TITLE,
        AUTHOR,
        ISBN
    }

    /**
     * Constructor for the Library ViewModel. Instantiates listener to Firestore.
     */
    public LibraryViewModel() {
        this.mLibraryRepository = new LibraryRepository();
        this.mBooks = this.mLibraryRepository.getBooks();
    }

    /**
     * Getter for the mCurrentBook object.
     *
     * @return CurrentBook MutableLiveData
     */
    public MutableLiveData<Book> getCurrentBook() {
        return this.mCurrentBook;
    }

    /**
     * Getter for the mTakenPhoto object.
     *
     * @return TakenPhoto LiveData
     */
    public LiveData<Uri> getTakenPhoto() {
        return this.mTakenPhoto;
    }

    /**
     * Getter for the mIsLoading.
     *
     * @return IsLoading LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return this.mIsLoading;
    }

    /**
     * NavigationEvent getter for activity observers.
     *
     * @return Event of which fragment to navigate to
     */
    public SingleLiveEvent<NavDirections> getNavigationEvent() {
        if (mNavigationEvent == null) {
            mNavigationEvent = new SingleLiveEvent<>();
        }
        return mNavigationEvent;
    }

    /**
     * FailureMsgEvent getter for activity observers.
     *
     * @return Event of failure message to display
     */
    public SingleLiveEvent<String> getFailureMsgEvent() {
        if (mFailureMsgEvent == null) {
            mFailureMsgEvent = new SingleLiveEvent<>();
        }
        return mFailureMsgEvent;
    }

    /**
     * InvalidInputEvent getter for activity observers.
     *
     * @return Event of failure message to display
     */
    public SingleLiveEvent<Pair<InputKey, String>> getInvalidInputEvent() {
        if (mInvalidInputEvent == null) {
            mInvalidInputEvent = new SingleLiveEvent<>();
        }
        return mInvalidInputEvent;
    }

    /**
     * Getter for the mBooks object.
     *
     * @return LiveData<ArrayList < Book>> This returns the mBooks object
     */
    public LiveData<List<Book>> getBooks() {
        return this.mBooks;
    }

    /**
     * Cleans up resources, removes the snapshot listener from the repository.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        mLibraryRepository.detachListener();
    }

    /**
     * Prepare model to create a new book.
     */
    public void createBook() {
        // Current book and taken photo become empty
        mCurrentBook.setValue(new Book());
        mTakenPhoto.setValue(null);
        mNavigationEvent.setValue(LibraryFragmentDirections.actionLibraryFragmentToLibraryEditBookFragment());
    }

    /**
     * Filter displayed books.
     */
    public void filter() {
        // TODO
    }

    /**
     * Save the book that is currently being created or edited
     */
    public void saveBook() {
        // Validate data
        Book book = mCurrentBook.getValue();
        boolean invalid = false;
        try {
            validateTitle(book.getTitle());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.TITLE, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.TITLE, e.getMessage()));
            invalid = true;
        }
        try {
            validateAuthor(book.getAuthor());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.AUTHOR, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.AUTHOR, e.getMessage()));
            invalid = true;
        }
        try {
            validateIsbn(book.getIsbn());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.ISBN, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.ISBN, e.getMessage()));
            invalid = true;
        }
        if (invalid) {
            return;
        }

        Runnable uploadBook = () -> {
            if (book.getId() == null) {
                // Book is new
                book.setStatus(Book.Status.AVAILABLE); // A book is by default available
                mLibraryRepository.createBook(book,
                        o -> {
                            Book bookWithId = mCurrentBook.getValue();
                            bookWithId.setId(o.getId());
                            mCurrentBook.setValue(bookWithId);
                            mIsLoading.setValue(false);
                            mNavigationEvent.setValue(LibraryEditBookFragmentDirections.actionLibraryEditBookFragmentToLibraryBookDetailsFragment());
                        },
                        e -> {
                            mIsLoading.setValue(false);
                            mFailureMsgEvent.setValue("Failed to create new book.");
                            Log.e(TAG, "Failed to create new book.", e);
                        });
            } else {
                // Pre-existing book
                mLibraryRepository.updateBook(book,
                        o -> {
                            mIsLoading.setValue(false);
                            mNavigationEvent.setValue(LibraryEditBookFragmentDirections.actionLibraryEditBookFragmentToLibraryBookDetailsFragment());
                        },
                        e -> {
                            mIsLoading.setValue(false);
                            mFailureMsgEvent.setValue("Failed to update book.");
                            Log.e(TAG, "Failed to update book.", e);
                        });
            }
        };

        mIsLoading.setValue(true);
        if (mTakenPhoto.getValue() != null) {
            // If a photo was taken upload it to Firebase storage
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            UUID uuid = UUID.randomUUID();
            String uniquePhotoName = uuid.toString();
            StorageReference photoRef = storageRef.child(BOOK_PHOTO_STORE + uniquePhotoName + ".jpg");

            UploadTask uploadTask = photoRef.putFile(mTakenPhoto.getValue());
            uploadTask.addOnFailureListener(e -> {
                mFailureMsgEvent.setValue("Failed to upload taken photo.");
            });
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    return null;
                }
                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    System.out.println(downloadUri.toString());
                    book.setPhoto(downloadUri.toString());
                    uploadBook.run();
                }
            });
        } else {
            uploadBook.run();
        }
    }

    /**
     * Choose the current book that will be displayed then navigate to the details screen.
     *
     * @param position Position of book in list of books that is chosen
     */
    public void selectCurrentBook(View v, int position) {
        if (mBooks.getValue() == null) {
            mFailureMsgEvent.setValue("Failed show details for book.");
        } else {
            Book book = mBooks.getValue().get(position);
            mCurrentBook.setValue(book);
            mNavigationEvent.setValue(LibraryFragmentDirections.actionLibraryFragmentToLibraryBookDetailsFragment());
        }
    }

    /**
     * Prepare to edit the current book.
     */
    public void editCurrentBook() {
        // Empty out the taken photo
        mTakenPhoto.setValue(null);
        mNavigationEvent.setValue(LibraryBookDetailsFragmentDirections.actionLibraryBookDetailsFragmentToLibraryEditBookFragment());
    }

    /**
     * Delete the current book.
     */
    public void deleteCurrentBook() {
        if (mCurrentBook.getValue() == null) {
            mFailureMsgEvent.setValue("Failed to get current book.");
        } else {
            mIsLoading.setValue(true);
            mLibraryRepository.deleteBook(mCurrentBook.getValue(),
                    o -> {
                        mIsLoading.setValue(false);
                        mNavigationEvent.setValue(LibraryBookDetailsFragmentDirections.actionLibraryBookDetailsFragmentToLibraryFragment());
                    },
                    e -> {
                        mIsLoading.setValue(false);
                        mFailureMsgEvent.setValue("Failed to delete book.");
                    });
        }
    }

    /**
     * Code to be called when a barcode scan is finished successfully.
     *
     * @param isbn isbn from successful scan
     */
    @Override
    public void onFinishedScanSuccess(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            mFailureMsgEvent.setValue("Invalid ISBN found");
            return;
        }

        // Use the ISBN to search up book details
        mIsLoading.setValue(true);
        mLibraryRepository.fetchBookDataFromIsbn(isbn, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                String title = "";
                String author = "";
                JSONArray items;
                try {
                    // Grunge through JSON and grab the first valid values we find.
                    items = response.getJSONArray("items");
                    JSONObject firstItem = items.getJSONObject(0);
                    JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");
                    title = volumeInfo.getString("title");
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    author = authors.getString(0);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to grab values from json.", e);
                    mFailureMsgEvent.setValue("No relevant title or author found.");
                } finally {
                    Book book = mCurrentBook.getValue();
                    book.setIsbn(isbn);
                    book.setTitle(title);
                    book.setAuthor(author);
                    mCurrentBook.setValue(book);
                    mIsLoading.setValue(false);
                }
            }

            @Override
            public void onError(ANError error) {
                mFailureMsgEvent.setValue("Failed to find title or author from ISBN.");
                Log.e(TAG, "Failed to get title or author.", error);
                Book book = mCurrentBook.getValue();
                book.setIsbn(isbn);
                mCurrentBook.setValue(book);
            }
        });
    }

    /**
     * Update the taken photo. This photo will then be displayed.
     *
     * @param uri Uri of the taken photo.
     */
    public void takePhoto(Uri uri) {
        mTakenPhoto.setValue(uri);
    }

    /**
     * Code to be called when a barcode scan fails.
     *
     * @param e Throwable
     */
    @Override
    public void onFinishedScanFailure(Throwable e) {
        mFailureMsgEvent.setValue("Failed to scan barcode.");
        Log.e(TAG, "Failed to scan barcode.", e);
    }

    /**
     * Validate an title value.
     *
     * @param title Title to validate
     * @throws InvalidInputException Thrown when title is invalid
     */
    private void validateTitle(String title) throws InvalidInputException {
        // Non-empty
        if (title == null || title.isEmpty()) {
            throw new InvalidInputException("Title is empty.");
        }

        // Length
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new InvalidInputException("Title is too long.");
        }
    }

    /**
     * Validate an author value.
     *
     * @param author Author to validate.
     * @throws InvalidInputException Thrown when author is invalid
     */
    private void validateAuthor(String author) throws InvalidInputException {
        // Non-empty
        if (author == null || author.isEmpty()) {
            throw new InvalidInputException("Author is empty.");
        }

        // Length
        if (author.length() > MAX_AUTHOR_LENGTH) {
            throw new InvalidInputException("Author is too long.");
        }
    }

    /**
     * Validate an isbn value.
     *
     * @param isbn Isbn to validate.
     * @throws InvalidInputException Thrown when isbn is invalid.
     */
    private void validateIsbn(String isbn) throws InvalidInputException {
        // Length
        if (isbn == null || isbn.isEmpty() || isbn.length() != ISBN_LENGTH) {
            throw new InvalidInputException("Invalid isbn.");
        }

        // All numbers
        try {
            Long.parseLong(isbn, 10);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Isbn is not all numbers.");
        }
    }

    /**
     * Exception thrown when input data is invalid.
     */
    public static class InvalidInputException extends Exception {
        /**
         * Constructor for exception.
         *
         * @param errorMessage Reason that input is invalid
         */
        public InvalidInputException(String errorMessage) {
            super(errorMessage);
        }
    }
}
