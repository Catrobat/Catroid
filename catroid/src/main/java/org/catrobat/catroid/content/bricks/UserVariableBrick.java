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
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.content.bricks.brickspinner.UserVariableBrickTextInputDialogBuilder;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserUtils;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;
import org.catrobat.catroid.ui.UiUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class UserVariableBrick extends BrickBaseType implements UserVariableBrickInterface {

	private static final String VARIABLE_CATLANG_PARAMETER_NAME = "variable";

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

	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserVariableBrick clone = (UserVariableBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@IdRes
	protected abstract int getSpinnerId();

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
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(VARIABLE_CATLANG_PARAMETER_NAME)) {
			String userVariableName = "";
			if (userVariable != null) {
				userVariableName = CatrobatLanguageUtils.formatVariable(userVariable.getName());
			}
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, userVariableName);
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(VARIABLE_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String variableName = arguments.get(VARIABLE_CATLANG_PARAMETER_NAME);
		if (variableName == null) {
			throw new CatrobatLanguageParsingException("No variable given");
		}
		if (variableName.isEmpty()) {
			userVariable = null;
			return;
		}
		variableName = CatrobatLanguageParserUtils.Companion.getAndValidateVariableName(variableName);
		userVariable = sprite.getUserVariable(variableName);
		if (userVariable == null) {
			userVariable = project.getUserVariable(variableName);
			if (userVariable == null) {
				userVariable = project.getMultiplayerVariable(variableName);
				if (userVariable == null) {
					throw new CatrobatLanguageParsingException("No variable found with name: " + variableName);
				}
			}
		}

	}
}
