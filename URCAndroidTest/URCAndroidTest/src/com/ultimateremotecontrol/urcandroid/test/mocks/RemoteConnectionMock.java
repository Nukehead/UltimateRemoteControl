package com.ultimateremotecontrol.urcandroid.test.mocks;

import java.util.List;
import java.util.Vector;

import com.ultimateremotecontrol.urcandroid.model.Command;
import com.ultimateremotecontrol.urcandroid.model.IRemoteConnection;

public class RemoteConnectionMock implements IRemoteConnection {
	
	private boolean[] mResults;
	private int[] mDelays;
	private List<Command> mCommands = new Vector<Command>();
	

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
		try {
			wait(mDelays[commandNumber]);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return mResults[commandNumber];
	}

	public void sendCommandAsync(Command command) {
		// TODO Auto-generated method stub
		
	}

	public void cancelCurrentCommand() {
		// TODO Auto-generated method stub
		
	}

}
