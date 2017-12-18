package com.svp.svp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.svp.svp.R;

public class Activity_Main extends AppCompatActivity {

    // Layout
    TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout
        setUpLayout();

        // Data output
        startActivity(new Intent(this, Activity_Update.class));


    }

    private void setUpLayout() {
        setContentView(R.layout.activity_main);
        tvOutput = (TextView) findViewById(R.id.tvOutput);
    }
}
