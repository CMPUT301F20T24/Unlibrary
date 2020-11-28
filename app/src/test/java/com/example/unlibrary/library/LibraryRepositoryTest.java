package com.example.unlibrary.library;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.unlibrary.models.Book;
import com.example.unlibrary.util.FilterMap;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.io.IOException;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
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
public class LibraryRepositoryTest {
    private final Context mContext = ApplicationProvider.getApplicationContext();
    private LibraryRepository mRepository;

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    @Mock
    private Client mAlgoliaClient;

    private final int SLEEP_TIME = 10;
    private final int SLEEP_TIME_MILLIS = SLEEP_TIME * 1000;

    @Before
    public void setup() {
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

    @After
    public void cleanup() throws IOException {
        mDb.terminate();
    }

    @Test
    public void addDeleteBookTest() throws InterruptedException {
        Book book = new Book("9780441016075", "Halting State", "Charles Stross", mAuth.getCurrentUser().getUid(), null);

        mRepository.createBook(book, documentReference -> {
        }, e -> fail("Unable to create book: " + e.getMessage()));

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

        mRepository.deleteBook(book, aVoid -> {
        }, e -> fail("Unable to delete book " + e.getMessage()));

        // Callback to update repository books might not have been called yet
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        for (Book b : mRepository.getBooks().getValue()) {
            if (b.getId().equals(book.getId())) {
                fail("Book is still present");
            }
        }
    }

    @Test
    public void editBookTest() throws InterruptedException {
        Book book = new Book("9780441016025", "Halting State Book", "Charles Stross The Human", mAuth.getCurrentUser().getUid(), null);
        mRepository.createBook(book, documentReference -> {
        }, e -> fail("Unable to create book: " + e.getMessage()));

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        await().atMost(SLEEP_TIME, SECONDS).until(() -> book.getId() != null);

        List<Book> books = mRepository.getBooks().getValue();
        if (books == null) {
            fail("Repository does not contain any book");
        }

        Book updatedBook = books.get(0);
        updatedBook.setTitle("New title");
        updatedBook.setAuthor("Updated author");
        mRepository.updateBook(updatedBook, x -> {
        }, e -> fail("Unalbe to update book: " + e.getMessage()));

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        books = mRepository.getBooks().getValue();
        if (books == null) {
            fail("Repository does not contain any book");
        }
        assertEquals(updatedBook.getTitle(), books.get(0).getTitle());
        assertEquals(updatedBook.getAuthor(), books.get(0).getAuthor());
        assertEquals(updatedBook.getIsbn(), books.get(0).getIsbn());

        mRepository.deleteBook(updatedBook, aVoid -> {
        }, e -> fail("Unable to delete book " + e.getMessage()));

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        for (Book b : mRepository.getBooks().getValue()) {
            if (b.getId().equals(updatedBook.getId())) {
                fail("Book is still present");
            }
        }
    }

    @Test
    public void bookDataFromIsbnTest() throws InterruptedException {
        mRepository.fetchBookDataFromIsbn("9780441016075", new JSONObjectRequestListener() {
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
                    assertEquals("Halting State", title);
                    assertEquals("Charles Stross", author);
                } catch (JSONException e) {
                    fail("Invalid JSON returned");
                }
            }

            @Override
            public void onError(ANError error) {
                fail("Failed to get book data from isbn");
            }
        });

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();
    }

    @Test
    public void filterBooks() throws InterruptedException {
        Book book1 = new Book("9780221016025", "Book 1", "Caleb", mAuth.getCurrentUser().getUid(), null);
        book1.setStatus(Book.Status.ACCEPTED);
        Book book2 = new Book("9780221016323", "Book 2", "Golnoush", mAuth.getCurrentUser().getUid(), null);
        book2.setStatus(Book.Status.ACCEPTED);
        Book book3 = new Book("9780221111324", "Book 3", "Armi", mAuth.getCurrentUser().getUid(), null);
        mRepository.createBook(book1, documentReference -> {
        }, e -> fail("Unable to create book 1: " + e.getMessage()));
        mRepository.createBook(book2, documentReference -> {
        }, e -> fail("Unable to create book 2: " + e.getMessage()));
        mRepository.createBook(book3, documentReference -> {
        }, e -> fail("Unable to create book 3: " + e.getMessage()));

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        List<Book> books = mRepository.getBooks().getValue();
        if (books == null) {
            fail("Books is null");
        }

        FilterMap filter = new FilterMap(true);
        filter.set(Book.Status.ACCEPTED.toString(), true);
        mRepository.setFilter(filter);

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        books = mRepository.getBooks().getValue();
        if (books == null || books.size() != 2) {
            fail("Filtering did not work properly");
        }

        FilterMap filter2 = new FilterMap(true);
        filter2.set(Book.Status.REQUESTED.toString(), true);
        mRepository.setFilter(filter2);

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();

        books = mRepository.getBooks().getValue();
        if (books == null || books.size() != 0) {
            fail("Filtering did not work properly");
        }

        mRepository.deleteBook(book1, aVoid -> {
        }, e -> fail("Unable to delete book 1 " + e.getMessage()));
        mRepository.deleteBook(book2, aVoid -> {
        }, e -> fail("Unable to delete book 2 " + e.getMessage()));
        mRepository.deleteBook(book3, aVoid -> {
        }, e -> fail("Unable to delete book 3 " + e.getMessage()));

        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();
    }

