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
package org.catrobat.catroid.ui.dynamiclistview;

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
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;

import java.util.List;

public class UtilDynamicListView {

	public static final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 15;
	public static final int MOVE_DURATION = 100;
	public static final int LINE_THICKNESS = 15;
	public static final int INVALID_POINTER_ID = -1;
	static final int INVALID_ID = -1;

	private final ListView listView;

	private SpritesListFragment spritesListFragment = null;

	List dataList;
	boolean forSpriteList = false;
	boolean dismissLongPress = false;
	int lastEventY = -1;
	int downY = -1;
	int totalOffset = 0;
	boolean cellIsMobile = false;
	boolean isMobileScrolling = false;
	int smoothScrollAmountAtEdge = 0;

	long aboveItemId = INVALID_ID;
	long mobileItemId = INVALID_ID;
	long belowItemId = INVALID_ID;
	BitmapDrawable hoverCell;
	Rect hoverCellCurrentBounds;
	Rect hoverCellOriginalBounds;
	int activePointerId = INVALID_POINTER_ID;
	boolean isWaitingForScrollFinish = false;
	int scrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

	boolean swapElementsOnlyOnDrop = false;

	public UtilDynamicListView(ListView listView) {
		this.listView = listView;
	}

	void init(Context context) {
		listView.setMotionEventSplittingEnabled(false);
		listView.setOnItemLongClickListener(onItemLongClickListener);
		listView.setOnScrollListener(scrollListener);
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		smoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
	}

