/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.content.res.ColorStateList;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class KodeySetVariableBrick extends UserVariableBrick {
	private static final long serialVersionUID = 1L;
	private UserVariable userVariable;
	private Formula variableFormula;
	private transient AdapterView<?> adapterView;
	//this is used if there should be a sensor selected by default
	private boolean isStringInPrototype = false;
	public boolean inUserBrick = false;
	private String stringInPrototype;

	public KodeySetVariableBrick(Formula variableFormula, UserVariable userVariable) {
		this.variableFormula = variableFormula;
		this.userVariable = userVariable;
	}

	public KodeySetVariableBrick(double value) {
		this.variableFormula = new Formula(value);
		this.userVariable = null;
	}

	public KodeySetVariableBrick(String value) {
		this.isStringInPrototype = true;
		this.stringInPrototype = value;
		this.variableFormula = new Formula(value);
		this.userVariable = null;

	}

	@Override
	public Formula getFormula() {
		return variableFormula;
	}

	@Override
	public int getRequiredResources() { return BLUETOOTH_KODEY;	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setVariable(sprite, variableFormula, userVariable));
		return null;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_set_variable, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_set_variable_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView prototypeText = (TextView) view.findViewById(R.id.brick_set_variable_prototype_view);
		TextView textField = (TextView) view.findViewById(R.id.brick_set_variable_edit_text);
		prototypeText.setVisibility(View.GONE);
		variableFormula.setTextFieldId(R.id.brick_set_variable_edit_text);
		variableFormula.refreshTextField(view);
		textField.setVisibility(View.VISIBLE);
		textField.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.set_variable_spinner);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getUserBrickId());

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.createDataAdapter(context, userBrickId, ProjectManager.getInstance().getCurrentSprite(), inUserBrick);
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		variableSpinner.setAdapter(userVariableAdapterWrapper);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			variableSpinner.setClickable(true);
			variableSpinner.setEnabled(true);
		} else {
			variableSpinner.setClickable(false);
			variableSpinner.setFocusable(false);
		}

		setSpinnerSelection(variableSpinner, null);

		variableSpinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (((Spinner) view).getSelectedItemPosition() == 0
							&& ((Spinner) view).getAdapter().getCount() == 1) {
						NewDataDialog dialog = new NewDataDialog((Spinner) view, NewDataDialog.DialogType.USER_VARIABLE);
						dialog.addVariableDialogListener(KodeySetVariableBrick.this);
						dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
								NewDataDialog.DIALOG_FRAGMENT_TAG);
						return true;
					}
				}
				return false;
			}
		});
		variableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserVariableAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewDataDialog dialog = new NewDataDialog((Spinner) parent, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(KodeySetVariableBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
				}
				((UserVariableAdapterWrapper) parent.getAdapter()).resetIsTouchInDropDownView();
				userVariable = (UserVariable) parent.getItemAtPosition(position);
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				userVariable = null;
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_set_variable, null);
		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.set_variable_spinner);
		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getUserBrickId());

		variableSpinner.setFocusableInTouchMode(false);
		variableSpinner.setFocusable(false);
		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.createDataAdapter(context, userBrickId, ProjectManager.getInstance().getCurrentSprite(), inUserBrick);

		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);

		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner, null);

		TextView textSetVariable = (TextView) prototypeView.findViewById(R.id.brick_set_variable_prototype_view);
		if (isStringInPrototype == false) {
			//ToDo: #Kodey check if correct
			//textSetVariable.setText(String.valueOf(variableFormula.interpretDouble(sprite)));
			textSetVariable.setText(String.valueOf(BrickValues.SET_VARIABLE));
		} else {
			if (stringInPrototype.equalsIgnoreCase(Sensors.KODEY_FRONT_LEFT.toString())) {
				textSetVariable.setText(context.getResources().getString(
						R.string.formula_editor_kodey_sensor_front_left));
			} else if (stringInPrototype.equalsIgnoreCase(Sensors.KODEY_FRONT_RIGHT.toString())) {
				textSetVariable.setText(context.getResources().getString(
						R.string.formula_editor_kodey_sensor_front_right));
			} else if (stringInPrototype.equalsIgnoreCase(Sensors.KODEY_SIDE_LEFT.toString())) {
				textSetVariable.setText(context.getResources().getString(
						R.string.formula_editor_kodey_sensor_side_left));
			} else if (stringInPrototype.equalsIgnoreCase(Sensors.KODEY_SIDE_RIGHT.toString())) {
				textSetVariable.setText(context.getResources().getString(
						R.string.formula_editor_kodey_sensor_side_right));
			} else if (stringInPrototype.equalsIgnoreCase(Sensors.KODEY_BOTTOM_LEFT.toString())) {
				textSetVariable.setText(context.getResources().getString(
						R.string.formula_editor_kodey_sensor_bottom_left));
			} else if (stringInPrototype.equalsIgnoreCase(Sensors.KODEY_BOTTOM_RIGHT.toString())) {
				textSetVariable.setText(context.getResources().getString(
						R.string.formula_editor_kodey_sensor_bottom_right));
			} else {
				textSetVariable.setText(stringInPrototype);
			}
		}
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			TextView textSetVariable = (TextView) view.findViewById(R.id.brick_set_variable_label);
			TextView textTo = (TextView) view.findViewById(R.id.brick_set_variable_to_textview);
			TextView editVariable = (TextView) view.findViewById(R.id.brick_set_variable_edit_text);
			Spinner variablebrickSpinner = (Spinner) view.findViewById(R.id.set_variable_spinner);

			ColorStateList color = textSetVariable.getTextColors().withAlpha(alphaValue);
			variablebrickSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			textSetVariable.setTextColor(textSetVariable.getTextColors().withAlpha(alphaValue));
			textTo.setTextColor(textTo.getTextColors().withAlpha(alphaValue));
			editVariable.setTextColor(editVariable.getTextColors().withAlpha(alphaValue));
			editVariable.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public Brick clone() {
		SetVariableBrick clonedBrick = new SetVariableBrick(getFormulaWithBrickField(BrickField.VARIABLE)
				.clone(), userVariable);
		return clonedBrick;
	}


	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.VARIABLE);
	}

	private void updateUserVariableIfDeleted(UserVariableAdapterWrapper userVariableAdapterWrapper) {
		if (userVariable != null) {
			if (userVariableAdapterWrapper.getPositionOfItem(userVariable) == 0) {
				userVariable = null;
			}
		}
	}

	protected void setSpinnerSelection(Spinner variableSpinner, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = (UserVariableAdapterWrapper) variableSpinner
				.getAdapter();

		updateUserVariableIfDeleted(userVariableAdapterWrapper);

		if (userVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(userVariable), true);
		} else if (newUserVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(newUserVariable), true);
			userVariable = newUserVariable;
		} else {
			variableSpinner.setSelection(userVariableAdapterWrapper.getCount() - 1, true);
			userVariable = userVariableAdapterWrapper.getItem(userVariableAdapterWrapper.getCount() - 1);
		}
	}

	@Override
	public void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = ((UserVariableAdapterWrapper) spinnerToUpdate
				.getAdapter());
		userVariableAdapterWrapper.notifyDataSetChanged();
		setSpinnerSelection(spinnerToUpdate, newUserVariable);
	}

}
