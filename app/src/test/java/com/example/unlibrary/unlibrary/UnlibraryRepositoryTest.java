package com.example.unlibrary.unlibrary;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.unlibrary.exchange.ExchangeBookDetailsFragmentDirections;
import com.example.unlibrary.exchange.ExchangeRepository;
import com.example.unlibrary.library.LibraryRepository;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.example.unlibrary.util.FilterMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.List;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@RunWith(RobolectricTestRunner.class)
@LooperMode(PAUSED)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class UnlibraryRepositoryTest {
    private final Context mContext = ApplicationProvider.getApplicationContext();
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private FirebaseAuth mLibraryAuth;
    @Mock
    private Client mAlgoliaClient;

    private final int SLEEP_TIME = 10;
    private final int SLEEP_TIME_MILLIS = SLEEP_TIME * 1000;
    UnlibraryRepository mUnlibraryRepository;
    ExchangeRepository mExchangeRepository;
    LibraryRepository mLibaryRepository;

    @Before
    public void setup() throws InterruptedException {
        FirebaseApp.initializeApp(mContext);
        FirebaseApp.initializeApp(mContext);
        mDb = FirebaseFirestore.getInstance();
        mDb.useEmulator("127.0.0.1", 8080);
        // TODO: Get auth emulator working
        mAuth = mock(FirebaseAuth.class);
        when(mLibraryAuth.getCurrentUser()).thenReturn(mock(FirebaseUser.class));
        when(mLibraryAuth.getCurrentUser().getUid()).thenReturn("CvLZ5c6lYqeyriVnwNpGurcBZl5R");

        when(mAuth.getCurrentUser()).thenReturn(mock(FirebaseUser.class));
        when(mAuth.getCurrentUser().getUid()).thenReturn("F8naw3e");

        mAlgoliaClient = mock(Client.class);
        when(mAlgoliaClient.getIndex(anyString())).thenReturn(mock(Index.class));
        mLibaryRepository = new LibraryRepository(mDb, mAuth, mAlgoliaClient);
        mExchangeRepository = new ExchangeRepository(mDb, mAlgoliaClient);

        Book book1 = new Book("9780221016025", "Accepted", "Jane", "CvLZ5c6lYqeyriVnwNpGurcBZl5R", null);
        book1.setStatus(Book.Status.REQUESTED);
        Book book2 = new Book("9780221016323", "Requested", "Doe", "CvLZ5c6lYqeyriVnwNpGurcBZl5R", null);
        book2.setStatus(Book.Status.ACCEPTED);
        mLibaryRepository.createBook(book1, documentReference -> {
        }, e -> fail("Unable to create book 1: " + e.getMessage()));
        mLibaryRepository.createBook(book2, documentReference -> {
        }, e -> fail("Unable to create book 2: " + e.getMessage()));
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        Request request1 = new Request("F8naw3e", book1.getOwner());
        Request request2 = new Request("F8naw3e", book2.getOwner());
        mExchangeRepository.sendRequest(request1, book1, aVoid -> {}, e -> {
            fail("unable to get request");
        });
        mExchangeRepository.sendRequest(request2, book2, aVoid -> {}, e -> {
            fail("unable to get request");
        });
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        mUnlibraryRepository = new UnlibraryRepository(mDb, mAuth);
    }

    @Test
    public void filterTest() throws InterruptedException {

        List<Book> books = mUnlibraryRepository.getBooks().getValue();
        if (books == null) {
            fail("Books is null");
        }

        FilterMap filter = new FilterMap(true);
        filter.set(Book.Status.ACCEPTED.toString(), true);
        mUnlibraryRepository.setFilter(filter);

        books = mUnlibraryRepository.getBooks().getValue();
        if (books == null || books.size() != 1) {
            fail("Filtering accepted did not work properly");
        }

        FilterMap filter2 = new FilterMap(true);
        filter2.set(Book.Status.REQUESTED.toString(), true);
        mUnlibraryRepository.setFilter(filter2);

        if (books == null || books.size() != 1) {
            fail("Filtering requested did not work properly");
        }

        FilterMap filter3 = new FilterMap(true);
        filter3.set(Book.Status.BORROWED.toString(), true);
        mUnlibraryRepository.setFilter(filter3);

        books = mUnlibraryRepository.getBooks().getValue();
        if (books == null || books.size() != 0) {
            fail("Filtering borrowed did not work properly");
        }

        FilterMap filter4 = new FilterMap(true);
        mUnlibraryRepository.setFilter(filter4);

        books = mUnlibraryRepository.getBooks().getValue();
        if (books == null || books.size() != 2) {
            fail("No Filter did not work properly");
        }
    }

    @Test
    public void getRequestTest(){
        List<Book> books = mUnlibraryRepository.getBooks().getValue();
        if (books == null) {
            fail("Books is null");
        }
        mUnlibraryRepository.getRequest(books.get(0), request -> {
            assertEquals(request.getBook(), books.get(0).getOwner());
            assertEquals(request.getRequester(), "F8naw3e");
        }, fail-> {
            fail("Did not get correct request");
        });
    }

    @Test
    public void fetchOwnerTest() {
        List<Book> books = mUnlibraryRepository.getBooks().getValue();
        if (books == null) {
            fail("Books is null");
        }
        mUnlibraryRepository.fetchOwner(books.get(0), user -> {
            assertEquals(user.getUID(), "F8naw3e");
        });
    }

    @Test
    public void updateBookTest() throws InterruptedException {
        List<Book> books = mUnlibraryRepository.getBooks().getValue();
        if (books == null) {
            fail("Books is null");
        }
        Book book = books.get(0);
        book.setIsReadyForHandoff(true);
        mUnlibraryRepository.updateBook(book, aVoid -> {
        }, e -> fail("Failed to handover book."));

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        books = mUnlibraryRepository.getBooks().getValue();
        assertTrue(books.get(0).getIsReadyForHandoff());
    }

    @Test
    public void completeExchangeTest() throws InterruptedException {
        List<Book> books = mUnlibraryRepository.getBooks().getValue();
        if (books == null) {
            fail("Books is null");
        }
        Book book = books.get(0);
        book.setIsReadyForHandoff(true);
        mUnlibraryRepository.getRequest(books.get(0), request -> {
            mUnlibraryRepository.completeExchange(request, book, avoid -> {}, fVoid -> fail("Could not complete exchange"))}, fvoid -> fail("Could not get request");
    }
}


