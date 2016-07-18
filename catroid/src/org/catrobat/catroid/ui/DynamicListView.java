/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.catrobat.catroid.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

public class DynamicListView extends ListView {

	private static final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 15;
	private static final int MOVE_DURATION = 100;
	private static final int LINE_THICKNESS = 15;
	private static final int INVALID_POINTER_ID = -1;

	public List dataList;
	private boolean forSpriteList = false;
	private boolean dismissLongPress = false;
	private int lastEventY = -1;
	private int downY = -1;
	private int totalOffset = 0;
	private boolean cellIsMobile = false;
	private boolean isMobileScrolling = false;
	private int smoothScrollAmountAtEdge = 0;
	private static final int INVALID_ID = -1;
	private long aboveItemId = INVALID_ID;
	private long mobileItemId = INVALID_ID;
	private long belowItemId = INVALID_ID;
	private BitmapDrawable hoverCell;
	private Rect hoverCellCurrentBounds;
	private Rect hoverCellOriginalBounds;
	private int activePointerId = INVALID_POINTER_ID;
	private boolean isWaitingForScrollFinish = false;
	private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;

	public DynamicListView(Context context) {
		super(context);
		init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		setOnItemLongClickListener(onItemLongClickListener);
		setOnScrollListener(scrollListener);
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		smoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
	}

	private AdapterView.OnItemLongClickListener onItemLongClickListener =
			new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
					if ((pos == 0 && forSpriteList)) {
						return true;
					}
					dismissLongPress = true;
					totalOffset = 0;
					int itemNum = pos - getFirstVisiblePosition();

					View selectedView = getChildAt(itemNum);
					mobileItemId = getAdapter().getItemId(pos);
					hoverCell = getAndAddHoverView(selectedView);
					selectedView.setVisibility(INVISIBLE);
					cellIsMobile = true;
					updateNeighborViewsForID(mobileItemId);

