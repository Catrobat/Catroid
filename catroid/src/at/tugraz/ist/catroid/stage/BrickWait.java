package at.tugraz.ist.catroid.stage;

import java.util.Observable;

import android.os.Handler;
import android.util.Log;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class BrickWait extends Observable {
	//TODO pause/start needs to be tested
	
	public int mWaitTime=0; //the time to wait in milliseconds
	private int mAlreayWaited =0;
	public boolean mIsWaiting = false;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
    	public void run() {
    		checkWaitTime();
    		if (mIsWaiting)
    			mHandler.postDelayed(this, 100);
    	}

    };
    
    private void checkWaitTime(){
//    	Log.i("BrickWait", "checking wait time...");
    	if (mAlreayWaited >= mWaitTime){
    		mHandler.removeCallbacks(mRunnable);
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
    	mIsWaiting = false;
    	mHandler.removeCallbacks(mRunnable);
    	
    }
    
    public void start() {
    	mIsWaiting = true;
    	mRunnable.run();
    }

	
}
