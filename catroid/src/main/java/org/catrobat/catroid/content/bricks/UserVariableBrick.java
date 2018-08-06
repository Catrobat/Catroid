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
import android.content.DialogInterface;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerWithNewOption;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.recyclerview.dialog.NewVariableDialogFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class UserVariableBrick extends FormulaBrick implements NewVariableDialogFragment.NewVariableInterface,
		SpinnerWithNewOption.SpinnerSelectionListener<UserVariable> {

	protected UserVariable userVariable;

	private transient SpinnerWithNewOption<UserVariable> spinner;

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

	protected abstract int getSpinnerId();

	private List<UserVariable> getUserVariables() {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();

		List<UserVariable> variables = new ArrayList<>();
		variables.addAll(dataContainer.getSpriteUserVariables(sprite));
		variables.addAll(dataContainer.getProjectUserVariables());
		return variables;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		spinner = new SpinnerWithNewOption<>(getSpinnerId(), view, getUserVariables(), this);
		spinner.setSelection(userVariable);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public boolean onNewOptionClicked() {
		new NewVariableDialogFragment(this) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(userVariable);
			}
		}.show(((Activity) view.getContext()).getFragmentManager(), NewVariableDialogFragment.TAG);
		return false;
	}

	@Override
	public void onNewVariable(UserVariable item) {
		userVariable = item;
		spinner.add(item);
		//TODO: This should work some other way: i.e. it should not rely on the Brick being able to access its adapter.
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemSelected(UserVariable item) {
		userVariable = item;
	}
}
