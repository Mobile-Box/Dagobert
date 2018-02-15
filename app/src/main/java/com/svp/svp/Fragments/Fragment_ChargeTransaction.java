package com.svp.svp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.svp.svp.Activities.Activity_Update;
import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Constants.Constants_Network;
import com.svp.svp.Objects.Transaction;
import com.svp.svp.R;
import com.svp.svp.Utilitys.Utility_Dates;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class Fragment_ChargeTransaction extends Fragment {

    // Layout
    View mLayout;
    TextView tvAccount;
    EditText etName;
    EditText etType;
    EditText etDetailOne;
    EditText etDetailTwo;
    TextView tvAmount;
    TextView tvDate;
    TextView tvSubaccount;
    EditText etValueAddedTax;
    Button bCharge;
    Button bChargeWithModel;
    LinearLayout llDetailOne;
    LinearLayout llDetailTwo;

    // Variables
    Transaction mTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Layout
        mLayout = inflater.inflate(R.layout.fragment_charge_transaction_new, container, false);
        setLayout();

        // Data
        Bundle bundle = getArguments();
        mTransaction = (Transaction)bundle.getSerializable(Constants_Intern.TRANSACTION);
        tvAccount.setText(Integer.toString(mTransaction.getBankAccountId()));
        tvAmount.setText(Double.toString(mTransaction.getAmount())+" €");
        tvDate.setText(Utility_Dates.decodeDateFromSQL(mTransaction.getDate()));
        etName.setText(mTransaction.getName());
        etType.setText(mTransaction.getType());
        etDetailOne.setText(mTransaction.getDetailOne());
        etDetailTwo.setText(mTransaction.getDetailTwo());
        tvSubaccount.setText(Integer.toString(mTransaction.getSvpSubAccountId()));

        // Click Listener
        bCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chargeTransaction();
            }
        });

        bChargeWithModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addModel();
                //chargeTransaction();
            }
        });



        return mLayout;
    }

    private void setLayout() {
        tvAccount = mLayout.findViewById(R.id.tvAccount);
        tvAmount = mLayout.findViewById(R.id.tvAmount);
        tvDate = mLayout.findViewById(R.id.tvDate);
        etName = mLayout.findViewById(R.id.etName);
        etType = mLayout.findViewById(R.id.etType);
        etDetailOne = mLayout.findViewById(R.id.etDetailOne);
        etDetailTwo = mLayout.findViewById(R.id.etDetailTwo);
        tvSubaccount = mLayout.findViewById(R.id.tvSubaccount);
        etValueAddedTax = mLayout.findViewById(R.id.etValueAddedTax);
        bCharge = mLayout.findViewById(R.id.bCharge);
        bChargeWithModel = mLayout.findViewById(R.id.bChargeWithModel);
        llDetailOne = mLayout.findViewById(R.id.llDetailOne);
        llDetailTwo = mLayout.findViewById(R.id.llDetailTwo);
    }

    private void addModel() {
        RequestQueue queue;
        queue = Volley.newRequestQueue(getActivity());
        final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/addModel";
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_bank_account", mTransaction.getBankAccountId());
            jsonBody.put("name", etName.getText().toString());
            jsonBody.put("type", etType.getText().toString());
            jsonBody.put("detail_one", etDetailOne.getText().toString());
            jsonBody.put("detail_two", etDetailTwo.getText().toString());
            jsonBody.put("id_svp_subaccount", tvSubaccount.getText().toString());
            jsonBody.put("ust_value", etValueAddedTax.getText().toString());
            final String requestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Response - Model-Add", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.MODEL_ADDED)) {
                            Toast.makeText(getActivity(), getString(R.string.model_added), Toast.LENGTH_SHORT).show();
                        }
                        if (!jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.MODEL_EXISTS)) {
                            Toast.makeText(getActivity(), getString(R.string.model_exists), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null && response.statusCode == 200) {
                        responseString = new String(response.data);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            queue.add(stringRequest);
            queue.getCache().clear();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void chargeTransaction() {
        RequestQueue queue;
        queue = Volley.newRequestQueue(getActivity());
        final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/addTransaction";
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("code", mTransaction.getCode());
            jsonBody.put("name", etName.getText().toString());
            jsonBody.put("amount", mTransaction.getAmount());
            jsonBody.put("date", mTransaction.getDate());
            jsonBody.put("ust_value", etValueAddedTax.getText().toString());
            jsonBody.put("id_svp_subaccount", tvSubaccount.getText());
            final String requestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Response - Charge", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.TRANSACTION_OPERATED)){
                            ((Activity_Update)getActivity()).manuallyCharged(mTransaction);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null && response.statusCode == 200) {
                        responseString = new String(response.data);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            queue.add(stringRequest);
            queue.getCache().clear();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
