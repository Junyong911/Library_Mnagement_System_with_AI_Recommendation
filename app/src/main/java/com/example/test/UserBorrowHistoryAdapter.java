package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UserBorrowHistoryAdapter extends RecyclerView.Adapter<UserBorrowHistoryAdapter.BorrowHistoryViewHolder>{
    private List<BorrowedBook> borrowedBooksList;
    private Context context;

    public UserBorrowHistoryAdapter(Context context, List<BorrowedBook> borrowedBooksList) {
        this.context = context;
        this.borrowedBooksList = borrowedBooksList;
    }

    @NonNull
    @Override
    public BorrowHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrow_history, parent, false);
        return new BorrowHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowHistoryViewHolder holder, int position) {
        BorrowedBook book = borrowedBooksList.get(position);

        holder.bookTitleTextView.setText(book.getTitle());
        holder.bookAuthorGenreTextView.setText("by " + book.getAuthor() + " (" + book.getGenre() + ")");
        holder.bookRatingBar.setRating(book.getRating());

        // Load the book cover image using Picasso
        Picasso.get().load(book.getCoverUrl()).into(holder.bookCoverImageView);
    }

    @Override
    public int getItemCount() {
        return borrowedBooksList.size();
    }

    public static class BorrowHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCoverImageView;
        TextView bookTitleTextView, bookAuthorGenreTextView;
        RatingBar bookRatingBar;

        public BorrowHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.bookCoverImageView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorGenreTextView = itemView.findViewById(R.id.bookAuthorGenreTextView);
            bookRatingBar = itemView.findViewById(R.id.bookRatingBar);
        }
    }
}
