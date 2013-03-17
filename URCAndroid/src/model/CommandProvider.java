package model;

/**
 * Provides a common storage for the current command.
 *
 */
public class CommandProvider {
	private Command mCurrentCommand = null;
	
	public Command getCurrentCommand() {
		return mCurrentCommand;
	}
	
	public void setCurrentCommand(Command command) {
		mCurrentCommand = command;
	}

}
