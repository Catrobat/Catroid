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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
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

	private static final int SCROLL_DURATION = 1;
	private static final int SCROLL_SPEED = 10;
	private static final int DRAG_BACKGROUND_COLOR = Color.TRANSPARENT;

	private int maximumDragViewHeight;

	private int previousItemPosition;
	private int firstItemPosition;
	private int touchPointY;

	private int upperScrollBound;
	private int lowerScrollBound;
	private int upperDragBound;
	private int lowerDragBound;

	private ImageView dragView;

	private ImageView trashView;
	private int originalTrashWidth;
	private int originalTrashHeight;

	private DragAndDropListener dragAndDropListener;

	public DragAndDropListView(Context context) {
		super(context);
	}

	public DragAndDropListView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public DragAndDropListView(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);
	}

	public void setTrashView(ImageView trashView) {
		this.trashView = trashView;
		ViewGroup.LayoutParams parameters = trashView.getLayoutParams();
		originalTrashWidth = parameters.width;
		originalTrashHeight = parameters.height;
	}

	public void setOnDragAndDropListener(DragAndDropListener listener) {
		dragAndDropListener = listener;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (dragAndDropListener != null && dragView != null) {
			onTouchEvent(event);
		}

		touchPointY = (int) event.getRawY();
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();
		int itemPosition = pointToPosition(x, y);

		if (y > getChildAt(getChildCount() - 1).getBottom()) {
			itemPosition = getChildCount() - 1;
		}

		if (dragAndDropListener != null && dragView != null) {
			int action = event.getAction();
			switch (action) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:

					stopDragging();

					ViewGroup.LayoutParams layoutParamemters = trashView.getLayoutParams();
					layoutParamemters.width = originalTrashWidth;
					layoutParamemters.height = originalTrashHeight;
					trashView.setLayoutParams(layoutParamemters);
					trashView.setVisibility(View.GONE);

					if (x > getWidth() * 3 / 4) {
						dragAndDropListener.remove(itemPosition);
					} else {
						dragAndDropListener.drop(itemPosition);
					}

					break;

				case MotionEvent.ACTION_MOVE:

					if (y > lowerScrollBound) {
						smoothScrollBy(SCROLL_SPEED, SCROLL_DURATION);
						lowerDragBound -= SCROLL_SPEED;
						upperDragBound -= SCROLL_SPEED;
					} else if (y < upperScrollBound) {
						smoothScrollBy(-SCROLL_SPEED, SCROLL_DURATION);
						lowerDragBound += SCROLL_SPEED;
						upperDragBound += SCROLL_SPEED;
					}

					dragView(x, (int) event.getRawY());

					if (y > lowerDragBound || y < upperDragBound && itemPosition != INVALID_POSITION) {
						dragAndDropListener.drag(previousItemPosition, itemPosition);
						previousItemPosition = itemPosition;

						if (itemPosition > 0) {
							View upperChild = getChildAt(itemPosition - 1);
							upperDragBound = upperChild.getBottom() - upperChild.getHeight() / 2;
						} else {
							upperDragBound = 0;
						}

						if (itemPosition < getChildCount() - 1) {
							View lowerChild = getChildAt(itemPosition + 1);
							lowerDragBound = lowerChild.getTop() + lowerChild.getHeight() / 2;
						} else {
							lowerDragBound = getHeight();
						}
					}

					break;
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		upperScrollBound = height / 3;
		lowerScrollBound = height * 2 / 3;
		maximumDragViewHeight = height / 3;
	}

	public boolean onLongClick(View view) {

		int itemPosition = pointToPosition(view.getLeft(), view.getTop());
		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();

		view.setDrawingCacheEnabled(true);

		if (view.getDrawingCache() == null) {
			view.layout(0, 0, view.getWidth(), maximumDragViewHeight);
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(drawingCacheEnabled);

		startDragging(bitmap, touchPointY);
		dragAndDropListener.drag(itemPosition, itemPosition);

		trashView.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.trash_in);
		trashView.startAnimation(animation);

		previousItemPosition = itemPosition;
		firstItemPosition = previousItemPosition;

		return true;
	}

	private void startDragging(Bitmap bitmap, int y) {
		stopDragging();

		if (bitmap.getHeight() > maximumDragViewHeight) {
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), maximumDragViewHeight);
		}

		ImageView imageView = new ImageView(getContext());
		imageView.setBackgroundColor(DRAG_BACKGROUND_COLOR);
		imageView.setImageBitmap(bitmap);

		WindowManager.LayoutParams dragViewParameters = createLayoutParameters();
		dragViewParameters.y = y - bitmap.getHeight() / 2;

		WindowManager windowManager = getWindowManager();
		windowManager.addView(imageView, dragViewParameters);
		dragView = imageView;
	}

	private void dragView(int x, int y) {

		ViewGroup.LayoutParams trashViewParameters = trashView.getLayoutParams();
		WindowManager.LayoutParams dragViewParameters = (WindowManager.LayoutParams) dragView.getLayoutParams();

		if (x > 100 && x < getWidth() - 100) {
			float alpha = ((float) (getWidth() - x)) / (getWidth() - 100);
			float rate = 1 - alpha;
			trashViewParameters.width = (int) (originalTrashWidth * (1 + rate));
			trashViewParameters.height = (int) (originalTrashHeight * (1 + rate));
			trashView.setLayoutParams(trashViewParameters);

			dragViewParameters.alpha = alpha;
			dragViewParameters.width = getWidth() - trashViewParameters.width + 2;
		}

		dragViewParameters.y = y - dragView.getHeight() / 2;

		WindowManager windowManager = getWindowManager();
		windowManager.updateViewLayout(dragView, dragViewParameters);
	}

	private void stopDragging() {
		if (dragView != null) {
			dragView.setVisibility(GONE);
			WindowManager windowManager = getWindowManager();
			windowManager.removeView(dragView);
			dragView.setImageDrawable(null);
			dragView = null;
		}
	}

	private WindowManager.LayoutParams createLayoutParameters() {

		WindowManager.LayoutParams windowParameters = new WindowManager.LayoutParams();
		windowParameters.gravity = Gravity.TOP | Gravity.LEFT;

		windowParameters.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParameters.width = getWidth() - originalTrashWidth + 2;
		windowParameters.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParameters.format = PixelFormat.TRANSLUCENT;

		return windowParameters;
	}

	private WindowManager getWindowManager() {
		return (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	}
}