package com.svp.svp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.svp.svp.Activities.Activity_Main;
import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Objects.Navigation.Navigation_Date;
import com.svp.svp.Objects.Navigation.Navigation_Month;
import com.svp.svp.R;

import java.util.ArrayList;

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
                ((Activity_Main)mContext).buildBalanceSheetFragment(Constants_Intern.BALANCESHEET_TYPE_ACCOUNTS, mDates.get(position));
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
        Holder_Date h = (Holder_Date)holder;
        Navigation_Date date = mDates.get(position);
        h.tvName.setText(date.getName(date.getValue()));
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

}
