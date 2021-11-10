package com.github.teamjcd.bpp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.github.teamjcd.bpp.R;
import com.github.teamjcd.bpp.provider.BppBaseColumns;
import com.github.teamjcd.bpp.repository.BppBaseRepository;
import com.github.teamjcd.bpp.util.BppUtils;

public abstract class BppBaseEditorFragment<U extends BppBaseRepository<T>, T extends BppBaseColumns> extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, View.OnKeyListener {
    private static final String TAG = BppBaseEditorFragment.class.getSimpleName();

    public static final String URI_EXTRA = BppBaseEditorFragment.class.getName() + ".URI_EXTRA";

    protected static final int MENU_DELETE = Menu.FIRST;
    protected static final int MENU_SAVE = MENU_DELETE + 1;
    protected static final int MENU_CANCEL = MENU_SAVE + 1;

    protected EditTextPreference mName;
    protected EditTextPreference mValue;

    protected U mRepository;
    protected T mColumns;

    protected boolean mNew;
    protected boolean mReadOnly;

    public EditTextPreference getNamePreference() {
        return mName;
    }

    public EditTextPreference getValuePreference() {
        return mValue;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentActivity activity = getActivity();
        if (activity != null) {
            final Intent intent = activity.getIntent();
            final String action = intent.getAction();

            if (TextUtils.isEmpty(action)) {
                activity.finish();
                return;
            }

            Uri uri = null;
            if (action.equals(getEditAction())) {
                uri = intent.getParcelableExtra(URI_EXTRA);
                if (!uri.isPathPrefixMatch(getUriPrefix())) {
                    Log.e(TAG, "Invalid edit request. Uri: " + uri);
                    activity.finish();
                    return;
                }
            } else if (action.equals(getInsertAction())) {
                Uri insertUri = intent.getParcelableExtra(URI_EXTRA);
                if (!insertUri.isPathPrefixMatch(getUriPrefix())) {
                    Log.e(TAG, "Invalid insert request. Uri: " + insertUri);
                    activity.finish();
                    return;
                }
                mNew = true;
            } else {
                activity.finish();
                return;
            }

            mRepository = getRepository(getContext());

            if (uri != null) {
                mColumns = mRepository.get(uri);
            } else {
                mColumns = getColumns();
            }

            mReadOnly = mColumns.isDefault();

            if (mReadOnly) {
                mValue.setEnabled(false);
            }

            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                getPreferenceScreen().getPreference(i).setOnPreferenceChangeListener(this);
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(getPreferencesResId());

        mName = findPreference("name");
        mValue = findPreference("value");

        mName.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            editText.setSelection(editText.getText().length());
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        fillUI(savedInstanceState == null);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary(newValue != null ? String.valueOf(newValue) : null);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(0, MENU_SAVE, 0, R.string.menu_save)
                .setIcon(android.R.drawable.ic_menu_save);

        menu.add(0, MENU_CANCEL, 0, R.string.menu_cancel)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        if (!mNew && !mReadOnly) {
            menu.add(0, MENU_DELETE, 0, R.string.menu_delete)
                    .setIcon(R.drawable.ic_delete_24);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentActivity activity = getActivity();

        switch (item.getItemId()) {
            case MENU_DELETE:
                mRepository.delete(mColumns.getId());
                if (activity != null) {
                    activity.finish();
                }
                return true;
            case MENU_SAVE:
                if (validateAndSave() && activity != null) {
                    activity.finish();
                }
                return true;
            case MENU_CANCEL:
                if (activity != null) {
                    activity.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (validateAndSave()) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }

            return true;
        }

        return false;
    }

    private void fillUI(boolean firstTime) {
        if (firstTime) {
            mName.setText(mColumns.getName());
            if (!mNew) {
                mValue.setText(formatValue(mColumns.getValue()));
            }
        }

        mName.setSummary(mName.getText());
        mValue.setSummary(mValue.getText());
    }

    private boolean validateAndSave() {
        final String errorMsg = validate();
        if (errorMsg != null) {
            showError(errorMsg);
            return false;
        }

        mColumns.setName(mName.getText());

        if (!mReadOnly) {
            mColumns.setValue(BppUtils.parseHex(mValue.getText()));
        }

        if (mNew) {
            mRepository.save(mColumns);
        } else {
            mRepository.update(mColumns);
        }

        return true;
    }

    private void showError(String msg) {
        BppBaseEditorFragment.ErrorDialog.showError(this, msg);
    }

    private static class ErrorDialog extends DialogFragment {
        private String msg;

        public static void showError(PreferenceFragmentCompat editor, String msg) {
            BppBaseEditorFragment.ErrorDialog dialog = new BppBaseEditorFragment.ErrorDialog();
            dialog.setMessage(msg);
            dialog.show(editor.getChildFragmentManager(), "error");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //noinspection ConstantConditions
            return new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null)
                    .setMessage(msg)
                    .create();
        }

        private void setMessage(String msg) {
            this.msg = msg;
        }
    }

    protected abstract String getEditAction();

    protected abstract String getInsertAction();

    protected abstract Uri getUriPrefix();

    protected abstract U getRepository(Context context);

    protected abstract T getColumns();

    protected abstract int getPreferencesResId();

    protected abstract String validate();

    protected abstract String formatValue(long value);
}
