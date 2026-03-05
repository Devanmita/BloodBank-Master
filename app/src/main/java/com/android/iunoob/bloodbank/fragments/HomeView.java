package com.android.iunoob.bloodbank.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.iunoob.bloodbank.adapters.BloodRequestAdapter;
import com.android.iunoob.bloodbank.viewmodels.CustomUserData;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends Fragment {

    private RecyclerView recentPosts;
    private BloodRequestAdapter restAdapter;
    private List<CustomUserData> postLists;
    private ProgressDialog pd;
    private DatabaseHelper dbHelper;

    public HomeView() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_view_fragment, container, false);

        if (getActivity() != null) {
            getActivity().setTitle("Blood Point");
        }

        recentPosts = view.findViewById(R.id.recyleposts);
        recentPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        postLists = new ArrayList<>();
        dbHelper = new DatabaseHelper(getContext());

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        restAdapter = new BloodRequestAdapter(postLists);
        RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
        recentPosts.setLayoutManager(pmLayout);
        recentPosts.setItemAnimator(new DefaultItemAnimator());
        recentPosts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recentPosts.setAdapter(restAdapter);

        loadPosts();

        return view;
    }

    @SuppressLint("Range")
    private void loadPosts() {
        pd.show();
        postLists.clear();
        Cursor cursor = dbHelper.getAllPosts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CustomUserData post = new CustomUserData();
                post.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_USER_NAME)));
                post.setContact(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_CONTACT)));
                post.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_ADDRESS)));
                post.setDivision(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_DIVISION)));
                post.setBloodGroup(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_BLOOD_GROUP)));
                post.setTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_TIME)));
                post.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_DATE)));
                post.setUser_email(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_POST_USER_EMAIL)));
                postLists.add(post);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(getActivity(), "No posts found.", Toast.LENGTH_LONG).show();
        }

        restAdapter.notifyDataSetChanged();
        pd.dismiss();
    }
}
