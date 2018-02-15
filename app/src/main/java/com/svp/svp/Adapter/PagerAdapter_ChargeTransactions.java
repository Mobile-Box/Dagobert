package com.svp.svp.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Fragments.Fragment_ChargeTransaction;
import com.svp.svp.Objects.Transaction;

import java.util.ArrayList;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class PagerAdapter_ChargeTransactions extends FragmentStatePagerAdapter {

    ArrayList<Transaction> mTransactions;

    public PagerAdapter_ChargeTransactions(FragmentManager fm, ArrayList<Transaction> transactions) {
        super(fm);
        mTransactions = transactions;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment_ChargeTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants_Intern.TRANSACTION, mTransactions.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTransactions.size();
    }

}
