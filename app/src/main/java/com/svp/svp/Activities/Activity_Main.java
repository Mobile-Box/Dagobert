package com.svp.svp.Activities;

// Provides screen for Navigation Drawer and Fragments, called by the menu in the drawer

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Fragments.Fragment_BalanceSheet;
import com.svp.svp.Objects.Navigation_Date;
import com.svp.svp.Objects.Navigation_Month;
import com.svp.svp.Objects.Navigation_Year;
import com.svp.svp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class Activity_Main extends AppCompatActivity implements View.OnClickListener {

    // Layout
    TextView tvOutput;
    RelativeLayout rlUpdate;
    RelativeLayout rlBusinessAssets;
    RelativeLayout rlLiabilities;
    RelativeLayout rlClaims;
    RecyclerView mRecyclerView;
    DrawerLayout mDrawer;
    FrameLayout mContainer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout
        setUpLayout();

        // Navigation

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == 0) calendar.add(Calendar.YEAR, -1);
        ArrayList<Navigation_Date> dates = new ArrayList<>();
        dates.add(new Navigation_Year(calendar.get(Calendar.YEAR)));
        for (int i = calendar.get(Calendar.MONTH); i >= 0; i--) {
            dates.add(new Navigation_Month(calendar.get(Calendar.YEAR), i));
        }
        calendar.add(Calendar.YEAR, -1);
        dates.add(new Navigation_Year(calendar.get(Calendar.YEAR)));
        for (int i = 11; i >= Constants_Intern.NAVIGATION_LAST_MONTH; i--) {
            dates.add(new Navigation_Month(calendar.get(Calendar.YEAR), i));
        }

        //ListAdapter_NavigationDates adapter = new ListAdapter_NavigationDates(dates);
        //mRecyclerView.setAdapter(adapter);

        // Add Fragment
        Fragment_BalanceSheet fragment = new Fragment_BalanceSheet();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpLayout() {
        setContentView(R.layout.activity_main);

        rlUpdate = findViewById(R.id.rlUpdate);
        rlBusinessAssets = findViewById(R.id.rlBusinessAssets);
        rlClaims = findViewById(R.id.rlClaims);
        rlLiabilities = findViewById(R.id.rlLiabilities);
        mDrawer = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawer.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // setOnClickListener
        rlUpdate.setOnClickListener(this);
        rlBusinessAssets.setOnClickListener(this);

        // RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlUpdate:
                startActivity(new Intent(this, Activity_Update.class));
                break;
            case R.id.rlBusinessAssets:

                break;
            case R.id.rlLiabilities:

                break;
            case R.id.rlClaims:

                break;

        }
    }
}
