package com.svp.svp.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
import com.svp.svp.Adapter.PagerAdapter_ChargeTransactions;
import com.svp.svp.Constants.Constants_Network;
import com.svp.svp.Objects.Source;
import com.svp.svp.Objects.Transaction;
import com.svp.svp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Eric Schumacher on 18.12.2017.
 */

public class Activity_Update extends AppCompatActivity {

    // Layout
    TextView tvTransCharged;
    TextView tvTransNoModel;
    TextView tvTransMultiModel;
    Button bRestart;
    ViewPager mViewPager;

    // Variables
    private ArrayList<Source> mSources;
    final ArrayList<Transaction> mNo_Model = new ArrayList<Transaction>();
    final ArrayList<Transaction> mMultiple_Model = new ArrayList<Transaction>();
    String mLastUrl;
    PagerAdapter_ChargeTransactions mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setLayout();

        // Set up Adapter
        mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), mNo_Model);
        mViewPager.setAdapter(mAdapter);

        // Update
        mSources = createSources();
        processTransactions();
    }

    private void setLayout() {
        tvTransCharged = findViewById(R.id.tvChargedTransactions);
        tvTransNoModel = findViewById(R.id.tvTransactionsNoModel);
        tvTransMultiModel = findViewById(R.id.tvTransactionsNoModel);
        bRestart = findViewById(R.id.bRestart);
        mViewPager = findViewById(R.id.viewPager);
    }

    public void updateAdapter() {

    }

    public void processTransactions() {
        mLastUrl = "";

        // Check if there ara any transaction files
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        for (int y = currentYear; y >= currentYear - 1; y--) {
            for (int m = y == currentYear ? currentMonth : 12; m > 0; m--) {
                for (Source source : mSources) {
                    String year = Integer.toString(y);
                    String year_month = year + "_" + Integer.toString(m);
                    String[] urlToCheck = {"http://svp-server.com/svp-gmbh/dagobert/transactions/" + year + "/" + year_month + "/" + source.getFileName() + "_" + year_month + ".csv"};
                    // Start process
                    new receiveTransactions(source, urlToCheck[0]).execute(urlToCheck);
                    if (y == currentYear - 1 && m == 1) {
                        mLastUrl = urlToCheck[0];
                    }
                }
            }
        }
    }

    private void operateTransactions(ArrayList<Transaction> transactions, final String transactionUrl) {
        int counter = 0;


        for (final Transaction transaction : transactions) {
            counter++;

            RequestQueue queue = null;
            queue = Volley.newRequestQueue(this);
            final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/operations.php/processTransaction";
            final JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("name", transaction.getName());
                jsonBody.put("type", transaction.getType());
                jsonBody.put("detail_one", transaction.getDetailOne());
                jsonBody.put("detail_two", transaction.getDetailOne());
                jsonBody.put("amount", transaction.getAmount());
                jsonBody.put("date", transaction.getDate());
                final String requestBody = jsonBody.toString();
                Log.i("RequestBody " + Integer.toString(counter), requestBody);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("ResponseFromServer", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.NO_MODEL)) {
                                // Save transaction in separate ArrayList
                                mNo_Model.add(transaction);
                                mAdapter.notifyDataSetChanged();

                            }
                            if (!jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.MULTIPLE_MODEL)) {
                                // Fusion with separate ArrayList
                                mMultiple_Model.add(transaction);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("url", transactionUrl);
                        Log.i("url", mLastUrl);
                        if (transactionUrl.equals(mLastUrl)) {
                            Log.i("Last Call", Integer.toString(mNo_Model.size()));
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

                if (counter == 5) {
                    break;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    class receiveTransactions extends AsyncTask<String, String, ArrayList<Transaction>> {

        Source mSource;
        String mUrl;

        public receiveTransactions(Source source, String url) {
            mSource = source;
            mUrl = url;
        }

        protected ArrayList<Transaction> doInBackground(String... urls) {

            // Initiate return variable
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            mUrl = urls[0];

            // Open InputStream
            InputStream inputStream = null;
            try {
                inputStream = new URL(urls[0]).openStream();
            } catch (Exception e) {
                //Log.i("Failure", urls[0]);
            }
            if (inputStream != null) {
                Log.i("Succes", "Found file");
                // Read Data into return variable
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));

                    reader.readLine(); // jump first line - It's just the header
                    String csvLine;
                    while ((csvLine = reader.readLine()) != null) {
                        String[] row = csvLine.split(";");
                        ArrayList<String> arrayLine = new ArrayList<>();
                        for (String eachWord : row) //Iterate each String from the array
                            arrayLine.add(eachWord);
                        double amount = Double.parseDouble(arrayLine.get(mSource.getAmountPosition()).replace(",", ".").replaceAll("€, £", ""));
                        transactions.add(new Transaction(arrayLine.get(mSource.getCodePosition()), arrayLine.get(mSource.getNamePosition()), arrayLine.get(mSource.getTypePosition()), mSource.getDetailOnePosition() == 0 ? "empty" : arrayLine.get(mSource.getDetailOnePosition()), mSource.getDetailTwoPosition() == 0 ? "empty" : arrayLine.get(mSource.getDetailTwoPosition()), amount, formatDateForSQL(arrayLine.get(mSource.getDatePosition())), mSource.getBankAccountId()));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("Error in reading CSV file: " + ex);
                } finally {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        throw new RuntimeException("Error while closing input stream: " + e);
                    }
                }
            }
            return transactions;
        }

        protected void onPostExecute(ArrayList<Transaction> t) {
            if (t.size() > 0) operateTransactions(t, mUrl);
        }
    }

    private String formatDateForSQL(String date) {
        date = date.replace(".", "");
        date = date.substring(4, 8) + date.substring(2, 4) + date.substring(0, 2);
        return date;
    }

    private ArrayList<Source> createSources() {
        ArrayList<Source> sources = new ArrayList<>();
        sources.add(new Source("Amazon_DE", 1, 8, 3, 4, 0, 6, 1, 4));
        sources.add(new Source("Amazon_UK", 1, 8, 3, 4, 0, 6, 1, 5));
        sources.add(new Source("Amazon_ES", 1, 8, 3, 4, 0, 6, 1, 6));
        sources.add(new Source("Commerzbank_4600", 3, 3, 2, 0, 0, 4, 1, 1));
        sources.add(new Source("Commerzbank_9200", 3, 3, 2, 0, 0, 4, 1, 2));
        sources.add(new Source("Commerzbank_4500", 3, 3, 2, 0, 0, 4, 1, 3));
        sources.add(new Source("Paypal", 12, 3, 4, 15, 5, 8, 0, 7));
        //sources.add(new Source("GLS", 2, 9, 4, 5, 0, 9, 1, 8));
        return sources;
    }
}
