package com.svp.svp.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Fragments.Fragment_Transaction_Charge;
import com.svp.svp.Fragments.Fragment_Transaction_Show;
import com.svp.svp.Objects.Operation;
import com.svp.svp.Objects.Transaction;

import java.util.ArrayList;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class PagerAdapter_ChargeTransactions extends FragmentStatePagerAdapter {

    // Constants
    private final static int TYPE_CHARGE = 0;
    private final static int TYPE_SHOW = 1;

    // Variables
    ArrayList<Transaction> mTransactions;
    private int mType;

    public PagerAdapter_ChargeTransactions(FragmentManager fm, ArrayList<Transaction> transactions, int type) {
        super(fm);
        mType = type;
        mTransactions = transactions;
    }

    @Override
    public Fragment getItem(int position) {
        if (mType == TYPE_CHARGE) {
            Fragment fragment = new Fragment_Transaction_Charge();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants_Intern.TRANSACTION, mTransactions.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
        if (mType == TYPE_SHOW) {
            Fragment fragment = new Fragment_Transaction_Show();
            Transaction t = mTransactions.get(position);
            Operation operation = new Operation(t.getId(), t.getCode(), t.getName(), t.getDate(), t.getAmount(), t.getAmount()/(1+t.getUstValue()/100), t.getSvpSubAccountId(), t.getUstValue());
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants_Intern.OPERATION, operation);
            fragment.setArguments(bundle);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTransactions.size();
    }

}
