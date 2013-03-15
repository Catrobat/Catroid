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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SetVariableBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private UserVariable userVariable;
	private Formula variableFormula;

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
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_set_variable, null);
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

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.variable_spinner);
		UserVariableAdapter variabeAdapter = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.createUserVariableAdapter(context, sprite.getName());
		variabeAdapter.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(variabeAdapter);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			variableSpinner.setClickable(true);
			variableSpinner.setEnabled(true);
		} else {
			variableSpinner.setClickable(false);
			variableSpinner.setFocusable(false);
		}

		if (userVariable != null) {
			variableSpinner.setSelection(variabeAdapter.getPositionOfItem(userVariable));
		}

		variableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				userVariable = (UserVariable) parent.getItemAtPosition(position);
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
		return View.inflate(context, R.layout.brick_set_variable, null);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_variable_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
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

}
