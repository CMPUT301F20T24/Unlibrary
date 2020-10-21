package com.example.unlibrary.models;

import androidx.core.util.Pair;

import java.util.List;

public class Request {
    private String mId;
    private String mRequester;
    private String mBook;
    private State mState;
    private List<String> mPhotos;
    private Pair<Double, Double> mLocation; // TODO: Check whether this can be stored in Firestore

    /**
     * Empty constructor. Needed for Firestore.
     */
    public Request() {
    }

    /**
     * Constructs a request for a book.
     *
     * @param requester reference to a user unique id
     * @param book      reference to the book's unique id
     */
    public Request(String requester, String book) {
        mRequester = requester;
        mBook = book;
    }

    public String getId() {
        return mId;
    }

    public String getRequester() {
        return mRequester;
    }

    public String getBook() {
        return mBook;
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    public List<String> getPhotos() {
        return mPhotos;
    }

    public void addPhoto(String photo) {
        mPhotos.add(photo);
    }

    public void removePhoto(String photo) {
        mPhotos.remove(photo);
    }

    public Pair<Double, Double> getLocation() {
        return mLocation;
    }

    public void setLocation(Pair<Double, Double> location) {
        mLocation = location;
    }

    /**
     * Represents the state of a request
     *
     * REQUESTED - Borrower initiates a request to borrow a book, may be accepted/declined by the owner
     * ACCEPTED - Owner has accepted to lend the book to a borrower, other borrowers are moved to declined
     * DECLINED - Owner has explicitly or implicitly decided not to lend the book to the borrower
     * BORROWED - Owner has handed off the book to the borrower
     * ARCHIVED - Borrower has returned the book back to the owner and the borrow request is complete
     */
    enum State {
        REQUESTED,
        ACCEPTED,
        DECLINED,
        BORROWED,
        ARCHIVED
    }
}
