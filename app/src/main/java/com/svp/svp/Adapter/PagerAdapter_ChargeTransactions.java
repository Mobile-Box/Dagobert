package com.svp.svp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class PagerAdapter_ChargeTransactions extends FragmentStatePagerAdapter {

    public PagerAdapter_ChargeTransactions(FragmentManager fm, ArrayList<Transaction>) {
        super(fm);
    }

    //

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
