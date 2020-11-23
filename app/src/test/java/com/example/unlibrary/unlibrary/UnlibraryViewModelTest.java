package com.example.unlibrary.unlibrary;

import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnlibraryViewModelTest {
    UnlibraryViewModel mViewModel;
    @Mock
    UnlibraryRepository mMockRepository;

    MutableLiveData<List<Book>> mMockBooks;

    @Before
    public void setup() {
        // Initialize mock books
        ArrayList<Book> mockBooks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mockBooks.add(new Book(String.valueOf(i), "Title_" + i, "Someone", String.valueOf(i), null));
        }
        mMockBooks = new MutableLiveData<>(mockBooks);

        mMockRepository = mock(UnlibraryRepository.class);
        when(mMockRepository.getBooks()).thenReturn(this.mMockBooks);
        mViewModel = new UnlibraryViewModel(mMockRepository);
    }

    /**
     * Ensure viewmodel just forwards books from repository
     */
    @Test
    public void testGetBooks() {
        assertEquals(mMockBooks.getValue(), mViewModel.getBooks().getValue());
    }

}
