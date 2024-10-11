package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder> {

    private String[] genres;
    private Context context;

    public UserSearchAdapter(Context context, String[] genres) {
        this.context = context;
        this.genres = genres;
    }

    @NonNull
    @Override
    public UserSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.genre_item, parent, false);
        return new UserSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSearchViewHolder holder, int position) {
        holder.genreName.setText(genres[position]);

        // Apply different background colors based on genre
        switch (genres[position]) {
            case "Fiction":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.fiction_color));
                break;
            case "Non-fiction":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.non_fiction_color));
                break;
            case "Science":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.science_color));
                break;
            case "Fantasy":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.fantasy_color));
                break;
            case "Biography":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.biography_color));
                break;
            case "Mystery":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.mystery_color));
                break;
            case "Adventure":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.adventure_color));
                break;
            case "Historical":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.historical_color));
                break;
            case "Inspirational":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.inspirational_color));
                break;
            case "Literature":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.literature_color));
                break;
            case "Reference":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.reference_color));
                break;
            case "Short Stories":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.short_stories_color));
                break;
            case "Art":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.art_color));
                break;
            case "Business":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.business_color));
                break;
            case "Chick-lit":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.chick_lit_color));
                break;
            case "Children":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.children_color));
                break;
            case "Christian":
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.christian_color));
                break;
            default:
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white)); // Default color
                break;
        }

        // Handle the click event to navigate to the book list page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserBookListPage.class);
            intent.putExtra("genre", genres[position]);  // Pass the selected genre
            context.startActivity(intent);  // Start the UserBookListPage activity
        });
    }

    @Override
    public int getItemCount() {
        return genres.length;
    }

    public static class UserSearchViewHolder extends RecyclerView.ViewHolder {
        TextView genreName;

        public UserSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.genreName);
        }
    }
}
