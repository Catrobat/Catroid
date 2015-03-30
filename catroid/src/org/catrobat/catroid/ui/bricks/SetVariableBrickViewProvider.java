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

package org.catrobat.catroid.ui.bricks;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;

/**
 * FIXME: Same as ChangeVariableBrickViewFactory
 * Created by Illya Boyko on 03/03/15.
 */
public class SetVariableBrickViewProvider extends BrickViewProvider {
	public SetVariableBrickViewProvider(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	View createSetVariableBrickView(final SetVariableBrick brick, ViewGroup parent) {
		final View view = inflateBrickView(parent, R.layout.brick_set_variable);

		initFormulaEditView(brick, view, Brick.BrickField.VARIABLE, R.id.brick_set_variable_edit_text);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.set_variable_spinner);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getUserBrickId());

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.createDataAdapter(context, userBrickId, ProjectManager.getInstance().getCurrentSprite(), brick.isInUserBrick());
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		variableSpinner.setAdapter(userVariableAdapterWrapper);


		variableSpinner.setFocusableInTouchMode(false);
		variableSpinner.setFocusable(false);

		setSpinnerSelection(brick, variableSpinner, null);


		final NewDataDialog.NewVariableDialogListener listener = new NewDataDialog.NewVariableDialogListener() {
			@Override
			public void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable) {
				UserVariableAdapterWrapper userVariableAdapterWrapper = ((UserVariableAdapterWrapper) spinnerToUpdate
						.getAdapter());
				userVariableAdapterWrapper.notifyDataSetChanged();
				setSpinnerSelection(brick, spinnerToUpdate, newUserVariable);
			}
		};

		variableSpinner.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						&& (((Spinner) view).getSelectedItemPosition() == 0
						&& ((Spinner) view).getAdapter().getCount() == 1)) {
					NewDataDialog dialog = new NewDataDialog((Spinner) view, NewDataDialog.DialogType.USER_VARIABLE);

					dialog.addVariableDialogListener(listener);
					dialog.show(((FragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
					return true;
				}

				return false;
			}
		});
		variableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserVariableAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewDataDialog dialog = new NewDataDialog((Spinner) parent, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(listener);
					dialog.show(((FragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
				}
				((UserVariableAdapterWrapper) parent.getAdapter()).resetIsTouchInDropDownView();
				brick.setUserVariable((UserVariable) parent.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				brick.setUserVariable(null);
			}
		});

		return view;
	}

	private void setSpinnerSelection(SetVariableBrick brick, Spinner variableSpinner, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = (UserVariableAdapterWrapper) variableSpinner
				.getAdapter();

		updateUserVariableIfDeleted(brick, userVariableAdapterWrapper);

		if (brick.getUserVariable() != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(brick.getUserVariable()), true);
		} else if (newUserVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(newUserVariable), true);
			brick.setUserVariable(newUserVariable);
		} else {
			variableSpinner.setSelection(userVariableAdapterWrapper.getCount() - 1, true);
			brick.setUserVariable(userVariableAdapterWrapper.getItem(userVariableAdapterWrapper.getCount() - 1));
		}
	}

	private void updateUserVariableIfDeleted(SetVariableBrick brick, UserVariableAdapterWrapper userVariableAdapterWrapper) {
		if (brick.getUserVariable() != null && userVariableAdapterWrapper.getPositionOfItem(brick.getUserVariable()) == 0) {
			brick.setUserVariable(null);
		}
	}

}
