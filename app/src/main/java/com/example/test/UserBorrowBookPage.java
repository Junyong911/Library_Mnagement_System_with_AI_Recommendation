package com.example.test;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserBorrowBookPage extends AppCompatActivity {

    private ImageView bookCoverImageView;
    private TextView bookTitleTextView, bookAuthorTextView, bookRatingValue, statusTextView, borrowDateTextView, dueDateTextView, unavailableMessageTextView, unavailableSubtitleTextView;
    private Button borrowButton, requestBookButton;
    private DatabaseReference booksRef, borrowRecordsRef, bookRequestsRef;
    private String bookId, userId;
    private View dividerLine;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_borrow_book);

        // Initialize UI components
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        bookAuthorTextView = findViewById(R.id.bookAuthorTextView);
        bookRatingValue = findViewById(R.id.bookRatingValue);
        statusTextView = findViewById(R.id.statusTextView);
        borrowDateTextView = findViewById(R.id.borrowDateTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        borrowButton = findViewById(R.id.borrowButton);
        requestBookButton = findViewById(R.id.requestBookButton);
        unavailableMessageTextView = findViewById(R.id.unavailableMessageTextView);
        unavailableSubtitleTextView = findViewById(R.id.unavailableSubtitleTextView);
        dividerLine = findViewById(R.id.dividerLine);
        backButton = findViewById(R.id.backButton);

        // Get the book details from the intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        String title = intent.getStringExtra("bookTitle");
        String author = intent.getStringExtra("bookAuthor");
        String rating = intent.getStringExtra("bookRating");
        String coverUrl = intent.getStringExtra("imageUrl");
        String status = intent.getStringExtra("status");
        String genre = intent.getStringExtra("bookGenre");

        // Set the received book details to the views
        bookTitleTextView.setText(title);
        bookAuthorTextView.setText("by " + author + " ( " + genre + " )");
        bookRatingValue.setText(rating);

        // Load the book cover image
        if (coverUrl != null && !coverUrl.isEmpty()) {
            Log.d("UserBorrowBookPage", "Cover URL: " + coverUrl);
            Picasso.get().load(coverUrl).into(bookCoverImageView);
        }

        // Initialize Firebase references
        booksRef = FirebaseDatabase.getInstance().getReference("Books");
        borrowRecordsRef = FirebaseDatabase.getInstance().getReference("BorrowRecords");
        bookRequestsRef = FirebaseDatabase.getInstance().getReference("BookRequests");

        // Get current user ID from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e("UserBorrowBookPage", "Error: User not logged in");
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch book status and display the current and due dates
        fetchBookStatusAndDisplayDates();

        // Borrow button click listener with confirmation dialog
        borrowButton.setOnClickListener(v -> {
            if (bookId != null && userId != null) {
                // Show confirmation dialog
                showConfirmationDialog("Borrow Book", "Are you sure you want to borrow this book?", () -> {
                    // Check overdue books before allowing to borrow
                    checkOverdueBooksBeforeBorrow(userId, bookId);
                });
            } else {
                Toast.makeText(UserBorrowBookPage.this, "Error: Missing data", Toast.LENGTH_SHORT).show();
            }
        });

        // Request book button click listener with confirmation dialog
        requestBookButton.setOnClickListener(v -> {
            if (bookId != null && userId != null) {
                // Show confirmation dialog
                showConfirmationDialog("Request Book", "Are you sure you want to request this book?", () -> {
                    sendBookRequest(userId, bookId);
                });
            } else {
                Toast.makeText(UserBorrowBookPage.this, "Error: Unable to request the book.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> onBackPressed());

        // Bottom navigation actions
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(UserBorrowBookPage.this, UserHomePage.class));
                return true;
            } else if (item.getItemId() == R.id.book) {
                startActivity(new Intent(UserBorrowBookPage.this, UserMyBooksPage.class));
                return true;
            } else if (item.getItemId() == R.id.search) {
                startActivity(new Intent(UserBorrowBookPage.this, UserSearchPage.class));
                return true;
            } else if (item.getItemId() == R.id.setting) {
                startActivity(new Intent(UserBorrowBookPage.this, UserSearchPage.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void fetchBookStatusAndDisplayDates() {
        // Always show the borrow date and due date when the page loads
        booksRef.child(bookId).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if ("available".equalsIgnoreCase(status)) {
                    statusTextView.setText("Available");
                    statusTextView.setTextColor(Color.GREEN); // Set text color to green
                    requestBookButton.setVisibility(View.GONE);  // Hide request button
                } else {
                    statusTextView.setText("Unavailable");
                    statusTextView.setTextColor(Color.RED); // Set text color to red
                    unavailableMessageTextView.setVisibility(View.VISIBLE);
                    dividerLine.setVisibility(View.VISIBLE);
                    unavailableSubtitleTextView.setVisibility(View.VISIBLE);
                    requestBookButton.setVisibility(View.VISIBLE);  // Show request button
                }

                // Display the borrow date (current date) and due date (7 days later)
                displayBorrowAndDueDate(System.currentTimeMillis());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserBorrowBookPage", "Failed to fetch book status", databaseError.toException());
                Toast.makeText(UserBorrowBookPage.this, "Error: Could not retrieve book status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayBorrowAndDueDate(long borrowTimestamp) {
        // Format the current date and calculate the due date (7 days from the current date)
        String borrowDate = formatDate(borrowTimestamp);  // Format the borrow date
        String dueDate = formatDate(borrowTimestamp + (7 * 24 * 60 * 60 * 1000));  // Due date after 7 days

        borrowDateTextView.setText("Borrow Date: " + borrowDate);
        dueDateTextView.setText("Due Date: " + dueDate);
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return sdf.format(calendar.getTime());
    }

    // Check for overdue books before allowing borrowing
    private void checkOverdueBooksBeforeBorrow(String userId, String bookId) {
        DatabaseReference userBorrowHistoryRef = FirebaseDatabase.getInstance()
                .getReference("UserBorrowingHistory")
                .child(userId);

        userBorrowHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int overdueCount = 0;
                long currentTime = System.currentTimeMillis();

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot borrowSnapshot : bookSnapshot.getChildren()) {
                        UserBorrowingHistory history = borrowSnapshot.getValue(UserBorrowingHistory.class);
                        if (history != null) {
                            // Calculate due date as 7 days after the borrow timestamp
                            long dueTime = history.getBorrowTimestamp() + (7 * 24 * 60 * 60 * 1000);  // 7 days in milliseconds
                            if (currentTime > dueTime) {
                                overdueCount++;  // Increment count if the book is overdue
                            }
                        }
                    }
                }

                // If the user has 3 or more overdue books, restrict borrowing
                if (overdueCount >= 3) {
                    Toast.makeText(UserBorrowBookPage.this, "You cannot borrow more books. You have 3 or more overdue books.", Toast.LENGTH_LONG).show();
                } else {
                    // Proceed with borrowing
                    performBookBorrow(bookId, userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserBorrowBookPage", "Failed to check overdue books", databaseError.toException());
            }
        });
    }

    // Send a book request when the book is unavailable
    private void sendBookRequest(String userId, String bookId) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance()
                .getReference("BookRequests")
                .child(bookId)
                .child(userId);

        // Create a request entry
        requestRef.setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserBorrowBookPage.this, "Book request sent successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserBorrowBookPage.this, "Error: Could not send request.", Toast.LENGTH_SHORT).show();
                    Log.e("UserBorrowBookPage", "Failed to send book request", e);
                });
    }

    // Perform book borrow operation
    private void performBookBorrow(String bookId, String userId) {
        // First, check the current status of the book from Firebase
        booksRef.child(bookId).child("status").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String currentStatus = task.getResult().getValue(String.class);
                if ("unavailable".equals(currentStatus)) {
                    // If the book is already borrowed, show a message and do not allow borrowing
                    Toast.makeText(UserBorrowBookPage.this, "This book is currently unavailable for borrowing.", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with borrowing since the book is available
                    updateBookStatusAndCreateRecord(bookId, userId);
                }
            } else {
                Log.e("UserBorrowBookPage", "Failed to check book status", task.getException());
                Toast.makeText(UserBorrowBookPage.this, "Error: Failed to check book status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update book status and create the borrow record
    private void updateBookStatusAndCreateRecord(String bookId, String userId) {
        try {
            // Update the book's status to unavailable
            booksRef.child(bookId).child("status").setValue("unavailable")
                    .addOnSuccessListener(aVoid -> {
                        // Create a borrow record
                        String borrowId = borrowRecordsRef.push().getKey();
                        if (borrowId == null) {
                            Toast.makeText(UserBorrowBookPage.this, "Error: Could not generate borrow ID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get book details from the Intent
                        String bookTitle = getIntent().getStringExtra("bookTitle");
                        String bookAuthor = getIntent().getStringExtra("bookAuthor");
                        String bookRating = getIntent().getStringExtra("bookRating");
                        String bookReviewsCount = getIntent().getStringExtra("bookReviewsCount");
                        String bookCoverUrl = getIntent().getStringExtra("bookCoverUrl");
                        String bookGenre = getIntent().getStringExtra("bookGenre");

                        // Create a new borrow record with additional book details and the genre
                        UserBorrowBookClass borrowRecord = new UserBorrowBookClass(
                                borrowId,
                                bookId,
                                userId,
                                System.currentTimeMillis(),
                                bookTitle,
                                bookAuthor,
                                bookRating,
                                bookReviewsCount,
                                bookCoverUrl,
                                bookGenre  // Pass the genre to the constructor
                        );

                        // Store the borrow record in Firebase
                        borrowRecordsRef.child(borrowId).setValue(borrowRecord)
                                .addOnSuccessListener(aVoid1 -> {
                                    // Also store the borrow record in UserBorrowingHistory
                                    DatabaseReference userBorrowHistoryRef = FirebaseDatabase.getInstance()
                                            .getReference("UserBorrowingHistory")
                                            .child(userId)
                                            .child(bookId)
                                            .child(borrowId);
                                    userBorrowHistoryRef.setValue(borrowRecord)
                                            .addOnSuccessListener(aVoid2 -> {
                                                Toast.makeText(UserBorrowBookPage.this, "Book borrowed successfully", Toast.LENGTH_SHORT).show();
                                                // Check and notify if the book was requested
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("UserBorrowBookPage", "Failed to add to UserBorrowingHistory", e);
                                                Toast.makeText(UserBorrowBookPage.this, "Error: Failed to add to history", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("UserBorrowBookPage", "Failed to add borrow record", e);
                                    Toast.makeText(UserBorrowBookPage.this, "Error: Failed to add borrow record", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserBorrowBookPage", "Failed to update book status", e);
                        Toast.makeText(UserBorrowBookPage.this, "Error: Failed to update book status", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("UserBorrowBookPage", "Exception in performBookBorrow", e);
            Toast.makeText(this, "Error: An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }
    }






    // Method to show confirmation dialog before performing actions
    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(UserBorrowBookPage.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Cancel", null)
                .show();
    }
}

