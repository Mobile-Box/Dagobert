package com.svp.svp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.svp.svp.Objects.Source;
import com.svp.svp.Objects.Transaction;
import com.svp.svp.R;
import com.svp.svp.Utilitys.Utility_Dates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class Fragment_Transaction_Charge extends Fragment implements View.OnClickListener, View.OnLongClickListener {

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
    TextView tvValueAddedTax;
    Button bCharge;
    Button bChargeWithModel;
    LinearLayout llDetailOne;
    LinearLayout llDetailTwo;

    // Variables
    Transaction mTransaction;
    int mSubaccountId;
    int mValueAddedTax;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Layout
        mLayout = inflater.inflate(R.layout.fragment_charge_transaction_new, container, false);
        setLayout();

        // Data
        Bundle bundle = getArguments();
        mTransaction = (Transaction) bundle.getSerializable(Constants_Intern.TRANSACTION);
        tvAccount.setText(Source.getSpeceficBankAccountName(mTransaction.getBankAccountId()));
        DecimalFormat df2 = new DecimalFormat(".##");
        tvAmount.setText(String.format("%.2f", mTransaction.getAmount()) + " €");
        tvDate.setText(Utility_Dates.decodeDateFromSQL(mTransaction.getDate()));
        etName.setText(mTransaction.getName());
        etType.setText(mTransaction.getType());
        etDetailOne.setText(mTransaction.getDetailOne());
        etDetailTwo.setText(mTransaction.getDetailTwo());
        tvSubaccount.setText(Integer.toString(mTransaction.getSvpSubAccountId()));

        mValueAddedTax = 19;
        tvValueAddedTax.setText(Integer.toString(mValueAddedTax) + " %");

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
        tvValueAddedTax = mLayout.findViewById(R.id.tvValueAddedTax);
        bCharge = mLayout.findViewById(R.id.bCharge);
        bChargeWithModel = mLayout.findViewById(R.id.bChargeWithModel);
        llDetailOne = mLayout.findViewById(R.id.llDetailOne);
        llDetailTwo = mLayout.findViewById(R.id.llDetailTwo);

        // ClickListener Content
        tvSubaccount.setOnClickListener(this);
        tvValueAddedTax.setOnClickListener(this);
        tvSubaccount.setOnLongClickListener(this);

        // ClickListener Buttons
        bCharge.setOnClickListener(this);
        bChargeWithModel.setOnClickListener(this);
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
            jsonBody.put("id_svp_subaccount", mSubaccountId);
            jsonBody.put("ust_value", tvValueAddedTax.getText().toString());
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
            mTransaction.setName(etName.getText().toString());
            jsonBody.put("amount", mTransaction.getAmount());
            jsonBody.put("date", mTransaction.getDate());
            jsonBody.put("ust_value", mValueAddedTax);
            jsonBody.put("id_svp_subaccount", mSubaccountId);
            final String requestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Response - Charge", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.TRANSACTION_OPERATED)) {
                            Toast.makeText(getActivity(), getString(R.string.transaction_operated), Toast.LENGTH_LONG).show();
                            ((Activity_Update) getActivity()).manuallyCharged(mTransaction, jsonObject.getInt(Constants_Network.ID_OPERATION));

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

    private void accountClickReaction() {
                RequestQueue queue;
                queue = Volley.newRequestQueue(getActivity());
                final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/accounts";
                final JSONObject jsonBody = new JSONObject();
                try {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response - Model-Add", response);
                            try {
                                final JSONArray jsonArray = new JSONArray(response);
                                CharSequence[] accountNames = new CharSequence[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    accountNames[i] = jsonObject.getString("name");
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                        .setTitle(getString(R.string.accounts))
                                        .setItems(accountNames, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                // pass accountId to subaccount selector list
                                                try {
                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                    showSubaccountsAndReact(jsonObject.getString("name"), jsonObject.getInt("id"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    dialogInterface.dismiss();
                                                }
                                            }
                                        })
                                        .setCancelable(true);
                                builder.show();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }

    private void showSubaccountsAndReact(final String accountName, int accountId) {
        RequestQueue queue;
        queue = Volley.newRequestQueue(getActivity());
        final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/subaccounts/" + Integer.toString(accountId);
        final JSONObject jsonBody = new JSONObject();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final JSONArray jsonArray = new JSONArray(response);
                        CharSequence[] subaccountNames = new CharSequence[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            subaccountNames[i] = jsonObject.getString("name");
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle(accountName)
                                .setItems(subaccountNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // save subaccountId to TextView
                                        try {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            tvSubaccount.setText(accountName + " / " + jsonObject.getString("name"));
                                            mSubaccountId = jsonObject.getInt("id");
                                            mTransaction.setSvpSubAccountId(jsonObject.getInt("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } finally {
                                            dialogInterface.dismiss();
                                        }
                                    }
                                })
                                .setCancelable(true);
                        builder.show();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bCharge:
                chargeTransaction();
                break;
            case R.id.bChargeWithModel:
                addModel();
                chargeTransaction();
                break;
            case R.id.tvSubaccount:
                accountClickReaction();
                break;
            case R.id.tvValueAddedTax:
                final CharSequence[] items = {"19 %", "7 %", "0 %"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.value_added_tax))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tvValueAddedTax.setText(items[i]);
                                switch (i) {
                                    case 0:
                                        mTransaction.setUstValue(19);
                                        mValueAddedTax = 19;
                                        break;
                                    case 1:
                                        mTransaction.setUstValue(7);
                                        mValueAddedTax = 7;
                                        break;
                                    case 2:
                                        mTransaction.setUstValue(0);
                                        mValueAddedTax = 0;
                                        break;
                                    default:
                                        break;
                                }
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(true);
                builder.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.tvSubaccount:
                mSubaccountId = 2;
                tvSubaccount.setText("Verkauf / Ebay");
                return true;
        }
        return false;
    }
}
