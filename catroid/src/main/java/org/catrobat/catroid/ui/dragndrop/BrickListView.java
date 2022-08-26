/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.catrobat.catroid.content.bricks.Brick;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import static android.animation.ValueAnimator.REVERSE;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_INDEX_MASK;
import static android.view.MotionEvent.ACTION_POINTER_INDEX_SHIFT;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

public class BrickListView extends ListView {

	private static final int SMOOTH_SCROLL_BY = 15;
	private static final int ANIMATION_DURATION = 250;

	private int upperScrollBound;
	private int lowerScrollBound;

	private BitmapDrawable hoveringDrawable;
	private Rect viewBounds = new Rect();

	private int currentPositionOfHoveringBrick;

	private Brick brickToMove;
	private List<Integer> brickPositionsToHighlight = new ArrayList<>();

	private int motionEventId = -1;
	private float downY = 0;
	private int offsetToCenter = 0;

	private boolean invalidateHoveringItem = false;

	private BrickAdapterInterface brickAdapterInterface;

	private int translucentBlack = Color.argb(128, 0, 0, 0);

	public BrickListView(Context context) {
		super(context);
	}

	public BrickListView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public BrickListView(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);
	}

	public boolean isCurrentlyMoving() {
		return hoveringDrawable != null;
	}

	public boolean isCurrentlyHighlighted() {
		return !brickPositionsToHighlight.isEmpty();
	}

	public List<Integer> getBrickPositionsToHighlight() {
		return brickPositionsToHighlight;
	}

	public void highlightMovingItem() {
		ObjectAnimator animator = ObjectAnimator.ofInt(hoveringDrawable, "alpha", 255, 0);
		animator.setDuration(ANIMATION_DURATION);
		animator.setRepeatMode(REVERSE);
		animator.setRepeatCount(5);
		animator.start();
		animator.addUpdateListener(animation -> invalidate());
	}

	public void cancelHighlighting() {
		brickPositionsToHighlight.clear();
		invalidate();
	}

	public void highlightControlStructureBricks(List<Integer> positions) {
		cancelHighlighting();
		brickPositionsToHighlight.addAll(positions);
		invalidate();
	}

	public void startMoving(Brick brickToMove) {
		cancelMove();

		List<Brick> flatList = new ArrayList<>();
		brickToMove.addToFlatList(flatList);

		if (brickToMove != flatList.get(0)) {
			return;
		}

		this.brickToMove = flatList.get(0);
		flatList.remove(0);

		upperScrollBound = getHeight() / 8;
		lowerScrollBound = getHeight() / 8 * 6;

		currentPositionOfHoveringBrick = brickAdapterInterface.getPosition(this.brickToMove);

		invalidateHoveringItem = true;

		prepareHoveringItem(getChildAtVisiblePosition(currentPositionOfHoveringBrick));

		brickAdapterInterface.setItemVisible(currentPositionOfHoveringBrick, false);

		if (!brickAdapterInterface.removeItems(flatList)) {
			invalidateViews();
		}
	}

	public void stopMoving() {
		brickAdapterInterface.moveItemTo(currentPositionOfHoveringBrick, brickToMove);
		cancelMove();
	}

	public void cancelMove() {
		brickAdapterInterface.setAllPositionsVisible();
		brickToMove = null;
		hoveringDrawable = null;
		motionEventId = -1;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (hoveringDrawable == null) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {
			case ACTION_UP:
			case ACTION_CANCEL:
				stopMoving();
				break;
			case ACTION_DOWN:
				downY = event.getY();
				motionEventId = event.getPointerId(0);
				break;
			case ACTION_MOVE:
				float dY = event.getY() - downY;
				downY += dY;
				downY -= offsetToCenter;

				viewBounds.offsetTo(viewBounds.left, (int) downY);
				hoveringDrawable.setBounds(viewBounds);

				invalidate();
				swapListItems();
				scrollWhileDragging();
				break;
			case ACTION_POINTER_UP:
				int pointerIndex = (event.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;
				if (event.getPointerId(pointerIndex) == motionEventId) {
					stopMoving();
				}
		}
		return true;
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (brickToMove != null || !brickPositionsToHighlight.isEmpty()) {
			canvas.drawColor(translucentBlack);
		}

		if (invalidateHoveringItem) {
			View childAtVisiblePosition = getChildAtVisiblePosition(currentPositionOfHoveringBrick);
			if (childAtVisiblePosition != null) {
				invalidateHoveringItem = false;
				prepareHoveringItem(childAtVisiblePosition);
			}
		}

		if (hoveringDrawable != null) {
			hoveringDrawable.draw(canvas);
		}

		for (int pos : brickPositionsToHighlight) {
			if (pos >= getFirstVisiblePosition() && pos <= getLastVisiblePosition()) {
				drawHighlightedItem(getChildAtVisiblePosition(pos), canvas);
			}
		}
	}

	@VisibleForTesting
	public void drawHighlightedItem(View view, Canvas canvas) {
		if (view == null) {
			return;
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));

		BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
		drawable.setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		drawable.draw(canvas);
	}

	private void prepareHoveringItem(View view) {
		if (view == null) {
			return;
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));

		viewBounds.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

		BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
		drawable.setBounds(viewBounds);
		hoveringDrawable = drawable;

		setOffsetToCenter(viewBounds);
	}

	private void setOffsetToCenter(@NonNull Rect viewBounds) {
		offsetToCenter = (viewBounds.height() / 2);
	}

	private void swapListItems() {
		int itemPositionAbove = currentPositionOfHoveringBrick - 1;
		int itemPositionBelow = currentPositionOfHoveringBrick + 1;

		View itemBelow = null;
		View itemAbove = null;

		if (isPositionValid(itemPositionAbove)) {
			itemAbove = getChildAtVisiblePosition(itemPositionAbove);
		}

		if (isPositionValid(itemPositionBelow)) {
			itemBelow = getChildAtVisiblePosition(itemPositionBelow);
		}

		boolean isAbove = (itemBelow != null) && (downY > itemBelow.getY());
		boolean isBelow = (itemAbove != null) && (downY < itemAbove.getY());

		if (isAbove || isBelow) {
			int swapWith = isAbove ? itemPositionBelow : itemPositionAbove;
			int translationY = isAbove ? 10 - viewBounds.height() : viewBounds.height() - 10;

			if (brickAdapterInterface.onItemMove(currentPositionOfHoveringBrick, swapWith)) {

				brickAdapterInterface.setItemVisible(currentPositionOfHoveringBrick, true);
				currentPositionOfHoveringBrick = swapWith;
				brickAdapterInterface.setItemVisible(currentPositionOfHoveringBrick, false);

				View viewToSwapWith = isAbove ? itemBelow : itemAbove;

				ObjectAnimator animator = ObjectAnimator.ofFloat(viewToSwapWith, View.TRANSLATION_Y, translationY);
				animator.setDuration(ANIMATION_DURATION);
				animator.start();
				animator.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						BrickListView.this.invalidateViews();
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}
				});
			}
		}
	}

	private void scrollWhileDragging() {
		if (downY > lowerScrollBound) {
			smoothScrollBy(SMOOTH_SCROLL_BY, 0);
		} else if (downY < upperScrollBound) {
			smoothScrollBy(-SMOOTH_SCROLL_BY, 0);
		}
	}

	private View getChildAtVisiblePosition(int positionInAdapter) {
		return getChildAt(positionInAdapter - getFirstVisiblePosition());
	}

	private boolean isPositionValid(int position) {
		return (position >= 0 && position < getCount());
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (!(adapter instanceof BrickAdapterInterface)) {
			throw new IllegalArgumentException("Adapter has to implement the BrickListView.AdapterInterface.");
		}
		super.setAdapter(adapter);
		brickAdapterInterface = (BrickAdapterInterface) adapter;
	}
}
