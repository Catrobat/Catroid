/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class CustomScrollAwareBehavior extends FloatingActionButton.Behavior{

	private Handler handler = new Handler();
	private FloatingActionButton fab;

	public CustomScrollAwareBehavior(Context context, AttributeSet attrs) {
		super();
	}

	@Override
	public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
			FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
		fab = child;
		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
				super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
						nestedScrollAxes);
	}



	Runnable showRunnable = new Runnable() {
		@Override
		public void run() {
			fab.show();
		}
	};


	@Override
	public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
			View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
				dyUnconsumed);
		if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
			handler.removeCallbacks(showRunnable);
			handler.postDelayed(showRunnable,2000);
			child.hide();
		}
	}
}