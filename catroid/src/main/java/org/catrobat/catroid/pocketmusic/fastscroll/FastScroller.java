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

package org.catrobat.catroid.pocketmusic.fastscroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.fastscroll.viewprovider.DefaultScrollerViewProvider;
import org.catrobat.catroid.pocketmusic.fastscroll.viewprovider.ScrollerViewProvider;
import org.catrobat.catroid.pocketmusic.ui.TactScrollRecyclerView;

public class FastScroller extends LinearLayout {

	private static final int STYLE_NONE = -1;
	private final RecyclerViewScrollListener scrollListener = new RecyclerViewScrollListener(this);
	private TactScrollRecyclerView recyclerView;

	private View bubble;
	private View handle;
	private TextView bubbleTextView;

	private int bubbleOffset;
	private int handleColor;
	private int bubbleColor;
	private int bubbleTextAppearance;
	private int scrollerOrientation;

	private boolean manuallyChangingPosition;

	private ScrollerViewProvider viewProvider;
	private SectionTitleProvider titleProvider;

	public FastScroller(Context context) {
		this(context, null);
	}

	public FastScroller(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FastScroller(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setClipChildren(false);
		TypedArray style = context.obtainStyledAttributes(attrs, R.styleable.fastscroll__fastScroller, R.attr.fastscroll__style, 0);
		try {
			bubbleColor = style.getColor(R.styleable.fastscroll__fastScroller_fastscroll__bubbleColor, STYLE_NONE);
			handleColor = style.getColor(R.styleable.fastscroll__fastScroller_fastscroll__handleColor, STYLE_NONE);
			bubbleTextAppearance = style.getResourceId(R.styleable.fastscroll__fastScroller_fastscroll__bubbleTextAppearance, STYLE_NONE);
		} finally {
			style.recycle();
		}
		setViewProvider(new DefaultScrollerViewProvider());
	}

	public void setViewProvider(ScrollerViewProvider viewProvider) {
		removeAllViews();
		this.viewProvider = viewProvider;
		viewProvider.setFastScroller(this);
		bubble = viewProvider.provideBubbleView(this);
		handle = viewProvider.provideHandleView(this);
		bubbleTextView = viewProvider.provideBubbleTextView();
		addView(bubble);
		addView(handle);
	}

	public void setRecyclerView(TactScrollRecyclerView recyclerView) {
		this.recyclerView = recyclerView;

		if (recyclerView.getAdapter() instanceof SectionTitleProvider) {
			titleProvider = (SectionTitleProvider) recyclerView.getAdapter();
		}
		recyclerView.addOnScrollListener(scrollListener);
	}

	@Override
	public void setOrientation(int orientation) {
		scrollerOrientation = orientation;
		super.setOrientation(orientation == HORIZONTAL ? VERTICAL : HORIZONTAL);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		initHandleMovement();
		bubbleOffset = viewProvider.getBubbleOffset();

		applyStyling();

		if (!isInEditMode()) {
			scrollListener.updateHandlePosition(recyclerView);
		}
	}

	private void applyStyling() {
		if (bubbleColor != STYLE_NONE) {
			setBackgroundTint(bubbleTextView, bubbleColor);
		}
		if (handleColor != STYLE_NONE) {
			setBackgroundTint(handle, handleColor);
		}
		if (bubbleTextAppearance != STYLE_NONE) {
			TextViewCompat.setTextAppearance(bubbleTextView, bubbleTextAppearance);
		}
	}

	private void setBackgroundTint(View view, int color) {
		final Drawable background = DrawableCompat.wrap(view.getBackground());
		if (background == null) {
			return;
		}
		DrawableCompat.setTint(background.mutate(), color);
		view.setBackground(background);
	}

	private void initHandleMovement() {
		handle.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				requestDisallowInterceptTouchEvent(true);
				if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
					if (titleProvider != null && event.getAction() == MotionEvent.ACTION_DOWN) {
						viewProvider.onHandleGrabbed();
					}
					manuallyChangingPosition = true;
					float relativePos = getRelativeTouchPosition(event);
					setScrollerPosition(relativePos);
					setRecyclerViewPosition(relativePos);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					manuallyChangingPosition = false;
					if (titleProvider != null) {
						viewProvider.onHandleReleased();
					}
					return true;
				}
				return false;
			}
		});
	}

	private float getRelativeTouchPosition(MotionEvent event) {
		if (isVertical()) {
			float yInParent = event.getRawY() - Utils.getViewRawY(handle);
			return yInParent / (getHeight() - handle.getHeight());
		} else {
			float xInParent = event.getRawX() - Utils.getViewRawX(handle);
			return xInParent / (getWidth() - handle.getWidth());
		}
	}

	private void setRecyclerViewPosition(float relativePos) {
		if (recyclerView == null) {
			return;
		}
		int itemCount = recyclerView.getAdapter().getItemCount();
		int targetPos = (int) Utils.getValueInRange(0, itemCount - 1, (int) (relativePos * (float) itemCount));

		int currentLeftViewPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
				.findFirstCompletelyVisibleItemPosition();
		int scrollByPositionX = targetPos - currentLeftViewPosition;
		recyclerView.scrollBy(scrollByPositionX * recyclerView.getTactViewWidth(), 0);

		if (titleProvider != null && bubbleTextView != null) {
			bubbleTextView.setText(titleProvider.getSectionTitle(targetPos));
		}
	}

	void setScrollerPosition(float relativePos) {
		if (isVertical()) {
			bubble.setY(Utils.getValueInRange(
					0,
					getHeight() - bubble.getHeight(),
					relativePos * (getHeight() - handle.getHeight()) + bubbleOffset)
			);
			handle.setY(Utils.getValueInRange(
					0,
					getHeight() - handle.getHeight(),
					relativePos * (getHeight() - handle.getHeight()))
			);
		} else {
			bubble.setX(Utils.getValueInRange(
					0,
					getWidth() - bubble.getWidth(),
					relativePos * (getWidth() - handle.getWidth()) + bubbleOffset)
			);
			handle.setX(Utils.getValueInRange(
					0,
					getWidth() - handle.getWidth(),
					relativePos * (getWidth() - handle.getWidth()))
			);
		}
	}

	public boolean isVertical() {
		return scrollerOrientation == VERTICAL;
	}

	boolean shouldUpdateHandlePosition() {
		return handle != null && !manuallyChangingPosition && recyclerView.getChildCount() > 0;
	}

	ScrollerViewProvider getViewProvider() {
		return viewProvider;
	}
}
