package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private List<Bookmark> bookmarkList;
    private DatabaseReference bookmarksRef;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks_books, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookmarkList = new ArrayList<>();
        adapter = new BookmarkAdapter(bookmarkList, getContext(), this::onBookmarkClicked);
        recyclerView.setAdapter(adapter);

        // Firebase setup
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        bookmarksRef = FirebaseDatabase.getInstance().getReference("Bookmarks").child(userId);

        // Load bookmarks from Firebase
        loadBookmarks();

        return view;
    }

    private void loadBookmarks() {
        bookmarksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookmarkList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bookmark bookmark = snapshot.getValue(Bookmark.class);
                    bookmarkList.add(bookmark);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    // Handle bookmark click to navigate to Book Details page
    private void onBookmarkClicked(Bookmark bookmark) {
        Intent intent = new Intent(getActivity(), UserBookDetails.class);
        intent.putExtra("bookId", bookmark.getBookId());
        intent.putExtra("title", bookmark.getTitle());
        intent.putExtra("author", bookmark.getAuthor());
        intent.putExtra("genre", bookmark.getGenre());
        //intent.putExtra("description", bookmark.getDescription()); // Pass other details as needed
        intent.putExtra("coverUrl", bookmark.getCoverUrl());
        intent.putExtra("rating", bookmark.getRating());
        intent.putExtra("ratingsCount", bookmark.getRatingsCount());
        startActivity(intent);
    }
}
