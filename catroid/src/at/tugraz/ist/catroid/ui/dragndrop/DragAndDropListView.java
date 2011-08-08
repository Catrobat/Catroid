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

public class DragAndDropListView extends ExpandableListView implements OnLongClickListener {

	private static final int DRAG_BACKGROUND_COLOR = Color.parseColor("#e0103010");

	private ImageView dragView;
	private WindowManager.LayoutParams dragViewParameters;

	private int oldItemPosition;
	private int firstItemPosition;
	private int itemHeight;
	private int touchPointY;

	private int upperBound;
	private int lowerBound;

	private ImageView trashView;
	private int originalTrashWidth;
	private int originalTrashHeight;

	private int screenWidth;

	private DragAndDropListener dragAndDropListener;

	public DragAndDropListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
	}

	public void setTrashView(ImageView trashView) {
		this.trashView = trashView;
		ViewGroup.LayoutParams params = trashView.getLayoutParams();
		originalTrashWidth = params.width;
		originalTrashHeight = params.height;
	}

	public void setOnDragAndDropListener(DragAndDropListener listener) {
		dragAndDropListener = listener;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		touchPointY = (int) ev.getRawY();

		System.out.println("DragAndDropListView.onInterceptTouchEvent() y = " + touchPointY);

		if (dragAndDropListener != null && dragView != null) {
			onTouchEvent(ev);
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

					ViewGroup.LayoutParams layoutParams = trashView.getLayoutParams();
					layoutParams.width = originalTrashWidth;
					layoutParams.height = originalTrashHeight;
					trashView.setLayoutParams(layoutParams);
					trashView.setVisibility(View.GONE);

					if (ev.getX() > getWidth() * 3 / 4) {
						System.out.println("DragAndDropListView.onTouchEvent() removing x = " + ev.getX());
						dragAndDropListener.remove(oldItemPosition);
					} else {
						System.out.println("DragAndDropListView.onTouchEvent() dropping");
						dragAndDropListener.drop(firstItemPosition, oldItemPosition);
					}

					break;

				case MotionEvent.ACTION_MOVE:

					int y = (int) ev.getY();
					int itemPosition = pointToPosition((int) ev.getX(), y);

					System.out.println("DragAndDropListView.onTouchEvent() moving itemPosition = " + itemPosition);

					if (itemPosition == INVALID_POSITION) {
						break;
					}

					dragView((int) ev.getX(), (int) ev.getRawY());
					if (itemPosition >= 0) {
						dragAndDropListener.drag(oldItemPosition, itemPosition);
						oldItemPosition = itemPosition;

						adjustScrollBounds(y);

						if (y > lowerBound) {
							smoothScrollBy(10, 1);
						} else if (y < upperBound) {
							smoothScrollBy(-10, 1);
						}

					}

					break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	private void adjustScrollBounds(int y) {
		if (y >= getHeight() / 3) {
			upperBound = getHeight() / 3;
		}
		if (y <= getHeight() * 2 / 3) {
			lowerBound = getHeight() * 2 / 3;
		}

	}

	private void startDragging(Bitmap bitmap, int y) {
		stopDragging();

		ImageView imageView = new ImageView(getContext());
		imageView.setBackgroundColor(DRAG_BACKGROUND_COLOR);
		imageView.setImageBitmap(bitmap);

		dragViewParameters = createLayoutParameters();
		dragViewParameters.y = y - itemHeight / 2;

		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(imageView, dragViewParameters);
		dragView = imageView;
	}

	private WindowManager.LayoutParams createLayoutParameters() {

		WindowManager.LayoutParams windowParameters = new WindowManager.LayoutParams();
		windowParameters.gravity = Gravity.TOP | Gravity.LEFT;

		windowParameters.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParameters.width = screenWidth - originalTrashWidth + 2;
		windowParameters.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParameters.format = PixelFormat.TRANSLUCENT;

		return windowParameters;
	}

	private void dragView(int x, int y) {

		float alpha = 1.0f;
		android.view.ViewGroup.LayoutParams layoutParams = trashView.getLayoutParams();

		if (x > 100 && x < screenWidth - 100) {
			alpha = ((float) (screenWidth - x)) / (screenWidth - 100);
			float rate = 1 - alpha;
			layoutParams.width = (int) (originalTrashWidth * (1 + rate));
			layoutParams.height = (int) (originalTrashHeight * (1 + rate));
			trashView.setLayoutParams(layoutParams);

			dragViewParameters.alpha = alpha;
			dragViewParameters.width = screenWidth - layoutParams.width + 2;
		}

		dragViewParameters.y = y - itemHeight / 2;
		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.updateViewLayout(dragView, dragViewParameters);
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

		int itemPosition = pointToPosition(v.getLeft(), v.getTop());
		itemHeight = v.getHeight();

		boolean drawingCacheEnabled = v.isDrawingCacheEnabled();

		v.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
		v.setDrawingCacheEnabled(drawingCacheEnabled);

		startDragging(bitmap, touchPointY);
		dragAndDropListener.drag(itemPosition, itemPosition);

		trashView.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.trash_in);
		trashView.startAnimation(animation);

		oldItemPosition = itemPosition;
		firstItemPosition = oldItemPosition;

		return true;
	}

}