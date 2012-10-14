package com.ultimateremotecontrol.urcandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 123456;
	private static final String PREF_LAST_DEVICE = "URC_LAST_DEVICE";

	private BluetoothAdapter mBluetoothAdapter = null;
	private AlertDialog mCurrentAlert = null;
	private String mCurrentDevice = null;
	private SharedPreferences mSharedPrefs;

	private ViewState mCurrentState;

	private TextView mTopText = null;
	private TextView mDeviceText = null;
	private Button mSelectDeviceButton = null;

	private enum ViewState {
		EnablingBluetooth, BluetoothDisabled, ConnectDevice
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTopText = (TextView) this
				.findViewById(R.id.setup_label_top);
		mDeviceText = (TextView) this.findViewById(R.id.setup_current_device_label);
		mSelectDeviceButton = (Button) this.findViewById(R.id.setup_select_device_button);

		configureView(ViewState.EnablingBluetooth);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			URCLog.d("No bluetooth support found, displaying error message.");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			mCurrentAlert = builder
					.setCancelable(false)
					.setTitle(R.string.no_bluetooth_title)
					.setMessage(R.string.no_bluetooth_message)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									URCLog.d("Exiting.");
									finish();
								}
							}).create();
		}
	}

	@Override
	protected void onResume() {
		if (mCurrentAlert != null) {
			mCurrentAlert.show();
		}

		mSharedPrefs = this.getPreferences(MODE_PRIVATE);
		if (mSharedPrefs != null && mCurrentDevice != null) {
			mCurrentDevice = mSharedPrefs.getString(PREF_LAST_DEVICE, null);
		}

		if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()
				&& mCurrentState != ViewState.BluetoothDisabled) {
			configureView(ViewState.EnablingBluetooth);
			URCLog.d("Bluetooth is not enabled, requesting enabled bluetooth.");
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else if (mBluetoothAdapter.isEnabled()) {
			configureView(ViewState.ConnectDevice);
		}
		
		super.onResume();
	}

	@Override
	protected void onStop() {
		if (mCurrentAlert != null) {
			if (mCurrentAlert.isShowing()) {
				mCurrentAlert.dismiss();
			} else {
				mCurrentAlert = null;
			}
		}
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				configureView(ViewState.ConnectDevice);
				URCLog.d("Enable bluetooth request returned RESULT_OK.");
			} else {
				URCLog.d("Enabling bluetooth failed.");
				configureView(ViewState.BluetoothDisabled);
			}
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void configureView(ViewState state) {
		mCurrentState = state;
		switch (mCurrentState) {
		case EnablingBluetooth:
			mTopText.setText(R.string.setup_enablingbluetooth);
			mDeviceText.setVisibility(View.INVISIBLE);
			mSelectDeviceButton.setVisibility(View.INVISIBLE);
			break;
		case BluetoothDisabled:
			mTopText.setText(R.string.setup_bluetooth_disabled);
			mDeviceText.setVisibility(View.INVISIBLE);
			mSelectDeviceButton.setVisibility(View.INVISIBLE);
			break;
		case ConnectDevice:
			mTopText.setText(R.string.setup_welcome);
			if (mCurrentDevice != null) {
				mDeviceText.setText(String.format(
						this.getString(R.string.setup_label_currentdevice),
						mCurrentDevice));
			} else {
				mDeviceText.setText(String.format(
						this.getString(R.string.setup_label_currentdevice),
						this.getString(R.string.setup_nodeviceselected)));
			}			
			mDeviceText.setVisibility(View.VISIBLE);
			mSelectDeviceButton.setVisibility(View.VISIBLE);
			break;
		}

	}
}
