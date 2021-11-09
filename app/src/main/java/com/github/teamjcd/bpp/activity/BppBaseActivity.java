package com.github.teamjcd.bpp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.github.teamjcd.bpp.R;

public abstract class BppBaseActivity extends AppCompatActivity {
    private static final String TAG = BppBaseActivity.class.getSimpleName();

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Log.d(TAG, "Starting onCreate");

        setContentView(getLayoutResId());
        setSupportActionBar(findViewById(R.id.toolbar));
        setFragment(getSupportFragmentManager().findFragmentById(getFragmentResId()));
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

    protected abstract int getLayoutResId();

    protected abstract int getFragmentResId();
}
