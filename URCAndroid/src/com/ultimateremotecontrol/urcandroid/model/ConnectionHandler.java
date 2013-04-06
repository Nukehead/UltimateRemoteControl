package com.ultimateremotecontrol.urcandroid.model;

import java.util.Timer;
import java.util.TimerTask;

import com.ultimateremotecontrol.urcandroid.URCLog;

/**
 * Handles the delicate balance between remote connection, current command and the status listener.  
 *
 */
public class ConnectionHandler {
	
	/**
	 * The states of the currently sent command. 
	 */
	public enum State {
		Start,
		CommandSent,
		WaitingWarning,
		Error
	}
	
	/**
	 * The interval in which the ConnectionHandler updates its status.
	 */
	public static final int TICK_TIME = 100;

	/**
	 * The threshold at which tick count a send attempt is treated as stalled.
	 */
	public static final int WarningThreshold = 10;

	/**
	 * The threshold at which tick count a send attempt is treated as error and cancelled. 
	 */
	public static final int ErrorThreshold = 11;

	
	private Timer mTickTimer;
	
	public ConnectionHandler(IRemoteConnection connection, CommandProvider provider, TickStatusListener listener)
	{
		mTickTimer = new Timer();
		mTickTimer.scheduleAtFixedRate(new TickMachine(provider, connection, listener), 0, TICK_TIME);
	}
	
	public void stop() {
		mTickTimer.cancel();
	}
	
	private class TickMachine extends TimerTask {
		private CommandProvider mProvider;
		private IRemoteConnection mConnection;
		private Command mLastCommand;
		private Command mCurrentCommand;
		private int mSendTickCount;
		private TickStatusListener mListener;
		
		public TickMachine(CommandProvider provider, IRemoteConnection connection, TickStatusListener listener) {
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
					++mSendTickCount;
					if (nextCommand.compareTo(mLastCommand) == 0) {
						// Thank you, we're done.
					} else {
						mSendTickCount = 0;
						mConnection.sendCommandAsync(nextCommand);
						mCurrentCommand = nextCommand;
						mState = State.CommandSent;
					}
				}
				break;
			
			case CommandSent:
			case WaitingWarning:
				++mSendTickCount;
				// We have a command sent to the connection.
				// Was it transmitted successfully?
				Command lastSentCommand = mConnection.getLastSentCommand();
				if (mCurrentCommand == lastSentCommand) {
					// Success
					mLastCommand = mCurrentCommand;
					mCurrentCommand = null;
					mState = State.Start;
				} else {
					// Not done transmitting. Check if we've been waiting for too long.
					if (mSendTickCount == ErrorThreshold) {
						mConnection.cancelCurrentCommand();
						mState = State.Error;
					} else if (mSendTickCount >= WarningThreshold) {
						mState = State.WaitingWarning;
					}
				}
				break;
				
			default:
				// Procrastinate :)
			}
			
			if (currentState != mState && mListener != null) {
				URCLog.d(String.format("State changed to %s", mState.toString()));
				mListener.onStateChanged(mState);
				if (mState == State.Error) {
					mState = State.Start;
					URCLog.d(String.format("State changed to %s", mState.toString()));
					mListener.onStateChanged(mState);
				}
			}
		}
	}
}
