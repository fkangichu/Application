package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void Signup(View view) {
        Intent signup = new Intent(getApplicationContext(), Signup.class);
        startActivity(signup);
    }

    public void Login(View view) {
        EditText editTextEmailAddress = findViewById(R.id.email);
        EditText editTextPassword = findViewById(R.id.password);

        if (editTextEmailAddress.getText().toString().matches("") || editTextPassword.getText().toString().matches("")) {
            Toast.makeText(MainActivity.this, "Username and password required", Toast.LENGTH_SHORT).show();

        }
        else
        {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

}