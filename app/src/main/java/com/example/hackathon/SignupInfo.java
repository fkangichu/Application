package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class SignupInfo extends AppCompatActivity {

    private EditText full_name, email, password, confirm_password, birthday;
    private Button signup;
    private Spinner spinner;
    private FirebaseAuth mAuth;

    private static final String TAG = "SignupInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_info);

        mAuth = FirebaseAuth.getInstance();

        full_name = findViewById(R.id.full_name);
        full_name.requestFocus();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signup = findViewById(R.id.register);

        spinner = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        birthday = findViewById(R.id.birthday);
        birthday.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900: Math.min(year, 2100);
                        cal.set(Calendar.YEAR, year);

                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = Math.max(sel, 0);
                    current = clean;
                    birthday.setText(current);
                    birthday.setSelection(Math.min(sel, current.length()));



                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                if (full_name.getText().toString().trim().isEmpty()) {
                    full_name.setError("This field can not be blank");
                    full_name.requestFocus();

                }
                else if (emailText.isEmpty()) {
                    email.setError("This field can not be blank");
                    email.requestFocus();

                }
                else if (passwordText.isEmpty()) {
                    password.setError("This field can not be blank");
                    password.requestFocus();

                }
                else {
                    if (confirm_password.getText().toString().trim().isEmpty()) {
                        confirm_password.setError("This field can not be blank");
                        confirm_password.requestFocus();

                    }
                    else {
                        if (!confirm_password.getText().toString().equals(password.getText().toString())) {
                            confirm_password.setError("Passwords do not match");
                            confirm_password.requestFocus();
                        }
                        else {
                            if (confirm_password.getText().toString().equals(password.getText().toString())) {
                                confirm_password.setError(null);
                                password.setError(null);
                            }
                        }
                }

                }
                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(SignupInfo.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    mAuth.getCurrentUser();

                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.putExtra("Full name", full_name.getText().toString());
                                    startActivity(intent);
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupInfo.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}