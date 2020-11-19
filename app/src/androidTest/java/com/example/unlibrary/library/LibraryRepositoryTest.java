package com.example.unlibrary.library;

import com.example.unlibrary.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.fail;

public class LibraryRepositoryTest {
    private LibraryRepository repository;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String newBookId;

    @Before
    public void setup() {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8080);

        auth = FirebaseAuth.getInstance();
        auth.useEmulator("10.0.2.2", 9099);

        repository = new LibraryRepository(db, auth);
    }

    @Test
    public void addBookTest() {
        Book book = new Book("9780441016075", "Halting State", "Charles Stross", auth.getUid(), null);

        repository.createBook(book, documentReference -> {
            newBookId = documentReference.getId();
        }, e -> fail("Unable to create book: " + e.getMessage()));

        await().atMost(5, SECONDS).until(() -> newBookId != null);

        List<Book> books = repository.getBooks().getValue();
        if (books == null) {
            fail("Repository does not contain any book");
        }

        for (Book b : books) {
            if (b.getId().equals(newBookId)) {
                return;
            }
        }

        fail("New book not found in repository");
    }
}
