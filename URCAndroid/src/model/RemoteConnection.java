package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A connection to a remote device. Consumes commands and sends them to the device.
 */
public class RemoteConnection {
	private static final byte EOM = 0;
	private InputStream mIn;
	private OutputStream mOut;
	private boolean mCancel;
	
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
		mCancel = false;
		
		if (mOut != null && mIn != null) {
			try {
			
				mOut.write(command.toByte());
			} catch(IOException io) {
				// Something went wrong during the connection.
				return null;
			}
			
			try {
				// Sending is done, now we need to receive.
				// We wait until the response from the other side arrives or someone cancels
				boolean foundIt = false;
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				while (!foundIt && !mCancel) {
					// Read from input until we're done.
					int probablyAvailable = mIn.available();
					byte[] probableBuffer = new byte[probablyAvailable];
					mIn.read(probableBuffer);
					for(int i=0; i<probableBuffer.length; ++i) {
						 byte b = probableBuffer[i];
						 if (b == EOM) {
							 // We found it.
							 buffer.write(probableBuffer, 0, i);
							 foundIt = true;
							 break;
						 }
							 
					}
					
					// The input stream did not contain the byte we were looking for.
					// Store it in the buffer.
					if (!foundIt) {
						buffer.write(probableBuffer);
					}
				}
				
				if (foundIt) {
					// Convert the response to something useful.
					
				}
				
			} catch(IOException io) {
				// Something went wrong during receiving.
			}
			
			
		}
			
		return "";
	}
	
	public void sendCommandAsync(Command command) {
		new Thread(new SendCommandThread(command)).run();
	}
	
	private class SendCommandThread implements Runnable {
		private Command mCommand;

		public SendCommandThread(Command command) {
			mCommand = command;
		}

		@Override
		public void run() {
			sendCommand(mCommand);
		}
		
	}
	
	public void cancelCurrentCommand() {
		mCancel = true;
	}
}
