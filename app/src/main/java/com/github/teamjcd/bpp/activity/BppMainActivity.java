package com.github.teamjcd.bpp.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import com.github.teamjcd.bpp.R;

public class BppMainActivity extends BppBaseActivity {
    private static final String LOG_TAG = BppMainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Log.d(LOG_TAG, "Starting onCreate");

        setContentView(R.layout.activity_bpp_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        setFragment(getSupportFragmentManager().findFragmentById(R.id.fragment_bpp_main));
    }
}
