package com.example.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminBorrowRecordAdapter extends RecyclerView.Adapter<AdminBorrowRecordAdapter.BorrowViewHolder> {

    private List<UserBorrowBookClass> borrowList;
    private Context context;

    public AdminBorrowRecordAdapter(List<UserBorrowBookClass> borrowList, Context context) {
        this.borrowList = borrowList;
        this.context = context;
    }

    @NonNull
    @Override
    public BorrowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_borrow_dashboard, parent, false);
        return new BorrowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowViewHolder holder, int position) {
        UserBorrowBookClass borrowRecord = borrowList.get(position);

        // Fetch the username based on the userId
        fetchUsername(borrowRecord.getUserId(), holder.userNameTextView);

        holder.bookTitleTextView.setText(borrowRecord.getBookTitle());
        holder.bookGenreTextView.setText(borrowRecord.getBookGenre());
        holder.bookAuthorTextView.setText(borrowRecord.getBookAuthor());

        // Convert timestamp to readable date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(borrowRecord.getBorrowTimestamp()));
        holder.borrowTimestampTextView.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    public static class BorrowViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, bookTitleTextView, bookGenreTextView, bookAuthorTextView, borrowTimestampTextView;

        public BorrowViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView); // Add this to your XML
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookGenreTextView = itemView.findViewById(R.id.bookGenreTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            borrowTimestampTextView = itemView.findViewById(R.id.borrowTimestampTextView);
        }
    }

    // Function to fetch the username from the Firebase Users node
    private void fetchUsername(String userId, final TextView usernameTextView) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    usernameTextView.setText(username);  // Set the username in the TextView
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                usernameTextView.setText("Unknown User");
            }
        });
    }
}
