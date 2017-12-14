package com.svp.svp.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.svp.svp.R;

/**
 * Created by Eric Schumacher on 13.12.2017.
 */

public class Activity_ProcessTransactions extends AppCompatActivity {

    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_transactions);
        initiateLayout();
    }

    private void initiateLayout() {
        mViewPager = findViewById(R.id.viewPager);
    }
}
