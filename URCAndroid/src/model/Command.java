package model;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.ultimateremotecontrol.urcandroid.URCLog;

public class Command implements Comparable<Command> {
	
	private HashMap<String, String> mValues;
	
	public Command(short X, short Y, short Z, short A, short B, short C, short D, short E, short F, boolean G, boolean H, boolean I, boolean J, boolean K) {
		mValues = new HashMap<String, String>();
		mValues.put("X", String.valueOf(X));
		mValues.put("Y", String.valueOf(X));
		mValues.put("Z", String.valueOf(X));
		mValues.put("A", String.valueOf(X));
		mValues.put("B", String.valueOf(X));
		mValues.put("C", String.valueOf(X));
		mValues.put("D", String.valueOf(X));
		mValues.put("E", String.valueOf(X));
		mValues.put("F", String.valueOf(X));
		mValues.put("G", G ? "1" : "0");
		mValues.put("H", H ? "1" : "0");
		mValues.put("I", I ? "1" : "0");
		mValues.put("J", J ? "1" : "0");
		mValues.put("K", K ? "1" : "0");
		}

 	public int compareTo(Command another) {
		if (this == another) {
			return 0;
		}
		return 0;
	}
	
	public byte[] toByte()
	{
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mValues.keySet()) {
			resultBuffer.append(key);
			resultBuffer.append(mValues.get(key));
			resultBuffer.append(';');
		}
		resultBuffer.append('\n');
		String result = resultBuffer.toString();
		byte[] bytes = null;
		try {
			bytes = result.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException enc) {
			URCLog.d("No US-ASCII encoding available.");
		}
		
		return bytes;
	}

}
