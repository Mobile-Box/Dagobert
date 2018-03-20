package com.svp.svp.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Constants.Constants_Network;
import com.svp.svp.Fragments.Fragment_Transaction_Show;
import com.svp.svp.Objects.Operation;
import com.svp.svp.Objects.Source;
import com.svp.svp.Objects.Transaction;
import com.svp.svp.R;
import com.svp.svp.Utilitys.Utility_Dates;
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

// Eric Test

public class Activity_Update extends AppCompatActivity {

    // Layout
    TextView tvFiles;
    TextView tvTotal;
    TextView tvError;
    TextView tvCharged;
    TextView tvAlreadyCharged;
    ViewPager mViewPager;
    TabLayout mTabLayout;

    // Variables
    private ArrayList<Source> mSources;
    final ArrayList<Transaction> tNoModel = new ArrayList<>();
    final ArrayList<Transaction> tMultipleModel = new ArrayList<>();
    final ArrayList<Transaction> tManuallyCharged = new ArrayList<>();
    String mLastUrl;
    PagerAdapter_ChargeTransactions mAdapter;

    // Constants
    final static int TAB_NO_MODEL = 0;
    final static int TAB_MULTIPLE_MODEL = 1;
    final static int TAB_CHARGED_MANUALLY = 2;
    private final static int TYPE_CHARGE = 0;
    private final static int TYPE_SHOW = 1;

    // Counter
    private int cFiles = 0;
    private int cTotal = 0;
    private int cALL = 0;
    private int cALL2 = 0;
    private int cAlreadyCharged = 0;
    private int cError = 0;
    private int cCharged = 0;
    private int cNoModel = 0;
    private int cMultipleModel = 0;
    private int cManuallyCharged = 0;
    int counter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setLayout();
        cALL = 0;

