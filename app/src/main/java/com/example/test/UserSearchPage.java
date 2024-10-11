package com.example.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserSearchPage extends AppCompatActivity {

    private RecyclerView genreRecyclerView;
    private UserSearchAdapter userSearchAdapter;

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
    }

}

