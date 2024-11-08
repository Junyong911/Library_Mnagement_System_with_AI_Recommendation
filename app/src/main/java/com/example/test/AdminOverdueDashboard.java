package com.example.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.List;

public class AdminOverdueDashboard extends AppCompatActivity {

    private RecyclerView recyclerViewOverdueBooks;
    private AdminOverdueAdapter overdueAdapter;
    private List<UserBorrowingHistory> overdueBookList;
    private DatabaseReference userBorrowHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_overdue_dashboard);

        recyclerViewOverdueBooks = findViewById(R.id.recyclerViewOverdueBooks);
        recyclerViewOverdueBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOverdueBooks.addItemDecoration(new DividerItemDecoration(recyclerViewOverdueBooks.getContext(), LinearLayoutManager.VERTICAL));

        overdueBookList = new ArrayList<>();
        overdueAdapter = new AdminOverdueAdapter(overdueBookList, this);
        recyclerViewOverdueBooks.setAdapter(overdueAdapter);

        userBorrowHistoryRef = FirebaseDatabase.getInstance().getReference("UserBorrowingHistory");

        fetchOverdueBooks();
    }

    private void fetchOverdueBooks() {
        userBorrowHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                overdueBookList.clear();
                long currentTime = System.currentTimeMillis();

                // Iterate over each user ID in UserBorrowingHistory
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Iterate over each book borrowed by the user
                    for (DataSnapshot bookSnapshot : userSnapshot.getChildren()) {
                        for (DataSnapshot recordSnapshot : bookSnapshot.getChildren()) {
                            UserBorrowingHistory borrowingHistory = recordSnapshot.getValue(UserBorrowingHistory.class);

                            if (borrowingHistory != null) {
                                long borrowTime = borrowingHistory.getBorrowTimestamp();
                                long minutesOverdue = (currentTime - borrowTime) / (1000 * 60);

                                if (minutesOverdue > 5) { // Check if it's overdue by more than 5 minutes
                                    overdueBookList.add(borrowingHistory);
                                }
                            }
                        }
                    }
                }
                overdueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminOverdueDashboard.this, "Failed to load overdue books.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
