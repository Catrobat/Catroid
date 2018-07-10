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
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class ShowTextBrick extends UserVariableBrick {

	public static final String TAG = ShowTextBrick.class.getSimpleName();
	private static final long serialVersionUID = 1L;

	public ShowTextBrick() {
		addAllowedBrickField(BrickField.X_POSITION);
		addAllowedBrickField(BrickField.Y_POSITION);
	}

	public ShowTextBrick(int xPosition, int yPosition) {
		initializeBrickFields(new Formula(xPosition), new Formula(yPosition));
	}

	private void initializeBrickFields(Formula xPosition, Formula yPosition) {
		addAllowedBrickField(BrickField.X_POSITION);
		addAllowedBrickField(BrickField.Y_POSITION);
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_show_variable_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, BrickField.Y_POSITION);
				break;

			case R.id.brick_show_variable_edit_text_x:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.X_POSITION);
				break;
		}
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.Y_POSITION).getRequiredResources() | getFormulaWithBrickField(
				BrickField.X_POSITION).getRequiredResources();
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_show_variable;
	}

	@Override
	public View getView(final Context context, BrickAdapter brickAdapter) {
		super.getView(context, brickAdapter);

		TextView editTextX = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).setTextFieldId(R.id.brick_show_variable_edit_text_x);
		getFormulaWithBrickField(BrickField.X_POSITION).refreshTextField(view);

		editTextX.setOnClickListener(this);

		TextView editTextY = (TextView) view.findViewById(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).setTextFieldId(R.id.brick_show_variable_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_POSITION).refreshTextField(view);

		editTextY.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.show_variable_spinner);

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner, null);

		variableSpinner.setOnTouchListener(createSpinnerOnTouchListener());
		variableSpinner.setOnItemSelectedListener(createVariableSpinnerItemSelectedListener());

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);

		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.show_variable_spinner);

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());

		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);

		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner, null);

		TextView textViewPositionX = (TextView) prototypeView.findViewById(R.id.brick_show_variable_edit_text_x);
		textViewPositionX.setText(formatNumberForPrototypeView(BrickValues.X_POSITION));
		TextView textViewPositionY = (TextView) prototypeView.findViewById(R.id.brick_show_variable_edit_text_y);
		textViewPositionY.setText(formatNumberForPrototypeView(BrickValues.Y_POSITION));

		return prototypeView;
	}

	@Override
	public void onNewVariable(UserVariable userVariable) {
		Spinner spinner = view.findViewById(R.id.show_variable_spinner);
		setSpinnerSelection(spinner, userVariable);
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (userVariable == null || userVariable.getName() == null) {
			userVariable = new UserVariable("NoVariableSet", Constants.NO_VARIABLE_SELECTED);
			userVariable.setDummy(true);
		}
		sequence.addAction(sprite.getActionFactory().createShowVariableAction(sprite, getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION), userVariable));
		return null;
	}
}
