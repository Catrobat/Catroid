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

package org.catrobat.catroid.pocketmusic.fastscroller;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

	private final FastScroller scroller;
	private int oldScrollState = RecyclerView.SCROLL_STATE_IDLE;

	public RecyclerViewScrollListener(FastScroller scroller) {
		this.scroller = scroller;
	}

	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newScrollState) {
		if (newScrollState == RecyclerView.SCROLL_STATE_IDLE && oldScrollState != RecyclerView.SCROLL_STATE_IDLE) {
			scroller.getViewProvider().onScrollFinished();
		} else if (newScrollState != RecyclerView.SCROLL_STATE_IDLE && oldScrollState == RecyclerView
				.SCROLL_STATE_IDLE) {
			scroller.getViewProvider().onScrollStarted();
		}
		oldScrollState = newScrollState;
		super.onScrollStateChanged(recyclerView, newScrollState);
	}

	@Override
	public void onScrolled(RecyclerView rv, int dx, int dy) {
		if (scroller.shouldUpdateHandlePosition()) {
			updateHandlePosition(rv);
		}
		super.onScrolled(rv, dx, dy);
	}

	void updateHandlePosition(RecyclerView rv) {
		float relativePos;
		if (scroller.isVertical()) {
			int offset = rv.computeVerticalScrollOffset();
			int extent = rv.computeVerticalScrollExtent();
			int range = rv.computeVerticalScrollRange();
			relativePos = offset / (float) (range - extent);
		} else {
			int offset = rv.computeHorizontalScrollOffset();
			int extent = rv.computeHorizontalScrollExtent();
			int range = rv.computeHorizontalScrollRange();
			relativePos = offset / (float) (range - extent);
		}
		scroller.setScrollerPosition(relativePos);
	}
}
