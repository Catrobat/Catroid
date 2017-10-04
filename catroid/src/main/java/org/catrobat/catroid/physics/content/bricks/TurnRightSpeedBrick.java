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
package org.catrobat.catroid.physics.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BrickViewProvider;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class TurnRightSpeedBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public TurnRightSpeedBrick() {
		addAllowedBrickField(BrickField.PHYSICS_TURN_RIGHT_SPEED);
	}

	public TurnRightSpeedBrick(float degreesPerSecond) {
		initializeBrickFields(new Formula(degreesPerSecond));
	}

	public TurnRightSpeedBrick(Formula degreesPerSecond) {
		initializeBrickFields(degreesPerSecond);
	}

	private void initializeBrickFields(Formula degreesPerSecond) {
		addAllowedBrickField(BrickField.PHYSICS_TURN_RIGHT_SPEED);
		setFormulaWithBrickField(BrickField.PHYSICS_TURN_RIGHT_SPEED, degreesPerSecond);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physics_turn_right_speed, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_turn_right_speed_checkbox);

		TextView edit = (TextView) view.findViewById(R.id.brick_turn_right_speed_edit_text);

		getFormulaWithBrickField(BrickField.PHYSICS_TURN_RIGHT_SPEED).setTextFieldId(R.id.brick_turn_right_speed_edit_text);
		getFormulaWithBrickField(BrickField.PHYSICS_TURN_RIGHT_SPEED).refreshTextField(view);

		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_physics_turn_right_speed, null);
		TextView textTurnRightSpeed = (TextView) prototypeView.findViewById(R.id.brick_turn_right_speed_edit_text);
		textTurnRightSpeed.setText(String.valueOf(BrickValues.PHYSIC_TURN_DEGREES));
		return prototypeView;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, BrickField.PHYSICS_TURN_RIGHT_SPEED);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createTurnRightSpeedAction(sprite,
				getFormulaWithBrickField(BrickField.PHYSICS_TURN_RIGHT_SPEED)));
		return null;
	}
}
