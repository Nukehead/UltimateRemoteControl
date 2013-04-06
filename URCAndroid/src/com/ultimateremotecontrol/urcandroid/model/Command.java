package com.ultimateremotecontrol.urcandroid.model;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

import com.ultimateremotecontrol.urcandroid.URCLog;


/**
 * Stores the values representing the current settings of the remote control.
 * It features functions to convert the values into human readable strings and also
 * into byte arrays to transfer to a remote client.
 */
public class Command implements Comparable<Command> {
	
	public static final String ENCODING = "US-ASCII";
	private LinkedHashMap<String, String> mValues;
	
	/**
	 * Creates a new Command object with the given values.
	 * @param X Position sensor X
	 * @param Y Position sensor Y
	 * @param Z Position sensor Z
	 * @param A X coordinates of the left controller
	 * @param B Y coordinates of the left controller
	 * @param C X coordinates of the right controller
	 * @param D Y coordinates of the right controller
	 * @param E value of slider 1
	 * @param F value of slider 2
	 * @param G Toggle switch 1
	 * @param H Toggle switch 2
	 * @param I Toggle switch 3
	 * @param J Toggle switch 4
	 * @param K Toggle switch 5
	 * @param L Toggle switch 6
	 * @param M Toggle switch 7
	 * @param N Toggle switch 8
	 */
	public Command(int X, int Y, int Z, int A, int B, int C, int D, int E, int F, boolean G, boolean H, boolean I, boolean J, boolean K, boolean L, boolean M, boolean N) {
		mValues = new LinkedHashMap<String, String>();
		mValues.put("X", String.valueOf(normalize(X)));
		mValues.put("Y", String.valueOf(normalize(Y)));
		mValues.put("Z", String.valueOf(normalize(Z)));
		mValues.put("A", String.valueOf(normalize(A)));
		mValues.put("B", String.valueOf(normalize(B)));
		mValues.put("C", String.valueOf(normalize(C)));
		mValues.put("D", String.valueOf(normalize(D)));
		mValues.put("E", String.valueOf(normalize(E)));
		mValues.put("F", String.valueOf(normalize(F)));
		mValues.put("G", G ? "1" : "0");
		mValues.put("H", H ? "1" : "0");
		mValues.put("I", I ? "1" : "0");
		mValues.put("J", J ? "1" : "0");
		mValues.put("K", K ? "1" : "0");
		mValues.put("L", L ? "1" : "0");
		mValues.put("M", M ? "1" : "0");
		mValues.put("N", N ? "1" : "0");
		}

 	private short normalize(int x) {
		int upper = Math.min(255, x);
		int lower = Math.max(0, upper);
		return (short)lower;
	}

 	/**
 	 * Compare to another Command.
 	 * @param another The other Command to compare to.
 	 * @returns Returns 0 if the command is identical to the other or equal to the other. 1 if not.
 	 */
	public int compareTo(Command another) {
		if (this == another) {
			return 0;
		}
		
		if (another == null) {
			return 1;
		}
		
		if (this.toString().compareTo(another.toString()) == 0) {
			return 0;
		}
		
		return 1;
	}
	
	
	/**
	 * Converts the command to a byte Array encoding the result of toString to bytes using ASCII.
	 * @return Bytes using ASCII encoding of the toString result. Null of the conversion fails.
	 */
	public byte[] toByte()
	{
		String result = toString();
		byte[] bytes = null;
		try {
			bytes = result.getBytes(ENCODING);
		} catch (UnsupportedEncodingException enc) {
			URCLog.d("No US-ASCII encoding available.");
		}
		
		return bytes;
	}

	public String toString() {
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mValues.keySet()) {
			resultBuffer.append(key);
			resultBuffer.append(mValues.get(key));
			resultBuffer.append(';');
		}
		resultBuffer.append('\n');
		String result = resultBuffer.toString();
		return result;
	}
	
	/**
	 * Generates a byte[] which is the difference between the Command diff
	 * and this.
	 * @param diff The Command to diff from.
	 * @return The difference between diff and this.
	 */
	public byte[] toDiffByte(Command diff) {
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mValues.keySet()) {
			String thisValue = mValues.get(key);
			String otherValue = diff.mValues.get(key);
			if (thisValue.compareTo(otherValue) != 0) {
				resultBuffer.append(key);
				resultBuffer.append(mValues.get(key));
				resultBuffer.append(';');
			}
		}
		resultBuffer.append('\n');
		String result = resultBuffer.toString();
		byte[] bytes = null;
		try {
			bytes = result.getBytes(ENCODING);
		} catch (UnsupportedEncodingException enc) {
			URCLog.d("No US-ASCII encoding available.");
		}
		
		return bytes;		
	}

}
