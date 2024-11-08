package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class UserSettingPage extends AppCompatActivity {
    private ImageButton backButton;
    private LinearLayout borrowHistoryOption;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting_page);

        // Initialize the back button
        backButton = findViewById(R.id.backButton);
        borrowHistoryOption = findViewById(R.id.borrowHistoryOption); // ID of the borrow history LinearLayout

        // Back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the settings page and return to the previous page
                finish();
            }
        });

        // Borrow history option click listener
        borrowHistoryOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Permanent Borrow History page
                Intent intent = new Intent(UserSettingPage.this, UserPermanentBorrowHistory.class);
                startActivity(intent);
            }
        });
    }
}