					return true;
				}
			};

	private BitmapDrawable getAndAddHoverView(View v) {
		int w = v.getWidth();
		int h = v.getHeight();
		int top = v.getTop();
		int left = v.getLeft();

		Bitmap b = getBitmapWithBorder(v);
		BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

		hoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
		hoverCellCurrentBounds = new Rect(hoverCellOriginalBounds);
		drawable.setBounds(hoverCellCurrentBounds);

		return drawable;
	}

	private Bitmap getBitmapWithBorder(View v) {
		Bitmap bitmap = getBitmapFromView(v);
		Canvas can = new Canvas(bitmap);
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(LINE_THICKNESS);
		paint.setColor(Color.rgb(5, 34, 44));

		can.drawBitmap(bitmap, 0, 0, null);
		can.drawRect(rect, paint);

		return bitmap;
	}

	private Bitmap getBitmapFromView(View v) {
		Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		v.draw(canvas);
		return bitmap;
	}

	private void updateNeighborViewsForID(long itemID) {
		int position = getPositionForID(itemID);
		ArrayAdapter adapter = ((ArrayAdapter) getAdapter());
		aboveItemId = adapter.getItemId(position - 1);
		belowItemId = adapter.getItemId(position + 1);
	}

	public View getViewForID(long itemID) {
		int firstVisiblePosition = getFirstVisiblePosition();
		ArrayAdapter adapter = (ArrayAdapter) getAdapter();
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			int position = firstVisiblePosition + i;
			long id = adapter.getItemId(position);
			if (id == itemID) {
				return v;
			}
		}
		return null;
	}

	public int getPositionForID(long itemID) {
		View v = getViewForID(itemID);
		if (v == null) {
			return -1;
		} else {
			return getPositionForView(v);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (hoverCell != null) {
			hoverCell.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismissLongPress = false;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				downY = (int) event.getY();
				activePointerId = event.getPointerId(0);
				break;
			case MotionEvent.ACTION_MOVE:
				if (activePointerId == INVALID_POINTER_ID) {
					downY = (int) event.getY();
					activePointerId = event.getPointerId(0);
				}

				int pointerIndex = event.findPointerIndex(activePointerId);

				lastEventY = (int) event.getY(pointerIndex);
				int deltaY = lastEventY - downY;

				if (cellIsMobile) {
					hoverCellCurrentBounds.offsetTo(hoverCellOriginalBounds.left,
							hoverCellOriginalBounds.top + deltaY + totalOffset);
					hoverCell.setBounds(hoverCellCurrentBounds);
					invalidate();

					handleCellSwitch();

					isMobileScrolling = false;
					handleMobileCellScroll();

					return false;
				}
				break;
			case MotionEvent.ACTION_UP:
				touchEventsEnded();
				break;
			case MotionEvent.ACTION_CANCEL:
				touchEventsCancelled();
				break;
			case MotionEvent.ACTION_POINTER_UP:
				pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = event.getPointerId(pointerIndex);
				if (pointerId == activePointerId) {
					touchEventsEnded();
				}
				break;
			default:
				break;
		}

		return super.onTouchEvent(event);
	}

	private void handleCellSwitch() {
		final int deltaY = lastEventY - downY;
		int deltaYTotal = hoverCellOriginalBounds.top + totalOffset + deltaY;

		View belowView = getViewForID(belowItemId);
		View mobileView = getViewForID(mobileItemId);
		View aboveView = getViewForID(aboveItemId);

		boolean isBelow = (belowView != null) && (deltaYTotal > belowView.getTop());
		boolean isAbove = (aboveView != null) && (deltaYTotal < aboveView.getTop());

		if (aboveView != null && (getPositionForView(aboveView) == 0 && isAbove && forSpriteList)) {
			return;
		}

		if (isBelow || isAbove) {
			final long switchItemID = isBelow ? belowItemId : aboveItemId;
			View switchView = isBelow ? belowView : aboveView;
			final int originalItem = getPositionForView(mobileView);

			if (switchView == null) {
				updateNeighborViewsForID(mobileItemId);
				return;
			}

			swapElements(dataList, originalItem, getPositionForView(switchView));

			((BaseAdapter) getAdapter()).notifyDataSetChanged();

			downY = lastEventY;

			final int switchViewStartTop = switchView.getTop();

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				mobileView.setVisibility(View.VISIBLE);
				switchView.setVisibility(View.INVISIBLE);
			}

			updateNeighborViewsForID(mobileItemId);

			final ViewTreeObserver observer = getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					observer.removeOnPreDrawListener(this);

					View switchView = getViewForID(switchItemID);

					totalOffset += deltaY;

					int switchViewNewTop = switchView.getTop();
					int delta = switchViewStartTop - switchViewNewTop;

					switchView.setTranslationY(delta);

					ObjectAnimator animator = ObjectAnimator.ofFloat(switchView,
							View.TRANSLATION_Y, 0);
					animator.setDuration(MOVE_DURATION);
					animator.start();

					return true;
				}
			});
		}
	}

	private void swapElements(List arrayList, int indexOne, int indexTwo) {
		Object temp = arrayList.get(indexOne);
		arrayList.set(indexOne, arrayList.get(indexTwo));
		arrayList.set(indexTwo, temp);
	}

	private void touchEventsEnded() {
		final View mobileView = getViewForID(mobileItemId);
		if (cellIsMobile || isWaitingForScrollFinish) {
			cellIsMobile = false;
			isWaitingForScrollFinish = false;
			isMobileScrolling = false;
			activePointerId = INVALID_POINTER_ID;

			if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
				isWaitingForScrollFinish = true;
				return;
			}

			hoverCellCurrentBounds.offsetTo(hoverCellOriginalBounds.left, mobileView.getTop());

			ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(hoverCell, "bounds",
					boundEvaluator, hoverCellCurrentBounds);
			hoverViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					invalidate();
				}
			});
			hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					setEnabled(false);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					aboveItemId = INVALID_ID;
					mobileItemId = INVALID_ID;
					belowItemId = INVALID_ID;
					mobileView.setVisibility(VISIBLE);
					hoverCell = null;
					setEnabled(true);
					invalidate();
				}
			});
			hoverViewAnimator.start();
		} else {
			touchEventsCancelled();
		}
	}

	public void notifyListItemTouchActionUp() {
		if (dismissLongPress) {
			touchEventsCancelled();
		}
	}

	private void touchEventsCancelled() {
		View mobileView = getViewForID(mobileItemId);
		if (cellIsMobile) {
			aboveItemId = INVALID_ID;
			mobileItemId = INVALID_ID;
			belowItemId = INVALID_ID;
			mobileView.setVisibility(VISIBLE);
			hoverCell = null;
			invalidate();
		}
		cellIsMobile = false;
		isMobileScrolling = false;
		activePointerId = INVALID_POINTER_ID;
	}

	private TypeEvaluator<Rect> boundEvaluator = new TypeEvaluator<Rect>() {
		public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
			return new Rect(interpolate(startValue.left, endValue.left, fraction),
					interpolate(startValue.top, endValue.top, fraction),
					interpolate(startValue.right, endValue.right, fraction),
					interpolate(startValue.bottom, endValue.bottom, fraction));
		}

		public int interpolate(int start, int end, float fraction) {
			return (int) (start + fraction * (end - start));
		}
	};

	private void handleMobileCellScroll() {
		isMobileScrolling = handleMobileCellScroll(hoverCellCurrentBounds);
	}

	public boolean handleMobileCellScroll(Rect r) {
		int offset = computeVerticalScrollOffset();
		int height = getHeight();
		int extent = computeVerticalScrollExtent();
		int range = computeVerticalScrollRange();
		int hoverViewTop = r.top;
		int hoverHeight = r.height();
		boolean hoverCellIsOnTopOfScreen = hoverViewTop <= 0 && offset > 0;
		boolean hoverCellIsOnBottomOfScreen = hoverViewTop + hoverHeight >= height && (offset + extent) < range;

		if (hoverCellIsOnTopOfScreen) {
			smoothScrollBy(-smoothScrollAmountAtEdge, 0);
			return true;
		}

		if (hoverCellIsOnBottomOfScreen) {
			smoothScrollBy(smoothScrollAmountAtEdge, 0);
			return true;
		}

		return false;
	}

	public void setDataList(List data) {
		dataList = data;
	}

	public void isForSpriteList() {
		forSpriteList = true;
	}

	private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
		private int previousFirstVisibleItem = -1;
		private int previousVisibleItemCount = -1;
		private int currentFirstVisibleItem;
		private int currentVisibleItemCount;
		private int currentScrollState;

		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
				int totalItemCount) {
			currentFirstVisibleItem = firstVisibleItem;
			currentVisibleItemCount = visibleItemCount;

			previousFirstVisibleItem = (previousFirstVisibleItem == -1) ? currentFirstVisibleItem
					: previousFirstVisibleItem;
			previousVisibleItemCount = (previousVisibleItemCount == -1) ? currentVisibleItemCount
					: previousVisibleItemCount;

			checkAndHandleFirstVisibleCellChange();
			checkAndHandleLastVisibleCellChange();

			previousFirstVisibleItem = currentFirstVisibleItem;
			previousVisibleItemCount = currentVisibleItemCount;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			currentScrollState = scrollState;
			DynamicListView.this.scrollState = scrollState;
			isScrollCompleted();
		}

		private void isScrollCompleted() {
			if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
				if (cellIsMobile && isMobileScrolling) {
					handleMobileCellScroll();
				} else if (isWaitingForScrollFinish) {
					touchEventsEnded();
				}
			}
		}

		public void checkAndHandleFirstVisibleCellChange() {
			if (currentFirstVisibleItem != previousFirstVisibleItem && cellIsMobile && mobileItemId != INVALID_ID) {
				updateNeighborViewsForID(mobileItemId);
				handleCellSwitch();
			}
		}

		public void checkAndHandleLastVisibleCellChange() {
			int currentLastVisibleItem = currentFirstVisibleItem + currentVisibleItemCount;
			int previousLastVisibleItem = previousFirstVisibleItem + previousVisibleItemCount;
			if (currentLastVisibleItem != previousLastVisibleItem && cellIsMobile && mobileItemId != INVALID_ID) {
				updateNeighborViewsForID(mobileItemId);
				handleCellSwitch();
			}
		}
	};
}
