package com.ultimateremotecontrol.urcandroid.model;

import com.ultimateremotecontrol.urcandroid.model.ConnectionHandler.State;

public interface TickStatusListener {
	
	public void onStateChanged(State state);
	

}
