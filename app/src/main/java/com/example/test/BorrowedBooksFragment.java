package com.example.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BorrowedBooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserBorrowHistoryAdapter adapter;
    private List<UserBorrowingHistory> borrowedBooksList;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrowed_books, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        borrowedBooksList = new ArrayList<>();
        adapter = new UserBorrowHistoryAdapter(getContext(), borrowedBooksList, this); // Pass the fragment to the adapter
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        // Load data from Firebase
        loadBorrowedBooks();

        return view;
    }

    private void loadBorrowedBooks() {
        // Get the current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();  // Get the user's unique ID from FirebaseAuth

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserBorrowingHistory").child(userId);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    borrowedBooksList.clear();
                    for (DataSnapshot bookIdSnapshot : dataSnapshot.getChildren()) {  // Iterate through each bookId
                        for (DataSnapshot borrowRecordSnapshot : bookIdSnapshot.getChildren()) {  // Iterate through each borrowRecordId
                            UserBorrowingHistory history = borrowRecordSnapshot.getValue(UserBorrowingHistory.class);
                            if (history != null) {
                                borrowedBooksList.add(history);  // Add the history to the list
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();  // Update the adapter after data retrieval
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });
        }
    }

    // Method to handle returning a book
    public void returnBook(UserBorrowingHistory history) {
        // Update the book's status back to "available"
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Books").child(history.getBookId());
        bookRef.child("status").setValue("available");

        // Move the borrow history to UserBorrowingHistoryPermanent
        DatabaseReference permanentHistoryRef = FirebaseDatabase.getInstance()
                .getReference("UserBorrowingHistoryPermanent")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(history.getBookId())
                .child(history.getBorrowId());

        UserBorrowingHistoryPermanent permanentHistory = new UserBorrowingHistoryPermanent(
                history.getBorrowId(),
                history.getBookId(),
                history.getBorrowTimestamp(),
                history.getBookTitle(),
                history.getBookAuthor(),
                history.getBookRating(),
                history.getBookReviewsCount(),
                history.getBookCoverUrl(),
                history.getBookGenre()
        );

        permanentHistoryRef.setValue(permanentHistory)
                .addOnSuccessListener(aVoid -> {
                    // Remove the borrowed book from the user's current borrowing list
                    removeBorrowedBook(history);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    // Method to remove the borrowed book from the UserBorrowingHistory after returning
    private void removeBorrowedBook(UserBorrowingHistory history) {
        DatabaseReference userBorrowHistoryRef = FirebaseDatabase.getInstance()
                .getReference("UserBorrowingHistory")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(history.getBookId())
                .child(history.getBorrowId());

        userBorrowHistoryRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Success message, can add Toast here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle error during deletion
                });
    }
}