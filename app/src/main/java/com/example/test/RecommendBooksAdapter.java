package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecommendBooksAdapter extends RecyclerView.Adapter<RecommendBooksAdapter.BookViewHolder> {

    private List<Book> books;
    private Context context;

    public RecommendBooksAdapter(List<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bookTitleTextView.setText(book.getTitle());
        holder.bookAuthorTextView.setText("by " + book.getAuthor() + " (" + book.getGenre() + ")");
        holder.ratingTextView.setText(String.format("%.2f", book.getRating())); // Set book rating
        holder.ratingCountsTextView.setText(book.getRatingsCount() + " ratings"); // Set rating count

        // Load the book cover image using Picasso or Glide
        Picasso.get().load(book.getImageUrl()).into(holder.bookCoverImageView);

        // Set up click listener to navigate to book details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserBookDetails.class);
            intent.putExtra("bookId", book.getBookId());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("genre", book.getGenre());
            intent.putExtra("description", book.getDescription());
            intent.putExtra("barcode", book.getBarcode());
            intent.putExtra("status", book.getStatus());
            intent.putExtra("rating", book.getRating());
            intent.putExtra("ratingsCount", book.getRatingsCount());
            intent.putExtra("imageUrl", book.getImageUrl());

            // Start the Book Details activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCoverImageView;
        TextView bookTitleTextView, bookAuthorTextView, ratingTextView, ratingCountsTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.bookCoverImageView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            ratingCountsTextView = itemView.findViewById(R.id.ratingCountsTextView);
        }
    }
}
