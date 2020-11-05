package com.example.hackathon.ui.home;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hackathon.DatabaseHelper;
import com.example.hackathon.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    DatabaseHelper databaseHelper;
    Spinner spinnerMonth;
    Spinner spinnerYear;

//    ListView listTransactions;

    Map<String, Float> income = new HashMap<>();
    Map<String, Float> expense = new HashMap<>();

    Map<String, String> monthString = new HashMap<>();

    Map<String, Float> income_receivedCash = new HashMap<>();
    Map<String, Float> income_depositedCash = new HashMap<>();

    Map<String, Float> expense_airtime = new HashMap<>();
    Map<String, Float> expense_payBill = new HashMap<>();
    Map<String, Float> expense_sendMoney = new HashMap<>();
    Map<String, Float> expense_withdraw = new HashMap<>();
    Map<String, Float> expense_buyGoodsAndServices = new HashMap<>();

    Map<String, Float> incomeResult = new HashMap<>();
    Map<String, Float> expenseResult = new HashMap<>();

    ArrayList<String> monthValues = new ArrayList<>();
    ArrayList<String> yearValues = new ArrayList<>();

    PieChart pieChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerMonth = root.findViewById(R.id.calenderMonths);
        spinnerYear = root.findViewById(R.id.calenderYears);

//        listTransactions = root.findViewById(R.id.previousTransactions);

        databaseHelper = new DatabaseHelper(getActivity());

        getMessage();
        setDates();

//        addPreviousTransactions();

        pieChart = root.findViewById(R.id.mpesaTransactions);


        return root;
    }

//    private void addPreviousTransactions() {
//        ArrayAdapter<String> transactions = new ArrayAdapter<>();
//    }

    private void setDates() {

        monthString.put("January", "01");
        monthString.put("February","02");
        monthString.put("March", "03");
        monthString.put("April", "04");
        monthString.put("May", "05");
        monthString.put("June", "06");
        monthString.put("July", "07");
        monthString.put("August", "08");
        monthString.put("September", "09");
        monthString.put("October", "10");
        monthString.put("November", "11");
        monthString.put("December", "12");

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, monthValues);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, yearValues);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMonth.setAdapter(monthAdapter);
        spinnerYear.setAdapter(yearAdapter);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.v("item", (String) parent.getItemAtPosition(position));
                String monthSelected = "2020/" + monthString.get((String) parent.getItemAtPosition(position));

//                PieDataSet dataSet = new PieDataSet(, "Income");
                pieChart.setHoleRadius(45f);
                pieChart.setTransparentCircleAlpha(0);
                pieChart.getLegend().setEnabled(false);

                ArrayList<PieEntry> pieEntries = new ArrayList<>();
//                ArrayList<PieEntry> pieEntriesExpenses = new ArrayList<>();

                float[] data = {incomeResult.get(monthSelected), expenseResult.get(monthSelected)};

                pieEntries.add(new PieEntry(data[0], "Income"));
                pieEntries.add(new PieEntry(data[1], "Expense"));


//                for (int i=0; i<data.length; i++) {
//                }


//                pieChart.setData(addDataSet());
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "MPesa Transactions");

