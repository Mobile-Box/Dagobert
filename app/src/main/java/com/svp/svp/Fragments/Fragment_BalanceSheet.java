package com.svp.svp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.svp.svp.Activities.Activity_Main;
import com.svp.svp.Adapter.ListAdapter_BalanceSheets;
import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Constants.Constants_Network;
import com.svp.svp.Objects.BalanceSheet.BalanceSheet;
import com.svp.svp.Objects.BalanceSheet.BalanceSheet_Account;
import com.svp.svp.Objects.Navigation.Navigation_Date;
import com.svp.svp.Objects.Navigation.Navigation_Month;
import com.svp.svp.Objects.Navigation.Navigation_Year;
import com.svp.svp.R;
import com.svp.svp.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Eric Schumacher on 31.12.2017.
 */

public class Fragment_BalanceSheet extends Fragment implements View.OnClickListener {

    // Layout
    View mLayout;
    RecyclerView mRecyclerView;
    FloatingActionButton fabBack;

    // Variables
    ArrayList<BalanceSheet> mListBalanceSheet;
    ListAdapter_BalanceSheets mAdapter;
    int mAccountId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Layout
        mLayout = inflater.inflate(R.layout.fragment_balance_sheet, container, false);
        setLayout();

        // Get data
        mAccountId = getArguments().getInt(Constants_Intern.ID);

        // Get URL
        String url = buildUrl();
        Log.i("MyUrlLL: ", url);

        // Get balanceSheet content and fill Adapter with it
        mListBalanceSheet = new ArrayList<>();
        mAdapter = new ListAdapter_BalanceSheets(getActivity(), mListBalanceSheet);
        mRecyclerView.setAdapter(mAdapter);
        final JSONObject jsonBody = new JSONObject();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mListBalanceSheet.clear();
                    Log.i("ResponseFromServer", response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString(Constants_Network.BS_TYPE).equals(Constants_Network.BS_TYPE_ACCOUNT) || jsonObject.getString(Constants_Network.BS_TYPE).equals(Constants_Network.BS_TYPE_SUBACCOUNT) || jsonObject.getString(Constants_Network.BS_TYPE).equals(Constants_Network.BS_TYPE_OPERATION)) {
                                BalanceSheet_Account bs_account = new BalanceSheet_Account(jsonObject.getString(Constants_Network.BS_TYPE), jsonObject.getString(Constants_Network.BS_NAME), jsonObject.getDouble(Constants_Network.BS_AMOUNT), jsonObject.getInt(Constants_Network.BS_ID), (Navigation_Date)getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE));
                                mListBalanceSheet.add(bs_account);
                            } else {
                                BalanceSheet bs = new BalanceSheet(jsonObject.getString(Constants_Network.BS_TYPE), jsonObject.getString(Constants_Network.BS_NAME), (jsonObject.get(Constants_Network.BS_AMOUNT) !=null) ? jsonObject.getDouble(Constants_Network.BS_AMOUNT) : 111111, (Navigation_Date)getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE));
                                mListBalanceSheet.add(bs);
                            }
                            mAdapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }}) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
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
            AppController.getInstance().addToRequestQueue(stringRequest, "tag_str_req");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLayout;
    }



    private void setLayout() {
        mRecyclerView = mLayout.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fabBack = mLayout.findViewById(R.id.fabBack);
        fabBack.setOnClickListener(this);
        Log.i("Cheeeck", getArguments().getString(Constants_Intern.BALANCESHEET_TYPE));
        if (!getArguments().getString(Constants_Intern.BALANCESHEET_TYPE).equals(Constants_Intern.BALANCESHEET_TYPE_ACCOUNTS)) fabBack.setVisibility(View.VISIBLE);
    }

    private String buildUrl() {
        String urlStart = "http://svp-server.com/svp-gmbh/dagobert/src/routes/api.php/balanceSheet";
        return urlStart+"/"+getArguments().getString(Constants_Intern.BALANCESHEET_TYPE)+"/"+getArguments().getInt(Constants_Intern.ID)+"/"+(getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE) instanceof Navigation_Month ? Integer.toString((((Navigation_Month) getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE)).getYear()))+"/"+Integer.toString(((Navigation_Month)getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE)).getMonth()) : Integer.toString((((Navigation_Year) getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE)).getYear())));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabBack:
                if (getArguments().getString(Constants_Intern.BALANCESHEET_TYPE).equals(Constants_Intern.BALANCESHEET_TYPE_OPERATIONS)) {
                    try {
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://svp-server.com/svp-gmbh/dagobert/src/routes/api.php/balanceSheet/account_id/"+Integer.toString(mAccountId), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {mListBalanceSheet.clear();
                                Log.i("ResponseFromServerAccountId", response);
                                navigateBack(Integer.parseInt(response));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY", error.toString());
                            }}) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
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
                        AppController.getInstance().addToRequestQueue(stringRequest, "tag_str_req");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    navigateBack(0);}
        }
    }

    private void navigateBack(int accountId) {
        Log.i("Acccounts", BalanceSheet.getBackLevel(getArguments().getString(Constants_Intern.BALANCESHEET_TYPE)));
        Log.i("IID", Integer.toString(getArguments().getInt(Constants_Intern.ID)));
        Navigation_Date navigation_date = null;
        if (getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE) instanceof Navigation_Month) {
            navigation_date = (Navigation_Month)getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE);
        } else {
            navigation_date = (Navigation_Year)getArguments().getSerializable(Constants_Intern.NAVIGATION_DATE);
        }
        ((Activity_Main)getActivity()).buildBalanceSheetFragment(BalanceSheet.getBackLevel(getArguments().getString(Constants_Intern.BALANCESHEET_TYPE)), navigation_date, accountId);

    }
}
