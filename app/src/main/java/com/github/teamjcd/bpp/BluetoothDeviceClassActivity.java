package com.github.teamjcd.bpp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class BluetoothDeviceClassActivity extends AppCompatActivity {
    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
    }

    protected void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mFragment.onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mFragment.onOptionsItemSelected(item);
    }
}
