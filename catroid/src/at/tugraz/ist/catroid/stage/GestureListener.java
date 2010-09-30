package at.tugraz.ist.catroid.stage;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
	private StageManager mStageManager;
	

	public GestureListener(StageManager stageManager) {
		super();
		mStageManager = stageManager;
	}

	public boolean onSingleTapUp(MotionEvent ev) {
		Log.d("StageGestureDetection", "onsingleTapUp" + ev.toString());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent ev) {
		Log.d("StageGestureDetection", "onShowPress" + ev.toString());
	}

	@Override
	public void onLongPress(MotionEvent ev) {
		Log.d("StageGestureDetection", "onLongPress" + ev.toString());
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.d("StageGestureDetection", "onScroll" + e1.toString() + " distanceX=" + Float.toString(distanceX) + " distanceY=" + Float.toString(distanceY));
		return true;
	}

	@Override
	public boolean onDown(MotionEvent ev) {
		Log.d("StageGestureDetection", "onDown" + ev.toString());
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.d("StageGestureDetection", "onFlying Start" + e1.toString());
		Log.d("StageGestureDetection", "onFlying Stop" + e2.toString());
		return true;
	}
}
