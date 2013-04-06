package com.ultimateremotecontrol.urcandroid.test.mocks;

import java.util.List;
import java.util.Vector;

import android.util.Log;

import com.ultimateremotecontrol.urcandroid.model.Command;
import com.ultimateremotecontrol.urcandroid.model.IRemoteConnection;

public class RemoteConnectionMock implements IRemoteConnection {
	
	private boolean[] mResults;
	private int[] mDelays;
	private List<Command> mCommands = new Vector<Command>();
	private Command mLastSentCommand = null;
	

	public void setResults(boolean[] results, int[] delays) {
		mResults = results;
		mDelays = delays;
		mCommands.clear();
	}
	
	public Command[] getCommands() {
		return (Command[])mCommands.toArray(new Command[0]);
	}

	public boolean sendCommand(Command command) {
		mCommands.add(command);
		int commandNumber = mCommands.size();
		if (commandNumber <= mDelays.length) {
			try {
				Thread.sleep(mDelays[commandNumber-1]);
				mLastSentCommand = command;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return mResults[commandNumber-1];
		}
		return true;
	}

	public void sendCommandAsync(final Command command) {
		new Thread() {
			@Override
			public void run() {
				sendCommand(command);
				super.run();
			}
		}.start();
	}

	public void cancelCurrentCommand() {
	}

	public Command getLastSentCommand() {
		return mLastSentCommand;
	}

}
