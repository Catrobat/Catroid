/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.catrobat.catroid.content.bricks.BrickBaseType;

import java.util.ArrayList;
import java.util.List;

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
	private Rect viewBounds;

	private BitmapDrawable highlightedDrawable;

	private int currentPositionOfHoveringBrick;

	private List<BrickBaseType> bricksToMove = new ArrayList<>();
	private List<Integer> brickPositionsToHighlight = new ArrayList<>();

	private int motionEventId = -1;
	private float downY = 0;
	private int offsetToCenter = 0;

	private boolean invalidateHoveringItem = false;

	private BrickAdapterInterface brickAdapterInterface;

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
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				invalidate();
			}
		});
	}

	public void cancelHighlighting() {
		brickPositionsToHighlight.clear();
		highlightedDrawable = null;
		invalidateViews();
	}

	public void highlightControlStructureBricks(List<Integer> positions) {
		cancelHighlighting();
		brickPositionsToHighlight.addAll(positions);
		for (int pos : positions) {
			drawItem(getChildAtVisiblePosition(pos), false);
		}
		invalidate();
	}

	public void startMoving(List<BrickBaseType> bricksToMove, int positionOfFirstBrick) {
		cancelMove();
		this.bricksToMove.addAll(bricksToMove);
		brickAdapterInterface.removeItems(bricksToMove.subList(1, bricksToMove.size()));

		currentPositionOfHoveringBrick = positionOfFirstBrick;

		upperScrollBound = getHeight() / 8;
		lowerScrollBound = getHeight() / 8 * 6;

		brickAdapterInterface.setItemVisible(positionOfFirstBrick, false);
		invalidateHoveringItem = true;

		drawItem(getChildAtVisiblePosition(positionOfFirstBrick), true);
		setOffsetToCenter(viewBounds);
		invalidate();
	}

	public void stopMoving() {
		brickAdapterInterface.moveItemsTo(currentPositionOfHoveringBrick, bricksToMove);
		cancelMove();
	}

	public void cancelMove() {
		brickAdapterInterface.setAllPositionsVisible();
		bricksToMove.clear();
		hoveringDrawable = null;
		motionEventId = -1;
		invalidateViews();
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

		if (!bricksToMove.isEmpty() || !brickPositionsToHighlight.isEmpty()) {
			Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.FILL);
			paint.setAlpha(128);

			canvas.drawRect(rect, paint);
		}

		if (hoveringDrawable != null) {
			if (invalidateHoveringItem) {
				invalidateHoveringItem = false;
				drawItem(getChildAtVisiblePosition(currentPositionOfHoveringBrick), true);
			}
			hoveringDrawable.draw(canvas);
		}

		if (highlightedDrawable != null) {
			for (int pos : brickPositionsToHighlight) {
				if (pos >= getFirstVisiblePosition() && pos <= getLastVisiblePosition()) {
					drawItem(getChildAtVisiblePosition(pos), false);
					highlightedDrawable.draw(canvas);
				}
			}
		}
	}

	@VisibleForTesting
	public void drawItem(View view, boolean hoveringItem) {
		if (view == null) {
			return;
		}
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));
		viewBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		BitmapDrawable drawable;
		if (hoveringItem) {
			drawable = new BitmapDrawable(getResources(), getGlowingBorder(bitmap));
			drawable.setBounds(viewBounds);
			hoveringDrawable = drawable;
		} else {
			drawable = new BitmapDrawable(getResources(), bitmap);
			drawable.setBounds(viewBounds);
			highlightedDrawable = drawable;
		}
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

	public Bitmap getGlowingBorder(Bitmap bitmap) {
		Bitmap glowingBitmap = Bitmap
				.createBitmap(bitmap.getWidth() + 30, bitmap.getHeight() + 30, Bitmap.Config.ARGB_8888);
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

		return glowingBitmap;
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
