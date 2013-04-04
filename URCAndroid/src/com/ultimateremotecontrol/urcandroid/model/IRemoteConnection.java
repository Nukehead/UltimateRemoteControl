package com.ultimateremotecontrol.urcandroid.model;

/**
 * A connection to a remote device. Consumes commands and sends them to the device.
 */
public interface IRemoteConnection {
	/**
	 * Sends a command to the remote device. Blocks until a response is received or
	 * the connection is cancelled.
	 * @param command Command to be sent, should not be null.
	 * @return Returns false, if command was null or no result was received.
	 */
	public boolean sendCommand(Command command);
	/**
	 * Sends a command to the remote device. Returns immediately.
	 * @param command Command to be sent, should not be null.
	 */
	public void sendCommandAsync(Command command);
	/**
	 * Cancels sending the current command.
	 */
	public void cancelCurrentCommand();
	
}
