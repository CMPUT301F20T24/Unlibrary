package com.example.unlibrary.auth;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.unlibrary.util.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.assertEquals;

public class AuthViewModelTest {

    public AuthViewModel mViewModel;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        mViewModel = new AuthViewModel();
    }

    @Test
    public void testEmailLiveData() throws InterruptedException {
        mViewModel.getEmail().setValue("example@gmail.com");

        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getEmail()), "example@gmail.com");
    }

    @Test
    public void testPasswordLiveData() throws InterruptedException {
        mViewModel.getPassword().setValue("asdf");

        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getPassword()), "asdf");
    }

    @Test
    public void testUsernameLiveData() throws InterruptedException {
        mViewModel.getUsername().setValue("bob");

        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getUsername()), "bob");
    }

    @Test
    public void testCreateAccount() throws InterruptedException {
        mViewModel.getEmail().setValue("a@gmail.com");
        mViewModel.getPassword().setValue("pass");

        mViewModel.createAccount();

        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getEmail()), "");
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getPassword()), "");
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getFragmentNavigationEvent()).getClass(), RegisterFragment.class);
    }

    // TODO add more tests for validation. Probably should just make a singular validation method in the viewModel.
    // TODO figure out mocks for firebase auth
    @Test
    public void testLogin() throws InterruptedException {
        mViewModel.getEmail().setValue("bob@gmail.com");
        mViewModel.login();
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getFailureMsgEvent()), "Missing password.");
    }

    @Test
    public void testCancel() throws InterruptedException {
        mViewModel.getEmail().setValue("a@gmail.com");
        mViewModel.getPassword().setValue("pass");
        mViewModel.getUsername().setValue("asdf");

        mViewModel.cancel();

        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getEmail()), "");
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getPassword()), "");
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getUsername()), "");
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getFragmentNavigationEvent()).getClass(), LoginFragment.class);
    }

    // TODO add more tests for validation. Probably should just make a singular validation method in the viewModel.
    // TODO figure out mocks for firebase auth
    @Test
    public void testRegister() throws InterruptedException {
        mViewModel.getUsername().setValue("bob");
        mViewModel.register();
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getFailureMsgEvent()), "Missing email.");

        mViewModel.getEmail().setValue("asdf@gmail.com");
        mViewModel.register();
        assertEquals(LiveDataTestUtil.getOrAwaitValue(mViewModel.getFailureMsgEvent()), "Missing password.");
    }
}
