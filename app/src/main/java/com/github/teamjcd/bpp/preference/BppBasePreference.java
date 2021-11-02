package com.github.teamjcd.bpp.preference;

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
import com.github.teamjcd.bpp.R;
import com.github.teamjcd.bpp.activity.BppBaseActivity;

import static com.github.teamjcd.bpp.fragment.BppDeviceClassEditorFragment.URI_EXTRA;

public abstract class BppBasePreference extends Preference implements CompoundButton.OnCheckedChangeListener {
    private final static String TAG = BppBasePreference.class.getName();

    private String mSelectedKey = null;
    private CompoundButton mCurrentChecked = null;
    private boolean mProtectFromCheckedChange = false;
    private boolean mSelectable = true;

    public BppBasePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.widget_bpp_selectable);
    }

    public BppBasePreference(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.preference.R.attr.preferenceStyle);
    }

    public BppBasePreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);

        View widget = view.findViewById(R.id.widget_bpp_selectable_radiobutton);

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
            Uri url = ContentUris.withAppendedId(getContentUri(), pos);
            Intent editIntent = new Intent(getContext(), getIntentClass());
            editIntent.setAction(getAction());
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

    protected abstract Uri getContentUri();

    protected abstract Class<? extends BppBaseActivity> getIntentClass();

    protected abstract String getAction();
}
