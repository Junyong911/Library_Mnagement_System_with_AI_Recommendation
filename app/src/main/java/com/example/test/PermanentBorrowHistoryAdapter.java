package com.example.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PermanentBorrowHistoryAdapter extends RecyclerView.Adapter<PermanentBorrowHistoryAdapter.PermanentBorrowHistoryViewHolder> {
    private List<UserBorrowingHistoryPermanent> historyList;
    private Context context;

    public PermanentBorrowHistoryAdapter(List<UserBorrowingHistoryPermanent> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public PermanentBorrowHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_borrow_history_permanent_item, parent, false);
        return new PermanentBorrowHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PermanentBorrowHistoryViewHolder holder, int position) {
        UserBorrowingHistoryPermanent history = historyList.get(position);
        holder.bookTitleTextView.setText(history.getBookTitle());
        holder.bookAuthorTextView.setText("by " + history.getBookAuthor() + "( " +history.getBookGenre() + " )");
        holder.ratingTextView.setText(history.getBookRating());
        String formattedDate = formatDate(history.getBorrowTimestamp());
        holder.borrowDateTextView.setText("Borrow Date: " + formattedDate);

        // Load book cover using Picasso or Glide
        Picasso.get().load(history.getBookCoverUrl()).into(holder.bookCoverImageView);
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class PermanentBorrowHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCoverImageView;
        TextView bookTitleTextView, bookAuthorTextView, borrowDateTextView, ratingTextView;

        public PermanentBorrowHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.bookCoverImageView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            borrowDateTextView = itemView.findViewById(R.id.borrowDateTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }
}

