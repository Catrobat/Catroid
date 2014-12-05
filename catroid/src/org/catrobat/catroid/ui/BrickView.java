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

package org.catrobat.catroid.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Brick View.
 * Provides Checkable support and can be used in listView with selection.
 * <p/>
 * Created by Illya Boyko &lt;illya.boyko@gmail.com> on 02/12/14.
 */
public class BrickView extends LinearLayout implements Checkable {


	private CheckBox checkbox;
	private ViewGroup brickLayout;
	private int mode = Mode.DEFAULT;

	public BrickView(Context context) {
		super(context);
	}

	public BrickView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public BrickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		checkbox = (CheckBox) getChildAt(0);
		brickLayout = (ViewGroup) getChildAt(1);
	}


	@Override
	public void setChecked(boolean checked) {
		checkbox.setChecked(checked);
	}

	@Override
	public boolean isChecked() {
		return checkbox.isChecked();
	}

	@Override
	public void toggle() {
		checkbox.setChecked(!isChecked());
	}

	public void addMode(int mask) {
		int oldMode = this.mode;
		this.mode |= mask;
		onModeChanged(oldMode, mode);
	}

	public void removeMode(int mask) {
		int oldMode = this.mode;
		this.mode &= ~mask;
		onModeChanged(oldMode, mode);
	}

	public int getMode() {
		return mode;
	}

	public boolean hasMode(int mask) {
		return (this.mode & mask) == mask;
	}


	private void onModeChanged(int oldMode, int newMode) {
		if (oldMode == newMode) {
			return;
		}
		applyModeChanged(brickLayout, oldMode, newMode);
	}

	private void applyModeChanged(ViewGroup viewGroup, int oldMode, int newMode) {
		if (viewGroup != null) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				View child = viewGroup.getChildAt(i);
				if (child instanceof ViewGroup && !(child instanceof Spinner)) {
					applyModeChanged((ViewGroup) child, oldMode, newMode);
				} else {
					child.setClickable(newMode == Mode.DEFAULT);
					if (child instanceof Spinner) {
						//TODO: provide extra style for spinner in prototype View
						child.setEnabled(newMode == Mode.DEFAULT);
					}
				}
			}
		}
	}

	public class Mode {
		public static final int DEFAULT = 0;
		/**
		 * Prototype View Mode means user cannot edit child elements like Formula fields.
		 */
		public static final int PROTOTYPE = 2;
		/**
		 * Selection View Mode means that this view is in selection state.
		 */
		public static final int SELECTION = 4;
	}
}
