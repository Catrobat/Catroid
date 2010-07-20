package com.tugraz.android.app.stage;

import java.util.Observable;

public class BrickWait extends Observable implements Runnable{
	public int mWaitTime=0; //the time to wait in seconds
	
    public void run() {
    	synchronized (this){
	    	try {
				wait(mWaitTime*1000+1); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setChanged();
	        notifyObservers( "waited" );
    	}
    	
    }

	
}
