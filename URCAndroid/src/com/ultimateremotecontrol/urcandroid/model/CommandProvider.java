package com.ultimateremotecontrol.urcandroid.model;

/**
 * Provides a common storage for the current command.
 *
 */
public class CommandProvider {
	private Command mCurrentCommand = null;
	private static CommandProvider mProvider = null;
	
	private CommandProvider() {
		
	}
	
	public static CommandProvider getCommandProvider() {
		if (mProvider == null) {
			mProvider = new CommandProvider();
		}
		
		return mProvider;
	}
	
	
	
	public Command getCurrentCommand() {
		return mCurrentCommand;
	}
	
	public void setCurrentCommand(Command command) {
		mCurrentCommand = command;
	}

}
