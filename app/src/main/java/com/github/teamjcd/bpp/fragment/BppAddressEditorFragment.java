package com.github.teamjcd.bpp.fragment;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.github.teamjcd.bpp.R;
import com.github.teamjcd.bpp.content.BppAddressContentProvider;
import com.github.teamjcd.bpp.provider.BppAddressColumns;
import com.github.teamjcd.bpp.repository.BppAddressRepository;
import com.github.teamjcd.bpp.util.BppUtils;

public class BppAddressEditorFragment extends BppBaseEditorFragment<BppAddressRepository, BppAddressColumns> {
    @Override
    protected String getEditAction() {
        return BppMainFragment.ACTION_ADDRESS_EDIT;
    }

    @Override
    protected String getInsertAction() {
        return BppMainFragment.ACTION_ADDRESS_INSERT;
    }

    @Override
    protected Uri getUriPrefix() {
        return BppAddressContentProvider.URI;
    }

    @Override
    protected BppAddressRepository getRepository(Context context) {
        return new BppAddressRepository(context);
    }

    @Override
    protected BppAddressColumns getColumns() {
        return new BppAddressColumns();
    }

    @Override
    protected int getPreferencesResId() {
        return R.xml.fragment_bpp_address_editor;
    }

    @Override
    protected String validate() {
        String errMsg = null;

        final String name = getNamePreference().getText();
        final String cod = getValuePreference().getText();

        if (TextUtils.isEmpty(name)) {
            errMsg = getResources().getString(R.string.error_name_empty);
        } else if (TextUtils.isEmpty(cod)) {
            errMsg = getResources().getString(R.string.error_address_empty);
        }

        if (errMsg == null) {
            try {
                BppUtils.parseHex(cod);
            } catch (Exception e) {
                errMsg = getResources().getString(R.string.error_address_invalid);
            }
        }

        return errMsg;
    }

    @Override
    protected String formatValue(int value) {
        return BppUtils.formatAddress(value);
    }
}
