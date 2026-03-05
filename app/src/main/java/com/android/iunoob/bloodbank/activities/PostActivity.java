package com.android.iunoob.bloodbank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.iunoob.bloodbank.DatabaseHelper;
import com.android.iunoob.bloodbank.R;

import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ProgressDialog pd;

    EditText text1, text2;
    Spinner spinner1, spinner2;
    Button btnpost;

    DatabaseHelper dbHelper;
    SharedPreferences prefs;

    Calendar cal;
    String email;
    String Time, Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        pd = new ProgressDialog(this);
        pd.setMessage("Posting...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        getSupportActionBar().setTitle("Post Blood Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1 = findViewById(R.id.getMobile);
        text2 = findViewById(R.id.getLocation);

        spinner1 = findViewById(R.id.SpinnerBlood);
        spinner2 = findViewById(R.id.SpinnerDivision);

        btnpost = findViewById(R.id.postbtn);

        cal = Calendar.getInstance();
        calculateTimeAndDate();

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences(Dashboard.PREFS_NAME, MODE_PRIVATE);
        email = prefs.getString(Dashboard.KEY_EMAIL, null);

        if (email == null) {
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
            finish();
            return;
        }


        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text1.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Enter your contact number!",
                            Toast.LENGTH_LONG).show();
                }
                else if(text2.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Enter your location!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    pd.show();
                    postBloodRequest();
                }
            }
        });
    }

    private void postBloodRequest() {
        Cursor cursor = dbHelper.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME);
            if(nameColumnIndex == -1) {
                 pd.dismiss();
                 Toast.makeText(getApplicationContext(), "Database error occurred.", Toast.LENGTH_LONG).show();
                 cursor.close();
                 return;
            }
            String name = cursor.getString(nameColumnIndex);
            cursor.close();

            String contact = text1.getText().toString();
            String address = text2.getText().toString();
            String division = spinner2.getSelectedItem().toString();
            String bloodGroup = spinner1.getSelectedItem().toString();

            dbHelper.addPost(name, contact, address, division, bloodGroup, Time, Date, email);

            pd.dismiss();
            Toast.makeText(PostActivity.this, "Your post has been created successfully",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(PostActivity.this, Dashboard.class));
            finish();

        } else {
            pd.dismiss();
            Toast.makeText(getApplicationContext(), "User not found. Database error.",
                    Toast.LENGTH_LONG).show();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void calculateTimeAndDate() {
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        month+=1;
        Time = "";
        Date = "";
        String ampm="AM";

        if(cal.get(Calendar.AM_PM) == 1)
        {
            ampm = "PM";
        }
        if (hour == 0) { // handle 12 AM
            hour = 12;
        }

        if(hour<10)
        {
            Time += "0";
        }
        Time += hour;
        Time +=":";

        if(min<10) {
            Time += "0";
        }

        Time +=min;
        Time +=(" "+ampm);

        Date = day+"/"+month+"/"+year;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
