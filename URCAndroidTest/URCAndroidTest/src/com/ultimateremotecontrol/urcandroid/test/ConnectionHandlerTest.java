package com.ultimateremotecontrol.urcandroid.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.ultimateremotecontrol.urcandroid.model.Command;
import com.ultimateremotecontrol.urcandroid.model.CommandProvider;
import com.ultimateremotecontrol.urcandroid.model.ConnectionHandler;
import com.ultimateremotecontrol.urcandroid.model.ConnectionHandler.State;
import com.ultimateremotecontrol.urcandroid.model.TickStatusListener;
import com.ultimateremotecontrol.urcandroid.test.mocks.RemoteConnectionMock;

import junit.framework.Assert;
import junit.framework.TestCase;;

public class ConnectionHandlerTest extends TestCase implements TickStatusListener {
	
	private RemoteConnectionMock mRemoteConnection;
	private ConnectionHandler mConnectionHandler;
	private CountDownLatch mCountdown;

	@Override
	protected void setUp() throws Exception {
		mRemoteConnection = new RemoteConnectionMock();
		mConnectionHandler = new ConnectionHandler(mRemoteConnection, CommandProvider.getCommandProvider(), this); 
		
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		mConnectionHandler.stop();
		
		super.tearDown();
	}

	public void onStateChanged(State state) {
		if (state == State.CommandSent) {
			mCountdown.countDown();
		}
	}
	
	public void testSingleCommand() throws Exception {
		Command command = new Command(0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, false);
		mRemoteConnection.setResults(new boolean[] {true}, new int[] {250});
		CommandProvider.getCommandProvider().setCurrentCommand(command);
		WaitForResults(1);
		Command[] commands = mRemoteConnection.getCommands();
		if (commands.length > 0) {
			Assert.assertSame(command, commands[0]);
		}
		
	}

	private void WaitForResults(int results) throws InterruptedException {
		mCountdown = new CountDownLatch(results);
		mCountdown.await(10, TimeUnit.SECONDS);
	}

}
