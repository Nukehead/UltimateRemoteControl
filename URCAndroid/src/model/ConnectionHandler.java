package model;

import java.util.Timer;
import java.util.TimerTask;

public class ConnectionHandler {
	
	/**
	 * The interval in which the CommandProvider is queried for a new command.
	 */
	private static final int TICK_TIME = 200;
	
	
	private RemoteConnection mRemoteConnection;
	private CommandProvider mCommandProvider;
	
	private Timer mTickTimer;
	
	public ConnectionHandler(RemoteConnection connection, CommandProvider provider)
	{
		mRemoteConnection = connection;
		mCommandProvider = provider;
		mTickTimer = new Timer();
		mTickTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				// do tick
				// if no command is currently sent to the remote connection
				// get a new command, and send it (asynchronously).
				// if a command is currently sent to the remote connection
				// and it has not yet finished and the timeout threshold has been
				// reached, update status.
			}
			
		}, 0, TICK_TIME);
	}
}
