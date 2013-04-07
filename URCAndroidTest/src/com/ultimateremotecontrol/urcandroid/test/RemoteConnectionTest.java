package com.ultimateremotecontrol.urcandroid.test;

import com.ultimateremotecontrol.urcandroid.model.Command;
import com.ultimateremotecontrol.urcandroid.model.RemoteConnection;
import com.ultimateremotecontrol.urcandroid.test.mocks.ArduinoMock;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RemoteConnectionTest extends TestCase {

	private ArduinoMock mArduino;
	private RemoteConnection mConnection;

	@Override
	protected void setUp() throws Exception {
		mArduino = new ArduinoMock();
		mConnection = new RemoteConnection(mArduino.getInputStream(), mArduino.getOutputStream());
		
		super.setUp();
	}
	
	public void testSingleCommand() {
		Command simple = new Command(0, 1, 2, 3, 4, 5, 6, 7, 8, true, false, true, false, true, false, true, false);
		mArduino.start();
		mConnection.sendCommand(simple);
		mArduino.stop();
		Assert.assertEquals(simple.toString(), mArduino.getReceivedMessages().get(0));
	}
}
