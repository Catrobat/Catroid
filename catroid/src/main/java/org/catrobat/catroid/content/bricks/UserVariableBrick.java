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
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.NewVariableDialogFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class UserVariableBrick extends FormulaBrick implements NewVariableDialogFragment.NewVariableInterface,
		BrickSpinner.OnItemSelectedListener<UserVariable> {

	protected UserVariable userVariable;

	private transient BrickSpinner<UserVariable> spinner;

	public UserVariable getUserVariable() {
		return userVariable;
	}

	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		UserVariableBrick clone = (UserVariableBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@IdRes
	protected abstract int getSpinnerId();

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(dataContainer.getSpriteUserVariables(sprite));
		items.addAll(dataContainer.getProjectUserVariables());

		spinner = new BrickSpinner<>(getSpinnerId(), view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(userVariable);

		return view;
	}

	@Override
	public void onNewOptionSelected() {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}

		new NewVariableFromBrickDialogFragment(this)
				.show(activity.getSupportFragmentManager(), NewVariableFromBrickDialogFragment.TAG);
	}

	@Override
	public void onNewVariable(UserVariable userVariable) {
		spinner.add(userVariable);
		spinner.setSelection(userVariable);
		//TODO: This should work some other way: i.e. it should not rely on the Brick being able to access its adapter.
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onStringOptionSelected(String string) {
	}

	@Override
	public void onItemSelected(@Nullable UserVariable item) {
		userVariable = item;
	}

	public static class NewVariableFromBrickDialogFragment extends NewVariableDialogFragment {

		private UserVariableBrick userVariableBrick;

		public NewVariableFromBrickDialogFragment() {
		}

		public NewVariableFromBrickDialogFragment(UserVariableBrick userVariableBrick) {
			super(userVariableBrick);
			this.userVariableBrick = userVariableBrick;
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);
			userVariableBrick.spinner.setSelection(userVariableBrick.userVariable);
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			userVariableBrick.spinner.setSelection(userVariableBrick.userVariable);
		}
	}
}
