package com.svp.svp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.svp.svp.Activities.Activity_Main;
import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Objects.Navigation.Navigation_Date;
import com.svp.svp.Objects.Navigation.Navigation_Month;
import com.svp.svp.Objects.Navigation.Navigation_Year;
import com.svp.svp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Eric Schumacher on 04.01.2018.
 */

public class ListAdapter_Navigation_Dates extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_YEAR = 0;
    private final static int TYPE_MONTH = 1;


    ArrayList<Navigation_Date> mDates;
    Context mContext;
    LayoutInflater mLayoutInflater;

    public ListAdapter_Navigation_Dates(Context context, ArrayList<Navigation_Date> dates) {
        mDates = dates;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ClickInterface clickInterface = new ClickInterface() {
            @Override
            public void onClick(int position) {
                ((Activity_Main) mContext).buildBalanceSheetFragment(Constants_Intern.BALANCESHEET_TYPE_ACCOUNTS, mDates.get(position), 0);
            }
        };
        if (viewType == TYPE_YEAR) {
            return new Holder_Date(mLayoutInflater.inflate(R.layout.item_navigation_year, parent, false), clickInterface);
        }
        if (viewType == TYPE_MONTH) {
            return new Holder_Date(mLayoutInflater.inflate(R.layout.item_navigation_month, parent, false), clickInterface);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Holder_Date h = (Holder_Date) holder;
        Navigation_Date date = mDates.get(position);
        h.tvName.setText(date.getName(date.getValue()));
        showProfit(h.tvAmount, date);
    }

    @Override
    public int getItemCount() {
        return mDates.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDates.get(position) instanceof Navigation_Month) {
            return TYPE_MONTH;
        } else {
            return TYPE_YEAR;
        }
    }

    private class Holder_Date extends RecyclerView.ViewHolder implements View.OnClickListener {

        RelativeLayout rlItem;
        TextView tvName;
        TextView tvAmount;
        ClickInterface clickInterface;

        public Holder_Date(View itemView, ClickInterface clickInterface) {
            super(itemView);
            this.clickInterface = clickInterface;
            rlItem = itemView.findViewById(R.id.rlItem);
            tvName = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            rlItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rlItem:
                    clickInterface.onClick(getAdapterPosition());
                    break;
            }
        }
    }

    private interface ClickInterface {
        void onClick(int position);
    }

    private void showProfit(final TextView tvProfit, final Navigation_Date date) {
        String year;
        String month;
        if (date instanceof Navigation_Month) {
            Navigation_Month date_month = (Navigation_Month) date;
            year = Integer.toString(date_month.getYear());
            month = "/" + Integer.toString(date_month.getMonth());
        } else {
            Navigation_Year date_year = (Navigation_Year) date;
            year = Integer.toString(date_year.getYear());
            month = "";
        }
        RequestQueue queue;
        Log.i("Spinnst", "Du");
        queue = Volley.newRequestQueue(mContext);
        final String url = "http://www.svp-server.com/svp-gmbh/dagobert/src/routes/api.php/profit/" + year + month;
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Double amount = Double.parseDouble(response);
                        Locale locale = Locale.GERMAN;
                        String a = NumberFormat.getNumberInstance(locale).format(amount);
                        //a = String.format("%.0f", a);
                        try {
                        } catch (Exception e) {Log.i("EEERRROR", "What");}

                        tvProfit.setText(a + " €");

                    } catch (NumberFormatException e) {
                        //rlItem.setVisibility(View.GONE);
                        mDates.remove(date);
                        notifyDataSetChanged();
                        Log.i("Handled Error", "No money calculated for that month or year");
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
