/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog.NewVariableDialogListener;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SetVariableBrick extends BrickBaseType implements OnClickListener, NewVariableDialogListener {
	private static final long serialVersionUID = 1L;
	private UserVariable userVariable;
	private Formula variableFormula;
	private transient AdapterView<?> adapterView;

	public SetVariableBrick(Sprite sprite, Formula variableFormula, UserVariable userVariable) {
		this.sprite = sprite;
		this.variableFormula = variableFormula;
		this.userVariable = userVariable;
	}

	public SetVariableBrick(Sprite sprite, double value) {
		this.sprite = sprite;
		this.variableFormula = new Formula(value);
		this.userVariable = null;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
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

		TextView prototype_text = (TextView) view.findViewById(R.id.brick_set_variable_prototype_view);
		EditText edit_text = (EditText) view.findViewById(R.id.brick_set_variable_edit_text);
		prototype_text.setVisibility(View.GONE);
		variableFormula.setTextFieldId(R.id.brick_set_variable_edit_text);
		variableFormula.refreshTextField(view);
		edit_text.setVisibility(View.VISIBLE);
		edit_text.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.set_variable_spinner);
		UserVariableAdapter userVariableAdapter = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.createUserVariableAdapter(context, sprite);
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

		setSpinnerSelection(variableSpinner);

		variableSpinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (((Spinner) v).getSelectedItemPosition() == 0 && ((Spinner) v).getAdapter().getCount() == 1) {
						NewVariableDialog dialog = new NewVariableDialog((Spinner) v);
						dialog.addVariableDialogListener(SetVariableBrick.this);
						dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
								NewVariableDialog.DIALOG_FRAGMENT_TAG);
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
					NewVariableDialog dialog = new NewVariableDialog((Spinner) parent);
					dialog.addVariableDialogListener(SetVariableBrick.this);
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
		View prototypeView = View.inflate(context, R.layout.brick_set_variable, null);
		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.set_variable_spinner);
		variableSpinner.setFocusableInTouchMode(false);
		variableSpinner.setFocusable(false);
		UserVariableAdapter userVariableAdapter = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.createUserVariableAdapter(context, sprite);

		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				userVariableAdapter);

		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner);

		TextView textSetVariable = (TextView) prototypeView.findViewById(R.id.brick_set_variable_prototype_view);
		textSetVariable.setText(String.valueOf(variableFormula.interpretDouble(sprite)));

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_variable_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		TextView textSetVariable = (TextView) view.findViewById(R.id.brick_set_variable_label);
		TextView textTo = (TextView) view.findViewById(R.id.brick_set_variable_to_textview);
		EditText editVariable = (EditText) view.findViewById(R.id.brick_set_variable_edit_text);
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
		return view;
	}

	@Override
	public Brick clone() {
		SetVariableBrick clonedBrick = new SetVariableBrick(getSprite(), variableFormula.clone(), userVariable);
		return clonedBrick;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, variableFormula);
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetVariableBrick copyBrick = (SetVariableBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	private void updateUserVariableIfDeleted(UserVariableAdapterWrapper userVariableAdapterWrapper) {
		if (userVariable != null) {
			if (userVariableAdapterWrapper.getPositionOfItem(userVariable) == 0) {
				userVariable = null;
			}
		}
	}

	private void setSpinnerSelection(Spinner variableSpinner) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = (UserVariableAdapterWrapper) variableSpinner
				.getAdapter();

		updateUserVariableIfDeleted(userVariableAdapterWrapper);

		if (userVariable != null) {
			variableSpinner.setSelection(userVariableAdapterWrapper.getPositionOfItem(userVariable), true);
		} else {

			variableSpinner.setSelection(userVariableAdapterWrapper.getCount() - 1, true);
			userVariable = userVariableAdapterWrapper.getItem(userVariableAdapterWrapper.getCount() - 1);

		}
	}

	@Override
	public void onFinishNewVariableDialog(Spinner spinnerToUpdate) {
		UserVariableAdapterWrapper userVariableAdapterWrapper = ((UserVariableAdapterWrapper) spinnerToUpdate
				.getAdapter());
		userVariableAdapterWrapper.notifyDataSetChanged();
		setSpinnerSelection(spinnerToUpdate);
	}

}
