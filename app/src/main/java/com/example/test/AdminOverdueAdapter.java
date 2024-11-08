package com.example.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOverdueAdapter extends RecyclerView.Adapter<AdminOverdueAdapter.OverdueViewHolder> {

    private List<UserBorrowingHistory> overdueList;
    private Context context;

    public AdminOverdueAdapter(List<UserBorrowingHistory> overdueList, Context context) {
        this.overdueList = overdueList;
        this.context = context;
    }

    @NonNull
    @Override
    public OverdueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_overdue_dashboard, parent, false);
        return new OverdueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OverdueViewHolder holder, int position) {
        UserBorrowingHistory overdueRecord = overdueList.get(position);

        holder.bookTitleTextView.setText(overdueRecord.getBookTitle());

        // Format and display the borrow timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(overdueRecord.getBorrowTimestamp()));
        holder.borrowTimestampTextView.setText(formattedDate);

        // Calculate and display overdue days
        long daysOverdue = (System.currentTimeMillis() - overdueRecord.getBorrowTimestamp()) / (1000 * 60 * 60 * 24);
        holder.daysOverdueTextView.setText(String.valueOf(daysOverdue) + " days overdue");
    }

    @Override
    public int getItemCount() {
        return overdueList.size();
    }

    public static class OverdueViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitleTextView, borrowTimestampTextView, daysOverdueTextView;

        public OverdueViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            borrowTimestampTextView = itemView.findViewById(R.id.borrowTimestampTextView);
            daysOverdueTextView = itemView.findViewById(R.id.daysOverdueTextView);
        }
    }
}
