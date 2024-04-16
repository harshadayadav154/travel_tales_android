package com.example.travel_tales.adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_tales.R;
import com.example.travel_tales.databinding.JournalEntryCardBinding;
import com.example.travel_tales.models.JournalEntry;
import com.example.travel_tales.utility.DateUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Adapter class for displaying journal items in a RecyclerView.
 *
 * @author Nabin Ghatani 2024-04-16
 */
public class JournalListAdapter extends RecyclerView.Adapter<JournalListAdapter.JournalEntryViewHolder> {

    private List<JournalEntry> journalList;
    ExecutorService executor;

    /**
     * Constructor to initialize the list of journal items and the fragment type.
     */
    public JournalListAdapter() {
        this.journalList = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public JournalEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout using view binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        JournalEntryCardBinding binding = JournalEntryCardBinding.inflate(layoutInflater, parent, false);
        return new JournalEntryViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull JournalEntryViewHolder holder, int position) {
        // Retrieving the journal entry at the given position
        JournalEntry journal = journalList.get(position);

        // Loading image asynchronously if available, otherwise set default image
        if (!journal.getImagePaths().isEmpty()) {
            // Asynchronously loading the image using an executor
            executor.execute(() -> {
                // Decoding the bitmap from the first image path
                Bitmap bitmap = BitmapFactory.decodeFile(journal.getImagePaths().get(0));
                // Setting the bitmap on the ImageView on the UI thread
                holder.binding.imageViewJournal.post(() -> holder.binding.imageViewJournal.setImageBitmap(bitmap));
            });
        } else {
            // Setting the default image on the UI thread if no image is available
            holder.binding.imageViewJournal.post(() -> {
                try {
                    Drawable drawable = ContextCompat.getDrawable(holder.binding.imageViewJournal.getContext(), R.drawable.no_image_icn);
                    holder.binding.imageViewJournal.setImageDrawable(drawable);
                } catch (Resources.NotFoundException e) {
                    Log.e("ViewHolder", "Cannot find image", e);
                }
            });
        }

        // Setting the title, date, and description of the journal entry
        holder.binding.textViewTitle.setText(journal.getTitle());
        holder.binding.textViewDate.setText(DateUtility.formatDateToString(journal.getDate()));

        // Setting description with scrolling capability
        holder.binding.textViewDescription.setMovementMethod(new ScrollingMovementMethod());
        holder.binding.textViewDescription.setText(journal.getDescription());
    }



    @Override
    public int getItemCount() {
        return journalList.size();
    }

    /**
     * ViewHolder class to hold references to the views within each item of the RecyclerView.
     */
    public static class JournalEntryViewHolder extends RecyclerView.ViewHolder {
        private final JournalEntryCardBinding binding;

        public JournalEntryViewHolder(@NonNull JournalEntryCardBinding journalCardBinding) {
            super(journalCardBinding.getRoot());
            this.binding = journalCardBinding;
        }
    }

    /**
     * Sets the list of journals to be displayed.
     *
     * @param journalList The list of journals to be set.
     */
    public void setJournalEntryList(List<JournalEntry> journalList) {
        this.journalList = journalList;
    }
}
