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

public class SetColorBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public SetColorBrick() {
		addAllowedBrickField(BrickField.COLOR);
	}

	public SetColorBrick(Float color) {
		initializeBrickField(new Formula(color));
	}

	public SetColorBrick(Formula color) {
		initializeBrickField(color);
	}

	private void initializeBrickField(Formula color) {
		addAllowedBrickField(BrickField.COLOR);
		setFormulaWithBrickField(BrickField.COLOR, color);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.COLOR).getRequiredResources();
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_color_to;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		TextView edit = view.findViewById(R.id.brick_set_color_edit_text);
		getFormulaWithBrickField(BrickField.COLOR).setTextFieldId(R.id.brick_set_color_edit_text);
		getFormulaWithBrickField(BrickField.COLOR).refreshTextField(view);

		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		TextView textSetSizeTo = prototypeView.findViewById(R.id.brick_set_color_edit_text);
		textSetSizeTo.setText(formatNumberForPrototypeView(BrickValues.SET_COLOR_TO));
		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetColorAction(sprite,
				getFormulaWithBrickField(BrickField.COLOR)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.COLOR);
	}
}
