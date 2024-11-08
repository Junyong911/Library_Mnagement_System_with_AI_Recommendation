package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Bookmark> bookmarkList;
    private Context context;
    private OnBookmarkClickListener onBookmarkClickListener;

    public BookmarkAdapter(List<Bookmark> bookmarkList, Context context, OnBookmarkClickListener listener) {
        this.bookmarkList = bookmarkList;
        this.context = context;
        this.onBookmarkClickListener = listener;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);
        holder.bookTitleTextView.setText(bookmark.getTitle());
        holder.bookAuthorTextView.setText("by " + bookmark.getAuthor() + "( " +bookmark.getGenre() + " )");

        // Load cover image with Picasso or any other library
        Picasso.get().load(bookmark.getCoverUrl()).into(holder.bookCoverImageView);

        // Set onClick listener for each bookmark item
        holder.itemView.setOnClickListener(v -> onBookmarkClickListener.onBookmarkClick(bookmark));
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCoverImageView;
        TextView bookTitleTextView, bookAuthorTextView;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.bookCoverImageView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
        }
    }

    // Interface for click handling
    public interface OnBookmarkClickListener {
        void onBookmarkClick(Bookmark bookmark);
    }
}
