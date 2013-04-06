package com.ultimateremotecontrol.urcandroid.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.ultimateremotecontrol.urcandroid.model.Command;
import com.ultimateremotecontrol.urcandroid.model.CommandProvider;
import com.ultimateremotecontrol.urcandroid.model.ConnectionHandler;
import com.ultimateremotecontrol.urcandroid.model.ConnectionHandler.State;
import com.ultimateremotecontrol.urcandroid.model.TickStatusListener;
import com.ultimateremotecontrol.urcandroid.test.mocks.RemoteConnectionMock;

public class ConnectionHandlerTest extends TestCase implements TickStatusListener {
	
	private RemoteConnectionMock mRemoteConnection;
	private ConnectionHandler mConnectionHandler;
	private CountDownLatch mCountdownSent;
	private CountDownLatch mCountdownStart;
	private CountDownLatch mCountdownWarning;
	private CountDownLatch mCountdownError;

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
		if (state == State.CommandSent && mCountdownSent != null) {
			mCountdownSent.countDown();
		} else if (state == State.Start && mCountdownStart != null) {
			mCountdownStart.countDown();
		} else if (state == State.WaitingWarning && mCountdownWarning != null) {
			mCountdownWarning.countDown();
		} else if (state == State.Error && mCountdownError != null) {
			mCountdownError.countDown();
		}
	}
	
	public void testSingleCommand() throws Throwable {
		Command command = new Command(0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, false, false, false, false);
		mRemoteConnection.setResults(new boolean[] {true}, new int[] {250});
		CommandProvider.getCommandProvider().setCurrentCommand(command);
		Assert.assertTrue(waitForSent());
		Assert.assertTrue(waitForStart());
		Command[] commands = mRemoteConnection.getCommands();
		if (commands.length > 0) {
			Assert.assertSame(command, commands[0]);
		}
	}
	
	public void testMultipleCommands() throws Throwable {
		Command command = new Command(0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, false, false, false, false);
		Command command2 = new Command(1, 2, 3, 4, 5, 6, 7, 8, 9, true, true, true, true, true, true, true, true);
		mRemoteConnection.setResults(new boolean[] {true, true}, new int[] {900, 500});
		CommandProvider.getCommandProvider().setCurrentCommand(command);
		Assert.assertTrue(waitForSent());
		CommandProvider.getCommandProvider().setCurrentCommand(command2);
		Assert.assertTrue(waitForSent());
		Assert.assertTrue(waitForStart());
		Command[] commands = mRemoteConnection.getCommands();
		Assert.assertEquals(2, commands.length);
		Assert.assertEquals(command, commands[0]);
		Assert.assertEquals(command2, commands[1]);
	}
	
	
	public void testCommandTakesLong() throws Throwable {
		Command command = new Command(0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, false, false, false, false);
		mRemoteConnection.setResults(new boolean[] {true}, new int[] {1050});
		CommandProvider.getCommandProvider().setCurrentCommand(command);
		Assert.assertTrue(waitForSent());
		Assert.assertTrue(waitForWarning());
		Assert.assertTrue(waitForStart());
		Command[] commands = mRemoteConnection.getCommands();
		if (commands.length > 0) {
			Assert.assertSame(command, commands[0]);
		}
	}
	
	
	public void testCommandTakesTooLong() throws Throwable {
		Command command = new Command(0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, false, false, false, false);
		mRemoteConnection.setResults(new boolean[] {true}, new int[] {1150});
		CommandProvider.getCommandProvider().setCurrentCommand(command);
		Assert.assertTrue(waitForSent());
		Assert.assertTrue(waitForWarning());
		Assert.assertTrue(waitForError());
		Assert.assertTrue(waitForStart());		
		CommandProvider.getCommandProvider().setCurrentCommand(null);
		Command[] commands = mRemoteConnection.getCommands();
		if (commands.length > 0) {
			Assert.assertSame(command, commands[0]);
		}
	}	

	private boolean waitForSent() throws InterruptedException {
		mCountdownSent = new CountDownLatch(1);
		return mCountdownSent.await(10, TimeUnit.SECONDS);
	}
	
	private boolean waitForStart() throws InterruptedException {
		mCountdownStart = new CountDownLatch(1);
		return mCountdownStart.await(10, TimeUnit.SECONDS);
	} 
	
	private boolean waitForWarning() throws InterruptedException {
		mCountdownWarning = new CountDownLatch(1);
		return mCountdownWarning.await(10, TimeUnit.SECONDS);
	}
	
	private boolean waitForError() throws InterruptedException {
		mCountdownError = new CountDownLatch(1);
		return mCountdownError.await(10, TimeUnit.SECONDS);
	}

}
