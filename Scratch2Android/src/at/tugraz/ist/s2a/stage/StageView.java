package at.tugraz.ist.s2a.stage;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.s2a.R;

/**
 * 
 * The stage view which extends a SurfaceView and and creates a new
 * StageViewThread
 * 
 * @author Thomas Holzmann
 * 
 */

public class StageView extends SurfaceView implements SurfaceHolder.Callback {
	private StageViewThread mThread;

	public StageView(Context context) {
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mThread = new StageViewThread(holder, context, new Handler());
		setFocusable(true); // need to get the key events
		setBackgroundResource(R.drawable.play_splash);
	}

	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {}

	
	public void surfaceCreated(SurfaceHolder holder) {
		// mThread.setRunning(true);
		// mThread.start();

	}

	
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		mThread.setRunning(false);
		while (retry) {
			try {
				mThread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	public StageViewThread getThread() {
		return mThread;

		
	}
	


}
