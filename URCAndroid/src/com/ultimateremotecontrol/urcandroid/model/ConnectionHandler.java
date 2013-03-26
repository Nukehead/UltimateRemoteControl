package com.ultimateremotecontrol.urcandroid.model;

import java.util.Timer;
import java.util.TimerTask;

public class ConnectionHandler {
	public enum State {
		Start,
		CommandSent,
		WaitingWarning,
	}
	
	/**
	 * The interval in which the CommandProvider is queried for a new command.
	 */
	private static final int TICK_TIME = 100;

	/**
	 * The threshold at which tick count a send attempt is treated as stalled.
	 */
	public static final int WarningThreshold = 1000 / TICK_TIME;

	/**
	 * The threshold at which tick count a send attempt is treated as error and cancelled. 
	 */
	public static final int ErrorThreshold = WarningThreshold + 1;

	
	private Timer mTickTimer;
	
	public ConnectionHandler(RemoteConnection connection, CommandProvider provider, TickStatusListener listener)
	{
		mTickTimer = new Timer();
		mTickTimer.scheduleAtFixedRate(new TickMachine(provider, connection, listener), 0, TICK_TIME);
	}
	
	private class TickMachine extends TimerTask {
		private CommandProvider mProvider;
		private RemoteConnection mConnection;
		private Command mLastCommand;
		private int mSendTickCount;
		private TickStatusListener mListener;
		
		public TickMachine(CommandProvider provider, RemoteConnection connection, TickStatusListener listener) {
			mProvider = provider;
			mConnection = connection;
			mListener = listener;
			
			mLastCommand = null;
			
			mSendTickCount = 0;
		}
		
		State mState = State.Start;
		
		@Override
		public void run() {
			State currentState = mState;
			switch (mState) {
			case Start:
				// Fetch a command.
				Command nextCommand = mProvider.getCurrentCommand();
				if (nextCommand != null) {
					if (nextCommand == mLastCommand) {
						// Thank you, we're done.
					} else {
						mSendTickCount = 0;
						mConnection.sendCommandAsync(nextCommand);
						mState = State.CommandSent;
					}
				}
				break;
			
			case CommandSent:
			case WaitingWarning:
				// We have a command sent to the connection.
				// Did we receive a result?
				// TODO: Check for result.
				String result = null;
				
				if (result == null) {
				
					if (mSendTickCount == ErrorThreshold) {
						mConnection.cancelCurrentCommand();
						mState = State.Start;
					} else if (mSendTickCount >= WarningThreshold) {
						mState = State.WaitingWarning;
					}
				}
				break;
				
			default:
				// Procrastinate :)
				
			}
			
			if (currentState != mState) {
				mListener.onStateChanged(mState);
			}
				
			
		}
		
		
		
	}
}
