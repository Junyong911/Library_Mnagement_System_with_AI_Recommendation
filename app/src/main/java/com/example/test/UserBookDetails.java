package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log; // Add import for logging
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class UserBookDetails extends AppCompatActivity {

    private ImageView bookCoverImageView;
    private TextView bookTitleTextView, bookAuthorTextView, ratingTextView, bookDescriptionTextView;
    private AppCompatButton borrowButton;
    private FrameLayout bookCoverFrame;
    private RatingBar userRatingBar, averageRatingBar; // Add averageRatingBar for book rating

    private DatabaseReference bookDatabaseReference;
    private String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_book_details);

        // Initialize UI components
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        bookAuthorTextView = findViewById(R.id.bookAuthorTextView);
        ratingTextView = findViewById(R.id.ratingTextView);

        bookCoverFrame = findViewById(R.id.bookCoverFrame);
        userRatingBar = findViewById(R.id.userRatingBar); // User's rating
        averageRatingBar = findViewById(R.id.averageRatingBar);

        TextView bookDescriptionTextView = findViewById(R.id.bookDescriptionTextView);


        // Initialize Firebase database reference
        bookDatabaseReference = FirebaseDatabase.getInstance().getReference("Books");

        // Get the current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data passed from the book list
        Intent intent = getIntent();
        String bookId = intent.getStringExtra("bookId");
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String genre = intent.getStringExtra("genre");
        String description =  intent.getStringExtra("description");
        String barcode = intent.getStringExtra("barcode");
        String status = intent.getStringExtra("status");
        float rating = intent.getFloatExtra("rating", 0);
        long ratingsCount = intent.getLongExtra("ratingsCount", 0);
        String coverUrl = intent.getStringExtra("imageUrl");

        // Log to see if the barcode is retrieved correctly
        Log.d("UserBookDetails", "Barcode fetched: " + barcode);

        // Set book details
        bookTitleTextView.setText(title);
        bookAuthorTextView.setText("by " + author + " (" + genre + ")");
        fetchBookRatingDetails(bookId);

        bookDescriptionTextView.setText(description);

        // Load book cover image (with Picasso)
        if (coverUrl != null && !coverUrl.isEmpty()) {
            Picasso.get().load(coverUrl).into(bookCoverImageView, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) bookCoverImageView.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(palette -> {
                        int dominantColor = palette.getDominantColor(ContextCompat.getColor(UserBookDetails.this, R.color.default_background));
                        bookCoverFrame.setBackgroundColor(dominantColor);
                    });
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                }
            });
        }

        // Fetch the current user's rating for this book
        fetchUserRating(bookId);

        // Set up rating bar change listener for the user to submit their rating
        userRatingBar.setOnRatingBarChangeListener((ratingBar, newRating, fromUser) -> {
            if (fromUser) {
                // If the user changed the rating, update it in Firebase
                updateUserRating(bookId, newRating);
            }
        });

        borrowButton = findViewById(R.id.borrowButton);

        borrowButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(UserBookDetails.this, UserBorrowBookPage.class);

            // Log the barcode before passing to the next activity
            Log.d("UserBookDetails", "Passing barcode to borrow page: " + barcode);

            // Pass the book details to the borrow page
            intent1.putExtra("bookTitle", title); // Pass the book title
            intent1.putExtra("bookAuthor", author); // Pass the book author
            intent1.putExtra("bookRating", String.format("%.2f", rating)); // Pass the rating
            intent1.putExtra("bookReviewsCount", ratingsCount + " ratings"); // Pass the review count
            intent1.putExtra("bookCoverUrl", coverUrl);
            intent1.putExtra("barcode", barcode);
            intent1.putExtra("bookId", bookId);
            intent1.putExtra("status", status);
            // Start the borrow page activity
            startActivity(intent1);
        });

    }

    // Fetch and display the current user's rating for this book
    private void fetchUserRating(String bookId) {
        DatabaseReference userRatingRef = bookDatabaseReference.child(bookId).child("ratings").child(userId);
        userRatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float userRating = dataSnapshot.getValue(Float.class);
                    userRatingBar.setRating(userRating); // Set the user's rating
                    // Change the color of the rating bar stars to yellow if the user has already rated
                    userRatingBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_light)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Update the user's rating in Firebase and recalculate the average rating
    private void updateUserRating(String bookId, float newRating) {
        DatabaseReference userRatingRef = bookDatabaseReference.child(bookId).child("ratings").child(userId);
        userRatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float previousRating = 0;
                boolean isNewRating = false;

                // Check if user has already rated the book
                if (dataSnapshot.exists()) {
                    previousRating = dataSnapshot.getValue(Float.class); // Get previous rating
                } else {
                    isNewRating = true; // First time rating, count the rating count
                }

                // Update the user's rating in the database
                userRatingRef.setValue(newRating);

                // Update the book's average rating and ratings count
                updateBookAverageRating(bookId, newRating, previousRating, isNewRating);

                // After rating, change the star color to yellow
                userRatingBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_light)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Update the book's average rating and only increase the rating count if it's a new rating
    private void updateBookAverageRating(String bookId, float newRating, float previousRating, boolean isNewRating) {
        DatabaseReference bookRef = bookDatabaseReference.child(bookId);
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    if (book != null) {
                        float currentAverage = book.getRating();
                        long ratingsCount = book.getRatingsCount();

                        // Recalculate the new average rating
                        if (isNewRating) {
                            ratingsCount++; // Increase ratings count only for a new rating
                            float newAverage = ((currentAverage * (ratingsCount - 1)) + newRating) / ratingsCount;
                            bookRef.child("rating").setValue(newAverage);
                            bookRef.child("ratingsCount").setValue(ratingsCount);
                        } else {
                            // Adjust the average by removing the old rating and adding the new rating
                            float newAverage = ((currentAverage * ratingsCount) - previousRating + newRating) / ratingsCount;
                            bookRef.child("rating").setValue(newAverage);
                        }

                        // Update the UI
                        ratingTextView.setText(String.format("%.2f", currentAverage) + " • " + ratingsCount + " ratings");
                        averageRatingBar.setRating(currentAverage); // Update the average rating displayed
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Fetch book's average rating and number of ratings
    private void fetchBookRatingDetails(String bookId) {
        DatabaseReference bookRef = bookDatabaseReference.child(bookId);
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    if (book != null) {
                        float averageRating = book.getRating();
                        long ratingsCount = book.getRatingsCount();

                        // Update UI with average rating and number of ratings
                        averageRatingBar.setRating(averageRating);
                        ratingTextView.setText(String.format("%.2f", averageRating) + " • " + ratingsCount + " ratings");
                    } else {
                        // In case book object is null (no ratings yet), show default message
                        averageRatingBar.setRating(0);
                        ratingTextView.setText("No ratings yet");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
