package com.svp.svp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.svp.svp.Interfaces.Interface_Click;
import com.svp.svp.Objects.BalanceSheet.BalanceSheet;
import com.svp.svp.Objects.BalanceSheet.BalanceSheet_Account;
import com.svp.svp.R;

import java.util.ArrayList;

/**
 * Created by Eric Schumacher on 02.01.2018.
 */

public class ListAdapter_BalanceSheets extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BALANCESHEET = 0;
    private static final int TYPE_BALANCESHEET_ACCOUNT = 1;

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<BalanceSheet> mBS;

    public ListAdapter_BalanceSheets(Context context, ArrayList<BalanceSheet> bs) {
        mBS = bs;
        mContext = context;
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
                    }
                });
                return holder;
            case TYPE_BALANCESHEET_ACCOUNT:
                Holder_BalanceSheet_Account holder_account = new Holder_BalanceSheet_Account(mLayoutInflater.inflate(R.layout.item_balance_sheet, parent, false), new Interface_Click() {
                    @Override
                    public void onCick(int position) {
                        // open SubAccounts
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
        h.tvAmount.setText(Double.toString(bs.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mBS.size();
    }

    @Override
    public int getItemViewType(int position) {
        BalanceSheet bs = mBS.get(position);
        if (bs instanceof BalanceSheet_Account) {
            return TYPE_BALANCESHEET_ACCOUNT;
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
