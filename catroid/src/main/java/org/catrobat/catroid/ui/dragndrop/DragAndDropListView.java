/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.ui.dragndrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class DragAndDropListView extends ListView implements CheckBoxListAdapter.ListItemLongClickHandler, View.OnTouchListener {

	public static final String TAG = DragAndDropListView.class.getSimpleName();
	private static final int SMOOTH_SCROLL_BY = 15;
	private int upperScrollBound;
	private int lowerScrollBound;

	private DragAndDropAdapterInterface adapterInterface;

	private View view;
	private BitmapDrawable hoveringListItem;
	private int position;

	private Rect viewBounds;
	private float downY = 0;
	private int offsetToCenter = 0;

	private static int longpressTime = ViewConfiguration.getLongPressTimeout();
	Timer longpressTimer;

	public DragAndDropListView(Context context) {
		super(context);
	}

	public DragAndDropListView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public DragAndDropListView(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);
	}

	public void setAdapterInterface(DragAndDropAdapterInterface adapterInterface) {
		this.adapterInterface = adapterInterface;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (longpressTimer != null) {
			longpressTimer.cancel();
		}
		if (hoveringListItem == null) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				stopDragging();
				break;
			case MotionEvent.ACTION_DOWN:
				downY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float dY = event.getY() - downY;
				downY += dY;
				downY -= offsetToCenter;

				viewBounds.offsetTo(viewBounds.left, (int) downY);
				hoveringListItem.setBounds(viewBounds);

				invalidate();
				swapListItems();
				scrollWhileDragging();
				break;
		}
		return true;
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		upperScrollBound = getHeight() / 6;
		lowerScrollBound = getHeight() / 6 * 4;

		this.view = view;
		this.position = position;
		view.setVisibility(INVISIBLE);
		hoveringListItem = getHoveringListItem(view);
		setOffsetToCenter(viewBounds);
		invalidate();
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (hoveringListItem != null) {
			hoveringListItem.draw(canvas);
		}
	}

	@Override
	public boolean onTouch(final View view, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				longpressTimer = new Timer();
				TimerTask longPressTask = new TimerTask() {
					@Override
					public void run() {
						((Activity) getContext()).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								view.performLongClick();
							}
						});
					}
				};
				longpressTimer.schedule(longPressTask, longpressTime);
				return true;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				longpressTimer.cancel();
				view.performClick();
				break;
			default:
				break;
		}
		return false;
	}

	private BitmapDrawable getHoveringListItem(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

		viewBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		drawable.setBounds(viewBounds);

		return drawable;
	}

	private void setOffsetToCenter(Rect viewBounds) {
		offsetToCenter = (viewBounds.height() / 2);
	}

	private void swapListItems() {
		int itemPositionAbove = position - 1;
		int itemPositionBelow = position + 1;

		View itemBelow = null;
		View itemAbove = null;

		if (isPositionValid(itemPositionAbove)) {
			itemAbove = getChildAt(getVisiblePosition(itemPositionAbove));
		}

		if (isPositionValid(itemPositionBelow)) {
			itemBelow = getChildAt(getVisiblePosition(itemPositionBelow));
		}

		boolean isAbove = (itemBelow != null) && (downY > itemBelow.getY());
		boolean isBelow = (itemAbove != null) && (downY < itemAbove.getY());

		if (isAbove || isBelow) {
			int swapWith = isAbove ? itemPositionBelow : itemPositionAbove;
			position = adapterInterface.swapItems(position, swapWith);

			view.setVisibility(VISIBLE);
			view = getChildAt(getVisiblePosition(position));
			view.setVisibility(INVISIBLE);

			invalidateViews();
		}
	}

	private void scrollWhileDragging() {
		if (downY > lowerScrollBound) {
			smoothScrollBy(SMOOTH_SCROLL_BY, 0);
		} else if (downY < upperScrollBound) {
			smoothScrollBy(-SMOOTH_SCROLL_BY, 0);
		}
	}

	private int getVisiblePosition(int positionInAdapter) {
		return positionInAdapter - getFirstVisiblePosition();
	}

	private boolean isPositionValid(int position) {
		return (position >= 0 && position < getCount());
	}

	private void stopDragging() {
		view.setVisibility(VISIBLE);
		view = null;
		hoveringListItem = null;
		invalidate();
	}

	public static void setDragAndDropDelay(boolean enabled) {
		longpressTime = ViewConfiguration.getLongPressTimeout();
		if (enabled) {
			longpressTime *= 2;
		}
	}
}
