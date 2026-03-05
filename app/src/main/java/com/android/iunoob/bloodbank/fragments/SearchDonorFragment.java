package com.android.iunoob.bloodbank.fragments;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.iunoob.bloodbank.DatabaseHelper;
import com.android.iunoob.bloodbank.R;
import com.android.iunoob.bloodbank.adapters.SearchDonorAdapter;
import com.android.iunoob.bloodbank.viewmodels.DonorData;

import java.util.ArrayList;
import java.util.List;

public class SearchDonorFragment extends Fragment {

    private View view;
    private Spinner bloodgroupSpinner, divisionSpinner;
    private Button btnSearch;
    private ProgressDialog pd;
    private List<DonorData> donorList;
    private RecyclerView recyclerView;
    private SearchDonorAdapter sdadapter;
    private DatabaseHelper dbHelper;

    public SearchDonorFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_donor_fragment, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Searching...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        dbHelper = new DatabaseHelper(getContext());

        bloodgroupSpinner = view.findViewById(R.id.btngetBloodGroup);
        divisionSpinner = view.findViewById(R.id.btngetDivison);
        btnSearch = view.findViewById(R.id.btnSearch);
        recyclerView = view.findViewById(R.id.showDonorList);

        if (getActivity() != null) {
            getActivity().setTitle("Find Blood Donor");
        }

        setupRecyclerView();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        donorList = new ArrayList<>();
        sdadapter = new SearchDonorAdapter(donorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (getActivity() != null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        }
        recyclerView.setAdapter(sdadapter);
    }

    private void performSearch() {
        pd.show();
        donorList.clear();

        int bloodGroupPos = bloodgroupSpinner.getSelectedItemPosition();
        int divisionPos = divisionSpinner.getSelectedItemPosition();

        Cursor cursor = dbHelper.searchDonors(bloodGroupPos, divisionPos);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                DonorData donor = new DonorData();
                donor.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME)));
                donor.setContact(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_PHONE)));
                donor.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_ADDRESS)));
                donorList.add(donor);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(getActivity(), "No donors found for this criteria.", Toast.LENGTH_LONG).show();
        }

        sdadapter.notifyDataSetChanged();
        pd.dismiss();
    }
}
