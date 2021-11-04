package com.github.teamjcd.bpp.fragment;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.github.teamjcd.bpp.R;
import com.github.teamjcd.bpp.content.BppDeviceClassContentProvider;
import com.github.teamjcd.bpp.provider.BppDeviceClassColumns;
import com.github.teamjcd.bpp.repository.BppDeviceClassRepository;
import com.github.teamjcd.bpp.util.BppUtils;

public class BppDeviceClassEditorFragment extends BppBaseEditorFragment<BppDeviceClassRepository, BppDeviceClassColumns> {
    @Override
    protected String getEditAction() {
        return BppMainFragment.ACTION_DEVICE_CLASS_EDIT;
    }

    @Override
    protected String getInsertAction() {
        return BppMainFragment.ACTION_DEVICE_CLASS_INSERT;
    }

    @Override
    protected Uri getUriPrefix() {
        return BppDeviceClassContentProvider.URI;
    }

    @Override
    protected BppDeviceClassRepository getRepository(Context context) {
        return new BppDeviceClassRepository(context);
    }

    @Override
    protected BppDeviceClassColumns getColumns() {
        return new BppDeviceClassColumns();
    }

    @Override
    protected int getPreferencesResId() {
        return R.xml.fragment_bpp_device_class_editor;
    }

    @Override
    protected String validate() {
        String errMsg = null;

        final String name = getNamePreference().getText();
        final String cod = getValuePreference().getText();

        if (TextUtils.isEmpty(name)) {
            errMsg = getResources().getString(R.string.error_name_empty);
        } else if (TextUtils.isEmpty(cod)) {
            errMsg = getResources().getString(R.string.error_device_class_empty);
        }

        if (errMsg == null) {
            try {
                BppUtils.parseHex(cod);
            } catch (Exception e) {
                errMsg = getResources().getString(R.string.error_device_class_invalid);
            }
        }

        return errMsg;
    }

    @Override
    protected String formatValue(int value) {
        return BppUtils.formatDeviceClass(value);
    }
}
