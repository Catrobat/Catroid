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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SetVariableBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private UserVariable userVariable;
	private transient View view;
	private Formula variable_formula;

	public SetVariableBrick(Sprite sprite, Formula variable_formula) {
		this.sprite = sprite;
		this.variable_formula = variable_formula;
	}

	public SetVariableBrick(Sprite sprite, double value) {
		this.sprite = sprite;
		this.variable_formula = new Formula(Double.toString(value));
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		if (userVariable == null) {
			return;
		}
		double value = variable_formula.interpretFloat();
		userVariable.setValue(value);
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_set_variable, null);

		TextView prototype_text = (TextView) view.findViewById(R.id.brick_set_variable_prototype_view);
		EditText edit_text = (EditText) view.findViewById(R.id.brick_set_variable_edit_text);
		prototype_text.setVisibility(View.GONE);
		variable_formula.setTextFieldId(R.id.brick_set_variable_edit_text);
		variable_formula.refreshTextField(view);
		edit_text.setVisibility(View.VISIBLE);
		edit_text.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.variable_spinner);
		variableSpinner.setAdapter(createUserVariableAdapter(context));
		variableSpinner.setClickable(true);
		variableSpinner.setFocusable(true);

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

	private ArrayAdapter<?> createUserVariableAdapter(Context context) {
		ArrayAdapter<UserVariable> arrayAdapter = new ArrayAdapter<UserVariable>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		UserVariablesContainer userVariablesContainer = ProjectManager.getInstance().getCurrentProject()
				.getUserVariables();
		for (UserVariable userVariable : userVariablesContainer.getUserVariables(sprite.getName())) {
			arrayAdapter.add(userVariable);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_variable, null);
	}

	@Override
	public Brick clone() {
		SetVariableBrick clonedBrick = new SetVariableBrick(getSprite(), variable_formula);
		return clonedBrick;
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, variable_formula);
	}

}
