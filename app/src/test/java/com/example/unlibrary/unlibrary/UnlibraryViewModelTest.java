package com.example.unlibrary.unlibrary;

import android.view.View;
import android.widget.Toast;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.User;
import com.google.android.gms.tasks.OnSuccessListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Toast.class)
public class UnlibraryViewModelTest {
    UnlibraryViewModel mViewModel;
    @Mock
    UnlibraryRepository mMockRepository;

    MutableLiveData<List<Book>> mMockBooks;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    public void setupMocks() {
        mockStatic(Toast.class);

        // Mock getBooks
        ArrayList<Book> mockBooks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Book book = new Book(String.valueOf(i), "Title_" + i, "Someone", String.valueOf(i), null);
            book.setId(String.valueOf(i));
            mockBooks.add(book);
        }
        mMockBooks = new MutableLiveData<>(mockBooks);
        when(mMockRepository.getBooks()).thenReturn(this.mMockBooks);

        // Mock fetchOwner
        doAnswer(invocation -> {
            OnSuccessListener successCallback = invocation.getArgument(1, OnSuccessListener.class);
            Book targetBook = invocation.getArgument(0, Book.class);
            String ownerId = null;
            for (Book book: mockBooks) {
                if (book.getId().equals(targetBook.getId())) {
                    ownerId = book.getOwner();
                    break;
                }
            }
            if (ownerId == null) {
                fail("Could not mock owner for book");
            }
            User owner = new User(ownerId, null, null);
            successCallback.onSuccess(owner);
            return null;
        }).when(mMockRepository).fetchOwner(any(), any());

        when(Toast.makeText(any(), anyString(), anyInt())).thenReturn(mock(Toast.class));
    }

    @Before
    public void setup() {
        mMockRepository = mock(UnlibraryRepository.class);
        setupMocks();

        mViewModel = new UnlibraryViewModel(mMockRepository);
    }

    /**
     * Ensure viewmodel just forwards books from repository
     */
    @Test
    public void testGetBooks() {
        LiveData<List<Book>> viewModelBooks = mViewModel.getBooks();
        verify(mMockRepository).getBooks();
        assertEquals(mMockBooks.getValue(), viewModelBooks.getValue());
    }

    /**
     * Ensure viewmodel updates current book owner when selectCurrentBook is called
     */
    @Test
    public void testUpdateCurrentBookOwner() {
        int bookIndexInArray = 0;
        mViewModel.selectCurrentBook(mock(View.class), bookIndexInArray);
        verify(mMockRepository).fetchOwner(any(), any());
        assertEquals(mMockBooks.getValue().get(0).getOwner(), mViewModel.getCurrentBookOwner().getValue().getUID());
    }

}
