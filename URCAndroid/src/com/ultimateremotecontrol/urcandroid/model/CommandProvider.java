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
	
	/**
	 * Returns the single Command Provider.
	 * @return Returns the single Command Provider.
	 */
	public static CommandProvider getCommandProvider() {
		if (mProvider == null) {
			mProvider = new CommandProvider();
		}
		
		return mProvider;
	}
	
	/**
	 * Gets the current command.	
	 * @return The current command.
	 */
	public Command getCurrentCommand() {
		return mCurrentCommand;
	}
	
	/**
	 * Sets the current command.
	 * @param command The command to set.
	 */
	public void setCurrentCommand(Command command) {
		mCurrentCommand = command;
	}

}
