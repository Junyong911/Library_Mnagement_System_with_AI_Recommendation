package com.example.test;

import android.content.Intent;
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

public class UserBookAdapter extends RecyclerView.Adapter<UserBookAdapter.BookViewHolder> {

    private List<Book> bookList;

    public UserBookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Set the book title and author
        holder.bookTitleTextView.setText(book.getTitle());
        holder.bookAuthorTextView.setText("by " + book.getAuthor() + " (" + book.getGenre() + ")");

        // Fetch and display the book rating and rating count
        if (book.getRating() > 0 && book.getRatingsCount() > 0) {
            holder.ratingTextView.setText(String.format("%.2f", book.getRating()) );
            holder.bookReviewsCount.setText(" • " + book.getRatingsCount() + " ratings");
            //holder.bookRatingBar.setRating(book.getRating()); // Set rating in RatingBar
        } else {
            holder.ratingTextView.setText("No ratings yet");
            //holder.bookRatingBar.setRating(0); // Set default rating
        }

        // Load the book cover image using Picasso
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Picasso.get().load(book.getImageUrl()).into(holder.bookCoverImageView);
        }

        // Set up OnClickListener for navigating to the book details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), UserBookDetails.class);
            intent.putExtra("bookId", book.getBookId());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("genre", book.getGenre());
            intent.putExtra("description", book.getDescription());
            intent.putExtra("rating", book.getRating());
            intent.putExtra("ratingsCount", book.getRatingsCount());
            intent.putExtra("imageUrl", book.getImageUrl());
            intent.putExtra("barcode", book.getBarcode());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {

        ImageView bookCoverImageView;
        TextView bookTitleTextView, bookAuthorTextView, ratingTextView, bookReviewsCount;
        RatingBar bookRatingBar; // Add RatingBar for average rating

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.bookCoverImageView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            bookReviewsCount = itemView.findViewById(R.id.bookReviewsCount);
            //bookRatingBar = itemView.findViewById(R.id.bookRatingBar); // Initialize RatingBar
        }
    }

    public void updateBookList(List<Book> filteredList) {
        bookList.clear();
        bookList.addAll(filteredList);
        notifyDataSetChanged();
    }
}
