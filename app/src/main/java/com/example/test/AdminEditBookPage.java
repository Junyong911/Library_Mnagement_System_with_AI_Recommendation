package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminEditBookPage extends AppCompatActivity {

    private EditText titleEditText, genreEditText, authorEditText, isbnEditText, descriptionEditText;
    private ImageView bookCoverImageView;
    private Button updateBookButton;
    private ImageButton deleteIcon, backButton;

    private DatabaseReference databaseReference;
    private String bookId, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_edit_book_page);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://librarymanagementsystemai-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Books");

        // Initialize UI components
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        titleEditText = findViewById(R.id.titleEditText);
        genreEditText = findViewById(R.id.genreEditText);
        authorEditText = findViewById(R.id.authorEditText);
        isbnEditText = findViewById(R.id.isbnEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        updateBookButton = findViewById(R.id.updateBookButton);
        deleteIcon = findViewById(R.id.deleteIcon);
        backButton = findViewById(R.id.backButton);

        // Get book details from Intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        titleEditText.setText(intent.getStringExtra("title"));
        genreEditText.setText(intent.getStringExtra("genre"));
        authorEditText.setText(intent.getStringExtra("author"));
        isbnEditText.setText(intent.getStringExtra("isbn"));
        descriptionEditText.setText(intent.getStringExtra("description"));
        imageUrl = intent.getStringExtra("imageUrl");

        // Load book cover image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(bookCoverImageView);
        }

        // Set up the update button click listener
        updateBookButton.setOnClickListener(v -> showEditConfirmationDialog());

        // Set up the delete button click listener
        deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Set up the back button click listener
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(AdminEditBookPage.this, AdminInventoryPage.class);
            startActivity(backIntent);
            finish(); // Close current activity
        });
    }

    // Function to show confirmation dialog before editing
    private void showEditConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Edit Book")
                .setMessage("Are you sure you want to edit this book?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Proceed to update the book if the user confirms
                    updateBookDetails();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Function to update the book details in Firebase
    private void updateBookDetails() {
        String title = titleEditText.getText().toString().trim();
        String genre = genreEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String isbn = isbnEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || genre.isEmpty() || author.isEmpty() || isbn.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating = 0.0f;
        int ratingsCount =0;
        // Update book in Firebase
        Book updatedBook = new Book(bookId, title, genre, author, isbn, description, imageUrl, null, "Available", rating, ratingsCount);
        databaseReference.child(bookId).setValue(updatedBook).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminEditBookPage.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after update
            } else {
                Toast.makeText(AdminEditBookPage.this, "Failed to update book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to show confirmation dialog before deleting
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Proceed to delete the book if the user confirms
                    deleteBook();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Function to delete the book from Firebase
    private void deleteBook() {
        if (bookId != null) {
            databaseReference.child(bookId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminEditBookPage.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after deletion
                } else {
                    Toast.makeText(AdminEditBookPage.this, "Failed to delete book", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