//                ArrayList<Integer> colors = new ArrayList<>();
//                colors.add(Color.BLUE);
//                colors.add(Color.RED);
//                colors.add(Color.CYAN);
//                colors.add(Color.YELLOW);

                PieData pieData = new PieData(pieDataSet);
                pieDataSet.setSliceSpace(2);
                pieDataSet.setValueTextSize(12);
                pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);

                pieChart.setData(pieData);

                pieChart.notifyDataSetChanged();
                pieChart.invalidate();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void getMessage() {
        Log.d(TAG, "get data from db");
        Cursor data = databaseHelper.getData();

        while (data.moveToNext()) {
            String messageBody = data.getString(1);
            String date = data.getString(2);
//            Log.i(TAG, "getMessage: " + messageBody);

            if (messageBody.contains("You have received")) {
                String receivedCash = (StringUtils.substringBetween(messageBody, "received", "from")
                        .substring(4)).replace(",", "");
                Float receivedCashF = Float.parseFloat(receivedCash);
                income_receivedCash.put(date, receivedCashF);
                income.put(date, receivedCashF);
            }

            else if (messageBody.contains("Give")) {
                String depositedCash = (StringUtils.substringBetween(messageBody, "Give", "cash")
                        .substring(4)).replace(",", "");
                Float depositedCashF = Float.parseFloat(depositedCash);
                income_depositedCash.put(date, depositedCashF);
                income.put(date, depositedCashF);
            }

            else if (messageBody.contains("airtime") && messageBody.contains("You bought")) {
                String airtimeExpense = (StringUtils.substringBetween(messageBody, "bought", "of")
                        .substring(4)).replace(",", "");
                Float airtimeExpenseF = Float.parseFloat(airtimeExpense);
                expense_airtime.put(date, airtimeExpenseF);
                expense.put(date, airtimeExpenseF);
            }

            else if (messageBody.contains("sent to") && messageBody.contains("for account")) {
                String payBill = (StringUtils.substringBetween(messageBody, "Confirmed", "sent")
                        .substring(5)).replace(",", "");
                Float payBillF = Float.parseFloat(payBill);
                expense_payBill.put(date, payBillF);
                expense.put(date, payBillF);
            }

            else if (messageBody.contains("sent to")) {
                String sendMoney = (StringUtils.substringBetween(messageBody, "Confirmed", "sent")
                        .substring(5)).replace(",", "");
                Float sendMoneyF = Float.parseFloat(sendMoney);
                expense_sendMoney.put(date, sendMoneyF);
                expense.put(date, sendMoneyF);
            }

            else if (messageBody.contains("Withdraw")) {
                String withdrawExpense = (StringUtils.substringBetween(messageBody, "Withdraw", "from")
                        .substring(4)).replace(",", "");
                Float withdrawExpenseF = Float.parseFloat(withdrawExpense);
                expense_withdraw.put(date, withdrawExpenseF);
                expense.put(date, withdrawExpenseF);
            }

            else if (messageBody.contains("paid")) {
                String buyGoodsAndServices = (StringUtils.substringBetween(messageBody, "Confirmed", "paid")
                        .substring(5)).replace(",", "");
                Float buyGoodsAndServicesF = Float.parseFloat(buyGoodsAndServices);
                expense_buyGoodsAndServices.put(date, buyGoodsAndServicesF);
                expense.put(date, buyGoodsAndServicesF);
            }
        }

        for (Map.Entry<String, Float> incomeEntry  : income.entrySet()) {
            String key = incomeEntry.getKey().split("-")[0] + "/" + incomeEntry.getKey().split("-")[1];
            Float value = incomeEntry.getValue();
            Float oldValue = incomeResult.get(key) != null ? incomeResult.get(key) : 0;
            incomeResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> expenseEntry  : expense.entrySet()) {
            String key = expenseEntry.getKey().split("-")[0] + "/" + expenseEntry.getKey().split("-")[1];
            Float value = expenseEntry.getValue();
            Float oldValue = expenseResult.get(key) != null ? expenseResult.get(key) : 0;
            expenseResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> entry  : expenseResult.entrySet()) {
            String key = entry.getKey();
            Float entryExpense = entry.getValue();
            Float entryIncome = incomeResult.get(key);

//            results.put(entryExpense, entryIncome);

            int months = Integer.parseInt(entry.getKey().split("/")[1]);
            String year = entry.getKey().split("/")[0];

            String monthString = new DateFormatSymbols().getMonths()[months-1];

            monthValues.add(monthString);
            yearValues.add(year);

//            System.out.println(key);
//            System.out.println("Month Expense" + entry.getKey() + " - Value = " + entry.getValue());
        }
    }

//    private PieData addDataSet() {
//        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, monthValues);
//        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, yearValues);
//
//        ArrayList<PieEntry> entries1 = new ArrayList<>();
//
//        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinnerMonth.setAdapter(monthAdapter);
//        spinnerYear.setAdapter(yearAdapter);
//
//        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.v("item", (String) parent.getItemAtPosition(position));
//
////                if ((String) parent.getItemAtPosition(position) == "November") {
////                    Float testexpense = incomeResult.get(2020/11);
////                    Log.i(TAG, "onItemSelected: " + testexpense);
////                }
//
////                pieChart.notifyDataSetChanged();
////                pieChart.invalidate();
//            }
////
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//            }
//        });
//
//
//
//        PieDataSet ds1 = new PieDataSet(entries1, "Quarterly Revenues 2015");
////        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
//        ds1.setSliceSpace(2f);
//        ds1.setValueTextColor(Color.WHITE);
//        ds1.setValueTextSize(12f);
//
//        PieData d = new PieData(ds1);
////        d.setValueTypeface(tf);
//
//        return d;
//    }
}