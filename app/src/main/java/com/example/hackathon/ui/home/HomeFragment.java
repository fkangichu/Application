package com.example.hackathon.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hackathon.DatabaseHelper;
import com.example.hackathon.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    DatabaseHelper databaseHelper;

    Spinner spinnerMonth;
    Spinner spinnerYear;

    String yearSelected;

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

    Map<String, Float> receivedCashResult = new HashMap<>();
    Map<String, Float> depositedCashResult = new HashMap<>();

    Map<String, Float> airtimeResult = new HashMap<>();
    Map<String, Float> paybillResult = new HashMap<>();
    Map<String, Float> sendMoneyResult = new HashMap<>();
    Map<String, Float> withdrawResult = new HashMap<>();
    Map<String, Float> buyGoodsAndServicesResult = new HashMap<>();

    ArrayList<String> monthValues = new ArrayList<>();
    ArrayList<String> yearValues = new ArrayList<>();

    PieChart pieChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

//        Log.d(TAG, "onCreateView: " + getArguments().getString("Full name"));

//        TextView textView = root.findViewById(R.id.text);
//        textView.setText(String.format(
//                "Welcome ", getArguments().getString("Full name")
//        ));

        spinnerMonth = root.findViewById(R.id.calenderMonths);
        spinnerYear = root.findViewById(R.id.calenderYears);

        databaseHelper = new DatabaseHelper(getActivity());

        getMessage();
        addPreviousTransactions();
        setDates();

        pieChart = root.findViewById(R.id.mpesaTransactions);


        return root;
    }

    private void addPreviousTransactions() {
        for (Map.Entry<String, Float> receivedCashEntry  : income_receivedCash.entrySet()) {
            String key = receivedCashEntry.getKey().split("-")[0] + "/" + receivedCashEntry.getKey().split("-")[1];
            Float value = receivedCashEntry.getValue();
            Float oldValue = receivedCashResult.get(key) != null ? receivedCashResult.get(key) : 0;
            receivedCashResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> depositedCashEntry  : income_depositedCash.entrySet()) {
            String key = depositedCashEntry.getKey().split("-")[0] + "/" + depositedCashEntry.getKey().split("-")[1];
            Float value = depositedCashEntry.getValue();
            Float oldValue = depositedCashResult.get(key) != null ? depositedCashResult.get(key) : 0;
            depositedCashResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> airtimeEntry  : expense_airtime.entrySet()) {
            String key = airtimeEntry.getKey().split("-")[0] + "/" + airtimeEntry.getKey().split("-")[1];
            Float value = airtimeEntry.getValue();
            Float oldValue = airtimeResult.get(key) != null ? airtimeResult.get(key) : 0;
            airtimeResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> payBillEntry  : expense_payBill.entrySet()) {
            String key = payBillEntry.getKey().split("-")[0] + "/" + payBillEntry.getKey().split("-")[1];
            Float value = payBillEntry.getValue();
            Float oldValue = paybillResult.get(key) != null ? paybillResult.get(key) : 0;
            paybillResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> sendMoneyEntry  : expense_sendMoney.entrySet()) {
            String key = sendMoneyEntry.getKey().split("-")[0] + "/" + sendMoneyEntry.getKey().split("-")[1];
            Float value = sendMoneyEntry.getValue();
            Float oldValue = sendMoneyResult.get(key) != null ? sendMoneyResult.get(key) : 0;
            sendMoneyResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> withdrawEntry  : expense_withdraw.entrySet()) {
            String key = withdrawEntry.getKey().split("-")[0] + "/" + withdrawEntry.getKey().split("-")[1];
            Float value = withdrawEntry.getValue();
            Float oldValue = withdrawResult.get(key) != null ? withdrawResult.get(key) : 0;
            withdrawResult.put(key, oldValue + value);
        }

        for (Map.Entry<String, Float> buyGoodsAndServicesEntry  : expense_buyGoodsAndServices.entrySet()) {
            String key = buyGoodsAndServicesEntry.getKey().split("-")[0] + "/" + buyGoodsAndServicesEntry.getKey().split("-")[1];
            Float value = buyGoodsAndServicesEntry.getValue();
            Float oldValue = buyGoodsAndServicesResult.get(key) != null ? buyGoodsAndServicesResult.get(key) : 0;
            buyGoodsAndServicesResult.put(key, oldValue + value);
        }
    }

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

        Set<String> set = new HashSet<>(yearValues);
        yearValues.clear();
        yearValues.addAll(set);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, monthValues);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, yearValues);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMonth.setAdapter(monthAdapter);
        spinnerYear.setAdapter(yearAdapter);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, final View view, final int i, long l) {
                yearSelected = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, final int position, long id) {
                final String monthSelected = "2020/" + monthString.get((String) parent.getItemAtPosition(position));

                pieChart.setHoleRadius(50f);
                pieChart.setTransparentCircleAlpha(0);
                pieChart.getLegend().setEnabled(false);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText("MPESA Transactions");
                pieChart.setDrawCenterText(true);
                pieChart.setRotationEnabled(false);
                pieChart.setHighlightPerTapEnabled(true);
                pieChart.setCenterTextSize(15);
                pieChart.animateY(1400, Easing.EaseInOutQuad);
                pieChart.setEntryLabelColor(Color.WHITE);
                pieChart.setEntryLabelTextSize(12f);
                pieChart.setExtraOffsets(5, 10, 5, 5);

                ArrayList<PieEntry> pieEntries = new ArrayList<>();

                float[] transactionOverview = {incomeResult.get(monthSelected), expenseResult.get(monthSelected)};

                pieEntries.add(new PieEntry(transactionOverview[0], "Income"));
                pieEntries.add(new PieEntry(transactionOverview[1], "Expenses"));

                PieDataSet pieDataSet = new PieDataSet(pieEntries, "MPESA Transactions");

                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.GRAY);
                colors.add(Color.MAGENTA);
