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

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.pocketmusic.fastscroll.FastScroller;

public abstract class ScrollerViewProvider {

	private FastScroller scroller;
	private ViewBehavior handleBehavior;
	private ViewBehavior bubbleBehavior;

	public void setFastScroller(FastScroller scroller) {
		this.scroller = scroller;
	}

	protected Context getContext() {
		return scroller.getContext();
	}

	protected FastScroller getScroller() {
		return scroller;
	}

	public abstract View provideHandleView(ViewGroup container);

	public abstract View provideBubbleView(ViewGroup container);

	public abstract TextView provideBubbleTextView();

	public abstract int getBubbleOffset();

	@Nullable
	protected abstract ViewBehavior provideHandleBehavior();

	@Nullable
	protected abstract ViewBehavior provideBubbleBehavior();

	protected ViewBehavior getHandleBehavior() {
		if (handleBehavior == null) {
			handleBehavior = provideHandleBehavior();
		}
		return handleBehavior;
	}

	protected ViewBehavior getBubbleBehavior() {
		if (bubbleBehavior == null) {
			bubbleBehavior = provideBubbleBehavior();
		}
		return bubbleBehavior;
	}

	public void onHandleGrabbed() {
		if (getHandleBehavior() != null) {
			getHandleBehavior().onHandleGrabbed();
		}
		if (getBubbleBehavior() != null) {
			getBubbleBehavior().onHandleGrabbed();
		}
	}

	public void onHandleReleased() {
		if (getHandleBehavior() != null) {
			getHandleBehavior().onHandleReleased();
		}
		if (getBubbleBehavior() != null) {
			getBubbleBehavior().onHandleReleased();
		}
	}

	public void onScrollStarted() {
		if (getHandleBehavior() != null) {
			getHandleBehavior().onScrollStarted();
		}
		if (getBubbleBehavior() != null) {
			getBubbleBehavior().onScrollStarted();
		}
	}

	public void onScrollFinished() {
		if (getHandleBehavior() != null) {
			getHandleBehavior().onScrollFinished();
		}
		if (getBubbleBehavior() != null) {
			getBubbleBehavior().onScrollFinished();
		}
	}
}