        // Set up Adapter for ViewPager
        mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), tNoModel, TYPE_CHARGE);
        mViewPager.setAdapter(mAdapter);

        // Set up TabLayout
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.transaction_no_model)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.transaction_multiple_model)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.transaction_charged_manually)));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case TAB_NO_MODEL:
                        mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), tNoModel, TYPE_CHARGE);
                        mViewPager.setAdapter(mAdapter);
                        break;
                    case TAB_MULTIPLE_MODEL:
                        mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), tMultipleModel, TYPE_CHARGE);
                        mViewPager.setAdapter(mAdapter);
                        break;
                    case TAB_CHARGED_MANUALLY:
                        mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), tManuallyCharged, TYPE_SHOW);
                        mViewPager.setAdapter(mAdapter);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
        tvAlreadyCharged = findViewById(R.id.tvAlreadyChargedTransactions);
        tvError = findViewById(R.id.tvError);
        tvFiles = findViewById(R.id.tvFiles);
        tvTotal = findViewById(R.id.tvTotal);
        mViewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tabLayout);
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
                    String[] urlToCheck = {"http://svp-server.com/svp-gmbh/dagobert/transactions/" + year + "/" + year_month + "/" + source.getFileName() + "_" + year_month + "." + source.getFileType()};
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


        for (final Transaction transaction : transactions) {
            counter++;
            Log.i("CounterLog: ", Integer.toString(counter));

            //RequestQueue queue = null;
            //queue = Volley.newRequestQueue(this);
            final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/processTransaction";
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
                //Log.i("RequestBody " + Integer.toString(counter), requestBody);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.i("ResponseFromServer", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // No Model
                            if (!jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.NO_MODEL)) {
                                // Save transaction in separate ArrayList
                                cNoModel = cNoModel + 1;
                                mTabLayout.getTabAt(0).setText(Integer.toString(cNoModel) + " - " + getString(R.string.transaction_no_model));
                                tNoModel.add(transaction);
                                mAdapter.notifyDataSetChanged();
                            }
                            // Multiple Model
                            if (!jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.MULTIPLE_MODEL)) {
                                // Fusion with separate ArrayList
                                cMultipleModel = cMultipleModel + 1;
                                mTabLayout.getTabAt(1).setText(Integer.toString(cMultipleModel) + " - " + getString(R.string.transaction_multiple_model));
                                tMultipleModel.add(transaction);
                                mAdapter.notifyDataSetChanged();
                            }
                            // Already charged
                            if (jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.OPERATION_EXISTS_ALREADY)) {
                                cAlreadyCharged = cAlreadyCharged + 1;
                                tvAlreadyCharged.setText(getString(R.string.transactions_already_charged) + " " + Integer.toString(cAlreadyCharged));
                            }
                            // Transaction charged
                            if (jsonObject.getString(Constants_Network.RESPONSE).equals(Constants_Network.SUCCESS) && jsonObject.getString(Constants_Network.DETAILS).equals(Constants_Network.TRANSACTION_OPERATED)) {
                                cCharged = cCharged + 1;
                                tvCharged.setText(getString(R.string.charged_transactions) + " " + Integer.toString(cCharged));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("url", transactionUrl);
                        Log.i("url", mLastUrl);
                        if (transactionUrl.equals(mLastUrl)) {
                            //Log.i("Last Call", Integer.toString(mManuals.size()));
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
                //Log.i("ErrorCode5: ", "error");
                e.printStackTrace();
                //cError++;
            }
            if (inputStream != null) {
                // Read Data into return variable
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                    reader.readLine(); // jump first line - It's just the header
                    String csvLine;
                    int rowNumber = 0;
                    while ((csvLine = reader.readLine()) != null) {
                        rowNumber++;
                        cTotal++;
                        //Log.i("BackFromServer: ", csvLine);
                        //String[] row = csvLine.split(";");
                        String[] row = csvLine.split(mSource.getFileSplit());
                        ArrayList<String> arrayLine = new ArrayList<>();
                        for (String eachWord : row) //Iterate each String from the array

                            arrayLine.add(eachWord);
                            Log.i("OUUtput", arrayLine.get(0));
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

                            if (mSource.getBankAccountId() == 5 || mSource.getBankAccountId() == 4 || mSource.getBankAccountId() == 6)
                                a = a.substring(1); // Pound and Euro
                            double amount;
                            try {
                                amount = Double.parseDouble(a);
                            } catch (NumberFormatException e) {
                                amount = 0;
                                e.printStackTrace();
                                cError++;
                                Log.i("Error9", a.toString());
                                continue;
                            }


                            // Date
                            String date;
                            if (mSource.getBankAccountId() == 7) {
                                date = arrayLine.get(mSource.getDatePosition()).replace("\"", "");
                            } else {
                                date = arrayLine.get(mSource.getDatePosition());
                            }

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
                            String name = "";
                            if (mSource.getFileName().matches("Amazon.*") && arrayLine.size() < 9) {
                                Log.i("Triggered", "Error");
                                Log.i("TriggeredWhat", arrayLine.toString());
                                name = Constants_Network.EMPTY;
                            } else {
                                if (mSource.getFileName().matches("GLS.*")) {

                                    for (int i = 5; i < 19; i++) {
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
                                        amount, Utility_Dates.encodeDateForSQL(date), mSource.getBankAccountId()));
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Log.i("ErrorCode2:", arrayLine.toString());
                                Log.i("Goo", "Code: " + code + " DetailOne: " + ((mSource.getDetailOnePosition() == 0 || (arrayLine.get(mSource.getDetailOnePosition())).equals("")) ? Constants_Network.EMPTY : arrayLine.get(mSource.getDetailOnePosition()) + " Name: " + name));
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
                int all = cALL2 + t.size();
                Log.i("Gegencheck2: ", Integer.toString(all));
                tvFiles.setText(getString(R.string.number_files) + Integer.toString(cFiles));
                tvTotal.setText((getString(R.string.transactions_total) + Integer.toString(cTotal)));
                tvError.setText(getString(R.string.number_error) + Integer.toString(cError));
                operateTransactions(t, mUrl);
            }
        }
    }


    private ArrayList<Source> createSources() {
        ArrayList<Source> sources = new ArrayList<>();
        sources.add(new Source("Amazon_DE", "txt", "\\t", 1, 8, 3, 4, 0, 6, 0, 4));
        sources.add(new Source("Amazon_UK", "txt", "\\t",1, 8, 3, 4, 0, 6, 0, 5));
        sources.add(new Source("Amazon_ES", "txt", "\\t",1, 8, 3, 4, 0, 6, 0, 6));

        sources.add(new Source("Commerzbank_4600", "CSV", ";", 3, 3, 2, 0, 0, 4, 1, 1));
        sources.add(new Source("Commerzbank_9200", "CSV", ";", 3, 3, 2, 0, 0, 4, 1, 2));
        sources.add(new Source("Commerzbank_5000", "CSV", ";", 3, 3, 2, 0, 0, 4, 1, 3));
        sources.add(new Source("Paypal", "CSV", "\",\"",12, 3, 4, 15, 5, 9, 0, 7));
        sources.add(new Source("GLS", "txt", "\\t",2, 6, 4, 3, 0, 19, 2, 8));
        return sources;
    }

    public void manuallyCharged(Transaction transaction, int idOperation) {
        tvCharged.setText(getString(R.string.charged_transactions) + " " + Integer.toString(cCharged++));
        switch (mTabLayout.getSelectedTabPosition()) {
            case TAB_NO_MODEL:
                tNoModel.remove(transaction);
                mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), tNoModel, TYPE_CHARGE);
                mViewPager.setAdapter(mAdapter);
                cNoModel = cNoModel - 1;
                mTabLayout.getTabAt(TAB_NO_MODEL).setText(Integer.toString(cNoModel) + " - " + getString(R.string.transaction_no_model));
                break;
            case TAB_MULTIPLE_MODEL:
                tMultipleModel.remove(transaction);
                mAdapter = new PagerAdapter_ChargeTransactions(getSupportFragmentManager(), tMultipleModel, TYPE_CHARGE);
                mViewPager.setAdapter(mAdapter);
                cMultipleModel = cMultipleModel - 1;
                mTabLayout.getTabAt(TAB_MULTIPLE_MODEL).setText(Integer.toString(cMultipleModel) + " - " + getString(R.string.transaction_multiple_model));
                break;
            default:
                break;
        }
        transaction.setId(idOperation);
        tManuallyCharged.add(transaction);
        cManuallyCharged = cManuallyCharged + 1;
        mTabLayout.getTabAt(TAB_CHARGED_MANUALLY).setText(Integer.toString(cManuallyCharged) + " - " + getString(R.string.transaction_charged_manually));
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
