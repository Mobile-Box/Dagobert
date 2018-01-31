package com.svp.svp.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.svp.svp.Adapter.PagerAdapter_ChargeTransactions;
import com.svp.svp.Constants.Constants_Network;
import com.svp.svp.Objects.Source;
import com.svp.svp.Objects.Transaction;
import com.svp.svp.R;
import com.svp.svp.Volley.AppController;

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
    TextView tvFiles;
    TextView tvTotal;
    TextView tvError;
    TextView tvCharged;
    TextView tvNoOrMultiModel;
    TextView tvAlreadyCharged;
    ViewPager mViewPager;

    // Variables
    private ArrayList<Source> mSources;
    final ArrayList<Transaction> mManuals = new ArrayList<Transaction>();
    String mLastUrl;
    PagerAdapter_ChargeTransactions mAdapter;

    // Counter
    private int cFiles = 0;
    private int cTotal = 0;
    private int cAlreadyCharged = 0;
    private int cError = 0;
    private int cCharged = 0;
    private int cNoOrMultipleModel = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setLayout();

        // Set up Adapter
        mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), mManuals);
        mViewPager.setAdapter(mAdapter);

        // Update
        mSources = createSources();
        processTransactions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cCharged > 0) {
            // Calculate taxes

        }
    }

    private void setLayout() {
        tvCharged = findViewById(R.id.tvChargedTransactions);
        tvNoOrMultiModel = findViewById(R.id.tvTransactionsNoOrMultipleModel);
        tvAlreadyCharged = findViewById(R.id.tvAlreadyChargedTransactions);
        tvError = findViewById(R.id.tvError);
        tvFiles = findViewById(R.id.tvFiles);
        tvTotal = findViewById(R.id.tvTotal);
        mViewPager = findViewById(R.id.viewPager);
    }

    public void processTransactions() {
        mLastUrl = "";
        int counter = 0;

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

            //RequestQueue queue = null;
            //queue = Volley.newRequestQueue(this);
            final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/operations.php/processTransaction";
            final JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("code", transaction.getCode());
                jsonBody.put("name", transaction.getName());
                jsonBody.put("type", transaction.getType());
                jsonBody.put("detail_one", transaction.getDetailOne());
                jsonBody.put("detail_two", transaction.getDetailTwo());
                jsonBody.put("amount", transaction.getAmount());
                jsonBody.put("date", transaction.getDate());
                jsonBody.put("id_bank_account", transaction.getBankAccountId());
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
                                tvNoOrMultiModel.setText(getString(R.string.transaction_no_or_multiple_model)+" "+Integer.toString(cNoOrMultipleModel++));
                                mManuals.add(transaction);
                                mAdapter.notifyDataSetChanged();

                            }
                            if (!jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.MULTIPLE_MODEL)) {
                                // Fusion with separate ArrayList
                                tvNoOrMultiModel.setText(getString(R.string.transaction_no_or_multiple_model)+" "+Integer.toString(cNoOrMultipleModel++));
                                mManuals.add(transaction);
                                mAdapter.notifyDataSetChanged();
                            }
                            if (jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.OPERATION_EXISTS_ALREADY)) {

                                tvAlreadyCharged.setText(getString(R.string.transactions_already_charged)+" "+Integer.toString(cAlreadyCharged++));
                                Log.i("Fuck2", Integer.toString(cAlreadyCharged++));
                                tvAlreadyCharged.setText("Fuck");
                            }
                            if (jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.TRANSACTION_OPERATED)) {
                                Log.i("Fuck", Integer.toString(cCharged++));
                                tvCharged.setText(getString(R.string.charged_transactions)+" "+Integer.toString(cCharged++));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("url", transactionUrl);
                        Log.i("url", mLastUrl);
                        if (transactionUrl.equals(mLastUrl)) {
                            Log.i("Last Call", Integer.toString(mManuals.size()));
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
                        } finally {

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
                AppController.getInstance().addToRequestQueue(stringRequest, "tag_str_req");
                //queue.add(stringRequest);
                //queue.getCache().clear();
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

            int counter = 0;

            // Initiate return variable
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            mUrl = urls[0];

            // Open InputStream
            InputStream inputStream = null;
            try {
                inputStream = new URL(urls[0]).openStream();
                cFiles++;
            } catch (Exception e) {
                Log.i("ErrorCode5: ", "error");
                e.printStackTrace();
                //cError++;
            }
            if (inputStream != null) {
                // Read Data into return variable
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));

                    reader.readLine(); // jump first line - It's just the header
                    String csvLine;
                    int rowNumber = 0;
                    while ((csvLine = reader.readLine()) != null) {
                        rowNumber++;
                        String[] row = csvLine.split(";");
                        ArrayList<String> arrayLine = new ArrayList<>();
                        for (String eachWord : row) //Iterate each String from the array
                            arrayLine.add(eachWord);
                        if (arrayLine.size() > 0) {
                            String a;
                            try {
                                a = arrayLine.get(mSource.getAmountPosition()).replace(".", "");
                                a = a.replace(",", ".");
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Log.i("ErrorCode1:", arrayLine.toString());
                                cError++;
                                continue;
                            }

                            Log.i("Show", arrayLine.toString());

                            if (mSource.getBankAccountId() == 5) a = a.substring(1); // UK
                            double amount;
                            amount = Double.parseDouble(a);

                            String code;
                            try {
                                code = mSource.buildCode(arrayLine, rowNumber);
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Log.i("ErrorCode3:", arrayLine.toString());
                                cError++;
                                continue;
                            }

                            Log.i("Code", code);
                            String name="";
                            if (mSource.getFileName().matches("Amazon.*") && arrayLine.size() < 9) {
                                Log.i("Triggered", "Error");
                                Log.i("TriggeredWhat", arrayLine.toString());
                                name = Constants_Network.EMPTY;
                            } else {
                                if (mSource.getFileName().matches("GLS.*")) {

                                    for (int i = 5; i<19; i++) {
                                        name = name + arrayLine.get(i);
                                    }
                                    Log.i("OHHHH", "name");
                                    Log.i("GLSWhat", arrayLine.toString());
                                } else {
                                    name = ((arrayLine.get(mSource.getNamePosition())).equals("")) ? Constants_Network.EMPTY : arrayLine.get(mSource.getNamePosition());
                                }
                            }

                            name = name.replace("'", "");
                            // Get rid of all " ' " signs

                            try {
                                transactions.add(new Transaction(code, name,
                                        ((arrayLine.get(mSource.getTypePosition())).equals("")) ? Constants_Network.EMPTY : arrayLine.get(mSource.getTypePosition()),
                                        (mSource.getDetailOnePosition() == 0 || (arrayLine.get(mSource.getDetailOnePosition())).equals("")) ? Constants_Network.EMPTY : arrayLine.get(mSource.getDetailOnePosition()),
                                        (mSource.getDetailTwoPosition() == 0 || (arrayLine.get(mSource.getDetailTwoPosition())).equals("")) ? Constants_Network.EMPTY : arrayLine.get(mSource.getDetailTwoPosition()),
                                        amount, formatDateForSQL(arrayLine.get(mSource.getDatePosition())), mSource.getBankAccountId()));
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Log.i("ErrorCode2:", arrayLine.toString());
                                Log.i("Goo", "Code: "+ code+ " DetailOne: "+ ((mSource.getDetailOnePosition() == 0 || (arrayLine.get(mSource.getDetailOnePosition())).equals("")) ? Constants_Network.EMPTY : arrayLine.get(mSource.getDetailOnePosition())+ " Name: "+ name));
                                Log.i("DATEPROB: ", arrayLine.get(mSource.getDatePosition()));
                                cError++;
                                if (arrayLine.get(1).isEmpty()) {
                                    Log.i("ErrorCode2-Test", "isEmpty()");
                                }
                                if (arrayLine.get(1).equals("")) {
                                    Log.i("ErrorCode2-Test", "equals()");
                                }
                            }
                        }
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
            if (t.size() > 0) {
                tvFiles.setText(getString(R.string.number_files)+Integer.toString(cFiles));
                cTotal = cTotal+t.size();
                tvTotal.setText(getString(R.string.transactions_total)+Integer.toString(cTotal));
                tvError.setText(getString(R.string.number_error)+Integer.toString(cError));
                operateTransactions(t, mUrl);
            }
        }
    }

    private String formatDateForSQL(String date) {
        date = date.replace(".", "");
        date = date.substring(4, 8) + date.substring(2, 4) + date.substring(0, 2);
        return date;
    }

    private ArrayList<Source> createSources() {
        ArrayList<Source> sources = new ArrayList<>();
        sources.add(new Source("Amazon_DE", 1, 8, 3, 4, 0, 6, 0, 4));
        sources.add(new Source("Amazon_UK", 1, 8, 3, 4, 0, 6, 0, 5));
        sources.add(new Source("Amazon_ES", 1, 8, 3, 4, 0, 6, 0, 6));
        sources.add(new Source("Commerzbank_4600", 3, 3, 2, 0, 0, 4, 1, 1));
        sources.add(new Source("Commerzbank_9200", 3, 3, 2, 0, 0, 4, 1, 2));
        sources.add(new Source("Commerzbank_4500", 3, 3, 2, 0, 0, 4, 1, 3));
        sources.add(new Source("Paypal", 12, 3, 4, 15, 5, 8, 0, 7));
        sources.add(new Source("GLS", 2, 6, 4, 3, 0, 19, 2, 8));
        return sources;
    }

    public void manuallyCharged(Transaction transaction) {
        tvCharged.setText(getString(R.string.charged_transactions)+" "+Integer.toString(cCharged++));
        tvNoOrMultiModel.setText(getString(R.string.transaction_no_or_multiple_model)+" "+Integer.toString(cNoOrMultipleModel--));
        mManuals.remove(transaction);
        mAdapter.notifyDataSetChanged();
    }
}
