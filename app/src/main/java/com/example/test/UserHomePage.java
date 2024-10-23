package com.example.test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserHomePage extends AppCompatActivity {

    private ImageView posterImageView;
    private int[] posterImages = {
            R.drawable.poster_1,  // replace with your actual poster images
            R.drawable.poster_2,
    };
    private int currentIndex = 0;
    private FirebaseAuth mAuth;
    private ImageButton logoutIcon;
    private LinearLayout homePageLayout;
    private DatabaseReference userNotificationsRef;
    private RecyclerView recyclerViewPopularBooks;
    private PopularBookAdapter popularBooksAdapter;
    private List<Book> popularBooksList;
    private DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);

        mAuth = FirebaseAuth.getInstance();
        logoutIcon = findViewById(R.id.logoutIcon);
        posterImageView = findViewById(R.id.posterImageView);
        homePageLayout = findViewById(R.id.homePageLayout);

        String userId = mAuth.getCurrentUser().getUid();

        userNotificationsRef = FirebaseDatabase.getInstance().getReference("UserNotifications").child(userId);
        startImageSlider();

        loadNotifications();
        logoutIcon.setOnClickListener(v -> showLogoutConfirmationDialog());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
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
            } else if (item.getItemId() == R.id.setting) {
                // Navigate to Settings Page
                Intent intent = new Intent(UserHomePage.this, UserSettingPage.class);
                startActivity(intent);
            }
            return false;
        });



        recyclerViewPopularBooks = findViewById(R.id.recyclerViewPopularBooks);
        recyclerViewPopularBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        popularBooksList = new ArrayList<>();
        popularBooksAdapter = new PopularBookAdapter(popularBooksList, this);
        recyclerViewPopularBooks.setAdapter(popularBooksAdapter);

        // Fetch popular books
        fetchPopularBooks();


    }

    private void fetchPopularBooks() {
        booksRef = FirebaseDatabase.getInstance().getReference("Books");
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                popularBooksList.clear(); // Clear previous data

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);

                    if (book != null) {
                        popularBooksList.add(book);
                    }
                }

                // Sort by rating and get the top 5 highest-rated books
                Collections.sort(popularBooksList, new Comparator<Book>() {
                    @Override
                    public int compare(Book book1, Book book2) {
                        return Float.compare(book2.getRating(), book1.getRating()); // Sort in descending order
                    }
                });

                // Make sure to limit the list to exactly 5 books
                if (popularBooksList.size() > 5) {
                    popularBooksList = new ArrayList<>(popularBooksList.subList(0, 5));
                }

                popularBooksAdapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserHomePage.this, "Failed to load popular books.", Toast.LENGTH_SHORT).show();
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

                // Repeat the task every 5 seconds
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);
    }

    private void showLogoutConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Toast.makeText(UserHomePage.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserHomePage.this, SignIn.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    // Method to load notifications
    private void loadNotifications() {
        // Fetch notifications from Firebase
        userNotificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                    String message = notificationSnapshot.child("message").getValue(String.class);
                    String bookTitle = notificationSnapshot.child("bookTitle").getValue(String.class);
                    String notificationId = notificationSnapshot.getKey();

                    if (message != null && bookTitle != null) {
                        // Dynamically create a notification view
                        createNotificationView(notificationId, message, bookTitle);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserHomePage.this, "Error loading notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to create a dynamic notification view
    private void createNotificationView(String notificationId, String message, String bookTitle) {
        // Inflate the custom layout for notifications
        View notificationView = getLayoutInflater().inflate(R.layout.notification_banner, null);

        // Find views inside the inflated layout
        TextView notificationMessage = notificationView.findViewById(R.id.notificationMessage);
        Button copyButton = notificationView.findViewById(R.id.copyButton);
        Button dismissButton = notificationView.findViewById(R.id.dismissButton);

        // Set the notification message with book title
        notificationMessage.setText(" (" + bookTitle + ")" + message);

        // Copy button to copy the book title
        copyButton.setOnClickListener(v -> {
            // Copy the book title to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Book Title", bookTitle);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(UserHomePage.this, "Copied: " + bookTitle, Toast.LENGTH_SHORT).show();
        });

        // Dismiss button to remove the notification
        dismissButton.setOnClickListener(v -> {
            // Remove the notification view from the layout
            ((ViewGroup) notificationView.getParent()).removeView(notificationView);

            // Remove the notification from Firebase
            userNotificationsRef.child(notificationId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UserHomePage.this, "Notification dismissed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserHomePage.this, "Failed to dismiss notification", Toast.LENGTH_SHORT).show();
                    });
        });

        // Add the notification view to the layout
        homePageLayout.addView(notificationView);
    }

}
