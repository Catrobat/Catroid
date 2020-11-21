/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.content.bricks.brickspinner.UserVariableBrickTextInputDialogBuilder;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.UiUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_ALIGNMENT;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_COLOR;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_SIZE;
import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_CENTERED;
import static org.catrobat.catroid.utils.ShowTextUtils.convertColorToString;
import static org.catrobat.catroid.utils.ShowTextUtils.getStringAsInteger;
import static org.catrobat.catroid.utils.ShowTextUtils.isNumberAndInteger;

public abstract class UserVariableBrickWithVisualPlacement extends VisualPlacementBrick implements UserVariableBrickInterface {

	protected UserVariable userVariable;

	private transient BrickSpinner<UserVariable> spinner;

	@Override
	public UserVariable getUserVariable() {
		return userVariable;
	}

	@Override
	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
	}

	@IdRes
	protected abstract int getSpinnerId();

	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserVariableBrickWithVisualPlacement clone = (UserVariableBrickWithVisualPlacement) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(sprite.getUserVariables());
		items.addAll(ProjectManager.getInstance().getCurrentProject().getUserVariables());
		items.addAll(ProjectManager.getInstance().getCurrentProject().getMultiplayerVariables());

		spinner = new BrickSpinner<>(getSpinnerId(), view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(userVariable);
		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		final AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}

		final Project currentProject = ProjectManager.getInstance().getCurrentProject();
		final Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		UserVariableBrickTextInputDialogBuilder builder =
				new UserVariableBrickTextInputDialogBuilder(currentProject, currentSprite, userVariable, activity, spinner);

		builder.show();
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable UserVariable item) {
		userVariable = item;
	}

	@Override
	public BrickField getXBrickField() {
		return BrickField.X_POSITION;
	}

	@Override
	public BrickField getYBrickField() {
		return BrickField.Y_POSITION;
	}

	@Override
	public Intent generateIntentForVisualPlacement(BrickField brickFieldX, BrickField brickFieldY) {
		Intent intent = super.generateIntentForVisualPlacement(brickFieldX, brickFieldY);

		Object variableValue = 0;
		if (userVariable != null) {
			variableValue = userVariable.getValue();
		}

		String text = variableValue.toString();
		if (isNumberAndInteger(text)) {
			text = getStringAsInteger(text);
		}

		intent.putExtra(EXTRA_TEXT, text);
		intent.putExtra(EXTRA_TEXT_COLOR, convertColorToString(Color.BLACK));
		intent.putExtra(EXTRA_TEXT_SIZE, 1.0f);
		intent.putExtra(EXTRA_TEXT_ALIGNMENT, ALIGNMENT_STYLE_CENTERED);

		return intent;
	}
}