//                colors.add(Color.CYAN);
//                colors.add(Color.YELLOW);

                PieData pieData = new PieData(pieDataSet);
                pieDataSet.setSliceSpace(5f);
                pieDataSet.setValueTextSize(14);
                pieDataSet.setValueTextColor(Color.WHITE);
                pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
//                pieDataSet.setColors(colors);

                pieChart.setData(pieData);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();

                pieChart.setClickable(true);
                pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        PieEntry pe = (PieEntry) e;

                        String[] incomeItems = {
                                "Received Cash KSH " + receivedCashResult.get(monthSelected),
                                "Deposited Cash KSH " + depositedCashResult.get(monthSelected)
                        };

                        String[] expenseItems = {
                                "Airtime KSH " + airtimeResult.get(monthSelected),
                                "Sent Money KSH " + sendMoneyResult.get(monthSelected),
                                "Withdrawn Cash KSH " + withdrawResult.get(monthSelected),
                                "PayBill KSH " + paybillResult.get(monthSelected),
                                "Buy Goods and Services KSH " + buyGoodsAndServicesResult.get(monthSelected)
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        if (pe.getLabel().equals("Income")) {
                            builder.setTitle(pe.getLabel())
                                    .setItems(incomeItems, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                        }
                        else  if (pe.getLabel().equals("Expenses")) {
                            builder.setTitle(pe.getLabel())
                                    .setItems(expenseItems, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                        }
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void getMessage() {
        Cursor data = databaseHelper.getData();

        while (data.moveToNext()) {
            String messageBody = data.getString(1);
            String date = data.getString(2);

            if (messageBody.contains("You have received")) {
                String receivedCash = (StringUtils.substringBetween(messageBody, "received", "from")
                        .substring(4)).replace(",", "");
                Float receivedCashF = Float.parseFloat(receivedCash);
                income_receivedCash.put(date, receivedCashF);
                income.put(date, receivedCashF);
            }

            else if (messageBody.contains("Give") && messageBody.contains("cash")) {
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

            else if (messageBody.contains("sent to") && !messageBody.contains("for account")) {
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
            int months = Integer.parseInt(entry.getKey().split("/")[1]);
            String year = entry.getKey().split("/")[0];

            String monthString = new DateFormatSymbols().getMonths()[months-1];

            monthValues.add(monthString);
            yearValues.add(year);
        }

        for (Map.Entry<String, Float> receivedCashEntry  : income_receivedCash.entrySet()) {
            String key = receivedCashEntry.getKey().split("-")[0] + "/" + receivedCashEntry.getKey().split("-")[1];
            Float value = receivedCashEntry.getValue();
            Float oldValue = receivedCashResult.get(key) != null ? receivedCashResult.get(key) : 0;
            receivedCashResult.put(key, oldValue + value);
        }
    }
}