package com.ultimateremotecontrol.urcandroid.model;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

import com.ultimateremotecontrol.urcandroid.URCLog;

public class Command implements Comparable<Command> {
	
	private LinkedHashMap<String, String> mValues;
	
	public Command(int X, int Y, int Z, int A, int B, int C, int D, int E, int F, boolean G, boolean H, boolean I, boolean J, boolean K) {
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
		}

 	private short normalize(int x) {
		int upper = Math.min(255, x);
		int lower = Math.max(0, upper);
		return (short)lower;
	}

	public int compareTo(Command another) {
		if (this == another) {
			return 0;
		}
		return 0;
	}
	
	public byte[] toByte()
	{
		String result = toString();
		byte[] bytes = null;
		try {
			bytes = result.getBytes("US-ASCII");
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
			bytes = result.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException enc) {
			URCLog.d("No US-ASCII encoding available.");
		}
		
		return bytes;		
	}

}
