package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText email, password;

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    public void Signup(View view) {
        Intent signup = new Intent(getApplicationContext(), Signup.class);
        startActivity(signup);
    }

    public void Login(View view) {

        final String emailText = email.getText().toString().trim();
        final String passwordText = password.getText().toString().trim();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEmail(emailText)) {

                }

                if (isPhone(emailText)) {

                }
            }
        });

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email and password required", Toast.LENGTH_SHORT).show();

        }
        else
        {
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mAuth.getCurrentUser();
                                Log.i(TAG, "onComplete: " + mAuth.getCurrentUser());

                                if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {

                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                                        Toast.makeText(MainActivity.this, "Read SMS is required to check your MPESA transaction history",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    requestPermissions(new String[]{Manifest.permission.READ_SMS}, 1);
                                }
                            }

                            else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


        }
    }

    public static boolean isEmail(String text) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static boolean isPhone(String text) {
        if(!TextUtils.isEmpty(text)){
            return TextUtils.isDigitsOnly(text);
        } else{
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (mAuth.getCurrentUser() != null) {
//            startActivity(new Intent(MainActivity.this, MainActivity.class));
//            finish();
//        }

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        email.requestFocus();

    }

}