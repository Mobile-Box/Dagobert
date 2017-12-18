package com.svp.svp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Objects.Transaction;
import com.svp.svp.R;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class Fragment_ChargeTransaction extends Fragment {

    // Layout
    View mLayout;
    TextView tvAccount;
    EditText etName;
    EditText etType;
    EditText etDetailOne;
    EditText etDetailTwo;
    TextView tvAmount;
    TextView tvDate;
    EditText etSubaccountId;
    Button bCharge;
    Button bChargeWithModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Layout
        mLayout = inflater.inflate(R.layout.fragment_charge_transaction, container, false);
        setLayout();

        // Data
        Bundle bundle = getArguments();
        Transaction transaction = (Transaction)bundle.getSerializable(Constants_Intern.TRANSACTION);
        etName.setText(transaction.getName());



        return mLayout;
    }

    private void setLayout() {
        tvAccount = mLayout.findViewById(R.id.tvAccount);
        tvAmount = mLayout.findViewById(R.id.tvAmount);
        tvDate = mLayout.findViewById(R.id.tvDate);
        etName = mLayout.findViewById(R.id.etName);
        etType = mLayout.findViewById(R.id.etName);
        etDetailOne = mLayout.findViewById(R.id.etDetailOne);
        etDetailTwo = mLayout.findViewById(R.id.etDetailTwo);
        etSubaccountId = mLayout.findViewById(R.id.etSubaccountId);
        bCharge = mLayout.findViewById(R.id.bCharge);
        bChargeWithModel = mLayout.findViewById(R.id.bChargeWithModel);

    }
}
