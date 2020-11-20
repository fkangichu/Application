package com.example.hackathon.ui.loans;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hackathon.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class LoansFragment extends Fragment {

    private LoansViewModel loansViewModel;
    private Button requestLoan, loan1Request, getLoan;
    private LinearLayout availableLoans;
    private String value, rates, rate, amount, rateType;
    private RadioButton radio_ninjas;
    private EditText requestDifferentAmount;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loansViewModel =
                ViewModelProviders.of(this).get(LoansViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_loans, container, false);

        requestLoan = root.findViewById(R.id.requestLoan);
        loan1Request = root.findViewById(R.id.loan1Request);

        requestDifferentAmount = root.findViewById(R.id.requestDifferentAmount);
        radio_ninjas = root.findViewById(R.id.radio_ninjas);

        getLoan = root.findViewById(R.id.getLoan);

        final String ratingCustomerRequest = "{\n" +
                "\t\"customerId\":\"252525252525\",\n" +
                "\t\"scoringModelId\":\"5fad2cec5500565c9043c1aa\",\n" +
                "\t\"tags\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"tag\":\"AGE\",\n" +
                "\t\t\t\"tagValue\":18\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"tag\":\"GENDER\",\n" +
                "\t\t\t\"tagValue\":\"M\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"tag\":\"MPESA_PERIOD\",\n" +
                "\t\t\t\"tagValue\":12\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"tag\":\"AVERAGE_MONTHLY_TURNOVER\",\n" +
                "\t\t\t\"tagValue\":\"25000\"\n" +
                "\t\t},\n" +
                "        {\n" +
                "\t\t\t\"tag\":\"AVERAGE_RESIDUAL_INCOME_RATIO\",\n" +
                "\t\t\t\"tagValue\":\"0.75\"\n" +
                "\t\t},\n" +
                "        {\n" +
                "\t\t\t\"tag\":\"EXPENSE_TO_INCOME_RATIO\",\n" +
                "\t\t\t\"tagValue\":\"0.5\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "\n" +
                "}";

        try {
            URL url = new URL("http://2ea4d4dd669c.ngrok.io/api/rating/score-customer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(ratingCustomerRequest);

            os.flush();
            os.close();
            int responseCode = conn.getResponseCode();

            InputStream inputStream;
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            inputStream));

            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            value = jsonResponse.getString("finalScoreName");
            rates = jsonResponse.getString("rates");
            JSONArray jsonArray = jsonResponse.getJSONArray("rates");

            for (int i=0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    rate = jsonObject.getString("rate");
                    amount = jsonObject.getString("amount");
                    rateType = jsonObject.getString("rateType");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            conn.disconnect();

        } catch(Exception ex){
            ex.printStackTrace();
        }

        requestLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                availableLoans = root.findViewById(R.id.availableLoans);
                requestLoan.setVisibility(View.GONE);
                availableLoans.setVisibility(View.VISIBLE);
                RequestLoan();

            }
        });



        return root;
    }

    public void RequestLoan() {

        loan1Request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

                        builder.setView(layoutInflater.inflate(R.layout.layout_request_loan, null))
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                                .show();


                    }
                });
    }

}