package com.example.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends Fragment {

    private RecyclerView recyclerViewRecommendedBooks;
    private RecommendBooksAdapter adapter;
    private List<Book> recommendedBooksList;
    private String userId;
    private TextView userPreferenceTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_books, container, false);

        // Initialize RecyclerView
        recyclerViewRecommendedBooks = view.findViewById(R.id.recyclerView);
        recyclerViewRecommendedBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        recommendedBooksList = new ArrayList<>();
        adapter = new RecommendBooksAdapter(recommendedBooksList, getContext());
        recyclerViewRecommendedBooks.setAdapter(adapter);
        userPreferenceTextView = view.findViewById(R.id.userPreferenceTextView);

        // Retrieve userId from Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Get the logged-in user's UID
            fetchUserRecommendations(userId);
        } else {
            // Handle case where no user is logged in (navigate to login page, show error, etc.)
            // Example: show a message or redirect to login
        }

        return view;
    }

    private void fetchUserRecommendations(String userId) {
        DatabaseReference recommendationsRef = FirebaseDatabase.getInstance().getReference("Recommendations").child(userId);
        recommendationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String recommendedAuthor = dataSnapshot.child("recommended_author").getValue(String.class);
                    String recommendedGenre = dataSnapshot.child("recommended_genre").getValue(String.class);

                    // Set the user preferences in the TextView
                    String userPreferenceText = String.format("Based on your history, you seem to like %s books and books by %s.", recommendedGenre, recommendedAuthor);
                    userPreferenceTextView.setText(userPreferenceText);
                    fetchRecommendedBooks(recommendedAuthor, recommendedGenre);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void fetchRecommendedBooks(String recommendedAuthor, String recommendedGenre) {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("Books");
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recommendedBooksList.clear(); // Clear previous data

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    String bookAuthor = bookSnapshot.child("author").getValue(String.class);
                    String bookGenre = bookSnapshot.child("genre").getValue(String.class);

                    if (bookAuthor.equals(recommendedAuthor) || bookGenre.equals(recommendedGenre)) {
                        // Get book details
                        String bookTitle = bookSnapshot.child("title").getValue(String.class);
                        String bookCoverUrl = bookSnapshot.child("imageUrl").getValue(String.class);
                        String bookId = bookSnapshot.child("bookId").getValue(String.class);
                        String status = bookSnapshot.child("status").getValue(String.class);
                        //String genre = bookSnapshot.child("genre").getValue((String.class);
                        float rating = bookSnapshot.child("rating").getValue(Float.class);
                        int ratingsCount = bookSnapshot.child("ratingsCount").getValue(Integer.class);

                        // Create a Book object and add it to the list
                        Book book = new Book(bookId, bookTitle, bookGenre, bookAuthor, "", bookTitle, bookCoverUrl, "", status, rating, ratingsCount);
                        recommendedBooksList.add(book);
                    }
                }

                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
