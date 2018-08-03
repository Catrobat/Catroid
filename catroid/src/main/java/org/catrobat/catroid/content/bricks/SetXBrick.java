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
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SetXBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public SetXBrick() {
		addAllowedBrickField(BrickField.X_POSITION);
	}

	public SetXBrick(int xPositionValue) {
		initializeBrickFields(new Formula(xPositionValue));
	}

	public SetXBrick(Formula xPosition) {
		initializeBrickFields(xPosition);
	}

	private void initializeBrickFields(Formula xPosition) {
		addAllowedBrickField(BrickField.X_POSITION);
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.X_POSITION).getRequiredResources();
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_x;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		TextView editX = view.findViewById(R.id.brick_set_x_edit_text);

		getFormulaWithBrickField(BrickField.X_POSITION).setTextFieldId(R.id.brick_set_x_edit_text);
		getFormulaWithBrickField(BrickField.X_POSITION).refreshTextField(view);

		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		TextView textXPosition = prototypeView.findViewById(R.id.brick_set_x_edit_text);
		textXPosition.setText(formatNumberForPrototypeView(BrickValues.X_POSITION));
		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetXAction(sprite,
				getFormulaWithBrickField(BrickField.X_POSITION)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.X_POSITION);
	}
}
