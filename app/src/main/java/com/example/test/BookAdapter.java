package com.example.test;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.titleTextView.setText("Title: " + book.getTitle());
        holder.authorTextView.setText("Author: " + book.getAuthor());
        holder.genreTextView.setText("Genre: " + book.getGenre());

        // Load book cover image
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Picasso.get().load(book.getImageUrl()).into(holder.bookCoverImageView);
        }

        // Set an OnClickListener to navigate to the edit page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), AdminEditBookPage.class);
            intent.putExtra("bookId", book.getBookId());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("genre", book.getGenre());
            intent.putExtra("isbn", book.getIsbn());
            intent.putExtra("description", book.getDescription());
            intent.putExtra("imageUrl", book.getImageUrl());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {

        ImageView bookCoverImageView;
        TextView titleTextView, authorTextView, genreTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.bookCoverImageView);
            titleTextView = itemView.findViewById(R.id.bookTitleTextView);
            authorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            genreTextView = itemView.findViewById(R.id.bookGenreTextView);
        }
    }

    public void updateBookList(List<Book> filteredList) {
        bookList.clear();
        bookList.addAll(filteredList);
        notifyDataSetChanged();
    }
}

