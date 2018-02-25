package com.svp.svp.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Fragments.Fragment_Transaction_Show;
import com.svp.svp.Objects.Operation;
import com.svp.svp.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Eric Schumacher on 24.02.2018.
 */

public class Activity_Recharge extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recharge);
        getOperationAndShowInFragment(getIntent().getIntExtra(Constants_Intern.ID, 1));
    }

    private void getOperationAndShowInFragment(int id) {
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/operation/"+Integer.toString(id);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("Respone", response);
                        JSONObject jsonObject = new JSONObject(response);
                        Operation operation = new Operation(jsonObject.getInt("id"), jsonObject.getString("code"),
                                jsonObject.getString("name"), jsonObject.getString("date"), jsonObject.getDouble("amount_gross"), jsonObject.getDouble("amount_net"), jsonObject.getInt("id_svp_subaccount"), ((int)((jsonObject.getDouble("amount_gross")/jsonObject.getDouble("amount_net"))*100-100)));
                        Fragment_Transaction_Show fragment = new Fragment_Transaction_Show();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants_Intern.OPERATION, operation);
                        fragment.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.flMain, fragment);
                        transaction.commit();
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
}
