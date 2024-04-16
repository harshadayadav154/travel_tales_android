package com.example.travel_tales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.travel_tales.activities.HomeActivity;
import com.example.travel_tales.adapters.JournalListAdapter;
import com.example.travel_tales.databinding.FragmentListJournalBinding;
import com.example.travel_tales.db.DBHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListJournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListJournalFragment extends Fragment implements View.OnClickListener {
    private FragmentListJournalBinding binding;
    private DBHelper dbHelper;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ListJournalFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListJournalFragment.
     */
    public static ListJournalFragment newInstance(String param1, String param2) {
        ListJournalFragment fragment = new ListJournalFragment();
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
        binding = FragmentListJournalBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        init(); // Initializing
        setupRecyclerView(); // Setting up RecyclerView

        return rootView;
    }

    /**
     * Initialize database and set up click listener for cancel button.
     */
    private void init() {
        // Initializing the database helper
        dbHelper = new DBHelper(requireContext());

        // Setting up click listener for the cancel button
        //binding.btnGoToHome.setOnClickListener(this);
    }

    /**
     * Setup RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        JournalListAdapter adapter = new JournalListAdapter();
        adapter.setJournalEntryList(dbHelper.getAllJournalsByUserId(1));//todo fix this later
        binding.rcView.setAdapter(adapter);
        binding.rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == this.binding.btnGoToHome.getId()) {
            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
        }*/
    }
}