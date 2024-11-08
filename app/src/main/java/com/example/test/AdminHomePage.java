package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

public class AdminHomePage extends AppCompatActivity {

    private ImageButton addBookButton, inventoryButton, logoutIcon, scanButton;
    private CardView totalBooksCard, borrowDashboardButton, overdueBooksCard;
    private TextView totalBooksCountTextView, borrowBooksCountTextView;
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
        scanButton = findViewById(R.id.scanButton);
        totalBooksCard = findViewById(R.id.totalBooksCard);
        totalBooksCountTextView = findViewById(R.id.totalBooksCount);
        borrowBooksCountTextView = findViewById(R.id.borrowBooksCount);
        logoutIcon = findViewById(R.id.logoutIcon); // Initialize the logout icon

        // Load total books from the database
        loadTotalBooksCount();
        loadTotalBorrowRecordsCount();

        borrowDashboardButton = findViewById(R.id.borrowBooksCard);
        overdueBooksCard = findViewById(R.id.overdueBooksCard);

        overdueBooksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AdminBorrowDashboardActivity
                Intent intent = new Intent(AdminHomePage.this, AdminOverdueDashboard.class);
                startActivity(intent);
            }
        });

        borrowDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AdminBorrowDashboardActivity
                Intent intent = new Intent(AdminHomePage.this, AdminBorrowDashboard.class);
                startActivity(intent);
            }
        });

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

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start scanning QR code
                ScanOptions options = new ScanOptions();
                options.setPrompt("Scan the QR Code to return the book");
                options.setBeepEnabled(true);
                options.setBarcodeImageEnabled(true);
                barcodeLauncher.launch(options);
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

    // Launcher for QR code scanner
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String qrCodeData = result.getContents();
                    Log.d("AdminHomePage", "Scanned QR Code: " + qrCodeData);

                    // Handle the scanned QR code, expecting just the borrowId
                    handleQRCodeScan(qrCodeData);
                } else {
                    Toast.makeText(AdminHomePage.this, "No QR code scanned", Toast.LENGTH_SHORT).show();
                }
            }
    );


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
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void loadTotalBooksCount() {
        // Access the "Books" node in Firebase to retrieve the total number of books
        FirebaseDatabase.getInstance().getReference("Books")
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

    // Load total borrow records count
    private void loadTotalBorrowRecordsCount() {
        FirebaseDatabase.getInstance().getReference("BorrowRecords")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get the total number of borrow records from the snapshot
                        long totalBorrowCount = dataSnapshot.getChildrenCount();

                        // Update the TextView with the total number of borrow records
                        borrowBooksCountTextView.setText(String.valueOf(totalBorrowCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
    }

    private void handleQRCodeScan(String borrowId) {
        // Retrieve the borrow record using the borrowId
        DatabaseReference borrowRef = FirebaseDatabase.getInstance()
                .getReference("BorrowRecords")
                .child(borrowId);  // Using borrowId scanned from QR code

        borrowRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Borrow record exists, get userId and bookId from the borrow record
                    String userId = snapshot.child("userId").getValue(String.class);
                    String bookId = snapshot.child("bookId").getValue(String.class);

                    if (userId != null && bookId != null) {
                        // Proceed with the return operation using the fetched userId and bookId
                        performReturnOperation(userId, bookId, borrowId);  // Pass the userId, bookId, and borrowId
                    } else {
                        Log.e("AdminHomePage", "userId or bookId missing in the borrow record");
                        Toast.makeText(AdminHomePage.this, "Error: userId or bookId missing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("AdminHomePage", "Invalid QR code or borrowId not found");
                    Toast.makeText(AdminHomePage.this, "Invalid QR code or borrowId not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminHomePage", "Error fetching borrow record", error.toException());
                Toast.makeText(AdminHomePage.this, "Error fetching borrow record", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to perform return operation
    private void performReturnOperation(String userId, String bookId, String borrowId) {
        // Step 1: Update the book's status back to available
        DatabaseReference bookRef = FirebaseDatabase.getInstance()
                .getReference("Books")
                .child(bookId);

        bookRef.child("status").setValue("available").addOnSuccessListener(aVoid -> {
            notifyUserForReturnedBook(bookId);
            // Step 2: Move the borrow record to UserBorrowingHistoryPermanent
            DatabaseReference permanentHistoryRef = FirebaseDatabase.getInstance()
                    .getReference("UserBorrowingHistoryPermanent")
                    .child(userId)  // Use the fetched userId
                    .child(bookId)
                    .child(borrowId);

            // Get the borrow record details from BorrowRecords
            DatabaseReference borrowRecordRef = FirebaseDatabase.getInstance()
                    .getReference("BorrowRecords")
                    .child(borrowId);

            borrowRecordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Create a permanent borrow history object
                        UserBorrowingHistoryPermanent permanentHistory = dataSnapshot.getValue(UserBorrowingHistoryPermanent.class);

                        if (permanentHistory != null) {
                            // Store the record in UserBorrowingHistoryPermanent
                            permanentHistoryRef.setValue(permanentHistory)
                                    .addOnSuccessListener(aVoid1 -> {
                                        // Step 3: Remove the borrow record from UserBorrowingHistory
                                        removeBorrowedBook(userId, bookId, borrowId);


                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("AdminHomePage", "Failed to move borrow record to permanent history", e);
                                        Toast.makeText(AdminHomePage.this, "Failed to move to permanent history", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.e("AdminHomePage", "Borrow record not found for borrowId: " + borrowId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("AdminHomePage", "Error fetching borrow record for permanent storage", databaseError.toException());
                }
            });
        }).addOnFailureListener(e -> {
            Log.e("AdminHomePage", "Failed to update book status", e);
            Toast.makeText(AdminHomePage.this, "Failed to update book status", Toast.LENGTH_SHORT).show();
        });
    }

    private void notifyUserForReturnedBook(String bookId) {
        // Reference to the Books node to get the book title
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Books").child(bookId);

        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the book data exists
                if (dataSnapshot.exists()) {
                    // Retrieve the book title from the Books node
                    String bookTitle = dataSnapshot.child("title").getValue(String.class);

                    if (bookTitle != null) {
                        // Now reference the BookRequests node to find users who requested this book
                        DatabaseReference bookRequestsRef = FirebaseDatabase.getInstance().getReference("BookRequests").child(bookId);

                        bookRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot requestSnapshot) {
                                for (DataSnapshot userRequestSnapshot : requestSnapshot.getChildren()) {
                                    String userId = userRequestSnapshot.getKey();

                                    if (userId != null) {
                                        // Store the notification in the user's notifications
                                        DatabaseReference userNotificationsRef = FirebaseDatabase.getInstance()
                                                .getReference("UserNotifications")
                                                .child(userId)
                                                .push(); // Unique key for each notification

                                        // Create notification data with book title
                                        Map<String, String> notificationData = new HashMap<>();
                                        notificationData.put("message", "The book you requested is now available.");
                                        notificationData.put("bookTitle", bookTitle);  // Store the book title
                                        notificationData.put("bookId", bookId);

                                        // Save the notification data in Firebase
                                        userNotificationsRef.setValue(notificationData)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("AdminHomePage", "Notification added for user: " + userId);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("AdminHomePage", "Failed to add notification", e);
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("AdminHomePage", "Error fetching book requests", databaseError.toException());
                            }
                        });
                    } else {
                        Log.e("AdminHomePage", "Book title not found for bookId: " + bookId);
                    }
                } else {
                    Log.e("AdminHomePage", "Book not found for bookId: " + bookId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminHomePage", "Error fetching book details", databaseError.toException());
            }
        });
    }



    // Method to remove the borrowed book from UserBorrowingHistory after returning
    private void removeBorrowedBook(String userId, String bookId, String borrowId) {
        DatabaseReference userBorrowHistoryRef = FirebaseDatabase.getInstance()
                .getReference("UserBorrowingHistory")
                .child(userId)
                .child(bookId)
                .child(borrowId);

        userBorrowHistoryRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Success message, can add Toast here if needed
                    Toast.makeText(AdminHomePage.this, "Book returned successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle error during deletion
                    Log.e("AdminHomePage", "Failed to remove borrow record", e);
                    Toast.makeText(AdminHomePage.this, "Failed to remove borrow record", Toast.LENGTH_SHORT).show();
                });
    }

}