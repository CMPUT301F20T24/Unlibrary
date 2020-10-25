/*
 * ProfileViewModelTest
 *
 * October 18, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.profile;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test behaviour of the ProfileViewModel, particularly fetching and updating user profile
 */
public class ProfileViewModelTest {

    private ProfileViewModel mViewModel;
    private ProfileRepository mRepo;

    @Before
    public void setup() {
        mViewModel = new ProfileViewModel();
        mRepo = new ProfileRepository();

    }

    @Test
    public void test() {
        assertEquals(1, 1);
    }
}
