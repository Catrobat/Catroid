/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class JumpingSumoRotateLeftBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public JumpingSumoRotateLeftBrick(float degree) {
		initializeBrickFields(new Formula(degree));
	}

	private void initializeBrickFields(Formula degree) {
		addAllowedBrickField(BrickField.JUMPING_SUMO_ROTATE);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE, degree);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createJumpingSumoRotateLeftAction(sprite,
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE)));
		return null;
	}
	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_ROTATE);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_jumping_sumo_rotate_left, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_jumping_sumo_rotate_left_checkbox);

		TextView editDegree = (TextView) view.findViewById(R.id.brick_jumping_sumo_change_left_variable_edit_text);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE)
				.setTextFieldId(R.id.brick_jumping_sumo_change_left_variable_edit_text);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE).refreshTextField(view);
		editDegree.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_jumping_sumo_rotate_left, null);
		TextView textDegree = (TextView) prototypeView.findViewById(R.id
				.brick_jumping_sumo_change_left_variable_edit_text);
		textDegree.setText(String.valueOf(BrickValues.JUMPING_SUMO_ROTATE_DEFAULT_DEGREE));
		return prototypeView;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.JUMPING_SUMO;
	}
}
