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

package org.catrobat.catroid.pocketmusic.fastscroll.viewprovider;

import android.graphics.drawable.InsetDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;

public class DefaultScrollerViewProvider extends ScrollerViewProvider {

	protected View bubble;
	protected View handle;

	@Override
	public View provideHandleView(ViewGroup container) {
		handle = new View(getContext());

		int verticalInsetTop = getContext().getResources().getDimensionPixelSize(R.dimen.fastscroll__handle_inset_top);
		int verticalInsetBottom = getContext().getResources().getDimensionPixelSize(R.dimen
				.fastscroll__handle_inset_bottom);
		int horizontalInset = !getScroller().isVertical() ? 0 : getContext().getResources().getDimensionPixelSize(R.dimen.fastscroll__handle_inset);
		InsetDrawable handleBg = new InsetDrawable(ContextCompat.getDrawable(getContext(), R.drawable
				.fastscroll__default_handle), horizontalInset, verticalInsetTop, horizontalInset, verticalInsetBottom);
		handle.setBackground(handleBg);

		int handleWidth = getContext().getResources().getDimensionPixelSize(R.dimen.fastscroll__handle_clickable_width);
		int handleHeight = getContext().getResources().getDimensionPixelSize(R.dimen.fastscroll__handle_height);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(handleWidth, handleHeight);
		handle.setLayoutParams(params);

		return handle;
	}

	@Override
	public View provideBubbleView(ViewGroup container) {
		bubble = LayoutInflater.from(getContext()).inflate(R.layout.fastscroll__default_bubble, container, false);
		return bubble;
	}

	@Override
	public TextView provideBubbleTextView() {
		return (TextView) bubble;
	}

	@Override
	public int getBubbleOffset() {
		return (int) (getScroller().isVertical() ? ((float) handle.getHeight() / 2f) - bubble.getHeight() : ((float)
				handle.getWidth() / 2f) - bubble.getWidth());
	}

	@Override
	protected ViewBehavior provideHandleBehavior() {
		return new DefaultHandleBehavior(new VisibilityAnimationManager.Builder(handle).withHideDelay(2000)
				.withHideAnimator(R.animator.fastscroll_fade_out).withShowAnimator(R.animator.fastscroll_fade_in).build());
	}

	@Override
	protected ViewBehavior provideBubbleBehavior() {
		return new DefaultBubbleBehavior(new VisibilityAnimationManager.Builder(bubble).withHideDelay(0)
				.withPivotX(0.5f)
				.withPivotY(1f).build());
	}
}
