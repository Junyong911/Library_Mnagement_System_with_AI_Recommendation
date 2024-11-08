package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserBorrowHistoryAdapter extends RecyclerView.Adapter<UserBorrowHistoryAdapter.BorrowHistoryViewHolder> {

    private Context context;
    private List<UserBorrowingHistory> borrowedBooksList;
    private BorrowedBooksFragment fragment;

    public UserBorrowHistoryAdapter(Context context, List<UserBorrowingHistory> borrowedBooksList, BorrowedBooksFragment fragment) {
        this.context = context;
        this.borrowedBooksList = borrowedBooksList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public BorrowHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrow_history, parent, false);
        return new BorrowHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowHistoryViewHolder holder, int position) {
        UserBorrowingHistory history = borrowedBooksList.get(position);

        holder.bookTitleTextView.setText(history.getBookTitle());
        holder.bookAuthorTextView.setText("by " + history.getBookAuthor());
        //holder.bookRatingValue.setText(history.getBookRating());
        //holder.bookReviewsCount.setText(history.getBookReviewsCount() + " ratings");

        // Load book cover image using Glide
        Glide.with(context).load(history.getBookCoverUrl()).into(holder.bookCoverImageView);

        // Calculate due date (7 days from borrow date) and check if overdue
        long borrowTimestamp = history.getBorrowTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(borrowTimestamp);
        calendar.add(Calendar.DAY_OF_YEAR, 7); // Add 7 days to the borrow date

        Date dueDate = calendar.getTime();
        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        if (currentDate.after(dueDate)) {
            // If the current date is after the due date, mark it as overdue
            holder.dueDateTextView.setText("Due Date: Overdue");
            holder.dueDateTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            // Otherwise, display the due date
            holder.dueDateTextView.setText("Due Date: " + dateFormat.format(dueDate));
            holder.dueDateTextView.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        // Set up the return button
        holder.returnButton.setOnClickListener(v -> {
            showReturnConfirmationDialog(history);
        });
    }

    @Override
    public int getItemCount() {
        return borrowedBooksList.size();
    }

    // Show confirmation dialog for returning the book
    private void showReturnConfirmationDialog(UserBorrowingHistory history) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Return Book");
        builder.setMessage("Are you sure you want to return this book?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Generate the QR code and show to the user for admin scanning
                showQRCodeForReturn(history);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    // Method to generate and display a QR code based on the borrowId and bookId
    private void showQRCodeForReturn(UserBorrowingHistory history) {
        AlertDialog.Builder qrDialog = new AlertDialog.Builder(context);
        qrDialog.setTitle("Scan this QR Code to Return the Book");

        // Create an ImageView to display the QR code
        ImageView qrCodeImageView = new ImageView(context);

        // Generate the QR code data using only borrowId
        String qrData = history.getBorrowId();
        Log.d("UserBorrowHistoryAdapter", "QR Data: " + qrData); // Log the QR data for debugging

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 600, 600); // Generate QR code
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        qrDialog.setView(qrCodeImageView);
        qrDialog.setPositiveButton("Done", null);
        qrDialog.show();
    }

    public static class BorrowHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCoverImageView, starIcon;
        TextView bookTitleTextView, bookAuthorTextView, bookRatingValue, bookReviewsCount, dueDateTextView;
        AppCompatButton returnButton;

        public BorrowHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.bookCoverImageView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            bookRatingValue = itemView.findViewById(R.id.bookRatingValue);
            bookReviewsCount = itemView.findViewById(R.id.bookReviewsCount);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView); // Add dueDateTextView
            starIcon = itemView.findViewById(R.id.starIcon);
            returnButton = itemView.findViewById(R.id.returnButton); // Ensure this ID matches your layout
        }
    }
}