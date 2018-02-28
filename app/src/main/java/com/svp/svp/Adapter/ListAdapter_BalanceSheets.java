package com.svp.svp.Adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.svp.svp.Activities.Activity_Main;
import com.svp.svp.Constants.Constants_Network;
import com.svp.svp.Interfaces.Interface_BalanceSheetFragment;
import com.svp.svp.Interfaces.Interface_Click;
import com.svp.svp.Objects.BalanceSheet.BalanceSheet;
import com.svp.svp.Objects.BalanceSheet.BalanceSheet_Account;
import com.svp.svp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Eric Schumacher on 02.01.2018.
 */

public class ListAdapter_BalanceSheets extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Types
    private static final int TYPE_BALANCESHEET = 0;
    private static final int TYPE_BALANCESHEET_LINKED = 1;

    // Variables
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<BalanceSheet> mBS;

    // Interface
    Interface_BalanceSheetFragment iBalanceSheetFragment;

    public ListAdapter_BalanceSheets(Context context, ArrayList<BalanceSheet> bs) {
        mBS = bs;
        mContext = context;
        iBalanceSheetFragment = (Activity_Main)context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BALANCESHEET:
                Holder_BalanceSheet holder = new Holder_BalanceSheet(mLayoutInflater.inflate(R.layout.item_balance_sheet, parent, false), new Interface_Click() {
                    @Override
                    public void onCick(int position) {
                        // do nothing here
                        Log.i("TYYPE: ", "WhATT");
                    }
                });
                return holder;
            case TYPE_BALANCESHEET_LINKED:
                Holder_BalanceSheet_Account holder_account = new Holder_BalanceSheet_Account(mLayoutInflater.inflate(R.layout.item_balance_sheet, parent, false), new Interface_Click() {
                    @Override
                    public void onCick(int position) {
                        BalanceSheet_Account bs = (BalanceSheet_Account)mBS.get(position);
                        if (!bs.getType().equals(Constants_Network.BS_TYPE_OPERATION)) {
                            iBalanceSheetFragment.buildBalanceSheetFragment(BalanceSheet.getNextLevel(bs.getType()), bs.getDate(), bs.getId());
                        } else {
                            Log.i("NoT", "POssible");
                            iBalanceSheetFragment.buildTransactionShowFragment(bs.getId());
                        }
                    }
                });
                return holder_account;
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Holder_BalanceSheet h = (Holder_BalanceSheet)holder;
        BalanceSheet bs = mBS.get(position);
        h.tvName.setText(bs.getName());
        Locale locale = Locale.GERMAN;
        String a = NumberFormat.getNumberInstance(locale).format(bs.getAmount());
        //h.tvAmount.setText(String.format("%.2f", bs.getAmount()).replace(".", ",")+" €");
        h.tvAmount.setText(a+" €");
        if ((bs instanceof BalanceSheet_Account)) {
            h.tvName.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorPrimaryText, null));
            h.tvAmount.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorPrimaryText, null));
        }
    }

    @Override
    public int getItemCount() {
        return mBS.size();
    }

    @Override
    public int getItemViewType(int position) {
        BalanceSheet bs = mBS.get(position);
        if (bs instanceof BalanceSheet_Account) {
            return TYPE_BALANCESHEET_LINKED;
        } else {
            return TYPE_BALANCESHEET;
        }
    }

    private class Holder_BalanceSheet extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName;
        TextView tvAmount;
        LinearLayout llItem;
        Interface_Click interfaceClick;

        public Holder_BalanceSheet(View itemView, Interface_Click interfaceClick) {
            super(itemView);

            llItem = itemView.findViewById(R.id.llItem);
            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvValue);
            this.interfaceClick = interfaceClick;
            llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.llItem:
                    interfaceClick.onCick(getAdapterPosition());
            }
        }
    }

    private class Holder_BalanceSheet_Account extends Holder_BalanceSheet{

        public Holder_BalanceSheet_Account(View itemView, Interface_Click interfaceClick) {
            super(itemView, interfaceClick);
        }
    }
}
