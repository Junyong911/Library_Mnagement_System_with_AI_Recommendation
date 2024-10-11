package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserHomePage extends AppCompatActivity {

    private ImageView posterImageView;
    private int[] posterImages = {
            R.drawable.poster_1,  // replace with your actual poster images
            R.drawable.poster_2,

    };

    private int currentIndex = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);

        posterImageView = findViewById(R.id.posterImageView);
        startImageSlider();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                // Use if-else instead of switch-case
                if (item.getItemId() == R.id.home) {
                    // Stay on Home
                    Intent intent = new Intent(UserHomePage.this, UserHomePage.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.book) {
                    // Navigate to Book Page
                    Intent intent = new Intent(UserHomePage.this, UserMyBooksPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.search) {
                    // Navigate to Search Page
                    Intent intent = new Intent(UserHomePage.this, UserSearchPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.profile1) {
                    // Navigate to Settings Page
                    Intent intent = new Intent(UserHomePage.this, UserSearchPage.class);
                    startActivity(intent);
                }
                return false;
            }


        });
    }

    private void startImageSlider() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Change the image
                currentIndex = (currentIndex + 1) % posterImages.length;
                posterImageView.setImageResource(posterImages[currentIndex]);

                // Repeat the task every 3 seconds
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(runnable);
    }
}
