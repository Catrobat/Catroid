/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Author: Romain Guy
 * <p/>
 * Using example: <?xml version="4.0" encoding="utf-8"?> <com.example.android.layout.FlowLayout
 * xmlns:f="http://schemas.android.com/apk/res/org.apmem.android"
 * xmlns:android="http://schemas.android.com/apk/res/android" f:horizontalSpacing="6dip" f:verticalSpacing="12dip"
 * android:layout_width="wrap_content" android:layout_height="wrap_content" android:paddingLeft="6dip"
 * android:paddingTop="6dip" android:paddingRight="12dip"> <Button android:layout_width="wrap_content"
 * android:layout_height="wrap_content" f:layout_horizontalSpacing="32dip" f:layout_breakLine="true"
 * android:text="Cancel" />
 * <p/>
 * </com.example.android.layout.FlowLayout>
 */

package org.catrobat.catroid.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;

import java.util.LinkedList;

public class DragNDropBrickLayout extends BrickLayout {

	private boolean dragging;

	private static final float BIAS_SAME_LINE_FUDGE_FACTOR = 10;
	private static final int MIN_MILLISECONDS_FOR_TAP = 300;

	private int lastInsertableSpaceIndex;
	private boolean justStartedDragging;
	private boolean secondDragFrame;
	private int draggedItemIndex;
	private int dragPointOffsetX;
	private int dragPointOffsetY;

	private int viewToWindowSpaceX;
	private int viewToWindowSpaceY;

	private long dragBeganMillis;
	private long dragEndMillis;

	private View draggedItemInLayout;

	private WeirdFloatingWindowData dragView;
	private WeirdFloatingWindowData dragCursor1;
	private WeirdFloatingWindowData dragCursor2;
	private LineBreakListener lineBreakListener;
	private LinkedList<Integer> breaks;

	public DragAndDropBrickLayoutListener parent;

	public DragNDropBrickLayout(Context context) {
		super(context);
	}

