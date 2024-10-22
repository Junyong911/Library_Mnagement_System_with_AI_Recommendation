package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserSearchPage extends AppCompatActivity {

    private RecyclerView genreRecyclerView;
    private UserSearchAdapter userSearchAdapter;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_search_page);  // Use your search page layout here

        // Initialize RecyclerView
        genreRecyclerView = findViewById(R.id.genreRecyclerView);
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample genre list (you can get this from strings.xml or a database)
        String[] genreList = getResources().getStringArray(R.array.genre_array);

        // Set adapter
        userSearchAdapter = new UserSearchAdapter(this, genreList);
        genreRecyclerView.setAdapter(userSearchAdapter);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSearchPage.this, UserHomePage.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                // Use if-else instead of switch-case
                if (item.getItemId() == R.id.home) {
                    // Stay on Home
                    Intent intent = new Intent(UserSearchPage.this, UserHomePage.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.book) {
                    // Navigate to Book Page
                    Intent intent = new Intent(UserSearchPage.this, UserMyBooksPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.search) {

                } else if (item.getItemId() == R.id.setting) {
                    // Navigate to Settings Page
                    Intent intent = new Intent(UserSearchPage.this, UserSearchPage.class);
                    startActivity(intent);
                }
                return false;
            }


        });
    }

}

