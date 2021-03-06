package com.svp.svp.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Objects.Operation;
import com.svp.svp.R;
import com.svp.svp.Utilitys.Utility_Dates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Eric Schumacher on 20.02.2018.
 */

public class Fragment_Transaction_Show extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    // Layout
    View mLayout;
    TextView tvAccount;
    EditText etName;
    TextView etAmount;
    TextView tvDate;
    TextView tvSubaccount;
    TextView tvValueAddedTax;
    Button bCharge;
    LinearLayout llAddModel;

    // Variables
    int mSubaccountId;
    int mValueAddedTax;
    double mAmount;
    Operation mOperation;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_charge_transaction_new, container, false);
        mContext = getActivity();

        // Layout
        setLayout();

        // Data
        mOperation = (Operation)getArguments().getSerializable(Constants_Intern.OPERATION);
        mAmount = mOperation.getAmount_gross();
        //etAmount.setText(String.format("%.2f", mAmount) + " €");
        etAmount.setText(Double.toString(mAmount));
        tvDate.setText(Utility_Dates.decodeDateFromSQL(mOperation.getDate()));
        etName.setText(mOperation.getName());
        mSubaccountId = mOperation.getId_svp_subaccount();
        tvSubaccount.setText(Integer.toString(mOperation.getId_svp_subaccount()));
        mValueAddedTax = mOperation.getUst_value();
        tvValueAddedTax.setText(Integer.toString(mOperation.getUst_value())+" %");

        return mLayout;
    }

    private void setLayout() {
        llAddModel = mLayout.findViewById(R.id.llModel);
        llAddModel.setVisibility(View.GONE);
        tvAccount = mLayout.findViewById(R.id.tvAccount);
        etAmount = mLayout.findViewById(R.id.etAmount);
        tvDate = mLayout.findViewById(R.id.tvDate);
        etName = mLayout.findViewById(R.id.etName);
        tvSubaccount = mLayout.findViewById(R.id.tvSubaccount);
        tvValueAddedTax = mLayout.findViewById(R.id.tvValueAddedTax);
        bCharge = mLayout.findViewById(R.id.bCharge);
        bCharge.setText(getString(R.string.recharge));

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    mAmount = Double.parseDouble(editable.toString());
                }

            }
        });

        // ClickListener Content
        tvSubaccount.setOnClickListener(this);
        tvValueAddedTax.setOnClickListener(this);
        tvDate.setOnClickListener(this);

        // ClickListener Buttons
        bCharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bCharge:
                reCharge();
                break;
            case R.id.tvSubaccount:
                Log.i("Was", "los");
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
                                        mValueAddedTax = 19;
                                        break;
                                    case 1:
                                        mValueAddedTax = 7;
                                        break;
                                    case 2:
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
            case R.id.tvDate:
                DatePickerDialog dialog = new DatePickerDialog(mContext, this, mOperation.getYear(), mOperation.getMonth(), mOperation.getDay());
                dialog.show();
            default:
                break;
        }
    }

    private void accountClickReaction() {
                RequestQueue queue;
                Log.i("Spinnst", "Du");
                queue = Volley.newRequestQueue(getActivity());
                final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/accounts";
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

    // Network connection
    private void reCharge() {
        RequestQueue queue;
        queue = Volley.newRequestQueue(getActivity());
        final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/operations/recharge";
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("code", mOperation.getCode());
            jsonBody.put("name", etName.getText().toString());
            jsonBody.put("amount", mAmount);
            jsonBody.put("date", mOperation.getDate());
            jsonBody.put("ust_value", mValueAddedTax);
            jsonBody.put("id_svp_subaccount", mSubaccountId);
            final String requestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String r = jsonBody.getString("RESPONSE");
                        String d = jsonBody.getString("DETAILS");
                        Toast.makeText(getActivity(), r+": "+d, Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        mOperation.setYear(i);
        mOperation.setMonth(i1);
        mOperation.setDay(i2);
        tvDate.setText(Utility_Dates.decodeDateFromSQL(mOperation.getDate()));
    }
}