	public DragNDropBrickLayout(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public DragNDropBrickLayout(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
	}

	public void setListener(DragAndDropBrickLayoutListener parent) {
		this.parent = parent;
	}

	public void registerLineBreakListener(LineBreakListener listener) {
		lineBreakListener = listener;
	}

	@Override
	protected void allocateLineData() {
		breaks = new LinkedList<Integer>();
		super.allocateLineData();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingRight() - this.getPaddingLeft();
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		int lineThicknessWithorizontalSpacing = 0;
		int lineThickness = 0;
		int lineLengthWithHorizontalSpacing = 0;
		int lineLength = 0;

		int prevLinePosition = 0;

		int controlMaxLength = 0;
		int controlMaxThickness = 0;

		for (LineData lineData : lines) {
			lineData.allowableTextFieldWidth = 0;
			lineData.height = 0;
			lineData.minHeight = 0;
			lineData.numberOfTextFields = 0;
			lineData.totalTextFieldWidth = 0;
			for (ElementData elementData : lineData.elements) {
				elementData.height = 0;
				elementData.width = 0;
				elementData.posY = 0;
				elementData.posX = 0;
				elementData.view = null;
			}
		}

		LineData currentLine = lines.getFirst();

		lineThicknessWithorizontalSpacing = 0;
		lineThickness = 0;
		lineLengthWithHorizontalSpacing = 0;
		lineLength = 0;

		prevLinePosition = 0;

		controlMaxLength = 0;
		controlMaxThickness = 0;
		currentLine = lines.getFirst();

		int elementInLineIndex = 0;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}
			boolean forceNewLine = false;
			LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
			int horizontalSpacing = getHorizontalSpacing(layoutParams);
			int verticalSpacing = getVerticalSpacing(layoutParams);

			if (child instanceof Spinner) {
				child.measure(MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
						sizeHeight, modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeHeight));
			} else if (layoutParams.getNewLine()) {
				int width = sizeWidth - (lineLengthWithHorizontalSpacing + horizontalSpacing);
				if (width <= horizontalSpacing * 2) {
					forceNewLine = true;
					width = sizeWidth - (horizontalSpacing * 4);
				}
				child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
						sizeHeight, modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeHeight));
			} else {
				child.measure(MeasureSpec.makeMeasureSpec(sizeWidth,
						modeWidth == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeWidth), MeasureSpec
						.makeMeasureSpec(sizeHeight, modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
								: modeHeight));
			}

			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			boolean updateSmallestHeight = currentLine.minHeight == 0 || currentLine.minHeight > childHeight;
			currentLine.minHeight = (updateSmallestHeight ? childHeight : currentLine.minHeight);

			lineLength = lineLengthWithHorizontalSpacing + childWidth;
			lineLengthWithHorizontalSpacing = lineLength + horizontalSpacing;

			boolean previousWasNewLine = false;
			if (i > 0) {
				LayoutParams previousLayoutParams = (LayoutParams) getChildAt(i - 1).getLayoutParams();
				previousWasNewLine = previousLayoutParams.getNewLine();
			}

			if (lineLength > sizeWidth || previousWasNewLine || forceNewLine) {
				prevLinePosition = prevLinePosition + lineThicknessWithorizontalSpacing;

				currentLine = getNextLine(currentLine);
				elementInLineIndex = 0;

				lineThickness = childHeight;
				lineLength = childWidth;
				lineThicknessWithorizontalSpacing = childHeight + verticalSpacing;
				lineLengthWithHorizontalSpacing = lineLength + horizontalSpacing;
			}

			lineThicknessWithorizontalSpacing = Math.max(lineThicknessWithorizontalSpacing, childHeight
					+ verticalSpacing);
			lineThickness = Math.max(lineThickness, childHeight);

			currentLine.height = lineThickness;

			int posX = getPaddingLeft() + lineLength - childWidth;
			int posY = getPaddingTop() + prevLinePosition;

			ElementData element = getElement(currentLine, elementInLineIndex);
			element.view = child;
			element.posX = posX;
			element.posY = posY;
			element.width = childWidth;
			element.height = childHeight;
			elementInLineIndex++;

			controlMaxLength = Math.max(controlMaxLength, lineLength);
			controlMaxThickness = prevLinePosition + lineThickness;
		}

		int x = controlMaxLength;
		int y = controlMaxThickness;

		y += getPaddingTop() + getPaddingBottom();

		int centerVertically = 0;
		if (y < getSuggestedMinimumHeight()) {
			centerVertically = (getSuggestedMinimumHeight() - y) / 2;
		}

		y = Math.max(y, getSuggestedMinimumHeight());

		int i = 0;
		breaks.clear();
		for (LineData lineData : lines) {
			boolean firstInLine = true;
			for (ElementData elementData : lineData.elements) {
				if (elementData.view != null) {
					if (firstInLine && i != 0) {
						breaks.add(i);
					}
					firstInLine = false;

					int centerVerticallyWithinLine = 0;
					if (elementData.height < lineData.height) {
						centerVerticallyWithinLine = Math.round((lineData.height - elementData.height) * 0.5f);
					}

					elementData.posY += centerVertically + centerVerticallyWithinLine;
					LayoutParams layoutParams = (LayoutParams) elementData.view.getLayoutParams();
					layoutParams.setPosition(elementData.posX, elementData.posY);

					i++;
				}
			}
		}

		if (lineBreakListener != null) {
			lineBreakListener.setBreaks(breaks);
		}

		this.setMeasuredDimension(resolveSize(x, widthMeasureSpec), resolveSize(y, heightMeasureSpec));
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();
		viewToWindowSpaceX = (int) ev.getRawX() - x;
		viewToWindowSpaceY = (int) ev.getRawY() - y;

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				int itemPosition = click(x, y);
				if (itemPosition != -1) {
					beginDrag(x, y, itemPosition);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (dragging) {
					drag(x, y);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			default:
				if (dragging) {
					drop();
				}
				break;
		}
		return true;
	}

	private int click(int x, int y) {
		int itemPosition = 0;

		for (BrickLayout.LineData line : lines) {
			for (BrickLayout.ElementData e : line.elements) {
				if (e.view != null) {
					if (x > e.posX && y > e.posY && x < e.posX + e.width && y < e.posY + e.height) {

						dragPointOffsetX = (e.posX - x);
						dragPointOffsetY = (e.posY - y);
						return itemPosition;
					}
					itemPosition++;
				}
			}
		}
		return -1;
	}

	private void beginDrag(int x, int y, int itemIndex) {
		dragBeganMillis = System.currentTimeMillis();

		// frequent dragdrops can cause a null reference when the event for the new drag happens before the drop finishes.
		if (dragging || dragBeganMillis - dragEndMillis < 200) {
			return;
		}

		justStartedDragging = true;
		draggedItemIndex = itemIndex;

		stopDrag();

		draggedItemInLayout = getChildAt(itemIndex);
		if (draggedItemInLayout == null) {
			return;
		}
		draggedItemInLayout.setDrawingCacheEnabled(true);

		// Create a copy of the drawing cache so that it does not get recycled
		// by the framework when the list tries to clean up memory
		Bitmap bitmap = Bitmap.createBitmap(draggedItemInLayout.getDrawingCache());

		dragView = makeWeirdFloatingWindow(bitmap, draggedItemInLayout.getWidth(), draggedItemInLayout.getHeight());

		dragCursor1 = makeWeirdFloatingWindow(View.inflate(getContext(), R.layout.brick_user_data_insert, null));
		dragCursor2 = makeWeirdFloatingWindow(View.inflate(getContext(), R.layout.brick_user_data_insert, null));

		dragging = true;

		drag(x, y);
	}

	// move the drag view
	private void drag(int x, int y) {
		int centerOfDraggedElementX = x + dragPointOffsetX;
		int centerOfDraggedElementY = y + dragPointOffsetY;

		positionWierdFloatingWindow(dragView, centerOfDraggedElementX, centerOfDraggedElementY);

		int insertableSpaceIndex = findClosestInsertableSpace(centerOfDraggedElementX, centerOfDraggedElementY);

		if (secondDragFrame) {
			draggedItemInLayout.setVisibility(View.INVISIBLE);
			secondDragFrame = false;
		}

		if (justStartedDragging || lastInsertableSpaceIndex != insertableSpaceIndex) {

			repositionCursors(insertableSpaceIndex);

			lastInsertableSpaceIndex = insertableSpaceIndex;
			justStartedDragging = false;
			secondDragFrame = true;
		}
	}

	private void drop() {
		dragEndMillis = System.currentTimeMillis();

		long difference = dragEndMillis - dragBeganMillis;

		if (difference < MIN_MILLISECONDS_FOR_TAP
				&& (draggedItemIndex == lastInsertableSpaceIndex || draggedItemIndex == lastInsertableSpaceIndex + 1)) {

			parent.click(draggedItemIndex);
		} else {
			parent.reorder(draggedItemIndex, lastInsertableSpaceIndex);
		}

		stopDrag();
	}

	private void stopDrag() {
		removeWeirdFloatingWindow(dragView);
		removeWeirdFloatingWindow(dragCursor1);
		removeWeirdFloatingWindow(dragCursor2);

		dragView = null;
		dragCursor1 = null;
		dragCursor2 = null;

		View item = getChildAt(draggedItemIndex);
		if (item == null) {
			return;
		}
		item.setVisibility(VISIBLE);

		dragging = false;
	}

	private int countElements() {
		int previousElementIndex = 0;
		for (BrickLayout.LineData line : lines) {
			for (BrickLayout.ElementData element : line.elements) {
				if (element.view != null) {
					previousElementIndex++;
				}
			}
		}
		return previousElementIndex;
	}

	/**
	 * Finds the space closest to x,y where an element can be inserted
	 *
	 * @returns index of the element before the space or -1 for the beginning of the array
	 */
	private int findClosestInsertableSpace(int x, int y) {
		int previousElementIndex = -1;
		int closestPreviousElementIndex = -1;
		float closestDistance = 99999999;

		for (BrickLayout.LineData line : lines) {
			int elementIndex = 0;
			for (BrickLayout.ElementData e : line.elements) {
				if (e.view != null) {
					float edgeX = e.posX;
					float edgeY = e.posY;
					if (e.view.getVisibility() != GONE) {
						edgeX -= (e.width * 0.5f);
					}

					float dx = edgeX - x;
					float dy = edgeY - y;
					float d = dx * dx + dy * dy * BIAS_SAME_LINE_FUDGE_FACTOR;

					if (d < closestDistance) {
						closestDistance = d;
						closestPreviousElementIndex = previousElementIndex;
					}
					previousElementIndex++;

					edgeX = e.posX;
					if (elementIndex == line.elements.size() - 1 || line.elements.get(elementIndex + 1).view == null) {
						edgeX = (edgeX + (e.width * 0.5f) + getMeasuredWidth()) * 0.5f;
					} else if (e.view.getVisibility() != GONE) {
						edgeX += (e.width * 0.5f);
					}
					dx = edgeX - x;
					d = dx * dx + dy * dy * BIAS_SAME_LINE_FUDGE_FACTOR;

					if (d < closestDistance) {
						closestDistance = d;
						closestPreviousElementIndex = previousElementIndex;
					}
					elementIndex++;
				}
			}
		}
		return closestPreviousElementIndex;
	}

	private void repositionCursors(int insertableSpaceIndex) {
		if (dragCursor1 != null && dragCursor1.view != null && insertableSpaceIndex >= 0) {
			BrickLayout.ElementData previousElement = getElement(insertableSpaceIndex);

			int rightEdgeOfPreviousElementX = previousElement.posX + previousElement.width;
			int rightEdgeOfPreviousElementY = previousElement.posY + (int) (previousElement.height * 0.5f);

			positionWierdFloatingWindow(dragCursor1, rightEdgeOfPreviousElementX, rightEdgeOfPreviousElementY);
			dragCursor1.view.setVisibility(VISIBLE);
		} else {
			dragCursor1.view.setVisibility(GONE);
		}

		if (dragCursor2 != null && dragCursor2.view != null && insertableSpaceIndex < countElements() - 1) {
			BrickLayout.ElementData nextElement = getElement(insertableSpaceIndex + 1);

			int leftEdgeOfNextElementX = nextElement.posX;
			int leftEdgeOfNextElementY = nextElement.posY + (int) (nextElement.height * 0.5f);

			positionWierdFloatingWindow(dragCursor2, leftEdgeOfNextElementX, leftEdgeOfNextElementY);
			dragCursor2.view.setVisibility(VISIBLE);
		} else {
			dragCursor2.view.setVisibility(GONE);
		}
	}

	private BrickLayout.ElementData getElement(int i) {
		int index = 0;

		for (BrickLayout.LineData line : lines) {
			for (BrickLayout.ElementData e : line.elements) {
				if (e.view != null) {
					if (index == i) {
						return e;
					}
					index++;
				}
			}
		}
		return null;
	}

	private WeirdFloatingWindowData makeWeirdFloatingWindow(Bitmap bitmap, int width, int height) {
		Context context = getContext();
		ImageView v = new ImageView(context);
		v.setImageBitmap(bitmap);

		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(v, getFloatingWindowParams());

		return new WeirdFloatingWindowData(v, width, height);
	}

	private WeirdFloatingWindowData makeWeirdFloatingWindow(View view) {
		Context context = getContext();
		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(view, getFloatingWindowParams());

		return new WeirdFloatingWindowData(view, view.getWidth(), view.getHeight());
	}

	private void positionWierdFloatingWindow(WeirdFloatingWindowData window, int x, int y) {
		if (window != null && window.view != null) {
			WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) window.view.getLayoutParams();
			int uncenteringX = (int) (ScreenValues.SCREEN_WIDTH * -0.5f) + (int) (window.width * 0.5f);
			int uncenteringY = (int) (ScreenValues.SCREEN_HEIGHT * -0.5f) + (int) (window.height * 0.5f);
			layoutParams.x = x + viewToWindowSpaceX + uncenteringX;
			layoutParams.y = y + viewToWindowSpaceY + uncenteringY;

			WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.updateViewLayout(window.view, layoutParams);
		}
	}

	private void removeWeirdFloatingWindow(WeirdFloatingWindowData window) {
		if (window != null && window.view != null) {
			window.view.setVisibility(GONE);
			WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(window.view);
		}
	}

	private WindowManager.LayoutParams getFloatingWindowParams() {
		WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.CENTER;
		windowParams.x = 0;
		windowParams.y = 0;

		windowParams.height = LayoutParams.WRAP_CONTENT;
		windowParams.width = LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;
		return windowParams;
	}

	private class WeirdFloatingWindowData {
		public View view;
		public int width;
		public int height;

		public WeirdFloatingWindowData(View view, int width, int height) {
			this.view = view;
			this.width = width;
			this.height = height;
		}
	}
}
