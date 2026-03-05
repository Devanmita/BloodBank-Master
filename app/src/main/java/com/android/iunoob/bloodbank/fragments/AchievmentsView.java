package com.android.iunoob.bloodbank.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.iunoob.bloodbank.DatabaseHelper;
import com.android.iunoob.bloodbank.R;
import com.android.iunoob.bloodbank.activities.Dashboard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AchievmentsView extends Fragment {

    private TextView totalDonate, lastDonate, notice, dayRemaining;
    private LinearLayout donorAchievLayout, notDonorLayout, yesNoLayout;
    private Button yesBtn;
    private ProgressDialog pd;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private String currentUserEmail;

    private int totalDonations = 0;
    private String lastDonationDate = "N/A";

    public AchievmentsView() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_achievment_fragment, container, false);

        if (getActivity() != null) {
            getActivity().setTitle("Achievements");
        }

        initializeViews(view);

        dbHelper = new DatabaseHelper(getContext());
        // Make sure to get context from getActivity() for SharedPreferences in a fragment
        prefs = getActivity().getSharedPreferences(Dashboard.PREFS_NAME, Context.MODE_PRIVATE);
        currentUserEmail = prefs.getString(Dashboard.KEY_EMAIL, null);

        loadAndDisplayUserData();

        yesBtn.setOnClickListener(v -> {
            recordNewDonation();
        });

        return view;
    }

    private void initializeViews(View view) {
        totalDonate = view.findViewById(R.id.totaldonate);
        lastDonate = view.findViewById(R.id.lastdonate);
        notice = view.findViewById(R.id.notice);
        dayRemaining = view.findViewById(R.id.dayremain);
        donorAchievLayout = view.findViewById(R.id.donorAchiev);
        notDonorLayout = view.findViewById(R.id.notdonor);
        yesNoLayout = view.findViewById(R.id.yesnolayout);
        yesBtn = view.findViewById(R.id.yesbtn);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
    }

    @SuppressLint("Range")
    private void loadAndDisplayUserData() {
        pd.show();
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            boolean isDonor = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_USER_IS_DONOR)) == 1;
            if (isDonor) {
                donorAchievLayout.setVisibility(View.VISIBLE);
                notDonorLayout.setVisibility(View.GONE);

                totalDonations = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_USER_TOTAL_DONATE));
                lastDonationDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_DONATE));

                totalDonate.setText("Total Donations: " + totalDonations + " times");
                lastDonate.setText("Last Donation: " + lastDonationDate);

                checkDonationEligibility();

            } else {
                donorAchievLayout.setVisibility(View.GONE);
                notDonorLayout.setVisibility(View.VISIBLE);
                yesNoLayout.setVisibility(View.GONE);
            }
            cursor.close();
        }
        pd.dismiss();
    }

    private void checkDonationEligibility() {
        if (lastDonationDate == null || lastDonationDate.equals("N/A")) {
            notice.setText("You are eligible to donate for the first time!");
            dayRemaining.setVisibility(View.GONE);
            yesNoLayout.setVisibility(View.VISIBLE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date lastDate = sdf.parse(lastDonationDate);
            Date today = new Date();
            long diffInMillis = Math.abs(today.getTime() - lastDate.getTime());
            long daysPassed = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            long daysLeft = 120 - daysPassed;

            if (daysLeft > 0) {
                notice.setText("You can donate again after:");
                dayRemaining.setText(daysLeft + " days");
                dayRemaining.setVisibility(View.VISIBLE);
                yesNoLayout.setVisibility(View.GONE);
            } else {
                notice.setText("You are eligible to donate again. Have you donated?");
                dayRemaining.setVisibility(View.GONE);
                yesNoLayout.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            notice.setText("Could not calculate eligibility.");
            yesNoLayout.setVisibility(View.GONE);
        }
    }

    private void recordNewDonation() {
        pd.show();
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        int newTotalDonations = totalDonations + 1;
        dbHelper.updateDonationHistory(currentUserEmail, todayDate, newTotalDonations);
        pd.dismiss();
        Toast.makeText(getContext(), "Thank you for your donation! Your record has been updated.", Toast.LENGTH_LONG).show();
        // Refresh the screen with the new data
        loadAndDisplayUserData();
    }
}
