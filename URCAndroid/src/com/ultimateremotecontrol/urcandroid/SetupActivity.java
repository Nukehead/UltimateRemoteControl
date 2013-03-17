package com.ultimateremotecontrol.urcandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * The activity to enable bluetooth and connect to a selected device. 
 *
 */
public class SetupActivity extends Activity {

	/** The id for the enable bluetooth request. */
	private static final int REQUEST_ENABLE_BT = 123456;
	
	/** The id for the select device request. */
	private static final int REQUEST_SELECT_DEVICE = 654321;
	
	/** The name of the last used device preferences identifier. */
	private static final String PREF_LAST_DEVICE = "URC_LAST_DEVICE";
	
	/** The UUID for the bluetooth service to connect to. */
	private static final UUID URC_UUID = UUID.fromString("508ca308-1b94-11e2-892e-0800200c9a66");

	
	/** The bluetooth adapter to use. */
	private BluetoothAdapter mBluetoothAdapter = null;
	
	/** The current alert dialog. */
	private AlertDialog mCurrentAlert = null;
	
	/** The currently selected device. */
	private String mCurrentDevice = null;
	
	/** The shared preferences for this app. */
	private SharedPreferences mSharedPrefs;

	/** The current state of the setup activity. */
	private ViewState mCurrentState;

	
	/** The top text in the layout. */ 
	private TextView mTopText = null;
	
	/** The text for the currently selected device. */
	private TextView mDeviceText = null;
	
	/** The select device button. */
	private Button mSelectDeviceButton = null;
	
	/** The connect device button. */
	private Button mConnectDeviceButton = null;
	
	/** The status text for the connection attempt. */
	private TextView mConnectStatusText = null;

	/**
	 * An enum for the state of the current setup activity. 
	 */
	private enum ViewState {
		EnablingBluetooth, /** The activity is enabing bluetooth. */ 
		BluetoothDisabled, /** Bluetooth is disabled. */ 
		ConnectDevice,  /** A device can be selected, and if one is selected, one can connect to it. */
		ConnectedDevice /** A connection to a device is being established. */
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);

