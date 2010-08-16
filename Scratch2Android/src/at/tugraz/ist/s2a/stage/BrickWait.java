package at.tugraz.ist.s2a.stage;

import java.util.Observable;

public class BrickWait extends Observable implements Runnable{
	public float mWaitTime=0; //the time to wait in seconds
	
    public void run() {
    	synchronized (this){
	    	try {
				wait((int)(mWaitTime*1000+1)); 
			} catch (Exception e) {
				e.printStackTrace();
			}
			setChanged();
	        notifyObservers( "waited" );
    	}
    	
    }

	
}
