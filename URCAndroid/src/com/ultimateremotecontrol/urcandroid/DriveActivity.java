package com.ultimateremotecontrol.urcandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ultimateremotecontrol.urcandroid.gui.VerticalSeekBar;
import com.ultimateremotecontrol.urcandroid.model.Command;
import com.ultimateremotecontrol.urcandroid.model.CommandProvider;
import com.ultimateremotecontrol.urcandroid.model.ConnectionHandler;
import com.ultimateremotecontrol.urcandroid.model.ConnectionHandler.State;
import com.ultimateremotecontrol.urcandroid.model.RemoteConnection;
import com.ultimateremotecontrol.urcandroid.model.TickStatusListener;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;	

public class DriveActivity extends Activity implements SensorEventListener, OnCheckedChangeListener, TickStatusListener, OnSeekBarChangeListener {

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Display mDisplay;
	private WindowManager mWindowManager;
	private int mX;
	private int mY;
	private int mZ;
	private int mE;
	private int mF;
	private ConnectionHandler mConnectionHandler;
	private BluetoothSocket mSocket;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drive);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        
        ToggleButton useAccelerometer = (ToggleButton)findViewById(R.id.toggleButtonAccelerometer);
        useAccelerometer.setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton1)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton2)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton3)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton4)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton5)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton6)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton7)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.toggleButton8)).setOnCheckedChangeListener(this);
		
		VerticalSeekBar leftBar = (VerticalSeekBar)findViewById(R.id.seekBarLeft);
		leftBar.setOnSeekBarChangeListener(this);
		VerticalSeekBar rightBar = (VerticalSeekBar)findViewById(R.id.seekBarRight);
		rightBar.setOnSeekBarChangeListener(this);		
		if (useAccelerometer.isChecked()) {
			useAccelerometer();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected void onResume() {
		String address = getIntent().getExtras().getString(SetupActivity.INTENT_EXTRA_DEVICE_ADDRESS);
		if (address != null) {
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
			if (device != null) {
				try {
					mSocket = device.createRfcommSocketToServiceRecord(SetupActivity.URC_UUID);
					URCLog.d("Socket created, trying to connect.");
					mSocket.connect();
					OutputStream out = mSocket.getOutputStream();
					InputStream in = mSocket.getInputStream();		
			
					mConnectionHandler = new ConnectionHandler(new RemoteConnection(in,  out), CommandProvider.getCommandProvider(), this);
					
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
		
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(this);
		if (mConnectionHandler != null) {
			mConnectionHandler.stop();			
		}
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
		super.onStop();
	}
	
	private void useAccelerometer() {
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}
	
	private void useUIControls() {
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(int arg0, int arg1) {
		
	}

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
  
        float X = 0;
        float Y = 0;
        float Z = 0;

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                X = event.values[0];
                Y = event.values[1];
                Z = event.values[2];
                break;
            case Surface.ROTATION_90:
                X = -event.values[1];
                Y = event.values[0];
                Z = event.values[2];
                break;
            case Surface.ROTATION_180:
                X = -event.values[0];
                Y = -event.values[1];
                Z = event.values[2];
                break;
            case Surface.ROTATION_270:
                X = event.values[1];
                Y = -event.values[0];
                Z = event.values[2];
                break;
        }
        mX = Math.round(((X+10.0f)/20.0f)*255);
        mY = Math.round(((Y+10.0f)/20.0f)*255);
        mZ = Math.round(((Z+10.0f)/20.0f)*255);
        
        //URCLog.d(String.format("X: %f Y: %f Z: %f mX: %d mY %d mZ %d", X, Y, Z, mX, mY, mZ));
        setCommand();
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.toggleButtonAccelerometer) {
			if (isChecked) {
				useAccelerometer();
			}
			else
			{
				useUIControls();
			}
		}
		setCommand();
	}

	public void onStateChanged(State state) {
		// TODO: StateChangeListener thingie
	}
	
	
	private void setCommand() {
		int X = mX;
		int Y = mY;
		int Z = mZ;
		int E = mE;
		int F = mF;
		boolean b1 = ((ToggleButton)findViewById(R.id.toggleButton1)).isChecked();
		boolean b2 = ((ToggleButton)findViewById(R.id.toggleButton2)).isChecked();
		boolean b3 = ((ToggleButton)findViewById(R.id.toggleButton3)).isChecked();
		boolean b4 = ((ToggleButton)findViewById(R.id.toggleButton4)).isChecked();
		boolean b5 = ((ToggleButton)findViewById(R.id.toggleButton5)).isChecked();
		boolean b6 = ((ToggleButton)findViewById(R.id.toggleButton6)).isChecked();
		boolean b7 = ((ToggleButton)findViewById(R.id.toggleButton7)).isChecked();
		boolean b8 = ((ToggleButton)findViewById(R.id.toggleButton8)).isChecked();
		CommandProvider.getCommandProvider().setCurrentCommand(new Command(X, Y, Z, 0, 0, 0, 0, E, F, b1, b2, b3, b4, b5, b6, b7, b8));
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int value = (int) (progress * 2.55);
		if (seekBar.getId() == R.id.seekBarLeft) {
			mE = value;
		} else {
			mF = value;
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}
