package com.example.unlibrary.unlibrary;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.unlibrary.exchange.ExchangeRepository;
import com.example.unlibrary.library.LibraryRepository;
import com.example.unlibrary.models.Book;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
    public void setup() {
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
        mUnlibraryRepository = new UnlibraryRepository(mDb, mAuth);
        mExchangeRepository = new ExchangeRepository(mDb, mAlgoliaClient);
    }

    @Test
    public void filterBooks() throws InterruptedException {

        Book book1 = new Book("9780221016025", "Accepted", "Jane", "CvLZ5c6lYqeyriVnwNpGurcBZl5R", null);
        book1.setStatus(Book.REQUESTED);
        Book book2 = new Book("9780221016323", "Requested", "Doe", "CvLZ5c6lYqeyriVnwNpGurcBZl5R", null);
        book2.setStatus(Book.Status.ACCEPTED);

    }
}