//    @Test
//    public void declineRequester() throws InterruptedException {
//        String requesterID = "CvLZ5c6lYqeyriVnwNpGurcBZl55";
//        String collection = "requests";
//        Book book = new Book("9711111111324", "To decline", "Armi", mAuth.getCurrentUser().getUid(), null);
//        book.setStatus(Book.Status.REQUESTED);
//        mRepository.createBook(book, documentReference -> {
//        }, e -> fail("Unable to create book: " + e.getMessage()));
//
//        Thread.sleep(SLEEP_TIME_MILLIS);
//        shadowOf(getMainLooper()).idle();
//
//        List<Book> books = mRepository.getBooks().getValue();
//        if (books == null) {
//            fail("Books is null");
//        }
//        for (Book b : books) {
//            if (! b.getIsbn().equals(book.getIsbn())) {
//                continue;
//            }
//            book = b;
//        }
//
//        // Add request to db
//        Request request = new Request(requesterID, book.getId());
//        mDb.collection(collection).add(request);
//
//        Thread.sleep(SLEEP_TIME_MILLIS);
//        shadowOf(getMainLooper()).idle();
//
//        AtomicBoolean declineFinished = new AtomicBoolean(false);
//        mRepository.declineRequester(requesterID, book.getId(), x -> {declineFinished.set(true);}, e -> fail("Failed to decline requester"), () -> fail("Request not found in db"));
//
//        Thread.sleep(SLEEP_TIME_MILLIS);
//        shadowOf(getMainLooper()).idle();
//        await().atMost(SLEEP_TIME, SECONDS).until(declineFinished::get);
//
//        // Assertions
//        mDb.collection(collection)
//                .whereEqualTo("requester", requesterID)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        fail("Failed to fetch request");
//                    }
////                    assertEquals(Request.State.ARCHIVED, task.getResult().getDocuments().get(0).toObject(Request.class).getState());
//                });
//        shadowOf(getMainLooper()).idle();
//
//        books = mRepository.getBooks().getValue();
//        if (books == null) {
//            fail("Repository does not contain any book");
//        }
//        for (Book b : books) {
//            if (! b.getId().equals(book.getId())) {
//                continue;
//            }
//            System.out.println("2");
//            assertEquals(Book.Status.AVAILABLE, b.getStatus());
//            break;
//        }
//
//        Thread.sleep(SLEEP_TIME_MILLIS);
//        shadowOf(getMainLooper()).idle();
//
//        mRepository.deleteBook(book, aVoid -> {
//        }, e -> fail("Unable to delete book " + e.getMessage()));
//
//        Thread.sleep(SLEEP_TIME_MILLIS);
//        shadowOf(getMainLooper()).idle();
//    }
}
