package com.example.test;

import android.annotation.SuppressLint;
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

public class BorrowHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserBorrowHistoryAdapter adapter;
    private List<BorrowedBook> borrowedBooksList;
    private List<UserBorrowBookClass> borrowedHistoryList;
    private DatabaseReference borrowHistoryRef, userBorrowHistoryRef;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the borrow history list
        borrowedHistoryList = new ArrayList<>();
        //adapter = new UserBorrowHistoryAdapter(borrowedHistoryList);
        recyclerView.setAdapter(adapter);

        // Firebase reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userBorrowHistoryRef = FirebaseDatabase.getInstance().getReference("UserBorrowingHistory")
                    .child(currentUser.getUid());

            loadBorrowHistory();
        }

        return view;
    }

    private void loadBorrowHistory() {
        userBorrowHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                borrowedHistoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserBorrowBookClass borrowRecord = snapshot.getValue(UserBorrowBookClass.class);

                    // Fetch related book details
                    if (borrowRecord != null) {
                        String bookId = borrowRecord.getBookId();
                        FirebaseDatabase.getInstance().getReference("Books")
                                .child(bookId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot bookSnapshot) {
                                        Book book = bookSnapshot.getValue(Book.class);
                                        if (book != null) {
                                            //borrowRecord.setBookTitle(book.getTitle());
                                           // borrowRecord.setBookAuthor(book.getAuthor());
                                            // Notify adapter to update
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                    }
                                });
                    }
                    borrowedHistoryList.add(borrowRecord);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
