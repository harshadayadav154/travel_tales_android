package com.example.travel_tales.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.travel_tales.R;
import com.example.travel_tales.databinding.FragmentViewJournalBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.JournalEntry;
import com.example.travel_tales.models.JournalMini;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateViewFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private FragmentViewJournalBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private DBHelper dbHelper;
    private JournalMini selectedJournal;

    public UpdateViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateJournalFragment.
     */
    public static UpdateViewFragment newInstance(String param1, String param2) {
        UpdateViewFragment fragment = new UpdateViewFragment();
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
        binding = FragmentViewJournalBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

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
        this.binding.buttonUpdate.setOnClickListener(this);
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
        if (v.getId() == this.binding.buttonUpdate.getId()) {
            UpdateJournalFragment fragment = new UpdateJournalFragment(selectedJournal);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    /**
     * Updates the spinner with the latest journal entries after a deletion.
     * Retrieves journal entries from the database and populates the spinner with journal titles.
     */
    private void updateSpinner() {
        List<JournalEntry> journalEntries = dbHelper.getAllJournalsByUserId(1); //todo - replace 1 with the actual user ID
        List<JournalMini> journalItems = journalEntries.stream()
                .map(x -> new JournalMini(x.getId(), x.getTitle())).collect(Collectors.toList());
        ArrayAdapter<JournalMini> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, journalItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerJournal.setAdapter(adapter);
    }
}