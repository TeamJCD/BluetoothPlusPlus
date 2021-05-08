package com.github.teamjcd.bpp;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import static com.github.teamjcd.bpp.BluetoothDeviceClassEditor.URI_EXTRA;
import static com.github.teamjcd.bpp.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_EDIT;
import static com.github.teamjcd.bpp.db.BluetoothDeviceClassContentProvider.DEVICE_CLASS_URI;

public class BluetoothDeviceClassPreference extends Preference implements CompoundButton.OnCheckedChangeListener {
    private final static String TAG = "BluetoothDeviceClassPreference";

    private static String mSelectedKey = null;
    @SuppressLint("StaticFieldLeak")
    private static CompoundButton mCurrentChecked = null;
    private boolean mProtectFromCheckedChange = false;
    private boolean mSelectable = true;

    public BluetoothDeviceClassPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BluetoothDeviceClassPreference(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.preference.R.attr.preferenceStyle);
        setWidgetLayoutResource(R.layout.widget_bluetooth_device_class_preference);
    }

    public BluetoothDeviceClassPreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);

        View widget = view.findViewById(R.id.bluetooth_device_class_radiobutton);
        if (widget instanceof RadioButton) {
            RadioButton rb = (RadioButton) widget;
            if (mSelectable) {
                rb.setOnCheckedChangeListener(this);

                boolean isChecked = getKey().equals(mSelectedKey);
                if (isChecked) {
                    mCurrentChecked = rb;
                    mSelectedKey = getKey();
                }

                mProtectFromCheckedChange = true;
                rb.setChecked(isChecked);
                mProtectFromCheckedChange = false;
                rb.setVisibility(View.VISIBLE);
            } else {
                rb.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        Context context = getContext();
        if (context != null) {
            int pos = Integer.parseInt(getKey());
            Uri url = ContentUris.withAppendedId(DEVICE_CLASS_URI, pos);
            Intent editIntent = new Intent(getContext(), BluetoothDeviceClassEditorActivity.class);
            editIntent.setAction(ACTION_BLUETOOTH_DEVICE_CLASS_EDIT);
            editIntent.putExtra(URI_EXTRA, url);
            context.startActivity(editIntent);
        }
    }

    public void setChecked() {
        mSelectedKey = getKey();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "ID: " + getKey() + " :" + isChecked);
        if (mProtectFromCheckedChange) {
            return;
        }

        if (isChecked) {
            if (mCurrentChecked != null) {
                mCurrentChecked.setChecked(false);
            }
            mCurrentChecked = buttonView;
            mSelectedKey = getKey();
            callChangeListener(mSelectedKey);
        } else {
            mCurrentChecked = null;
            mSelectedKey = null;
        }
    }

    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }
}
