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

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import at.tugraz.ist.catroid.content.WhenScript;

/**
 * @author DENISE
 * 
 */
public class SimpleGestureFilter extends SimpleOnGestureListener {

	public final static int SWIPE_UP = 1;
	public final static int SWIPE_DOWN = 2;
	public final static int SWIPE_LEFT = 3;
	public final static int SWIPE_RIGHT = 4;

	public final static int MODE_TRANSPARENT = 0;
	public final static int MODE_SOLID = 1;
	public final static int MODE_DYNAMIC = 2;

	private final static int ACTION_FAKE = -13; //just an unlikely number
	private int swipe_Min_Distance = 100;
	private int swipe_Max_Distance = 350;
	private int swipe_Min_Velocity = 100;

	private int mode = MODE_DYNAMIC;
	private boolean running = true;
	private boolean tapIndicator = false;

	private StageActivity context;
	private GestureDetector detector;
	private SimpleGestureListener listener;

	public SimpleGestureFilter(StageActivity stageActivity, SimpleGestureListener sgl) {

		this.context = stageActivity;
		this.detector = new GestureDetector(context, this);
		this.listener = sgl;
	}

	public void onTouchEvent(MotionEvent event) {

		if (!this.running) {
			return;
		}

		boolean result = this.detector.onTouchEvent(event);

		if (this.mode == MODE_SOLID) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		} else if (this.mode == MODE_DYNAMIC) {

			if (event.getAction() == ACTION_FAKE) {
				event.setAction(MotionEvent.ACTION_UP);
			} else if (result) {
				event.setAction(MotionEvent.ACTION_CANCEL);
			} else if (this.tapIndicator) {
				event.setAction(MotionEvent.ACTION_DOWN);
				this.tapIndicator = false;
			}

		}
		//else just do nothing, it's Transparent
	}

	public void setMode(int m) {
		this.mode = m;
	}

	public int getMode() {
		return this.mode;
	}

	public void setEnabled(boolean status) {
		this.running = status;
	}

	public void setSwipeMaxDistance(int distance) {
		this.swipe_Max_Distance = distance;
	}

	public void setSwipeMinDistance(int distance) {
		this.swipe_Min_Distance = distance;
	}

	public void setSwipeMinVelocity(int distance) {
		this.swipe_Min_Velocity = distance;
	}

	public int getSwipeMaxDistance() {
		return this.swipe_Max_Distance;
	}

	public int getSwipeMinDistance() {
		return this.swipe_Min_Distance;
	}

	public int getSwipeMinVelocity() {
		return this.swipe_Min_Velocity;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		final float xDistance = Math.abs(e1.getX() - e2.getX());
		final float yDistance = Math.abs(e1.getY() - e2.getY());

		if (xDistance > this.swipe_Max_Distance || yDistance > this.swipe_Max_Distance) {
			return false;
		}

		velocityX = Math.abs(velocityX);
		velocityY = Math.abs(velocityY);
		boolean result = false;

		if (velocityX > this.swipe_Min_Velocity && xDistance > this.swipe_Min_Distance) {
			if (e1.getX() > e2.getX()) {
				this.listener.onSwipe(SWIPE_LEFT);
				context.processOnTouch((int) e1.getX(), (int) e1.getY(), WhenScript.SWIPELEFT.toString());
			} else {
				this.listener.onSwipe(SWIPE_RIGHT);
				context.processOnTouch((int) e1.getX(), (int) e1.getY(), WhenScript.SWIPERIGHT.toString());
			}

			result = true;
		} else if (velocityY > this.swipe_Min_Velocity && yDistance > this.swipe_Min_Distance) {
			if (e1.getY() > e2.getY()) {
				this.listener.onSwipe(SWIPE_UP);
				context.processOnTouch((int) e1.getX(), (int) e1.getY(), WhenScript.SWIPEUP.toString());
			} else {
				this.listener.onSwipe(SWIPE_DOWN);
				context.processOnTouch((int) e1.getX(), (int) e1.getY(), WhenScript.SWIPEDOWN.toString());
			}

			result = true;
		}

		return result;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		this.tapIndicator = true;
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		this.listener.onDoubleTap();
		context.processOnTouch((int) arg0.getX(), (int) arg0.getY(), WhenScript.DOUBLETAPPED.toString());
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		this.listener.onSingleTouch();
		if (this.mode == MODE_DYNAMIC) { // we owe an ACTION_UP, so we fake an       
			arg0.setAction(ACTION_FAKE); //action which will be converted to an ACTION_UP later.                                    
			this.context.dispatchTouchEvent(arg0);
			context.processOnTouch((int) arg0.getX(), (int) arg0.getY(), WhenScript.TAPPED.toString());
			context.processOnTouch((int) arg0.getX(), (int) arg0.getY(), WhenScript.TOUCHINGSTOPS.toString());
			context.processOnTouch((int) arg0.getX(), (int) arg0.getY(), WhenScript.TOUCHINGSTARTS.toString());
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		this.listener.onLongPress();
		context.processOnTouch((int) e.getX(), (int) e.getY(), WhenScript.LONGPRESSED.toString());
	}

	static interface SimpleGestureListener {
		void onSwipe(int direction);

		void onDoubleTap();

		void onSingleTouch();

		void onLongPress();
	}

}