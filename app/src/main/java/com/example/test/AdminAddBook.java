package com.example.test;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminAddBook extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for picking image
    private static final int PICK_BARCODE_REQUEST = 2;  // Request code for barcode scanner

    private ImageView bookCoverImageView;
    private EditText titleEditText, authorEditText, isbnEditText, descriptionEditText;
    private ImageButton backButton, barcodeScanButton;
    private Button addBookButton;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private BookApi bookApi;
    private Spinner genreSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_book_page);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://librarymanagementsystemai-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Books");
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize Retrofit for Open Library API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openlibrary.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        bookApi = retrofit.create(BookApi.class);

        // Initialize UI components
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        titleEditText = findViewById(R.id.titleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        isbnEditText = findViewById(R.id.isbnEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addBookButton = findViewById(R.id.addBookButton);
        backButton = findViewById(R.id.backButton);
        barcodeScanButton = findViewById(R.id.barcodeIcon);

        // Initialize genre spinner
        genreSpinner = findViewById(R.id.genreSpinner);
        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(
                this, R.array.genre_array, android.R.layout.simple_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);
        int defaultPosition = genreAdapter.getPosition("Science");
        genreSpinner.setSelection(defaultPosition);

        // Set up click listener for selecting book cover manually
        bookCoverImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Set up barcode scan button to open barcode scanner
        barcodeScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddBook.this, BarcodeScanner.class);
                startActivityForResult(intent, PICK_BARCODE_REQUEST);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddBook.this, AdminHomePage.class);
                startActivity(intent);
            }
        });

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToFirebase();
            }
        });
    }

    // Function to open image picker for selecting book cover
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            bookCoverImageView.setImageURI(imageUri);  // Display the selected image
            bookCoverImageView.setTag(null);  // Clear any previously set API cover URL
        }

        if (requestCode == PICK_BARCODE_REQUEST && resultCode == RESULT_OK && data != null) {
            String barcode = data.getStringExtra("BARCODE");

            // Log the scanned barcode for debugging
            Log.d("Barcode", "Scanned ISBN: " + barcode);
            Toast.makeText(this, "Scanned ISBN: " + barcode, Toast.LENGTH_LONG).show();

            if (barcode != null && !barcode.isEmpty()) {
                fetchBookDetails(barcode);  // Fetch book details using the barcode
            } else {
                Log.e("Barcode", "No valid barcode scanned.");
                Toast.makeText(this, "No valid barcode scanned.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to generate a unique barcode for the book and set the status as "available"
    private void addBookToFirebase() {
        final String title = titleEditText.getText().toString().trim();
        String genre = genreSpinner.getSelectedItem().toString();
        final String author = authorEditText.getText().toString().trim();
        final String isbn = isbnEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();

        final String coverUrl = bookCoverImageView.getTag() != null ? bookCoverImageView.getTag().toString() : null;

        if (title.isEmpty() || genre.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique book ID (this can serve as the unique barcode)
        String bookId = databaseReference.push().getKey();
        String uniqueBarcode = generateUniqueBarcode(bookId);

        // Set book status as "available"
        String status = "available";

        // Check if an image was selected manually
        if (imageUri != null) {
            uploadImageAndSaveBook(bookId, title, genre, author, isbn, description, uniqueBarcode, status, null);
        } else if (coverUrl != null) {
            saveBookToDatabase(bookId, title, genre, author, isbn, description, uniqueBarcode, status, coverUrl);
        } else {
            saveBookToDatabase(bookId, title, genre, author, isbn, description, uniqueBarcode, status, null);
        }
    }

    // Method to generate a unique barcode using bookId
    private String generateUniqueBarcode(String bookId) {
        return "BARCODE-" + bookId;  // Customize the format if needed
    }

    private void uploadImageAndSaveBook(final String bookId, final String title, final String genre, final String author, final String isbn, final String description, final String barcode, final String status, @Nullable final String openLibraryCoverUrl) {
        StorageReference fileReference = storageReference.child("Books").child(bookId + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        saveBookToDatabase(bookId, title, genre, author, isbn, description, barcode, status, imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminAddBook.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBookToDatabase(String bookId, String title, String genre, String author, String isbn, String description, String barcode, String status, @Nullable String imageUrl) {
        float rating = 0.0f;
        int ratingsCount = 0;
        Book book = new Book(bookId, title, genre, author, isbn, description, imageUrl, barcode, status, rating, ratingsCount);
        databaseReference.child(bookId).setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminAddBook.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminAddBook.this, "Failed to add book", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Fetch book details using the Open Library API
    private void fetchBookDetails(String barcode) {
        Log.d("API Request", "Fetching book details for ISBN: " + barcode);

        Call<Map<String, BookResponse>> call = bookApi.getBookDetails("ISBN:" + barcode, "json", "data");

        call.enqueue(new Callback<Map<String, BookResponse>>() {
            @Override
            public void onResponse(Call<Map<String, BookResponse>> call, Response<Map<String, BookResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BookResponse bookItem = response.body().get("ISBN:" + barcode);

                    if (bookItem != null) {
                        String title = bookItem.title;
                        String author = bookItem.authors != null && !bookItem.authors.isEmpty() ? bookItem.authors.get(0).name : "Unknown";
                        String coverUrl = bookItem.cover != null ? bookItem.cover.large : null;
                        String description = bookItem.description != null ? bookItem.description : "No Description Available";

                        titleEditText.setText(title);
                        authorEditText.setText(author);
                        isbnEditText.setText(barcode);
                        descriptionEditText.setText(description);

                        if (coverUrl != null) {
                            Picasso.get().load(coverUrl).into(bookCoverImageView);
                            bookCoverImageView.setTag(coverUrl);  // Store the API cover URL in the tag
                        }
                    } else {
                        Toast.makeText(AdminAddBook.this, "Book not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminAddBook.this, "Failed to retrieve book details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, BookResponse>> call, Throwable t) {
                Toast.makeText(AdminAddBook.this, "Failed to connect to Open Library API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
