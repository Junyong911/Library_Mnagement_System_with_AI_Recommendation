package com.example.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class UserPermanentBorrowHistory extends AppCompatActivity {
    private RecyclerView borrowHistoryRecyclerView;
    private PermanentBorrowHistoryAdapter adapter;
    private List<UserBorrowingHistoryPermanent> borrowHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_borrow_history_permanent);

        // Initialize RecyclerView
        borrowHistoryRecyclerView = findViewById(R.id.bookRecyclerView);
        borrowHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        borrowHistoryList = new ArrayList<>();
        adapter = new PermanentBorrowHistoryAdapter(borrowHistoryList, this);
        borrowHistoryRecyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchBorrowHistory();
    }

    private void fetchBorrowHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            DatabaseReference historyRef = FirebaseDatabase.getInstance()
                    .getReference("UserBorrowingHistoryPermanent")
                    .child(userId);

            historyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    borrowHistoryList.clear();
                    for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot historySnapshot : bookSnapshot.getChildren()) {
                            UserBorrowingHistoryPermanent history = historySnapshot.getValue(UserBorrowingHistoryPermanent.class);
                            borrowHistoryList.add(history);
                        }
                    }
                    adapter.notifyDataSetChanged();  // Refresh the adapter
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserPermanentBorrowHistory.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
