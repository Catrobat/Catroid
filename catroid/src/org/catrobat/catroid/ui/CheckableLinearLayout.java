/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.catrobat.catroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * This is a simple wrapper for {@link android.widget.LinearLayout} that implements the {@link android.widget.Checkable}
 * interface by keeping an internal 'checked' state flag.
 * <p/>
 * This can be used as the root view for a custom list item layout for
 * {@link android.widget.AbsListView} elements with a
 * {@link android.widget.AbsListView#setChoiceMode(int) choiceMode} set.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

	/**
	 * Interface definition for a callback to be invoked when the checked state of this View is
	 * changed.
	 */
	public static interface OnCheckedChangeListener {

		/**
		 * Called when the checked state of a compound button has changed.
		 *
		 * @param checkableView The view whose state has changed.
		 * @param isChecked     The new checked state of checkableView.
		 */
		void onCheckedChanged(View checkableView, boolean isChecked);
	}


	private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

	private boolean checked = false;
	private OnCheckedChangeListener onCheckedChangeListener;

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean b) {
		if (b != this.checked) {
			this.checked = b;
			refreshDrawableState();
			if (onCheckedChangeListener != null) {
				onCheckedChangeListener.onCheckedChanged(this, this.checked);
			}
		}
	}

	public void toggle() {
		setChecked(!this.checked);
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}
}