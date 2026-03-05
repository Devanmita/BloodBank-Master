package com.android.iunoob.bloodbank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.iunoob.bloodbank.DatabaseHelper;
import com.android.iunoob.bloodbank.R;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button signin, signup;
    private ProgressDialog pd;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line to allow screenshots for this activity
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences(Dashboard.PREFS_NAME, MODE_PRIVATE);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        inputEmail = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);
        signin = findViewById(R.id.button_login);
        signup = findViewById(R.id.button_register);

        autoLoginIfPossible();

        signin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            pd.show();
            loginUser(email, password);
        });

        signup.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void autoLoginIfPossible() {
        String email = prefs.getString(Dashboard.KEY_EMAIL, null);
        if (email != null && dbHelper.isEmailExists(email)) {
            Intent intent = new Intent(this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            prefs.edit().remove(Dashboard.KEY_EMAIL).apply();
        }
    }

    private void loginUser(String email, String password) {
        if (dbHelper.authenticate(email, password)) {
            prefs.edit().putString(Dashboard.KEY_EMAIL, email).apply();
            pd.dismiss();

            Intent intent = new Intent(this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            pd.dismiss();
            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
        }
    }
}
