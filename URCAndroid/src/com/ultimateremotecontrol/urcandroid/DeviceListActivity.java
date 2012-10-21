package com.ultimateremotecontrol.urcandroid;

import java.util.Set;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

/** 
 * An Activity which displays a list of paired devices. The user can select a device from
 * the list.
 */
public class DeviceListActivity extends Activity implements OnItemClickListener {
	
	/** The id for the data stored in the intent. */
	public static final String INTENT_DEVICE_ID = "SelectedDeviceId";

	/** The bluetooth adapter. */
    private BluetoothAdapter mBluetoothAdapter;
    
    /** The ArrayAdapter for the paired device list. */
    private ArrayAdapter<DeviceListEntry> mPairedDevicesAdapter;
    
    /**
     * Class to store a device's name and id and display it in the device list. 
     */
    private class DeviceListEntry {
    	/** The name of the device. */
    	public final String deviceName;
    	
    	/** The id of the device. */
    	public final String deviceId;
    	
    	/**
    	 * Constructs a new DeviceListEntry.
    	 * @param name The name of the device.
    	 * @param id The id of the device.
    	 */
    	public DeviceListEntry(String name, String id) {
    		deviceName = name;
    		deviceId = id;
    	}
    	
    	/**
    	 * Returns a String in the format of "deviceName - deviceId".
    	 */
    	@Override
    	public String toString() {
    		return deviceName + " - " + deviceId;
    	}
    	
    }

    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        
        // Get the default bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // Create a ListAdapter to easily populate the ListView with entries with a custom layout.
        mPairedDevicesAdapter = new ArrayAdapter<DeviceListEntry>(this, R.layout.device_list_entry, R.id.device_list_entry_name);
    	ListView pairedDevicesView = (ListView) this.findViewById(R.id.list_paired_devices);
    	pairedDevicesView.setAdapter(mPairedDevicesAdapter);
    	pairedDevicesView.setOnItemClickListener(this);
	}
    
	
	/**
	 * Populates the paired device list.
	 */
    @Override
    protected void onResume() {
    	// Get the paired devices.
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	mPairedDevicesAdapter.clear();
    	for (BluetoothDevice device : pairedDevices) {
    		// Add all paired devices to the ListAdapter, which will then display them in the ListView.
    		mPairedDevicesAdapter.add(new DeviceListEntry(device.getName(), device.getAddress()));
    	}
    	super.onResume();
    }

    
    /**
     * Implements the onItemClick method of the OnItemClickListener interface.
     * When a device is selected, the id of the device is written into the Intent and the activity is finished.
     */
	public void	onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DeviceListEntry selectedDevice = null;
		if (parent.getId() == R.id.list_paired_devices) {
			selectedDevice = mPairedDevicesAdapter.getItem(position);
		}
		
		if (selectedDevice != null) {
			// Store the selected device id in an Intent.
            Intent intent = new Intent();
            intent.putExtra(INTENT_DEVICE_ID, selectedDevice.deviceId);

            // Set result to OK with the intent, that contains the device id and finish the activity.
            setResult(Activity.RESULT_OK, intent);
            finish();
		}
	}    
}
