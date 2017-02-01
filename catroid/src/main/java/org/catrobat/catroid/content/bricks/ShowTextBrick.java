/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ShowTextBrick extends UserVariableBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	public String userVariableName;

	public static final String TAG = ShowTextBrick.class.getSimpleName();

	public ShowTextBrick() {
		addAllowedBrickField(BrickField.X_POSITION);
		addAllowedBrickField(BrickField.Y_POSITION);
	}

	public ShowTextBrick(int xPosition, int yPosition) {
		initializeBrickFields(new Formula(xPosition), new Formula(yPosition));
	}

	public ShowTextBrick(Formula xPosition, Formula yPosition) {
		initializeBrickFields(xPosition, yPosition);
	}

	private void initializeBrickFields(Formula xPosition, Formula yPosition) {
		addAllowedBrickField(BrickField.X_POSITION);
		addAllowedBrickField(BrickField.Y_POSITION);
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
	}

	public void setXPosition(Formula xPosition) {
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
	}

	public void setYPosition(Formula yPosition) {
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_show_variable_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, BrickField.X_POSITION);
				break;

			case R.id.brick_show_variable_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, BrickField.Y_POSITION);
				break;
		}
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.Y_POSITION).getRequiredResources() | getFormulaWithBrickField(
				BrickField.X_POSITION).getRequiredResources();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_show_variable, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_show_variable_checkbox);

		TextView editTextX = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).setTextFieldId(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).refreshTextField(view);

		editTextX.setOnClickListener(this);

		TextView editTextY = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).setTextFieldId(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).refreshTextField(view);

		editTextY.setOnClickListener(this);

		Spinner showVariableSpinner = (Spinner) view.findViewById(R.id.show_variable_spinner);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentScene().getDataContainer()
				.createDataAdapter(context, currentBrick, ProjectManager.getInstance().getCurrentSprite());
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		showVariableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(showVariableSpinner, null);

		showVariableSpinner.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN
						&& (((Spinner) view).getSelectedItemPosition() == 0
						&& ((Spinner) view).getAdapter().getCount() == 1)) {
					NewDataDialog dialog = new NewDataDialog((Spinner) view, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(ShowTextBrick.this);
					dialog.show(((Activity) view.getContext()).getFragmentManager(), NewDataDialog.DIALOG_FRAGMENT_TAG);
					return true;
				}

				return false;
			}
		});

		showVariableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserVariableAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewDataDialog dialog = new NewDataDialog((Spinner) parent, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(ShowTextBrick.this);
					int spinnerPos = ((UserVariableAdapterWrapper) parent.getAdapter())
							.getPositionOfItem(userVariable);
					dialog.setUserVariableIfCancel(spinnerPos);
					dialog.show(((Activity) view.getContext()).getFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
				}
				((UserVariableAdapterWrapper) parent.getAdapter()).resetIsTouchInDropDownView();
				userVariable = (UserVariable) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				userVariable = (UserVariable) arg0.getItemAtPosition(1);
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_show_variable, null);
		TextView textViewX = (TextView) prototypeView.findViewById(R.id.brick_show_variable_edit_text_x);
		textViewX.setText(Utils.getNumberStringForBricks(BrickValues.X_POSITION));
		TextView textViewY = (TextView) prototypeView.findViewById(R.id.brick_show_variable_edit_text_y);
		textViewY.setText(Utils.getNumberStringForBricks(BrickValues.Y_POSITION));
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (userVariable == null || userVariable.getName() == null) {
			userVariable = new UserVariable("NoVariableSet", Constants.NO_VARIABLE_SELECTED);
			userVariable.setDummy(true);
		}
		sequence.addAction(sprite.getActionFactory().createShowVariableAction(sprite, getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION), userVariable));
		return null;
	}

	void setUserVariableName(UserVariable userVariable) {
		if (userVariable.getName() != null) {
			userVariableName = userVariable.getName();
		} else {
			userVariableName = Constants.NO_VARIABLE_SELECTED;
		}
	}

	void setUserVariableName(String userVariableName) {
		this.userVariableName = userVariableName;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
		super.updateUserVariableReference(into, from);
	}
}
