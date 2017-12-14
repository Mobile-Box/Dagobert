package com.svp.svp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svp.svp.R;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class Fragment_ChargeTransaction extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_charge_transaction, container, false);



        return layout;

    }
}
