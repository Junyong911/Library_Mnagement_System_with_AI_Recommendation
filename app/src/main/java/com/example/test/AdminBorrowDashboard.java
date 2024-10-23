package com.example.test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AdminBorrowDashboard extends AppCompatActivity {

    private RecyclerView recyclerViewBorrowHistory;
    private AdminBorrowRecordAdapter borrowHistoryAdapter;
    private List<UserBorrowBookClass> borrowHistoryList;
    private List<UserBorrowBookClass> fullBorrowHistoryList; // Full list for search filtering
    private DatabaseReference borrowRecordsRef;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_borrow_dashboard);

        // Initialize RecyclerView
        recyclerViewBorrowHistory = findViewById(R.id.recyclerViewBorrowHistory);
        recyclerViewBorrowHistory.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        borrowHistoryList = new ArrayList<>();
        fullBorrowHistoryList = new ArrayList<>();
        borrowHistoryAdapter = new AdminBorrowRecordAdapter(borrowHistoryList, this);
        recyclerViewBorrowHistory.setAdapter(borrowHistoryAdapter);

        // Add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewBorrowHistory.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerViewBorrowHistory.addItemDecoration(dividerItemDecoration);

        // Reference to BorrowRecords and Users in Firebase
        borrowRecordsRef = FirebaseDatabase.getInstance().getReference("BorrowRecords");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Fetch Borrow Records
        fetchBorrowRecords();

        // Search icon click event
        findViewById(R.id.searchIcon).setOnClickListener(v -> showSearchDialog());
    }

    // Fetch Borrow Records from Firebase
    private void fetchBorrowRecords() {
        borrowRecordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                borrowHistoryList.clear();
                fullBorrowHistoryList.clear();

                // Loop through each borrow record
                for (DataSnapshot borrowSnapshot : dataSnapshot.getChildren()) {
                    UserBorrowBookClass borrowRecord = new UserBorrowBookClass(
                            borrowSnapshot.child("borrowId").getValue(String.class),
                            borrowSnapshot.child("bookId").getValue(String.class),
                            borrowSnapshot.child("userId").getValue(String.class),
                            borrowSnapshot.child("borrowTimestamp").getValue(Long.class),
                            borrowSnapshot.child("bookTitle").getValue(String.class),
                            borrowSnapshot.child("bookAuthor").getValue(String.class),
                            borrowSnapshot.child("bookRating").getValue(String.class),
                            borrowSnapshot.child("bookReviewsCount").getValue(String.class),
                            borrowSnapshot.child("bookCoverUrl").getValue(String.class),
                            borrowSnapshot.child("bookGenre").getValue(String.class)
                    );

                    // Fetch the actual username from the userId
                    String userId = borrowSnapshot.child("userId").getValue(String.class);
                    if (userId != null) {
                        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                String username = userSnapshot.child("username").getValue(String.class);

                                // Add the record to the lists
                                borrowHistoryList.add(borrowRecord);
                                fullBorrowHistoryList.add(borrowRecord);

                                // Sort by borrowTimestamp after fetching usernames
                                sortBorrowRecordsByTimestamp();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle errors if necessary
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminBorrowDashboard.this, "Failed to load borrow history.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Sort the borrow records by borrowTimestamp in ascending order
    private void sortBorrowRecordsByTimestamp() {
        Collections.sort(borrowHistoryList, new Comparator<UserBorrowBookClass>() {
            @Override
            public int compare(UserBorrowBookClass record1, UserBorrowBookClass record2) {
                return Long.compare(record1.getBorrowTimestamp(), record2.getBorrowTimestamp());
            }
        });

        // Notify adapter about the changes
        borrowHistoryAdapter.notifyDataSetChanged();
    }

    // Show the search dialog
    private void showSearchDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.admin_borrow_record_search_dialog, null);
        dialogBuilder.setView(dialogView);

        EditText searchEditText = dialogView.findViewById(R.id.searchEditText);
        Button searchButton = dialogView.findViewById(R.id.searchButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // Handle search button click
        searchButton.setOnClickListener(v -> {
            String searchText = searchEditText.getText().toString().trim();
            if (!searchText.isEmpty()) {
                searchBorrowRecords(searchText);
            }
            alertDialog.dismiss();
        });

        // Handle cancel button click - reset the list to fullBorrowHistoryList
        cancelButton.setOnClickListener(v -> {
            // Reset the list to the default full list
            borrowHistoryList.clear();
            borrowHistoryList.addAll(fullBorrowHistoryList); // Revert to full list
            borrowHistoryAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
        });
    }

    // Search the borrow records based on the search text
    private void searchBorrowRecords(String searchText) {
        borrowHistoryList.clear();  // Clear the current list

        // Perform case-insensitive search through all fields
        for (UserBorrowBookClass record : fullBorrowHistoryList) {
            if (record.getBookTitle().toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT)) ||
                    record.getBookAuthor().toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT)) ||
                    record.getBookGenre().toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT)) ||
                    new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(record.getBorrowTimestamp()).contains(searchText)) {
                // If any field matches, add it to the displayed list
                borrowHistoryList.add(record);
            }
        }

        borrowHistoryAdapter.notifyDataSetChanged();
    }
}
