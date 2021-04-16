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
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PointToBrick extends BrickBaseType implements BrickSpinner.OnItemSelectedListener<Sprite>,
		NewItemInterface<Sprite> {

	private static final long serialVersionUID = 1L;

	private Sprite pointedObject;

	private transient BrickSpinner<Sprite> spinner;

	public PointToBrick() {
	}

	public PointToBrick(Sprite pointedSprite) {
		this.pointedObject = pointedSprite;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		PointToBrick clone = (PointToBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_point_to;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList());
		items.remove(ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite());
		items.remove(ProjectManager.getInstance().getCurrentSprite());

		spinner = new BrickSpinner<>(R.id.brick_point_to_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(pointedObject);

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		((SpriteActivity) activity).registerOnNewSpriteListener(this);
		((SpriteActivity) activity).handleAddSpriteButton();
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void addItem(Sprite item) {
		spinner.add(item);
		spinner.setSelection(item);
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable Sprite item) {
		pointedObject = item;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPointToAction(sprite, pointedObject));
	}
}
