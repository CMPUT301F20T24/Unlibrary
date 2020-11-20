/*
 * FilterMap
 *
 * November 3, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import com.example.unlibrary.models.Book;

import java.util.EnumMap;
import java.util.Map;

/**
 * Wrapper class to manage the filter settings for the library book list.
 */
public class FilterMap {
    private final Map<Book.Status, Boolean> mFilter;

    /**
     * Construct a FilterMap. Defaults to filtering on no statuses.
     */
    public FilterMap() {
        mFilter = new EnumMap<>(Book.Status.class);
        mFilter.put(Book.Status.AVAILABLE, false);
        mFilter.put(Book.Status.REQUESTED, false);
        mFilter.put(Book.Status.ACCEPTED, false);
        mFilter.put(Book.Status.BORROWED, false);
    }

    /**
     * Enable/disable filtering on a particular status.
     *
     * @param status Status to filter on
     * @param enable Whether to enable or disable the filter
     */
    public void set(String status, Boolean enable) {
        mFilter.put(Book.Status.valueOf(status), enable);
    }

    /**
     * Get a map representing the filter.
     *
     * @return The underlying map
     */
    public Map<Book.Status, Boolean> getMap() {
        return mFilter;
    }

    /**
     * Update the filter based on the values in another map.
     *
     * @param map Map to grab new values from.
     */
    public void setMap(Map<Book.Status, Boolean> map) {
        for (Map.Entry<Book.Status, Boolean> e : map.entrySet()) {
            mFilter.put(e.getKey(), e.getValue());
        }
    }

    /**
     * Return a list of names for each filter field.
     *
     * @return List of names
     */
    public String[] itemStrings() {
        return new String[]{
                Book.Status.AVAILABLE.toString(),
                Book.Status.REQUESTED.toString(),
                Book.Status.ACCEPTED.toString(),
                Book.Status.BORROWED.toString()
        };
    }

    /**
     * Return a list of booleans indicating whether a field is filtered for.
     *
     * @return List of booleans
     */
    public boolean[] itemBooleans() {
        return new boolean[]{
                mFilter.get(Book.Status.AVAILABLE),
                mFilter.get(Book.Status.REQUESTED),
                mFilter.get(Book.Status.ACCEPTED),
                mFilter.get(Book.Status.BORROWED),
        };
    }
}
