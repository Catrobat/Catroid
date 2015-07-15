/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.util.List;

public class BrickBaseType implements Brick {
	private static final long serialVersionUID = 1L;
	private static final String TAG = BrickBaseType.class.getSimpleName();
	protected transient View view;
	protected transient CheckBox checkbox;
	protected transient boolean checked = false;
	protected transient BrickAdapter adapter;
	protected transient int alphaValue = 255;
	public transient boolean animationState = false;

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void setAnimationState(boolean animationState) {
		this.animationState = animationState;
	}

	@Override
	public int getAlphaValue() {
		return alphaValue;
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
			if (visibility == View.GONE) {
				checked = false;
			}
		}
	}

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public void setCheckedBoolean(boolean newValue) {
		checked = newValue;
	}

	@Override
	public void setCheckboxView(int id) {
		setCheckboxView(id, view);
	}

	@Override
	public void setCheckboxView(int id, View view) {
		int checkboxVisibility = View.GONE;
		boolean enabled = true;
		boolean isChecked = false;
		if (checkbox != null) {
			checkboxVisibility = checkbox.getVisibility();
			enabled = checkbox.isEnabled();
			isChecked = checkbox.isChecked();
		}
		checkbox = (CheckBox) view.findViewById(id);
		checkbox.setChecked(isChecked);
		checkbox.setVisibility(checkboxVisibility);
		checkbox.setEnabled(enabled);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		return null;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		return (Brick) super.clone();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		return null;
	}

	@Override
	public View getPrototypeView(Context context) {
		return null;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		BrickBaseType copyBrick = null;
		try {
			copyBrick = (BrickBaseType) clone();
		} catch (CloneNotSupportedException exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
		}
		return copyBrick;
	}

	@Override
	public void setAlpha(int newAlpha) {
		alphaValue = newAlpha;
	}
}
