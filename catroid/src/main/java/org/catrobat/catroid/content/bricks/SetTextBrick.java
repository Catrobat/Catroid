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

public class SetTextBrick extends FormulaBrick implements View.OnClickListener {

	private static final long serialVersionUID = 1L;

	public SetTextBrick() {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.STRING);
	}

	public SetTextBrick(int xDestinationValue, int yDestinationValue, String text) {
		initializeBrickFields(new Formula(xDestinationValue), new Formula(yDestinationValue), new Formula(text));
	}

	public SetTextBrick(Formula xDestination, Formula yDestination, Formula text) {
		initializeBrickFields(xDestination, yDestination, text);
	}

	private void initializeBrickFields(Formula xDestination, Formula yDestination, Formula text) {
		addAllowedBrickField(BrickField.X_DESTINATION);
		addAllowedBrickField(BrickField.Y_DESTINATION);
		addAllowedBrickField(BrickField.STRING);

		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
		setFormulaWithBrickField(BrickField.STRING, text);
	}

	public void setText(Formula text) {
		setFormulaWithBrickField(BrickField.STRING, text);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.X_DESTINATION).getRequiredResources()
				| getFormulaWithBrickField(BrickField.Y_DESTINATION).getRequiredResources()
				| getFormulaWithBrickField(BrickField.STRING).getRequiredResources();
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_drone_set_text;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		TextView editX = view.findViewById(R.id.brick_set_text_edit_text_x);
		TextView editY = view.findViewById(R.id.brick_set_text_edit_text_y);

		getFormulaWithBrickField(BrickField.X_DESTINATION).setTextFieldId(R.id.brick_set_text_edit_text_x);
		getFormulaWithBrickField(BrickField.X_DESTINATION).refreshTextField(view);
		editX.setOnClickListener(this);

		getFormulaWithBrickField(BrickField.Y_DESTINATION).setTextFieldId(R.id.brick_set_text_edit_text_y);
		getFormulaWithBrickField(BrickField.Y_DESTINATION).refreshTextField(view);
		editY.setOnClickListener(this);

		TextView editText = view.findViewById(R.id.brick_set_text_edit_text);

		getFormulaWithBrickField(BrickField.STRING).setTextFieldId(R.id.brick_set_text_edit_text);
		getFormulaWithBrickField(BrickField.STRING).refreshTextField(view);

		editText.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);

		TextView posX = prototypeView.findViewById(R.id.brick_set_text_edit_text_x);
		TextView posY = prototypeView.findViewById(R.id.brick_set_text_edit_text_y);

		TextView text = prototypeView.findViewById(R.id.brick_set_text_edit_text);
		TextView secondText = prototypeView.findViewById(R.id.brick_set_text_seconds_text_view);

		posX.setText(formatNumberForPrototypeView(BrickValues.X_POSITION));
		posY.setText(formatNumberForPrototypeView(BrickValues.Y_POSITION));
		text.setText(BrickValues.STRING_VALUE);
		secondText.setText(BrickValues.STRING_VALUE);

		return prototypeView;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_set_text_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, BrickField.X_DESTINATION);
				break;

			case R.id.brick_set_text_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, BrickField.Y_DESTINATION);
				break;

			case R.id.brick_set_text_edit_text:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.STRING);
				break;
		}
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetTextAction(sprite, getFormulaWithBrickField(BrickField.X_DESTINATION),
				getFormulaWithBrickField(BrickField.Y_DESTINATION),
				getFormulaWithBrickField(BrickField.STRING)));
		return null;
	}
}
