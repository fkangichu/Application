    package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

    public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText inputPhone = findViewById(R.id.phoneNumber);
        final Button buttonSendOTP = findViewById(R.id.buttonSendOTP);

        final ProgressBar progressBar = findViewById(R.id.progressBar);

        inputPhone.requestFocus();

        buttonSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputPhone.getText().toString().trim().isEmpty()) {
                    inputPhone.setError("Input phone number");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                buttonSendOTP.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(getApplicationContext(), OTPVerification.class);
                intent.putExtra("Phone number", inputPhone.getText().toString());
                startActivity(intent);
            }
        });
    }
}