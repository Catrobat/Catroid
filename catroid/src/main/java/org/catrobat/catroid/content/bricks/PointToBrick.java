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

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSpriteDialogWrapper;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.List;

public class PointToBrick extends BrickBaseType implements NewItemInterface<Sprite>,
		SpinnerWithNewOption.SpinnerSelectionListener<Sprite> {

	private static final long serialVersionUID = 1L;

	private Sprite pointedObject;

	private transient SpinnerWithNewOption<Sprite> spinner;

	public PointToBrick() {
	}

	public PointToBrick(Sprite pointedSprite) {
		this.pointedObject = pointedSprite;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		PointToBrick clone = (PointToBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_point_to;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);

		List<Sprite> sprites = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();
		sprites.remove(ProjectManager.getInstance().getCurrentSprite());
		sprites.remove(ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite());

		spinner = new SpinnerWithNewOption<>(R.id.brick_point_to_spinner, view, sprites, this);
		spinner.setSelection(pointedObject);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public boolean onNewOptionClicked() {
		new NewSpriteDialogWrapper(this, ProjectManager.getInstance().getCurrentlyEditedScene()) {

			@Override
			public void onWorkflowCanceled() {
				super.onWorkflowCanceled();
				spinner.setSelection(pointedObject);
			}
		}.showDialog(((Activity) view.getContext()).getFragmentManager());
		return false;
	}

	@Override
	public void addItem(Sprite item) {
		ProjectManager.getInstance().getCurrentlyEditedScene().addSprite(item);
		pointedObject = item;
		spinner.add(item);
	}

	@Override
	public void onItemSelected(Sprite item) {
		pointedObject = item;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPointToAction(sprite, pointedObject));
		return null;
	}
}
