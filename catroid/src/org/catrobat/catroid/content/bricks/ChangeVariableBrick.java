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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
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
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog.NewVariableDialogListener;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class ChangeVariableBrick extends FormulaBrick implements OnClickListener, NewVariableDialogListener {

	private static final long serialVersionUID = 1L;
	private UserVariable userVariable;
	private transient AdapterView<?> adapterView;
	public boolean inUserBrick = false;

	public ChangeVariableBrick() {
		addAllowedBrickField(BrickField.VARIABLE_CHANGE);
	}

	public ChangeVariableBrick(Formula variableFormula) {
		initializeBrickFields(variableFormula);
	}

	public ChangeVariableBrick(Formula variableFormula, UserVariable userVariable) {
		initializeBrickFields(variableFormula);
		this.userVariable = userVariable;
	}

	public ChangeVariableBrick(double value) {
		initializeBrickFields(new Formula(value));
	}

	public ChangeVariableBrick(Formula variableFormula, UserVariable userVariable, boolean inUserBrick) {
		initializeBrickFields(variableFormula);
		this.userVariable = userVariable;
		this.inUserBrick = inUserBrick;
	}

	private void initializeBrickFields(Formula variableFormula) {
		addAllowedBrickField(BrickField.VARIABLE_CHANGE);
		setFormulaWithBrickField(BrickField.VARIABLE_CHANGE, variableFormula);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.VARIABLE_CHANGE).getRequiredResources();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_change_variable_by, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_change_variable_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView prototypeText = (TextView) view.findViewById(R.id.brick_change_variable_prototype_view);
		TextView textField = (TextView) view.findViewById(R.id.brick_change_variable_edit_text);
		prototypeText.setVisibility(View.GONE);
		getFormulaWithBrickField(BrickField.VARIABLE_CHANGE).setTextFieldId(R.id.brick_change_variable_edit_text);
		getFormulaWithBrickField(BrickField.VARIABLE_CHANGE).refreshTextField(view);
		textField.setVisibility(View.VISIBLE);
		textField.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.change_variable_spinner);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getUserBrickId());

		UserVariableAdapter userVariableAdapter = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.createUserVariableAdapter(context, userBrickId, ProjectManager.getInstance().getCurrentSprite(), inUserBrick);
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				userVariableAdapter);
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
				if (event.getAction() == MotionEvent.ACTION_UP && ((Spinner) view).getSelectedItemPosition() == 0
						&& ((Spinner) view).getAdapter().getCount() == 1) {
					NewVariableDialog dialog = new NewVariableDialog((Spinner) view);
					dialog.addVariableDialogListener(ChangeVariableBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewVariableDialog.DIALOG_FRAGMENT_TAG);
					return true;

				}
				return false;
			}
		});

		variableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserVariableAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewVariableDialog dialog = new NewVariableDialog((Spinner) parent);
					dialog.addVariableDialogListener(ChangeVariableBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewVariableDialog.DIALOG_FRAGMENT_TAG);
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
		View prototypeView = View.inflate(context, R.layout.brick_change_variable_by, null);
		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.change_variable_spinner);
		variableSpinner.setFocusableInTouchMode(false);
		variableSpinner.setFocusable(false);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getDefinitionBrick().getUserBrickId());

		UserVariableAdapter changeVariableSpinnerAdapter = ProjectManager.getInstance().getCurrentProject()
				.getUserVariables().createUserVariableAdapter(context, userBrickId, ProjectManager.getInstance().getCurrentSprite(), inUserBrick);

		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				changeVariableSpinnerAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner, null);

		TextView textChangeVariable = (TextView) prototypeView.findViewById(R.id.brick_change_variable_prototype_view);
        textChangeVariable.setText(String.valueOf(BrickValues.CHANGE_VARIABLE));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_change_variable_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textSetVariable = (TextView) view.findViewById(R.id.brick_change_variable_label);
			TextView textTo = (TextView) view.findViewById(R.id.brick_change_variable_by);
			TextView editVariable = (TextView) view.findViewById(R.id.brick_change_variable_edit_text);
			Spinner variablebrickSpinner = (Spinner) view.findViewById(R.id.change_variable_spinner);

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
		ChangeVariableBrick clonedBrick = new ChangeVariableBrick(getFormulaWithBrickField(
				BrickField.VARIABLE_CHANGE).clone(), userVariable, inUserBrick);
		return clonedBrick;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.VARIABLE_CHANGE));
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.changeVariable(sprite, getFormulaWithBrickField(BrickField.VARIABLE_CHANGE),
				userVariable));
		return null;
	}

	@Override
	public Brick copyBrickForSprite(Sprite cloneSprite) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null) {
			throw new RuntimeException("The current project must be set before cloning it");
		}

		ChangeVariableBrick copyBrick = (ChangeVariableBrick) clone();
		copyBrick.userVariable = currentProject.getUserVariables().getUserVariable(userVariable.getName(), cloneSprite);
		return copyBrick;
	}

	private void updateUserVariableIfDeleted(UserVariableAdapterWrapper userVariableAdapterWrapper) {
		if (userVariable != null && userVariableAdapterWrapper.getPositionOfItem(userVariable) == 0) {
			userVariable = null;
		}
	}

	private void setSpinnerSelection(Spinner variableSpinner, UserVariable newUserVariable) {
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

	public void setInUserBrick(boolean inUserBrick) {
		this.inUserBrick = inUserBrick;
	}
}
