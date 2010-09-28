package at.tugraz.ist.catroid.stage;

import java.util.Observable;

import android.os.Handler;
import android.util.Log;

public class BrickWait extends Observable{
	public int mWaitTime=0; //the time to wait in milliseconds
	private int mAlreayWaited =0;
	public boolean mIsWaiting = false;
//	
//    public void run() {
//    	synchronized (this){
//	    	try {
//				wait((int)(mWaitTime*1000+1)); 
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			setChanged();
//	        notifyObservers( "waited" );
//    	}
//    	
//    }
	
    
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {

    	public void run() {

    		checkWaitTime();

    		/*
    		 * Now register it for running next time
    		 */

    		if (mIsWaiting)
    			handler.postDelayed(this, 100);
    	}

    };
    
    private void checkWaitTime(){
    	Log.i("BrickWait", "checking wait time...");
    	if (mAlreayWaited >= mWaitTime){
    		handler.removeCallbacks(runnable);
    		mAlreayWaited = 0;
    		mIsWaiting = false;
    		setChanged();
	        notifyObservers( "waited" );
    	}
    	else {
    		mAlreayWaited += 100;
    	}
    }
    
    public void pause() {
    	handler.removeCallbacks(runnable);
    	
    }
    
    public void start() {
    	mIsWaiting = true;
    	runnable.run();
    }

	
}
