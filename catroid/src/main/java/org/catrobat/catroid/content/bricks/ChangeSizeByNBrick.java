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

public class ChangeSizeByNBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	public ChangeSizeByNBrick() {
		addAllowedBrickField(BrickField.SIZE_CHANGE);
	}

	public ChangeSizeByNBrick(double sizeValue) {
		initializeBrickFields(new Formula(sizeValue));
	}

	public ChangeSizeByNBrick(Formula size) {
		initializeBrickFields(size);
	}

	private void initializeBrickFields(Formula size) {
		addAllowedBrickField(BrickField.SIZE_CHANGE);
		setFormulaWithBrickField(BrickField.SIZE_CHANGE, size);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.SIZE_CHANGE).getRequiredResources();
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_change_size_by_n;
	}

	@Override
	public View onCreateView(Context context) {

		super.onCreateView(context);

		TextView edit = view.findViewById(R.id.brick_change_size_by_edit_text);
		getFormulaWithBrickField(BrickField.SIZE_CHANGE).setTextFieldId(R.id.brick_change_size_by_edit_text);
		getFormulaWithBrickField(BrickField.SIZE_CHANGE).refreshTextField(view);

		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		TextView textChangeSizeBy = prototypeView.findViewById(R.id.brick_change_size_by_edit_text);
		textChangeSizeBy.setText(formatNumberForPrototypeView(BrickValues.CHANGE_SIZE_BY));
		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createChangeSizeByNAction(sprite,
				getFormulaWithBrickField(BrickField.SIZE_CHANGE)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.SIZE_CHANGE);
	}
}
