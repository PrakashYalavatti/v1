package com.simtech.app1;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DAP20 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String screenFrom = getIntent().getStringExtra("SCREEN_FROM");
        if(screenFrom.equalsIgnoreCase("30DAP")) {
            setContentView(R.layout.dap_20);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setContentView(R.layout.dap20);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.dap_20));
        // using toolbar as ActionBar
        setSupportActionBar(toolbar);


    }
}
