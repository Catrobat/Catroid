/*
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * 		This file incorporates work covered by the following copyright and  
 * 		permission notice:  
 *  
 *  	Copyright (C) 2010 Draggable and Droppable ListView Project
 * 
 */
package at.tugraz.ist.catroid.ui.dragndrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.utils.Utils;

public class DragAndDropListView extends ListView implements OnLongClickListener {

	private static final int SCROLL_SPEED = 25;
	private static final int DRAG_BACKGROUND_COLOR = Color.TRANSPARENT;

	private int maximumDragViewHeight;

	private int previousItemPosition;
	private int touchPointY;

	private int upperScrollBound;
	private int lowerScrollBound;
	private int upperDragBound;
	private int lowerDragBound;

	private ImageView dragView;
	private int position;
	private boolean newView;

	private ImageView trashView;
	private int originalTrashWidth;
	private int originalTrashHeight;
	private int touchedListPosition;

	private boolean dimBackground;
	private boolean isVibrating;

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

	public int getTouchedListPosition() {
		return touchedListPosition;
	}

	public void setInsertedBrick(int pos) {

		this.position = pos;
		newView = true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		if (dragAndDropListener != null && dragView != null) {
			onTouchEvent(event);
		}

		return super.onInterceptTouchEvent(event);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (dimBackground) {
			Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.FILL);
			paint.setAlpha(128);

			canvas.drawRect(rect, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();

		if (y < 0) {
			y = 0;
		} else if (y > getHeight()) {
			y = getHeight();
		}

		int itemPosition = pointToPosition(x, y);

		if (touchedListPosition != itemPosition) {
			touchedListPosition = itemPosition;
			if (dragAndDropListener != null) {
				dragAndDropListener.setTouchedScript(touchedListPosition);
			}
		}

		if (dragAndDropListener != null && dragView != null) {
			int action = event.getAction();
			switch (action) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:

					stopDragging();
					hideTrashView();

					if (x > getWidth() * 3 / 4) {
						dragAndDropListener.remove(itemPosition);
						isVibrating = false;
					} else {
						dragAndDropListener.drop(itemPosition);
					}

					dimBackground = false;
					break;

				case MotionEvent.ACTION_MOVE:

					scrollListWithDraggedItem(y);

					dragTouchedListItem(x, (int) event.getRawY());

					if (itemPosition != INVALID_POSITION) {
						dragItemInList(y, itemPosition);
					}

					dimBackground = true;
					break;
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {

		super.onSizeChanged(width, height, oldWidth, oldHeight);
		upperScrollBound = height / 6;
		lowerScrollBound = height * 5 / 6;
		maximumDragViewHeight = height / 3;
	}

	public boolean onLongClick(View view) {
		int itemPosition = calculateItemPositionAndTouchPointY(view);

		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();

		view.setDrawingCacheEnabled(true);

		view.measure(MeasureSpec.makeMeasureSpec(Values.SCREEN_WIDTH, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(Utils.getPhysicalPixels(400, getContext()), MeasureSpec.AT_MOST));
		view.layout(0, 0, Values.SCREEN_WIDTH, view.getMeasuredHeight());

		view.buildDrawingCache(true);

		if (view.getDrawingCache() == null) {
			return false;
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(drawingCacheEnabled);

		startDragging(bitmap, touchPointY);

		dragAndDropListener.drag(itemPosition, itemPosition);
		dimBackground = true;

		showTrashView();

		previousItemPosition = itemPosition;

		return true;
	}

	private void startDragging(Bitmap bitmap, int y) {
		stopDragging();

		if (bitmap.getHeight() > maximumDragViewHeight) {
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), maximumDragViewHeight);
		}

		ImageView imageView = new ImageView(getContext());
		imageView.setBackgroundColor(DRAG_BACKGROUND_COLOR);

		Bitmap glowingBitmap = Bitmap.createBitmap(bitmap.getWidth() + 30, bitmap.getHeight() + 30,
				Bitmap.Config.ARGB_8888);
		Canvas glowingCanvas = new Canvas(glowingBitmap);
		Bitmap alpha = bitmap.extractAlpha();
		Paint paintBlur = new Paint();
		paintBlur.setColor(Color.WHITE);
		glowingCanvas.drawBitmap(alpha, 15, 15, paintBlur);
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(15.0f, BlurMaskFilter.Blur.OUTER);
		paintBlur.setMaskFilter(blurMaskFilter);
		glowingCanvas.drawBitmap(alpha, 15, 15, paintBlur);
		paintBlur.setMaskFilter(null);
		glowingCanvas.drawBitmap(bitmap, 15, 15, paintBlur);

		imageView.setImageBitmap(glowingBitmap);

		WindowManager.LayoutParams dragViewParameters = createLayoutParameters();
		dragViewParameters.y = y - bitmap.getHeight() / 2;

		WindowManager windowManager = getWindowManager();
		windowManager.addView(imageView, dragViewParameters);
		dragView = imageView;
	}

	private void dragTouchedListItem(int x, int y) {

		ViewGroup.LayoutParams trashViewParameters = trashView.getLayoutParams();
		WindowManager.LayoutParams dragViewParameters = (WindowManager.LayoutParams) dragView.getLayoutParams();

		if (x > 100 && x < getWidth() - 100) {
			float alpha = ((float) (getWidth() - x)) / (getWidth() - 100);
			float rate = 1 - alpha;
			trashViewParameters.width = (int) (originalTrashWidth * (1 + rate));
			trashViewParameters.height = (int) (originalTrashHeight * (1 + rate));
			trashView.setLayoutParams(trashViewParameters);
		} else if (x >= getWidth() - 100) {
			float alpha = 100.0f / (getWidth() - 100);
			float rate = 1 - alpha;
			trashViewParameters.width = (int) (originalTrashWidth * (1 + rate));
			trashViewParameters.height = (int) (originalTrashHeight * (1 + rate));
			trashView.setLayoutParams(trashViewParameters);
		}

		if (x > getWidth() * 3 / 4 && !isVibrating) {
			isVibrating = true;
			Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 0, 100, 200 };
			if (v != null) {
				v.vibrate(pattern, 0);
			}
		} else if (x <= getWidth() * 3 / 4) {
			isVibrating = false;
			Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
			if (v != null) {
				v.cancel();
			}
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

		Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		if (v != null) {
			v.cancel();
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

	private int calculateItemPositionAndTouchPointY(View view) {
		int itemPosition = -1;
		int[] location = new int[2];
		if (newView) {
			itemPosition = this.position;
			(getChildAt(getChildCount() - 1)).getLocationOnScreen(location);
			touchPointY = location[1] + (getChildAt(getChildCount() - 1)).getHeight();
			newView = false;
		} else {
			itemPosition = pointToPosition(view.getLeft(), view.getTop());
			int visiblePosition = itemPosition - getFirstVisiblePosition();
			(getChildAt(visiblePosition)).getLocationOnScreen(location);
			touchPointY = location[1] + (getChildAt(visiblePosition)).getHeight() / 2;
		}

		return itemPosition;
	}

	private void showTrashView() {
		trashView.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.trash_in);
		trashView.startAnimation(animation);
	}

	private void hideTrashView() {
		ViewGroup.LayoutParams layoutParamemters = trashView.getLayoutParams();
		layoutParamemters.width = originalTrashWidth;
		layoutParamemters.height = originalTrashHeight;
		trashView.setLayoutParams(layoutParamemters);
		trashView.setVisibility(View.GONE);
	}

	private void scrollListWithDraggedItem(int y) {
		if (y > lowerScrollBound) {
			smoothScrollBy(SCROLL_SPEED, 0);
		} else if (y < upperScrollBound) {
			smoothScrollBy(-SCROLL_SPEED, 0);
		}
	}

	private void dragItemInList(int y, int itemPosition) {
		int index = previousItemPosition - getFirstVisiblePosition();

		if (index > 0) {
			View upperChild = getChildAt(index - 1);
			upperDragBound = upperChild.getBottom() - upperChild.getHeight() / 2;
		} else {
			upperDragBound = 0;
		}

		if (index < getChildCount() - 1) {
			View lowerChild = getChildAt(index + 1);
			lowerDragBound = lowerChild.getTop() + lowerChild.getHeight() / 2;
		} else {
			lowerDragBound = getHeight();
		}

		if ((y > lowerDragBound || y < upperDragBound)) {
			dragAndDropListener.drag(previousItemPosition, itemPosition);
			previousItemPosition = itemPosition;

		}
	}

	public void setHoveringBrick() {
		if (dragAndDropListener != null && dragView != null) {
			dragAndDropListener.setTouchedScript(position);
			stopDragging();
			dragAndDropListener.drop(position);
			trashView.setVisibility(GONE);
			dimBackground = false;
		}
	}
}
