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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.ScriptFragment;

public abstract class BrickBaseType implements Brick {

	private static final long serialVersionUID = 1L;

	public transient View view;
	private transient CheckBox checkbox;

	protected boolean commentedOut;

	@Override
	public boolean isCommentedOut() {
		return commentedOut;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		this.commentedOut = commentedOut;
	}

	@Nullable
	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		BrickBaseType clone = (BrickBaseType) super.clone();
		clone.view = null;
		clone.checkbox = null;
		clone.commentedOut = commentedOut;
		return clone;
	}

	public boolean hasHelpPage() {
		return true;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
	}

	@LayoutRes
	public abstract int getViewResource();

	@CallSuper
	@Override
	public View getView(Context context) {
		view = LayoutInflater.from(context).inflate(getViewResource(), null, false);
		checkbox = view.findViewById(R.id.brick_checkbox);
		return view;
	}

	public void onViewCreated() {
	}

	public void onPrototypeViewCreated() {
	}

	public void disableSpinners() {
		disableSpinners(view);
	}

	private void disableSpinners(View view) {
		if (view instanceof Spinner) {
			view.setEnabled(false);
			view.setClickable(false);
			view.setFocusable(false);
		}
		if (view instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) view;
			for (int i = 0; i < parent.getChildCount(); i++) {
				disableSpinners(parent.getChildAt(i));
			}
		}
	}

	void notifyDataSetChanged(AppCompatActivity activity) {
		ScriptFragment parentFragment = (ScriptFragment) activity
				.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG);
		if (parentFragment != null) {
			parentFragment.notifyDataSetChanged();
		}
	}
}