	private AdapterView.OnItemLongClickListener onItemLongClickListener =
			new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
					return adapterViewOnItemLongClick(position);
				}
			};

	boolean adapterViewOnItemLongClick(int position) {

		int adapterPosition = position;
		if (forSpriteList) {
			adapterPosition = getAdapterPositionForVisibleListViewPosition(position);
			getSpriteAdapter().getSpriteList().get(adapterPosition).setIsMobile(true);
			if (adapterPosition == 0) {
				return true;
			} else if (getSpriteAdapter().isGroupPosition(adapterPosition)) {
				spritesListFragment.collapseAllGroups();
			}
		}

		handleLongPress(position, adapterPosition);
		listView.invalidateViews();
		return true;
	}

	private void handleLongPress(int position, int adapterPosition) {
		dismissLongPress = true;
		totalOffset = 0;
		int itemNum = position - listView.getFirstVisiblePosition();

		View selectedView = listView.getChildAt(itemNum);
		mobileItemId = getItemId(adapterPosition);
		hoverCell = getAndAddHoverView(selectedView);
		if (!forSpriteList) {
			selectedView.setVisibility(ListView.INVISIBLE);
		}
		cellIsMobile = true;
		updateNeighborViewsForID(mobileItemId);
	}

	private BitmapDrawable getAndAddHoverView(View v) {
		int w = v.getWidth();
		int h = v.getHeight();
		int top = v.getTop();
		int left = v.getLeft();

		Bitmap b = getBitmapWithBorder(v);
		BitmapDrawable drawable = new BitmapDrawable(listView.getResources(), b);

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
		if (position == INVALID_ID) {
			return;
		}
		if (forSpriteList) {
			aboveItemId = getItemId(getAdapterPositionForVisibleListViewPosition(position - 1));
			belowItemId = getItemId(getAdapterPositionForVisibleListViewPosition(position + 1));
		} else {
			aboveItemId = getItemId(position - 1);
			belowItemId = getItemId(position + 1);
		}
	}

	private long getItemId(int position) {
		if (listView instanceof DynamicExpandableListView) {
			SpriteAdapter spriteAdapter = ((DynamicExpandableListView) listView).getSpriteAdapter();
			return spriteAdapter.getGroupOrChildId(position);
		} else {
			Adapter adapter = listView.getAdapter();
			return adapter.getItemId(position);
		}
	}

	private void notifyDataSetChanged() {
		if (listView instanceof DynamicExpandableListView) {
			SpriteAdapter spriteAdapter = ((DynamicExpandableListView) listView).getSpriteAdapter();
			spriteAdapter.notifyDataSetChanged();
		} else {
			BaseAdapter adapter = ((BaseAdapter) listView.getAdapter());
			adapter.notifyDataSetChanged();
		}
	}

	public View getViewForID(long itemID) {
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		for (int i = 0; i < listView.getChildCount(); i++) {
			View v = listView.getChildAt(i);
			int position = firstVisiblePosition + i;
			long id;
			if (forSpriteList) {
				id = getItemId(getAdapterPositionForVisibleListViewPosition(position));
			} else {
				id = getItemId(position);
			}
			if (id == itemID) {
				return v;
			}
		}
		return null;
	}

	public int getPositionForID(long itemID) {
		View v = getViewForID(itemID);
		if (v == null) {
			return INVALID_ID;
		} else {
			return listView.getPositionForView(v);
		}
	}

	public void dispatchDraw(Canvas canvas) {
		if (hoverCell != null) {
			hoverCell.draw(canvas);
		}
	}

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
					listView.invalidate();

					if (!swapElementsOnlyOnDrop) {
						handleCellSwitch(false);
					}

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
		return true;
	}

	private void handleCellSwitch(boolean fromDrop) {
		final int deltaY = lastEventY - downY;
		int deltaYTotal = hoverCellOriginalBounds.top + totalOffset + deltaY;

		View belowView = getViewForID(belowItemId);
		View mobileView = getViewForID(mobileItemId);
		View aboveView = getViewForID(aboveItemId);

		boolean isBelow = (belowView != null) && (deltaYTotal > belowView.getTop());
		boolean isAbove = (aboveView != null) && (deltaYTotal < aboveView.getTop());

		if (aboveView != null && (listView.getPositionForView(aboveView) == 0 && isAbove && forSpriteList)) {
			return;
		}

		if (isBelow || isAbove) {
			final long switchItemID = isBelow ? belowItemId : aboveItemId;
			View switchView = isBelow ? belowView : aboveView;
			final int originalItem = listView.getPositionForView(mobileView);

			if (switchView == null) {
				updateNeighborViewsForID(mobileItemId);
				return;
			}

			boolean dataListChanged = swapElements(dataList, originalItem, listView.getPositionForView(switchView),
					fromDrop);
			if (!dataListChanged) {
				return;
			}

			notifyDataSetChanged();

			downY = lastEventY;

			final int switchViewStartTop = switchView.getTop();

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && !forSpriteList) {
				mobileView.setVisibility(View.VISIBLE);
				switchView.setVisibility(View.INVISIBLE);
			}

			updateNeighborViewsForID(mobileItemId);

			final ViewTreeObserver observer = listView.getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					observer.removeOnPreDrawListener(this);

					View switchView = getViewForID(switchItemID);
					if (switchView == null) {
						return false;
					}

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

	private boolean swapElements(List arrayList, int indexMobileView, int indexViewToSwitch, boolean fromDrop) {
		if (forSpriteList) {
			return swapForSpriteList(arrayList, indexMobileView, indexViewToSwitch, fromDrop);
		} else {
			simpleElementsSwap(arrayList, indexMobileView, indexViewToSwitch);
			return true;
		}
	}

	private boolean swapForSpriteList(List<Sprite> arrayList, int indexMobileView, int indexViewToSwitch, boolean fromDrop) {
		DynamicExpandableListView expandableListView = (DynamicExpandableListView) listView;

		int indexMobileViewIncludingCollapsedElements = getAdapterPositionForVisibleListViewPosition(indexMobileView);
		int indexViewToSwitchIncludingCollapsedElements = getAdapterPositionForVisibleListViewPosition(indexViewToSwitch);

		Sprite mobileElement = arrayList.get(indexMobileViewIncludingCollapsedElements);
		Sprite elementToSwitch = arrayList.get(indexViewToSwitchIncludingCollapsedElements);

		boolean swapDownwards = indexMobileViewIncludingCollapsedElements < indexViewToSwitchIncludingCollapsedElements;

		if (elementToSwitch instanceof GroupSprite && mobileElement instanceof GroupSprite) {
			swapGroupSprites(indexMobileViewIncludingCollapsedElements,
					indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
		} else if (elementToSwitch instanceof SingleSprite && mobileElement instanceof GroupSprite) {
			swapGroupAndSingleSprite(indexMobileViewIncludingCollapsedElements,
					indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
		} else if (elementToSwitch instanceof GroupSprite && mobileElement instanceof SingleSprite) {
			int groupPosition = getSpriteAdapter().getGroupOrChildPositionByFlatPosition(indexViewToSwitchIncludingCollapsedElements);
			if (!expandableListView.isGroupExpanded(groupPosition)) {
				if (!fromDrop) {
					return false;
				}
				mobileElement.setConvertToGroupItemSprite(true);
				swapNotExpandedGroupWithSingleSprite(groupPosition, indexMobileViewIncludingCollapsedElements,
						indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
			} else {
				if (swapDownwards) {
					mobileElement.setConvertToGroupItemSprite(true);
					swapAndCloneMobileSprite(indexMobileViewIncludingCollapsedElements,
							indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
				} else {
					simpleElementsSwap(arrayList, indexMobileViewIncludingCollapsedElements, indexViewToSwitchIncludingCollapsedElements);
				}
			}
		} else if (elementToSwitch instanceof GroupSprite && mobileElement instanceof GroupItemSprite) {

			int groupPosition = getSpriteAdapter().getGroupOrChildPositionByFlatPosition(indexViewToSwitchIncludingCollapsedElements);
			if (swapDownwards && expandableListView.isGroupExpanded(groupPosition)) {
				simpleElementsSwap(arrayList, indexMobileViewIncludingCollapsedElements, indexViewToSwitchIncludingCollapsedElements);
			} else if (swapDownwards && !expandableListView.isGroupExpanded(groupPosition)) {
				mobileElement.setConvertToSingleSprite(true);
				swapNotExpandedGroupWithSingleSprite(groupPosition, indexMobileViewIncludingCollapsedElements,
						indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
			} else {
				mobileElement.setConvertToSingleSprite(true);
				swapAndCloneMobileSprite(indexMobileViewIncludingCollapsedElements,
						indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
			}
		} else if (elementToSwitch instanceof GroupItemSprite && mobileElement instanceof SingleSprite) {
			mobileElement.setConvertToGroupItemSprite(true);
			swapAndCloneMobileSprite(indexMobileViewIncludingCollapsedElements,
					indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
		} else if (elementToSwitch instanceof SingleSprite && mobileElement instanceof SingleSprite
				|| elementToSwitch instanceof GroupItemSprite && mobileElement instanceof GroupItemSprite) {
			simpleElementsSwap(arrayList, indexMobileViewIncludingCollapsedElements, indexViewToSwitchIncludingCollapsedElements);
		} else if (elementToSwitch instanceof SingleSprite && mobileElement instanceof GroupItemSprite) {
			mobileElement.setConvertToSingleSprite(true);
			swapAndCloneMobileSprite(indexMobileViewIncludingCollapsedElements,
					indexViewToSwitchIncludingCollapsedElements, arrayList, elementToSwitch, mobileElement);
		}
		return true;
	}

	private void swapGroupSprites(int indexMobileViewIncludingCollapsedElements,
			int indexViewToSwitchIncludingCollapsedElements, List<Sprite> arrayList, Sprite elementToSwitch, Sprite mobileElement) {

		boolean swapGroupDownwards = indexMobileViewIncludingCollapsedElements < indexViewToSwitchIncludingCollapsedElements;
		List<Sprite> mobileGroupItemSprites = getSpriteAdapter().getChildrenOfGroup(arrayList.get(indexMobileViewIncludingCollapsedElements));
		List<Sprite> switchGroupItemSprites = getSpriteAdapter().getChildrenOfGroup(arrayList.get(indexViewToSwitchIncludingCollapsedElements));

		if (swapGroupDownwards) {
			arrayList.set(indexMobileViewIncludingCollapsedElements, elementToSwitch);
			for (int pos = 0; pos < switchGroupItemSprites.size(); pos++) {
				int index = pos + indexMobileViewIncludingCollapsedElements + 1;
				arrayList.set(index, switchGroupItemSprites.get(pos));
			}
			int secondGroupIndex = indexMobileViewIncludingCollapsedElements + 1 + switchGroupItemSprites.size();
			arrayList.set(secondGroupIndex, mobileElement);
			for (int pos = 0; pos < mobileGroupItemSprites.size(); pos++) {
				int index = pos + secondGroupIndex + 1;
				arrayList.set(index, mobileGroupItemSprites.get(pos));
			}
		} else {
			arrayList.set(indexViewToSwitchIncludingCollapsedElements, mobileElement);
			for (int pos = 0; pos < mobileGroupItemSprites.size(); pos++) {
				int index = pos + indexViewToSwitchIncludingCollapsedElements + 1;
				arrayList.set(index, mobileGroupItemSprites.get(pos));
			}
			int secondGroupIndex = indexViewToSwitchIncludingCollapsedElements + 1 + mobileGroupItemSprites.size();
			arrayList.set(secondGroupIndex, elementToSwitch);
			for (int pos = 0; pos < switchGroupItemSprites.size(); pos++) {
				int index = pos + secondGroupIndex + 1;
				arrayList.set(index, switchGroupItemSprites.get(pos));
			}
		}
	}

	private void swapGroupAndSingleSprite(int indexMobileViewIncludingCollapsedElements,
			int indexViewToSwitchIncludingCollapsedElements, List<Sprite> arrayList, Sprite elementToSwitch, Sprite mobileElement) {
		boolean swapGroupDownwards = indexMobileViewIncludingCollapsedElements < indexViewToSwitchIncludingCollapsedElements;
		List<Sprite> mobileGroupItemSprites = getSpriteAdapter().getChildrenOfGroup(arrayList.get(indexMobileViewIncludingCollapsedElements));

		if (swapGroupDownwards) {
			arrayList.set(indexMobileViewIncludingCollapsedElements, elementToSwitch);
			arrayList.set(indexMobileViewIncludingCollapsedElements + 1, mobileElement);

			for (int pos = 0; pos < mobileGroupItemSprites.size(); pos++) {
				int index = pos + indexMobileViewIncludingCollapsedElements + 2;
				arrayList.set(index, mobileGroupItemSprites.get(pos));
			}
		} else {
			arrayList.set(indexViewToSwitchIncludingCollapsedElements, mobileElement);

			for (int pos = 0; pos < mobileGroupItemSprites.size(); pos++) {
				int index = pos + indexViewToSwitchIncludingCollapsedElements + 1;
				arrayList.set(index, mobileGroupItemSprites.get(pos));
			}

			arrayList.set(indexMobileViewIncludingCollapsedElements + mobileGroupItemSprites.size(), elementToSwitch);
		}
	}

	private void swapAndCloneMobileSprite(int indexMobileViewIncludingCollapsedElements, int
			indexViewToSwitchIncludingCollapsedElements, List<Sprite> arrayList, Sprite elementToSwitch, Sprite mobileElement) {

		Sprite mobileElementClone = mobileElement.shallowClone();

		arrayList.set(indexMobileViewIncludingCollapsedElements, elementToSwitch);
		arrayList.set(indexViewToSwitchIncludingCollapsedElements, mobileElementClone);
		notifyDataSetChanged();

		mobileItemId = getItemId(indexViewToSwitchIncludingCollapsedElements);
	}

	private void swapNotExpandedGroupWithSingleSprite(int groupPosition, int indexMobileViewIncludingCollapsedElements,
			int indexViewToSwitchIncludingCollapsedElements, List<Sprite> arrayList, Sprite elementToSwitch, Sprite mobileElement) {
		int numberOfGroupItems = getSpriteAdapter().getChildrenCountOfGroup(groupPosition);
		boolean swapGroupDownwards = indexMobileViewIncludingCollapsedElements < indexViewToSwitchIncludingCollapsedElements;

		if (swapGroupDownwards) {
			arrayList.set(indexMobileViewIncludingCollapsedElements, elementToSwitch);

			for (int i = indexViewToSwitchIncludingCollapsedElements + 1; i < indexViewToSwitchIncludingCollapsedElements + numberOfGroupItems + 1; i++) {
				Sprite groupItemSprite = arrayList.get(i);
				arrayList.set(i - 1, groupItemSprite);
			}
			arrayList.set(indexViewToSwitchIncludingCollapsedElements + numberOfGroupItems, mobileElement.shallowClone());
		} else {

			arrayList.set(indexMobileViewIncludingCollapsedElements, mobileElement.shallowClone());
		}
	}

	private void simpleElementsSwap(List arrayList, int indexMobileView, int indexViewToSwitch) {
		Object temp = arrayList.get(indexMobileView);
		arrayList.set(indexMobileView, arrayList.get(indexViewToSwitch));
		arrayList.set(indexViewToSwitch, temp);
	}

	private void touchEventsEnded() {
		final View mobileView = getViewForID(mobileItemId);
		if (cellIsMobile || isWaitingForScrollFinish) {
			handleCellSwitch(true);
			ProjectManager.getInstance().getCurrentProject().refreshSpriteReferences();

			cellIsMobile = false;
			isWaitingForScrollFinish = false;
			isMobileScrolling = false;
			activePointerId = INVALID_POINTER_ID;

			if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				isWaitingForScrollFinish = true;
				return;
			}

			hoverCellCurrentBounds.offsetTo(hoverCellOriginalBounds.left, mobileView.getTop());

			ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(hoverCell, "bounds",
					boundEvaluator, hoverCellCurrentBounds);
			hoverViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					listView.invalidate();
				}
			});
			hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					listView.setEnabled(false);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					unsetSpriteMobileState();
					aboveItemId = INVALID_ID;
					mobileItemId = INVALID_ID;
					belowItemId = INVALID_ID;
					if (!forSpriteList) {
						mobileView.setVisibility(View.VISIBLE);
					}
					hoverCell = null;
					listView.setEnabled(true);
					listView.invalidate();
					listView.invalidateViews();
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
		if (cellIsMobile) {
			View mobileView = getViewForID(mobileItemId);
			unsetSpriteMobileState();
			aboveItemId = INVALID_ID;
			mobileItemId = INVALID_ID;
			belowItemId = INVALID_ID;
			if (!forSpriteList) {
				mobileView.setVisibility(View.VISIBLE);
			}
			hoverCell = null;
			listView.invalidate();

			listView.invalidateViews();
		}
		cellIsMobile = false;
		isMobileScrolling = false;
		activePointerId = INVALID_POINTER_ID;
	}

	private void unsetSpriteMobileState() {
		if (forSpriteList) {
			int mobileElementPosition = getAdapterPositionForVisibleListViewPosition(getPositionForID(mobileItemId));
			if (mobileElementPosition != INVALID_ID) {
				getSpriteAdapter().getSpriteList().get(mobileElementPosition).setIsMobile(false);
			} else {
				resetAllVisibleAndInvisibleSpritesMobileState();
			}
		}
	}

	private void resetAllVisibleAndInvisibleSpritesMobileState() {
		for (Sprite sprite : getSpriteAdapter().getSpriteList()) {
			sprite.setIsMobile(false);
		}
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
		int offset = 0;
		int extent = 0;
		int range = 0;

		if (listView instanceof DynamicListView) {
			offset = ((DynamicListView) listView).getComputeVerticalScrollOffset();
			extent = ((DynamicListView) listView).getComputeVerticalScrollExtent();
			range = ((DynamicListView) listView).getComputeVerticalScrollRange();
		} else if (listView instanceof DynamicExpandableListView) {
			offset = ((DynamicExpandableListView) listView).getComputeVerticalScrollOffset();
			extent = ((DynamicExpandableListView) listView).getComputeVerticalScrollExtent();
			range = ((DynamicExpandableListView) listView).getComputeVerticalScrollRange();
		}

		int height = listView.getHeight();

		int hoverViewTop = r.top;
		int hoverHeight = r.height();
		boolean hoverCellIsOnTopOfScreen = hoverViewTop <= 0 && offset > 0;
		boolean hoverCellIsOnBottomOfScreen = hoverViewTop + hoverHeight >= height && (offset + extent) < range;

		if (hoverCellIsOnTopOfScreen) {
			listView.smoothScrollBy(-smoothScrollAmountAtEdge, 0);
			return true;
		}

		if (hoverCellIsOnBottomOfScreen) {
			listView.smoothScrollBy(smoothScrollAmountAtEdge, 0);
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
			UtilDynamicListView.this.scrollState = scrollState;
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
				if (!swapElementsOnlyOnDrop) {
					handleCellSwitch(false);
				}
			}
		}

		public void checkAndHandleLastVisibleCellChange() {
			int currentLastVisibleItem = currentFirstVisibleItem + currentVisibleItemCount;
			int previousLastVisibleItem = previousFirstVisibleItem + previousVisibleItemCount;
			if (currentLastVisibleItem != previousLastVisibleItem && cellIsMobile && mobileItemId != INVALID_ID) {
				updateNeighborViewsForID(mobileItemId);
				if (!swapElementsOnlyOnDrop) {
					handleCellSwitch(false);
				}
			}
		}
	};

	public void setSpritesListFragment(SpritesListFragment spritesListFragment) {
		this.spritesListFragment = spritesListFragment;
	}

	public SpritesListFragment getSpritesListFragment() {
		return spritesListFragment;
	}

	private SpriteAdapter getSpriteAdapter() {
		return spritesListFragment.getSpriteAdapter();
	}

	private int getAdapterPositionForVisibleListViewPosition(int listViewPosition) {
		return getSpriteAdapter().getAdapterPositionForVisibleListViewPosition(listViewPosition);
	}
}
