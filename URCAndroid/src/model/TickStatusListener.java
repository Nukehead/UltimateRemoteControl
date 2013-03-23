package model;

import model.ConnectionHandler.State;

public interface TickStatusListener {
	
	public void onStateChanged(State state);
	

}
