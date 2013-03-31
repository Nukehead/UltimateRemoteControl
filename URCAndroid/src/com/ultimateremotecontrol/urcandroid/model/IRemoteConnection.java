package com.ultimateremotecontrol.urcandroid.model;

public interface IRemoteConnection {
	public boolean sendCommand(Command command);
	public void sendCommandAsync(Command command);
	public void cancelCurrentCommand();
	
}
