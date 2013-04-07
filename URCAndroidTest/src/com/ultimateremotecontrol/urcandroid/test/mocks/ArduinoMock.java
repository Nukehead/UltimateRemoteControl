package com.ultimateremotecontrol.urcandroid.test.mocks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.ultimateremotecontrol.urcandroid.model.Command;

public class ArduinoMock {

	private static final byte EOM = '\n';
	private int mDelay = 100;
	
	private PipedInputStream mIn = new PipedInputStream();
	private PipedOutputStream mOut = new PipedOutputStream();
	
	private boolean mStop;
	private Vector<String> mReceivedMessages = new Vector<String>();
	

	public OutputStream getOutputStream() {
		PipedOutputStream output = new PipedOutputStream();
		try {
			mIn.connect(output);
		} catch (IOException io) {
			io.printStackTrace();
		}
		return output;
	}

	public InputStream getInputStream() {
		PipedInputStream input = new PipedInputStream();
		try {
			mOut.connect(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	public void setDelay(int delay) {
		mDelay = delay;
	}
	
	public Vector<String> getReceivedMessages() {
		return mReceivedMessages;
	}
	
	public void start() {
		new Thread() {
			@Override
			public void run() {
				try {
					loop();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void stop() {
		mStop = true;
	}

	private void loop() throws InterruptedException, UnsupportedEncodingException, IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		boolean endOfStream = false;
		while (!mStop && !endOfStream) {
			Thread.sleep(100);

			// Read from input until we're done or there's nothing to read.
			while (mIn.available() > 0 && !endOfStream) {
				int value = mIn.read();
				if (value == -1 ) {
					// The end of the stream has been reached. Exit
					endOfStream = true;
				} else {
					buffer.write(value);
					if (value == EOM) {
						String message = new String(buffer.toByteArray(), Command.ENCODING);
						mReceivedMessages.add(message);
						Thread.sleep(mDelay);
						mOut.write(new byte[] {'!', '\n'});
					}
				}
			}
		}
	}

}
