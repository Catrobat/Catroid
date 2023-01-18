/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static org.koin.java.KoinJavaComponent.inject;

public abstract class UserVariableBrickWithFormula extends FormulaBrick implements UserVariableBrickInterface {

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

	@NonNull
	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserVariableBrickWithFormula clone = (UserVariableBrickWithFormula) super.clone();
		clone.spinner = null;
		return clone;
	}

	@NonNull
	@Override
	public View getView(@NonNull Context context) {
		super.getView(context);

		ProjectManager projectManager = inject(ProjectManager.class).getValue();
		Sprite sprite = projectManager.getCurrentSprite();

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(sprite.getUserVariables());
		items.addAll(projectManager.getCurrentProject().getUserVariables());
		items.addAll(projectManager.getCurrentProject().getMultiplayerVariables());

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
		ProjectManager projectManager = inject(ProjectManager.class).getValue();
		final Project currentProject = projectManager.getCurrentProject();
		final Sprite currentSprite = projectManager.getCurrentSprite();

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
}
