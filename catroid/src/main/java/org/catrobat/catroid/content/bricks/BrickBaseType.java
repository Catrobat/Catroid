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
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.util.List;

public abstract class BrickBaseType implements Brick {

	private static final long serialVersionUID = 1L;

	public transient View view;
	protected transient CheckBox checkbox;

	protected boolean commentedOut;

	@Override
	public boolean isCommentedOut() {
		return commentedOut;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		this.commentedOut = commentedOut;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		BrickBaseType clone = (BrickBaseType) super.clone();
		clone.view = null;
		clone.checkbox = null;
		clone.commentedOut = commentedOut;
		return clone;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	protected abstract @LayoutRes int getLayoutRes();

	@Override
	public View getView(Context context, final BrickAdapter adapter) {
		if (view == null) {
			view = LayoutInflater.from(context).inflate(getLayoutRes(), null);
			checkbox = view.findViewById(R.id.brick_checkbox);
		}

		final Brick instance = this;
		BrickViewProvider.setSaturationOnBrick(instance, commentedOut);

		checkbox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				adapter.handleCheck(instance, ((CheckBox) view).isChecked());
			}
		});
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return LayoutInflater.from(context).inflate(getLayoutRes(), null);
	}

	@Override
	public abstract List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence);
}
