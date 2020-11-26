package com.example.unlibrary.library;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
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

import java.util.List;

import static android.os.Looper.getMainLooper;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
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
public class LibraryRepositoryTest {
    private final Context mContext = ApplicationProvider.getApplicationContext();
    private LibraryRepository mRepository;

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    @Mock
    private Client mAlgoliaClient;

    private final int SLEEP_TIME = 5;
    private final int SLEEP_TIME_MILLIS = SLEEP_TIME * 1000;

    @Before
    public void setup() throws InterruptedException {
        FirebaseApp.initializeApp(mContext);

        mDb = FirebaseFirestore.getInstance();
        mDb.useEmulator("127.0.0.1", 8080);

        // TODO: Get auth emulator working
        mAuth = mock(FirebaseAuth.class);
        when(mAuth.getCurrentUser()).thenReturn(mock(FirebaseUser.class));
        when(mAuth.getCurrentUser().getUid()).thenReturn("CvLZ5c6lYqeyriVnwNpGurcBZl5R");

        mAlgoliaClient = mock(Client.class);
        when(mAlgoliaClient.getIndex(anyString())).thenReturn(mock(Index.class));

        mRepository = new LibraryRepository(mDb, mAuth, mAlgoliaClient);
    }

    @Test
    public void addDeleteBookTest() throws InterruptedException {
        Book book = new Book("9780441016075", "Halting State", "Charles Stross", mAuth.getCurrentUser().getUid(), null);

        mRepository.createBook(book, documentReference -> {}, e -> fail("Unable to create book: " + e.getMessage()));

        // Callback to update repository books might not have been called yet
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        await().atMost(SLEEP_TIME, SECONDS).until(() -> book.getId() != null);

        List<Book> books = mRepository.getBooks().getValue();
        if (books == null) {
            fail("Repository does not contain any book");
        }

        // Callback to update repository books might not have been called yet
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        boolean addTestPass = false;
        for (Book b : mRepository.getBooks().getValue()) {
            if (b.getId().equals(book.getId())) {
                addTestPass = true;
            }
        }

        assertTrue("Add book test failed: book not found in repository", addTestPass);

        mRepository.deleteBook(book, aVoid -> {}, e -> fail("Unable to delete book " + e.getMessage()));

        // Callback to update repository books might not have been called yet
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        for (Book b : mRepository.getBooks().getValue()) {
            if (b.getId().equals(book.getId())) {
                fail("Book is still present");
            }
        }
    }
}
