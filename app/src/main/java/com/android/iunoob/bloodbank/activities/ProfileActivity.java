package com.android.iunoob.bloodbank.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.iunoob.bloodbank.DatabaseHelper;
import com.android.iunoob.bloodbank.R;

public class ProfileActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, retypePassword, fullName, address, contact;
    private Button btnSignup;
    private ProgressDialog pd;
    private Spinner gender, bloodgroup, division;
    private CheckBox isDonor;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private boolean isUpdate = false;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences(Dashboard.PREFS_NAME, MODE_PRIVATE);
        currentUserEmail = prefs.getString(Dashboard.KEY_EMAIL, null);

        initializeViews();

        if (currentUserEmail != null && dbHelper.isEmailExists(currentUserEmail)) {
            isUpdate = true;
            setupUpdateMode();
            loadUserProfile();
        } else {
            isUpdate = false;
            getSupportActionBar().setTitle("Register");
        }

        btnSignup.setOnClickListener(v -> handleRegistrationOrUpdate());
    }

    private void initializeViews() {
        inputEmail = findViewById(R.id.input_userEmail);
        inputPassword = findViewById(R.id.input_password);
        retypePassword = findViewById(R.id.input_password_confirm);
        fullName = findViewById(R.id.input_fullName);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.inputAddress);
        division = findViewById(R.id.inputDivision);
        bloodgroup = findViewById(R.id.inputBloodGroup);
        contact = findViewById(R.id.inputMobile);
        isDonor = findViewById(R.id.checkbox);
        btnSignup = findViewById(R.id.button_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupUpdateMode() {
        inputEmail.setVisibility(View.GONE);
        inputPassword.setVisibility(View.GONE);
        retypePassword.setVisibility(View.GONE);
        btnSignup.setText("Update Profile");
        getSupportActionBar().setTitle("Profile");
        findViewById(R.id.image_logo).setVisibility(View.GONE);
    }

    @SuppressLint("Range")
    private void loadUserProfile() {
        pd.show();
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            fullName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_NAME)));
            contact.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_PHONE)));
            address.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_ADDRESS)));
            gender.setSelection(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_USER_GENDER)));
            bloodgroup.setSelection(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_USER_BLOOD_GROUP)));
            division.setSelection(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_USER_DIVISION)));
            isDonor.setChecked(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_USER_IS_DONOR)) == 1);
            cursor.close();
        }
        pd.dismiss();
    }

    private void handleRegistrationOrUpdate() {
        String name = fullName.getText().toString().trim();
        String contactNum = contact.getText().toString().trim();
        String addr = address.getText().toString().trim();
        int genderPos = gender.getSelectedItemPosition();
        int bloodGroupPos = bloodgroup.getSelectedItemPosition();
        int divisionPos = division.getSelectedItemPosition();
        boolean donor = isDonor.isChecked();

        if (name.length() <= 2 || contactNum.length() < 10 || addr.length() <= 2) {
            Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_LONG).show();
            return;
        }

        pd.show();

        if (isUpdate) {
            dbHelper.updateUser(currentUserEmail, name, contactNum, addr, genderPos, bloodGroupPos, divisionPos, donor);
            pd.dismiss();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmPassword = retypePassword.getText().toString().trim();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_LONG).show();
                pd.dismiss();
                return;
            }

            if (password.length() < 6 || !password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords must match and be 6+ chars", Toast.LENGTH_LONG).show();
                pd.dismiss();
                return;
            }

            if (dbHelper.isEmailExists(email)) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_LONG).show();
                pd.dismiss();
                return;
            }

            boolean success = dbHelper.addUser(name, email, contactNum, password, addr, genderPos, bloodGroupPos, divisionPos, donor);

            if (!success) {
                Toast.makeText(this, "Failed to create account. Try again.", Toast.LENGTH_LONG).show();
                pd.dismiss();
                return;
            }

            // Log in the new user
            prefs.edit().putString(Dashboard.KEY_EMAIL, email).apply();
            pd.dismiss();
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // This will close the activity and go back.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
