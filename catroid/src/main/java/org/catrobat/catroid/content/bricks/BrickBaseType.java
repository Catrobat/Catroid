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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;

import java.util.Collections;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BrickBaseType implements Brick {

	private static final long serialVersionUID = 1L;

	public transient View view;
	private transient CheckBox checkbox;

	protected transient Brick parent;

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
	public Brick clone() throws CloneNotSupportedException {
		BrickBaseType clone = (BrickBaseType) super.clone();
		clone.view = null;
		clone.checkbox = null;
		clone.parent = null;
		clone.commentedOut = commentedOut;
		return clone;
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

	@Override
	public View getPrototypeView(Context context) {
		View view = getView(context);
		disableSpinners(view);
		return view;
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

	@Override
	public boolean consistsOfMultipleParts() {
		return false;
	}

	@Override
	public List<Brick> getAllParts() {
		return Collections.singletonList(this);
	}

	@Override
	public void addToFlatList(List<Brick> bricks) {
		bricks.add(this);
	}

	@Override
	public Script getScript() {
		return getParent().getScript();
	}

	@Override
	public int getPositionInScript() {
		if (getParent() instanceof ScriptBrick) {
			return getScript().getBrickList().indexOf(this);
		}
		return getParent().getPositionInScript();
	}

	@Override
	public Brick getParent() {
		return parent;
	}

	@Override
	public void setParent(Brick parent) {
		this.parent = parent;
	}

	@Override
	public List<Brick> getDragAndDropTargetList() {
		return getParent().getDragAndDropTargetList();
	}

	@Override
	public int getPositionInDragAndDropTargetList() {
		return getDragAndDropTargetList().indexOf(this);
	}

	@Override
	public boolean removeChild(Brick brick) {
		return false;
	}

	public boolean hasHelpPage() {
		return true;
	}

	void notifyDataSetChanged(AppCompatActivity activity) {
		ScriptFragment parentFragment = (ScriptFragment) activity
				.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG);
		if (parentFragment != null) {
			parentFragment.notifyDataSetChanged();
		}
	}

	public String getHelpUrl(String category) {
		return "https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/" + this.getClass().getSimpleName();
	}

	protected String getPositionInformation() {
		int position = -1;
		String scriptName = "unknown";
		if (getParent() != null) {
			position = getPositionInScript();
			scriptName = getScript().getClass().getSimpleName();
		}
		position += 2;
		return "Brick at position " + position + "\nin \"" + scriptName + "\"";
	}
}
