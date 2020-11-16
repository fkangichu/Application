package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

public class SignupInfo extends AppCompatActivity {

    private EditText full_name, email, password, confirm_password, birthday;
    private Button signup;
    private DatePickerDialog datePickerDialog;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_info);

        full_name = findViewById(R.id.full_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signup = findViewById(R.id.register);

        spinner = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item);
        spinner.setAdapter(adapter);
//        spinner.setPrompt("Select gender");
//        spinner.setAdapter(
//                new NothingSelectedSpinnerAdapter (
//                        adapter,
//                        R.layout.activity_signup_info,
//                        this
//                )
//        );

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
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    birthday.setText(current);
                    birthday.setSelection(sel < current.length() ? sel : current.length());



                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
//        birthday.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Calendar cldr = Calendar.getInstance();
//                int day = cldr.get(Calendar.DAY_OF_MONTH);
//                int month = cldr.get(Calendar.MONTH);
//                int year = cldr.get(Calendar.YEAR);
//
//                datePickerDialog = new DatePickerDialog(SignupInfo.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @SuppressLint("SetTextI18n")
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
//                                birthday.setText(d + "/" + (m + 1) + "/" + y);
//                            }
//                        }, year, month, day);
//                datePickerDialog.show();
//            }
//        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (full_name.getText().toString().trim().isEmpty()) {
                    full_name.setError("This field can not be blank");
                    full_name.requestFocus();

                }
                else if (email.getText().toString().trim().isEmpty()) {
                    email.setError("This field can not be blank");
                    email.requestFocus();

                }
                else if (password.getText().toString().trim().isEmpty()) {
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
                            password.setError("Passwords do not match");
                            password.requestFocus();
                            confirm_password.requestFocus();
                        }
                        else {
                            if (confirm_password.getText().toString().equals(password.getText().toString())) {
                                confirm_password.setError(null);
                                password.setError(null);

                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                intent.putExtra("Full name", full_name.getText().toString());
                                startActivity(intent);
                            }
                        }
                }

                }
            }
        });
    }
}