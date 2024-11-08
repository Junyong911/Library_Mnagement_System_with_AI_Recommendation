package com.example.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserBookListPage extends AppCompatActivity {

    private RecyclerView bookRecyclerView;
    private UserBookAdapter userBookAdapter;
    private List<Book> bookList, originalBookList;
    private String selectedGenre;
    private Spinner sortSpinner;
    private String currentSortOption = "Title";  // Default sorting by title

    private LinearLayout alphabetScroller;
    private TextView selectedAlphabetOverlay;
    private ImageButton searchIcon, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_book_list);

        // Get the selected genre from the intent
        selectedGenre = getIntent().getStringExtra("genre");

        TextView genreHeaderTextView = findViewById(R.id.genreHeader);
        genreHeaderTextView.setText(selectedGenre + " Books");

        // Initialize RecyclerView and Adapter
        bookRecyclerView = findViewById(R.id.bookRecyclerView);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookRecyclerView.addItemDecoration(new DividerItemDecoration(bookRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        bookList = new ArrayList<>();
        originalBookList = new ArrayList<>();
        userBookAdapter = new UserBookAdapter(bookList);
        bookRecyclerView.setAdapter(userBookAdapter);

        // Initialize the Spinner for sorting
        sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_book_list_sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // Spinner onItemSelectedListener for sorting books
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortOption = parent.getItemAtPosition(position).toString();
                sortBooks();  // Sort books whenever a sort option is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Alphabet Scroller
        alphabetScroller = findViewById(R.id.alphabet_scroller);
        selectedAlphabetOverlay = findViewById(R.id.selected_alphabet_overlay);

        // Add A-Z alphabet dynamically
        final List<TextView> alphabetTextViews = new ArrayList<>();
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            TextView letterTextView = new TextView(this);
            letterTextView.setText(String.valueOf(alphabet));
            letterTextView.setTextSize(16);
            letterTextView.setPadding(4, 4, 4, 4);  // Adjust padding for aesthetics
            letterTextView.setTextColor(getResources().getColor(android.R.color.black)); // Adjust color if needed

            // Add each letter to the LinearLayout
            alphabetScroller.addView(letterTextView);
            alphabetTextViews.add(letterTextView);
        }

        // Add touch listener for scrolling
        alphabetScroller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float y = event.getY();
                int scrollerHeight = alphabetScroller.getHeight();
                int alphabetIndex = (int) ((y / scrollerHeight) * alphabetTextViews.size());

                if (alphabetIndex >= 0 && alphabetIndex < alphabetTextViews.size()) {
                    String selectedAlphabet = alphabetTextViews.get(alphabetIndex).getText().toString();

                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            showSelectedAlphabetOverlay(selectedAlphabet);
                            scrollToBookWithAlphabet(selectedAlphabet); // Function to scroll based on selected letter
                            break;
                        case MotionEvent.ACTION_UP:
                            hideSelectedAlphabetOverlay();
                            break;
                    }
                }
                return true;
            }
        });

        // Load books from Firebase based on the selected genre
        loadBooksFromFirebase();

        searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@android.support.annotation.NonNull MenuItem item) {


                // Use if-else instead of switch-case
                if (item.getItemId() == R.id.home) {
                    // Stay on Home
                    Intent intent = new Intent(UserBookListPage.this, UserHomePage.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.book) {
                    // Navigate to Book Page
                    Intent intent = new Intent(UserBookListPage.this, UserMyBooksPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.search) {
                    Intent intent = new Intent(UserBookListPage.this, UserSearchPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.setting) {
                    // Navigate to Settings Page
                    Intent intent = new Intent(UserBookListPage.this, UserSearchPage.class);
                    startActivity(intent);
                }
                return false;
            }


        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserBookListPage.this, UserSearchPage.class);
                startActivity(intent);
            }
        });

    }

    private void sortBooks() {
        switch (currentSortOption) {
            case "Title":
                Collections.sort(bookList, (book1, book2) -> book1.getTitle().compareToIgnoreCase(book2.getTitle()));
                break;
            case "Author":
                Collections.sort(bookList, (book1, book2) -> book1.getAuthor().compareToIgnoreCase(book2.getAuthor()));
                break;
        }
        userBookAdapter.notifyDataSetChanged();
    }

    private void loadBooksFromFirebase() {
        FirebaseDatabase.getInstance("https://librarymanagementsystemai-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookList.clear();
                        originalBookList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Book book = snapshot.getValue(Book.class);
                            if (book != null && book.getGenre().equalsIgnoreCase(selectedGenre)) {
                                bookList.add(book);
                                originalBookList.add(book);  // Store both lists
                            }
                        }

                        // If no books match the selected genre, show a message
                        if (bookList.isEmpty()) {
                            Toast.makeText(UserBookListPage.this, "No books found for this genre.", Toast.LENGTH_SHORT).show();
                        }

                        userBookAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UserBookListPage.this, "Failed to load books", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Function to scroll to the book that starts with the selected alphabet
    private void scrollToBookWithAlphabet(String selectedAlphabet) {
        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);
            String comparisonValue;
            switch (currentSortOption) {
                case "Title":
                    comparisonValue = book.getTitle();
                    break;
                case "Author":
                    comparisonValue = book.getAuthor();
                    break;
                default:
                    comparisonValue = book.getTitle(); // Default to title
                    break;
            }

            if (comparisonValue.toUpperCase().startsWith(selectedAlphabet.toUpperCase())) {
                // Scroll to the book position in the RecyclerView
                ((LinearLayoutManager) bookRecyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                return; // Exit once the first match is found
            }
        }
    }

    private void showSelectedAlphabetOverlay(String alphabet) {
        selectedAlphabetOverlay.setText(alphabet);
        selectedAlphabetOverlay.setVisibility(View.VISIBLE);
    }

    private void hideSelectedAlphabetOverlay() {
        selectedAlphabetOverlay.setVisibility(View.GONE);
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate custom layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.user_book_list_dialog_search, null);
        builder.setView(dialogView);

        final EditText searchInput = dialogView.findViewById(R.id.searchInput);

        // Set up the dialog buttons
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchQuery = searchInput.getText().toString().trim();
                searchBooks(searchQuery);  // Call searchBooks function
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetBookList();  // Reset the list if canceled
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Filter books based on search query (only by title and author)
    private void searchBooks(String query) {
        List<Book> filteredList = new ArrayList<>();

        for (Book book : originalBookList) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }

        // Update the RecyclerView with the filtered list
        userBookAdapter.updateBookList(filteredList);
    }

    // Function to reset the book list after search is canceled or completed
    private void resetBookList() {
        userBookAdapter.updateBookList(originalBookList);  // Restore the original list
    }
}