		mTopText = (TextView) this
				.findViewById(R.id.setup_label_top);
		mDeviceText = (TextView) this.findViewById(R.id.setup_current_device_label);
		mSelectDeviceButton = (Button) this.findViewById(R.id.setup_select_device_button);
		mSelectDeviceButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent serverIntent = new Intent(v.getContext(), DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_SELECT_DEVICE);
				
			}
		});

		mConnectDeviceButton = (Button) this.findViewById(R.id.setup_connect_device_button);
		mConnectDeviceButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				connectToDevice();
			}
		});
		
		mConnectStatusText = (TextView) this.findViewById(R.id.setup_connect_state_label);
		
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

	protected void connectToDevice() {
		URCLog.d("Connecting to device.");
		mConnectStatusText.setText(R.string.setup_device_connectionfailed);
		if (mCurrentDevice != null) {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mCurrentDevice);
			if (device != null) {
				try {
					BluetoothSocket socket = device.createRfcommSocketToServiceRecord(URC_UUID);
					URCLog.d("Socket created, trying to connect.");
					socket.connect();
					OutputStream out = socket.getOutputStream();
					InputStream in = socket.getInputStream();
					URCLog.d("Connection successful, writing string.");
					out.write(new String("X128;Y129;Z130;A127;B126;C125;D124;E123;F122;G1;H0;I1;J0;K1").getBytes("UTF-8"));
					in.read();
					URCLog.d("Closing connection.");
					socket.close();
					mConnectStatusText.setText(R.string.setup_device_connected);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		configureView(ViewState.ConnectedDevice);
	}

	@Override
	protected void onResume() {
		if (mCurrentAlert != null) {
			mCurrentAlert.show();
		}

		mSharedPrefs = this.getPreferences(MODE_PRIVATE);
		if (mSharedPrefs != null && mCurrentDevice == null) {
			mCurrentDevice = mSharedPrefs.getString(PREF_LAST_DEVICE, null);
		}

		if (mBluetoothAdapter != null) {
			if (!mBluetoothAdapter.isEnabled()
					&& mCurrentState != ViewState.BluetoothDisabled) {
				configureView(ViewState.EnablingBluetooth);
				URCLog.d("Bluetooth is not enabled, requesting enabled bluetooth.");
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else if (mBluetoothAdapter.isEnabled()) {
				configureView(ViewState.ConnectDevice);
			}
		} else {
			configureView(ViewState.BluetoothDisabled);
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
			
		case REQUEST_SELECT_DEVICE:
			if (resultCode == RESULT_OK) {
				mCurrentDevice = data.getStringExtra(DeviceListActivity.INTENT_DEVICE_ID);

				configureView(ViewState.ConnectDevice);
				
				if (mCurrentDevice != null) {
					URCLog.d("Selected device is: " + mCurrentDevice);
				}
				
				if (mSharedPrefs != null && mCurrentDevice != null) {
					boolean putSuccessful = mSharedPrefs.edit().putString(PREF_LAST_DEVICE, mCurrentDevice).commit();
					if (false == putSuccessful) {
						URCLog.d("Saving the selected device in the preferences failed.");
					}
				}
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private synchronized void configureView(ViewState state) {
		mCurrentState = state;
		switch (mCurrentState) {
		case EnablingBluetooth:
			mTopText.setText(R.string.setup_enablingbluetooth);
			mDeviceText.setVisibility(View.INVISIBLE);
			mSelectDeviceButton.setVisibility(View.INVISIBLE);
			mConnectDeviceButton.setVisibility(View.INVISIBLE);
			mConnectStatusText.setVisibility(View.INVISIBLE);
			break;
		case BluetoothDisabled:
			mTopText.setText(R.string.setup_bluetooth_disabled);
			mDeviceText.setVisibility(View.INVISIBLE);
			mSelectDeviceButton.setVisibility(View.INVISIBLE);
			mConnectDeviceButton.setVisibility(View.INVISIBLE);
			mConnectStatusText.setVisibility(View.INVISIBLE);
			break;
		case ConnectDevice:
			mTopText.setText(R.string.setup_welcome);
			if (mCurrentDevice != null) {
				BluetoothDevice currentDevice = mBluetoothAdapter.getRemoteDevice(mCurrentDevice);
				String name = currentDevice.getName();
				if (name != null) {
				mDeviceText.setText(String.format(
						this.getString(R.string.setup_label_currentdevice),
						name));
				} else {
					mDeviceText.setText(String.format(
							this.getString(R.string.setup_label_currentdevice),
							mCurrentDevice));					
				}
				mConnectDeviceButton.setVisibility(View.VISIBLE);
			} else {
				mDeviceText.setText(String.format(
						this.getString(R.string.setup_label_currentdevice),
						this.getString(R.string.setup_nodeviceselected)));
				mConnectDeviceButton.setVisibility(View.INVISIBLE);
			}			
			mDeviceText.setVisibility(View.VISIBLE);
			mSelectDeviceButton.setVisibility(View.VISIBLE);
			mConnectStatusText.setVisibility(View.INVISIBLE);
			break;
		case ConnectedDevice:
			mTopText.setText(R.string.setup_welcome);
			if (mCurrentDevice != null) {
				BluetoothDevice currentDevice = mBluetoothAdapter.getRemoteDevice(mCurrentDevice);
				String name = currentDevice.getName();
				if (name != null) {
				mDeviceText.setText(String.format(
						this.getString(R.string.setup_label_currentdevice),
						name));
				} else {
					mDeviceText.setText(String.format(
							this.getString(R.string.setup_label_currentdevice),
							mCurrentDevice));					
				}
				mConnectDeviceButton.setVisibility(View.VISIBLE);
			} else {
				mDeviceText.setText(String.format(
						this.getString(R.string.setup_label_currentdevice),
						this.getString(R.string.setup_nodeviceselected)));
				mConnectDeviceButton.setVisibility(View.INVISIBLE);
			}			
			mDeviceText.setVisibility(View.VISIBLE);
			mSelectDeviceButton.setVisibility(View.VISIBLE);
			mConnectStatusText.setVisibility(View.VISIBLE);
			break;
		}

	}
}
