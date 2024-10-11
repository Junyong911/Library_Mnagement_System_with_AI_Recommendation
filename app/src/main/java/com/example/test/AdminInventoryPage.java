package com.example.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminInventoryPage extends AppCompatActivity {

    private Spinner sortSpinner;
    private RecyclerView bookRecyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private ImageButton backButton, searchIcon;
    private LinearLayout alphabetScroller;
    private TextView selectedAlphabetOverlay;
    private String currentSortOption = "Title";  // Default sorting by title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_inventory_page);

        // Initialize the Spinner
        sortSpinner = findViewById(R.id.sortSpinner);
        bookRecyclerView = findViewById(R.id.bookRecyclerView);

        // Initialize book list and adapter
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList);

        // Set up RecyclerView
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookRecyclerView.setAdapter(bookAdapter);

        // Add divider between book items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(bookRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(new ColorDrawable(Color.BLACK));  // Set divider color to black
        bookRecyclerView.addItemDecoration(dividerItemDecoration);

        // Populate the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // Spinner onItemSelectedListener
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortOption = parent.getItemAtPosition(position).toString(); // Get the selected option
                loadBooksFromFirebase();  // Reload and sort books based on selected option
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }


        });

        // Load books from Firebase
        loadBooksFromFirebase();

        // Back button functionality
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate to Admin Home Page
            Intent intent = new Intent(AdminInventoryPage.this, AdminHomePage.class);
            startActivity(intent);
        });

        // Initialize Alphabet Scroller and Overlay
        alphabetScroller = findViewById(R.id.alphabet_scroller);
        selectedAlphabetOverlay = findViewById(R.id.selected_alphabet_overlay);

        // Add A-Z alphabet dynamically
        final List<TextView> alphabetTextViews = new ArrayList<>();
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            TextView letterTextView = new TextView(this);
            letterTextView.setText(String.valueOf(alphabet));
            letterTextView.setTextSize(16);
            letterTextView.setGravity(Gravity.CENTER);
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

        searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the search dialog or input field
                showSearchDialog();
            }
        });
    }

    private void showSelectedAlphabetOverlay(String alphabet) {
        selectedAlphabetOverlay.setText(alphabet);
        selectedAlphabetOverlay.setVisibility(View.VISIBLE);
    }

    private void hideSelectedAlphabetOverlay() {
        selectedAlphabetOverlay.setVisibility(View.GONE);
    }

    private void loadBooksFromFirebase() {
        FirebaseDatabase.getInstance("https://librarymanagementsystemai-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Book book = snapshot.getValue(Book.class);
                            bookList.add(book);
                        }

                        // Sort bookList based on the selected sort option
                        sortBooks();

                        bookAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AdminInventoryPage.this, "Failed to load books", Toast.LENGTH_SHORT).show();
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
            case "Genre":
                Collections.sort(bookList, (book1, book2) -> book1.getGenre().compareToIgnoreCase(book2.getGenre()));
                break;
        }
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
                case "Genre":
                    comparisonValue = book.getGenre();
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

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setView(dialogView);

        final EditText searchInput = dialogView.findViewById(R.id.searchInput);

        // Set up the buttons
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchQuery = searchInput.getText().toString().trim();
                searchBooks(searchQuery);  // Perform search based on the query
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Optional: You can customize the button colors here if needed
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_blue_light));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
    }


    private void searchBooks(String query) {
        List<Book> filteredList = new ArrayList<>();

        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                    book.getGenre().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }

        // Update the RecyclerView with the filtered list
        bookAdapter.updateBookList(filteredList);
    }




}
