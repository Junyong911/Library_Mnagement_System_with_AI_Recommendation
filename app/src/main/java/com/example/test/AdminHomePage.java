package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomePage extends AppCompatActivity {

    private ImageButton addBookButton, inventoryButton, logoutIcon;
    private CardView totalBooksCard;
    private TextView totalBooksCountTextView;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home_page);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize the buttons and card
        addBookButton = findViewById(R.id.addBookButton);
        inventoryButton = findViewById(R.id.inventoryButton);
        totalBooksCard = findViewById(R.id.totalBooksCard);
        totalBooksCountTextView = findViewById(R.id.totalBooksCount); // TextView inside the card
        logoutIcon = findViewById(R.id.logoutIcon); // Initialize the logout icon

        // Load total books from the database
        loadTotalBooksCount();

        // Set up the onClick listener for Total Books card
        totalBooksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Inventory activity when Total Books card is clicked
                Intent intent = new Intent(AdminHomePage.this, AdminInventoryPage.class);
                startActivity(intent);
            }
        });

        // Set up the onClick listener for Add Book button
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Add Book activity
                Intent intent = new Intent(AdminHomePage.this, AdminAddBook.class);
                startActivity(intent);
            }
        });

        // Set up the onClick listener for Inventory button
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Inventory activity
                Intent intent = new Intent(AdminHomePage.this, AdminInventoryPage.class);
                startActivity(intent);
            }
        });


        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the confirmation dialog before logout
                showLogoutConfirmationDialog();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        // Create an AlertDialog.Builder
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Log out the user from Firebase
                    mAuth.signOut();

                    // Show a toast message indicating that the user has logged out
                    Toast.makeText(AdminHomePage.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                    // Redirect to the SignIn activity
                    Intent intent = new Intent(AdminHomePage.this, SignIn.class);
                    startActivity(intent);
                    finish(); // Close the AdminHomePage
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // If user cancels, just dismiss the dialog
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    private void loadTotalBooksCount() {
        // Access the "Books" node in Firebase to retrieve the total number of books
        FirebaseDatabase.getInstance("https://librarymanagementsystemai-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Books")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get the total number of books from the snapshot
                        long totalBooksCount = dataSnapshot.getChildrenCount();

                        // Update the TextView with the total number of books
                        totalBooksCountTextView.setText(String.valueOf(totalBooksCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
    }

    // You can delete the startBarcodeScanner() and onActivityResult() methods, as they are no longer needed.
}
