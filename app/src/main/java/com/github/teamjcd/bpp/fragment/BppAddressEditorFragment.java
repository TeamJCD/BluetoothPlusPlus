package com.github.teamjcd.bpp.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.github.teamjcd.bpp.R;
import com.github.teamjcd.bpp.content.BppAddressContentProvider;
import com.github.teamjcd.bpp.provider.BppAddressColumns;
import com.github.teamjcd.bpp.repository.BppAddressRepository;
import com.github.teamjcd.bpp.util.BppUtils;
import com.phearme.btscanselector.ABTScanSelectorEventsHandler;
import com.phearme.btscanselector.BTScanSelectorBuilder;

public class BppAddressEditorFragment extends BppBaseEditorFragment<BppAddressRepository, BppAddressColumns> {
    private static final int MENU_OBTAIN = MENU_DELETE - 1;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (mNew) {
            menu.add(1, MENU_OBTAIN, 0, R.string.menu_obtain)
                    .setIcon(android.R.drawable.ic_menu_search);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_OBTAIN) {
            showBtScanSelector();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
    protected String formatValue(long value) {
        return BppUtils.formatAddress(value);
    }

    private void showBtScanSelector() {
        BTScanSelectorBuilder.build(requireActivity(), new ABTScanSelectorEventsHandler() {
            @Override
            public void onDeviceSelected(BluetoothDevice bluetoothDevice) {
                mName.setText(bluetoothDevice.getName());
                mName.setSummary(bluetoothDevice.getName());

                mValue.setText(bluetoothDevice.getAddress());
                mValue.setSummary(bluetoothDevice.getAddress());
            }
        }, getResources().getString(R.string.menu_obtain));
    }
}
