package com.ultimateremotecontrol.urcandroid.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ultimateremotecontrol.urcandroid.URCLog;

/**
 * A connection to a remote device. Consumes commands and sends them to the device.
 */
public class RemoteConnection implements IRemoteConnection {
	private static final byte EOM = '!';
	private InputStream mIn;
	private OutputStream mOut;
	private boolean mCancel;
	private boolean mLastAttempt = true;
	
	public RemoteConnection(InputStream in, OutputStream out) {
		mIn = in;
		mOut = out;
	}
	

	/**
	 * Sends a command to the remote device. Blocks until a response is received or
	 * the connection is cancelled.
	 * @param command Command to be sent, should not be null.
	 * @return Returns false, if command was null or no result was received.
	 */
	public boolean sendCommand(Command command) {
		if (command == null) {
			return false;
		}
		mCancel = false;
		
		if (mOut != null && mIn != null) {
			if (!mLastAttempt) {
				waitForReady();		
			}
			
			try {
			
				mOut.write(command.toByte());
			} catch(IOException io) {
				// Something went wrong during the connection.
				URCLog.d("RemoteConnection", "Exception during sendCommand:\n" + io.getMessage());
				mLastAttempt = false;
				return mLastAttempt;
			}
			
			mLastAttempt = waitForReady();
		}
			
		return mLastAttempt;
	}


	/**
	 * Waits until the response from the remote device returns a ready signal.
	 * @return Returns true, if the response was received. False if cancelled or the connection
	 * exploded.
	 */
	private boolean waitForReady() {
		try {
			// Sending is done, now we need to receive.
			// We wait until the response from the other side arrives or someone cancels
			boolean foundIt = false;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			while (!foundIt && !mCancel) {
				// Read from input until we're done.
				int probablyAvailable = mIn.available();
				if (probablyAvailable > 0) {
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
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (foundIt) {
				return true;
			}
			
		} catch(IOException io) {
			// Something went wrong during receiving.
			URCLog.d("RemoteConnection", "Exception during waitForReady:\n" + io.getMessage());
		}
		
		return false;
	}
	
	public void sendCommandAsync(Command command) {
		new Thread(new SendCommandThread(command)).run();
	}
	
	private class SendCommandThread implements Runnable {
		private Command mCommand;

		public SendCommandThread(Command command) {
			mCommand = command;
		}

		public void run() {
			sendCommand(mCommand);
		}
		
	}
	
	public void cancelCurrentCommand() {
		mCancel = true;
	}
}
