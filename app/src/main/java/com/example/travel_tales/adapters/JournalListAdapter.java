package com.example.travel_tales.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
        // Binding data to the views using view binding
        JournalEntry journal = journalList.get(position);

        // Load image asynchronously if available, otherwise set default image
        if (!journal.getImagePaths().isEmpty()) {
            executor.execute(() -> {
                Bitmap bitmap = BitmapFactory.decodeFile(journal.getImagePaths().get(0));
                holder.binding.imageViewJournal.post(() -> holder.binding.imageViewJournal.setImageBitmap(bitmap));
            });
        } else {
            // Set the default image on the UI thread
            holder.binding.imageViewJournal.post(() -> holder.binding.imageViewJournal.setImageResource(R.drawable.no_image_icn));
        }

        holder.binding.textViewTitle.setText(journal.getTitle());
        // Setting date and description
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
