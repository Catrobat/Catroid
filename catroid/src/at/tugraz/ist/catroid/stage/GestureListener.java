/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.stage;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
	private StageActivity mStageView;

	public GestureListener(StageActivity stageActivity) {
		super();
		mStageView = stageActivity;
	}

	@Override
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
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		Log.d("StageGestureDetection", "onScroll" + e1.toString() + " distanceX="
				+ Float.toString(distanceX) + " distanceY=" + Float.toString(distanceY));
		return true;
	}

	@Override
	public boolean onDown(MotionEvent ev) {
		Log.d("StageGestureDetection", "onDown" + ev.toString() + " number of pointers " + ev.getPointerCount());
		mStageView.processOnTouch((int)ev.getX(),(int)ev.getY());
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.d("StageGestureDetection", "onFlying Start" + e1.toString());
		Log.d("StageGestureDetection", "onFlying Stop" + e2.toString());
		return true;
	}
}
