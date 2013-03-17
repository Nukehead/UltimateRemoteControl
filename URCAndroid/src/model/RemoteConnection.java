package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A connection to a remote device. Consumes commands and sends them to the device.
 */
public class RemoteConnection {
	private InputStream mIn;
	private OutputStream mOut;
	
	public RemoteConnection(InputStream in, OutputStream out) {
		mIn = in;
		mOut = out;
	}
	
	
	public boolean connect() {
		return false;
	}
	
	public void disconnect() {
		
	}
	

	/**
	 * Sends a command to the remote device. Blocks until a response is received or
	 * the connection is cancelled.
	 * @param command Command to be sent, should not be null.
	 * @return Returns null, if command was null or no result was received.
	 * TODO: Use a good result type.
	 */
	public String sendCommand(Command command) {
		if (command == null) {
			return null;
		}
		
		if (mOut != null) {
			try {
			
			mOut.write(command.toByte());
			} catch(IOException io) {
				// Something when wrong during the connection.
				return null;
			}
		}
		
		// Sending is done, now we need to receive.
		// Receiving is done in a separate thread, this one blocks until everything is received,
		// or someone cancelled.
		
		
			
		return "";
	}
}
