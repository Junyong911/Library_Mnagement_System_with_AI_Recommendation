package com.example.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

public class UserBorrowBookPage extends AppCompatActivity {

    private ImageView bookCoverImageView, barcodeImageView;
    private TextView bookTitleTextView, bookAuthorTextView, bookRatingValue, bookReviewsCount, bookBarcodeTextView, statusTextView;
    private Button borrowButton;
    private DatabaseReference booksRef, borrowRecordsRef;
    private String bookId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_borrow_book);

        // Initialize UI components
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        bookAuthorTextView = findViewById(R.id.bookAuthorTextView);
        bookRatingValue = findViewById(R.id.bookRatingValue);
        bookReviewsCount = findViewById(R.id.bookReviewsCount);
        bookBarcodeTextView = findViewById(R.id.bookBarcodeTextView);
        barcodeImageView = findViewById(R.id.barcodeImageView);
        borrowButton = findViewById(R.id.borrowButton);
        statusTextView = findViewById(R.id.statusTextView);

        // Get the book details from the intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        String title = intent.getStringExtra("bookTitle");
        String author = intent.getStringExtra("bookAuthor");
        String rating = intent.getStringExtra("bookRating");
        String reviewsCount = intent.getStringExtra("bookReviewsCount");
        String coverUrl = intent.getStringExtra("bookCoverUrl");
        String barcode = intent.getStringExtra("barcode");
        String status = intent.getStringExtra("status");

        // Log the barcode to check if it's passed correctly
        Log.d("UserBorrowBookPage", "Received barcode: " + barcode);

        // Set the received book details to the views
        bookTitleTextView.setText(title);
        bookAuthorTextView.setText("by " + author);
        bookRatingValue.setText(rating);
        bookReviewsCount.setText(reviewsCount);

        // Load the book cover image
        if (coverUrl != null && !coverUrl.isEmpty()) {
            Picasso.get().load(coverUrl).into(bookCoverImageView);
        }

        if (barcode != null) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(barcode, BarcodeFormat.QR_CODE, 600, 600);
                barcodeImageView.setImageBitmap(bitmap);  // Set the generated QR code image in the ImageView
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Initialize Firebase references
        booksRef = FirebaseDatabase.getInstance().getReference("Books");
        borrowRecordsRef = FirebaseDatabase.getInstance().getReference("BorrowRecords");

        fetchBookStatus(bookId);

        // Get current user ID from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e("UserBorrowBookPage", "Error: User not logged in");
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Borrow button click listener
        borrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookId != null && userId != null) {
                    performBookBorrow(bookId, userId);
                } else {
                    Toast.makeText(UserBorrowBookPage.this, "Error: Missing data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchBookStatus(String bookId) {
        booksRef.child(bookId).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if ("available".equalsIgnoreCase(status)) {
                    statusTextView.setText("Available");
                    statusTextView.setTextColor(Color.GREEN); // Set text color to green
                } else {
                    statusTextView.setText("Unavailable");
                    statusTextView.setTextColor(Color.RED); // Set text color to red
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserBorrowBookPage", "Failed to fetch book status", databaseError.toException());
                Toast.makeText(UserBorrowBookPage.this, "Error: Could not retrieve book status", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

                        UserBorrowBookClass borrowRecord = new UserBorrowBookClass(
                                borrowId,
                                bookId,
                                userId,
                                System.currentTimeMillis()
                        );
                        borrowRecordsRef.child(borrowId).setValue(borrowRecord)
                                .addOnSuccessListener(aVoid1 -> {
                                    // Add the borrow record to UserBorrowingHistory
                                    DatabaseReference userBorrowHistoryRef = FirebaseDatabase.getInstance()
                                            .getReference("UserBorrowingHistory")
                                            .child(userId)
                                            .child(bookId)
                                            .child(borrowId);
                                    userBorrowHistoryRef.setValue(borrowRecord)
                                            .addOnSuccessListener(aVoid2 -> {
                                                Toast.makeText(UserBorrowBookPage.this, "Book borrowed successfully", Toast.LENGTH_SHORT).show();
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



}
