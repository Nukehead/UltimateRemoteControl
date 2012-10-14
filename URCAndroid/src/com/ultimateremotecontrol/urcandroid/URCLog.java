package com.ultimateremotecontrol.urcandroid;

import android.util.Log;

public class URCLog {
	public static final String URCLogTag = "URCLog";
	
	public static int d(String message)
	{
		return URCLog.d(URCLogTag, message);
	}

	@SuppressWarnings("unused")
	public static int d(String tag, String message)
	{
		if (BuildConfig.DEBUG || Log.isLoggable(URCLogTag, Log.DEBUG))
		{
			return Log.d(tag, message);
		}
		
		return 0;
	}
}
