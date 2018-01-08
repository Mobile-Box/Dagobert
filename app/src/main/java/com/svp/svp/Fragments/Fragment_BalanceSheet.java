package com.svp.svp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svp.svp.R;

/**
 * Created by Eric Schumacher on 31.12.2017.
 */

public class Fragment_BalanceSheet extends Fragment {

    // Layout
    View mLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_balance_sheet, container, false);

        return mLayout;
    }
}
