package com.example.test;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class UserMyBooksPage extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyBooksPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_my_book_page);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.tabContentFrame);

        pagerAdapter = new MyBooksPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        // Set tab titles
        tabLayout.getTabAt(0).setText("Borrowed");
        tabLayout.getTabAt(1).setText("Recommend");
        tabLayout.getTabAt(2).setText("Bookmark");
    }
}
