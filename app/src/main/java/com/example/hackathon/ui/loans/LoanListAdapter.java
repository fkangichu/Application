package com.example.hackathon.ui.loans;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hackathon.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static android.content.ContentValues.TAG;

public class LoanListAdapter extends BaseAdapter {
    Context context;
    String response_data;
    LayoutInflater inflter;
    JSONArray ratesData;

    public  LoanListAdapter(Context _context, String _response_data){
        this.context = _context;
        this.response_data = _response_data;
        inflter = (LayoutInflater.from(_context));
        this.ratesData = new JSONArray();
        this.generateData();

    }

    @Override
    public int getCount() {
        return ratesData.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void generateData() {
        //JSONObject jsonResponse = new JSONObject(response_data.toString());
        try{
            JSONArray jsonArray = new JSONArray(response_data);
            for (int i=0; i < jsonArray.length(); i++){
                JSONArray jsonArray_sub = jsonArray.getJSONObject(i).getJSONArray("rates");
                for (int j = 0; j < jsonArray_sub.length(); j++) {

                    ratesData.put(jsonArray_sub.getJSONObject(j));
                }
            }

        }catch (Exception ex){

        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_available_loans_listview, null);
        TextView loan_type = view.findViewById(R.id.loan_type);
        TextView loan_rate = view.findViewById(R.id.loan_rate);
        TextView loan_amount = view.findViewById(R.id.loan_amount);
        Button btn_request_loan = view.findViewById(R.id.btn_request_loan);

        try{
            //loan_type.setText(ratesData.getJSONObject(i).getString("rateType"));
            loan_type.setText("Loan Option ".concat(String.valueOf(i + 1)));
            loan_rate.setText("Rate - " + ratesData.getJSONObject(i).getString("rate"));
            loan_amount.setText(ratesData.getJSONObject(i).getString("currency").concat(" - ").concat(ratesData.getJSONObject(i).getString("amount")));
            btn_request_loan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    final EditText input = new EditText(context);
                    builder.setView(input);

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    final CharSequence[] charSequence = new CharSequence[] {"Get full amount","Input different amount"};

                    builder.setTitle("Request amount")
                            .setSingleChoiceItems(charSequence, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("Get Loan", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ListView lv = ((AlertDialog)dialogInterface).getListView();
                                    Log.i(TAG, "onClick: "+ lv.getAdapter().getItem(lv.getCheckedItemPosition()));
                                }
                            })
                            .show();

                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return view;
    }
}
