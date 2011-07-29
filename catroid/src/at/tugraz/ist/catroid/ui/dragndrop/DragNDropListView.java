/*
 * Copyright (C) 2010 Draggable and Droppable ListView Project
 *
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
 * 
 */
package at.tugraz.ist.catroid.ui.dragndrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;

public class DragNDropListView extends ExpandableListView implements OnLongClickListener {

	private ImageView dragView;
	private WindowManager.LayoutParams windowParams;
	private int dragPosition;
	private int firstDragPosition;
	private int dragPoint;
	private int motionPositionY;
	private DragAndDropListener dragAndDropListener;
	private int upperBound;
	private int lowerBound;
	private ImageView trash;
	private int trashWidth;
	private int trashHeight;
	private int screenWidth;

	public DragNDropListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
	}

	public void setTrashView(ImageView trashView) {
		trash = trashView;
		android.view.ViewGroup.LayoutParams params = trash.getLayoutParams();
		trashWidth = params.width;
		trashHeight = params.height;
	}

	private void adjustScrollBounds(int y) {
		if (y >= getHeight() / 3) {
			upperBound = getHeight() / 3;
		}
		if (y <= getHeight() * 2 / 3) {
			lowerBound = getHeight() * 2 / 3;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		motionPositionY = (int) ev.getRawY();

		if (dragView != null) {
			switch (ev.getAction()) {
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:

					stopDragging();

					ViewGroup.LayoutParams layoutParams = trash.getLayoutParams();
					layoutParams.width = trashWidth;
					layoutParams.height = trashHeight;
					trash.setLayoutParams(layoutParams);
					trash.setVisibility(View.GONE);

					if (dragAndDropListener != null) {
						if (ev.getX() > Values.SCREEN_WIDTH * 3 / 4) {
							dragAndDropListener.remove(dragPosition);
						} else {
							dragAndDropListener.drop(firstDragPosition, dragPosition);
						}
					}
					break;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (dragAndDropListener != null && dragView != null) {
			int action = ev.getAction();
			switch (action) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					stopDragging();

					ViewGroup.LayoutParams layoutParams = trash.getLayoutParams();
					layoutParams.width = trashWidth;
					layoutParams.height = trashHeight;
					trash.setLayoutParams(layoutParams);
					trash.setVisibility(View.GONE);

					if (ev.getX() > Values.SCREEN_WIDTH * 3 / 4) {
						dragAndDropListener.remove(dragPosition);
					} else {
						dragAndDropListener.drop(firstDragPosition, dragPosition);
					}
					break;

				case MotionEvent.ACTION_DOWN:
					trash.setVisibility(View.VISIBLE);
					Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.trash_in);
					trash.startAnimation(animation);
				case MotionEvent.ACTION_MOVE:

					int x = (int) ev.getX();
					int y = (int) ev.getY();
					dragView(x, (int) ev.getRawY());
					int itemPosition = pointToPosition(x, y);
					if (itemPosition >= 0) {
						dragAndDropListener.drag(dragPosition, itemPosition);
						dragPosition = itemPosition;
						int speed = 0;
						adjustScrollBounds(y);
						if (y > lowerBound) {
							speed = y > (getHeight() + lowerBound) / 2 ? 16 : 4;

						} else if (y < upperBound) {
							speed = y < upperBound / 2 ? -16 : -4;
							speed = Math.max(-getScrollY(), speed);
						}

						if (getCount() > getLastVisiblePosition()) {
							scrollBy(0, speed);
						}

					}
					break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	private void startDragging(Bitmap bitmap, int y) {
		stopDragging();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = 0;
		windowParams.y = y - dragPoint;

		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = screenWidth - trashWidth + 2;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());
		int backgroundColor = Color.parseColor("#e0103010");
		imageView.setBackgroundColor(backgroundColor);
		imageView.setImageBitmap(bitmap);

		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(imageView, windowParams);
		dragView = imageView;
	}

	private void dragView(int x, int y) {

		float alpha = 1.0f;
		android.view.ViewGroup.LayoutParams layoutParams = trash.getLayoutParams();

		if (x > 100 && x < screenWidth - 100) {
			alpha = ((float) (screenWidth - x)) / (screenWidth - 100);
			float rate = 1 - alpha;
			layoutParams.width = (int) (trashWidth * (1 + rate));
			layoutParams.height = (int) (trashHeight * (1 + rate));
			trash.setLayoutParams(layoutParams);
			windowParams.alpha = alpha;
			windowParams.width = screenWidth - layoutParams.width + 2;
		}

		windowParams.y = y - dragPoint;
		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.updateViewLayout(dragView, windowParams);
	}

	private void stopDragging() {
		if (dragView != null) {
			dragView.setVisibility(GONE);
			WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			windowManager.removeView(dragView);
			dragView.setImageDrawable(null);
			dragView = null;
		}
	}

	public boolean onLongClick(View v) {

		int itemPosition = indexOfChild(v);
		dragPoint = v.getHeight() / 2;
		v.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
		v.setDrawingCacheEnabled(false);
		startDragging(bitmap, motionPositionY);
		dragAndDropListener.drag(itemPosition, itemPosition);
		trash.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.trash_in);
		trash.startAnimation(animation);
		dragPosition = itemPosition;
		firstDragPosition = dragPosition;

		return true;
	}

	public void setOnDragAndDropListener(DragAndDropListener listener) {
		dragAndDropListener = listener;
	}

}