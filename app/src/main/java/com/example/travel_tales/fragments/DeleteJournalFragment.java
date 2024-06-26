package com.example.travel_tales.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;

import com.example.travel_tales.activities.HomeActivity;
import com.example.travel_tales.databinding.FragmentDeleteJournalBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.JournalEntry;
import com.example.travel_tales.models.JournalMini;
import com.example.travel_tales.utility.NotificationUtility;
import com.example.travel_tales.utility.SharedPreferencesUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeleteJournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteJournalFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private FragmentDeleteJournalBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private DBHelper dbHelper;
    private JournalMini selectedJournal;
    private int userId;

    public DeleteJournalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteJournalFragment.
     */
    public static DeleteJournalFragment newInstance(String param1, String param2) {
        DeleteJournalFragment fragment = new DeleteJournalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteJournalBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        userId= SharedPreferencesUtil.getUserId(getContext());

        initializeComponents();
        registerEventListeners();

        return view;
    }

    // Initializing components
    private void initializeComponents() {
        dbHelper = new DBHelper(getContext());
        updateSpinner();
    }


    // This method register event listeners for UI components
    private void registerEventListeners() {
        this.binding.spinnerJournal.setOnItemSelectedListener(this);
        this.binding.buttonDelete.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Getting the selected item from the Spinner
        selectedJournal = (JournalMini) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.binding.buttonDelete.getId()) {
            // Showing a confirmation dialog before deleting
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Journal")
                    .setMessage("Are you sure you want to delete \"" + selectedJournal.getTitle() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Deleting the selected item
                        dbHelper.deleteJournalEntry(selectedJournal.getId());
                        // Updating the spinner after deletion
                        updateSpinner();
                        NotificationUtility.showRecordSuccessNotification(getContext(), NotificationUtility.RecordOperation.DELETE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .show();
        }
    }

    /**
     * Updates the spinner with the latest journal entries after a deletion.
     * Retrieves journal entries from the database and populates the spinner with journal titles.
     */
    private void updateSpinner() {
        List<JournalEntry> journalEntries = dbHelper.getAllJournalsByUserId(userId); //todo - replace 1 with the actual user ID
        List<JournalMini> journalItems = journalEntries.stream()
                .map(x -> new JournalMini(x.getId(), x.getTitle())).collect(Collectors.toList());
        ArrayAdapter<JournalMini> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, journalItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerJournal.setAdapter(adapter);
    }

}
