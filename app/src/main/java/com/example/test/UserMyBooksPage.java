package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class UserMyBooksPage extends AppCompatActivity {

    private TabLayout tabLayout;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_my_book_page); // Your layout

        tabLayout = findViewById(R.id.tabLayout);
        backButton = findViewById(R.id.backButton);

        // Set the default tab as "Borrowed"
        displayFragment(new BorrowedBooksFragment());

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new BorrowedBooksFragment(); // Show Borrowed Books
                        break;
                    case 1:
                        selectedFragment = new RecommendFragment(); // Show Borrowed Books
                        break;
                    case 2:
                        selectedFragment = new BookmarkFragment(); // Show Borrowed Books
                        break;
                }
                if (selectedFragment != null) {
                    displayFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                // Use if-else instead of switch-case
                if (item.getItemId() == R.id.home) {
                    // Stay on Home
                    Intent intent = new Intent(UserMyBooksPage.this, UserHomePage.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.book) {
                    // Navigate to Book Page
                    Intent intent = new Intent(UserMyBooksPage.this, UserMyBooksPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.search) {
                    // Navigate to Search Page
                    Intent intent = new Intent(UserMyBooksPage.this, UserSearchPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.setting) {
                    // Navigate to Settings Page
                    Intent intent = new Intent(UserMyBooksPage.this, UserSearchPage.class);
                    startActivity(intent);
                }
                return false;
            }


        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMyBooksPage.this, UserHomePage.class);
                startActivity(intent);
            }
        });
    }

    private void displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tabContentFrame, fragment);
        fragmentTransaction.commit();
    }
}
