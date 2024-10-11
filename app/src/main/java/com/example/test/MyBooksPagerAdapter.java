package com.example.test;

import android.support.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyBooksPagerAdapter extends FragmentPagerAdapter {
    public MyBooksPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BorrowHistoryFragment(); // Borrowed Books
            case 1:
                return null; // Recommend Books
            case 2:
                return null; // Bookmarked Books
            default:
                return new BorrowHistoryFragment();
        }
    }

    @Override
    public int getCount() {
        return 3; // Total number of tabs
    }
}
