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
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class JumpingSumoMoveBackwardBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public JumpingSumoMoveBackwardBrick(int durationInMilliseconds, int powerInPercent) {
		initializeBrickFields(new Formula(durationInMilliseconds / 1000.0), new Formula(powerInPercent));
	}

	private void initializeBrickFields(Formula durationInSeconds, Formula powerInPercent) {
		addAllowedBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS);
		addAllowedBrickField(BrickField.JUMPING_SUMO_SPEED);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS, durationInSeconds);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED, powerInPercent);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createJumpingSumoMoveBackwardAction(sprite,
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS),
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED)));
		return null;
	}

	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_jumping_sumo_move_backward_edit_text_second:
				FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS);
				break;
			case R.id.brick_jumping_sumo_move_backward_edit_text_power:
				FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_SPEED);
				break;
		}
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_jumping_sumo_move_backward, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_jumping_sumo_move_backward_checkbox);

		TextView editTime = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_backward_edit_text_second);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS)
				.setTextFieldId(R.id.brick_jumping_sumo_move_backward_edit_text_second);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS).refreshTextField(view);
		editTime.setOnClickListener(this);

		TextView editPower = (TextView) view.findViewById(R.id.brick_jumping_sumo_move_backward_edit_text_power);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED)
				.setTextFieldId(R.id.brick_jumping_sumo_move_backward_edit_text_power);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_SPEED).refreshTextField(view);
		editPower.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_jumping_sumo_move_backward, null);
		TextView textTime = (TextView) prototypeView.findViewById(R.id
				.brick_jumping_sumo_move_backward_edit_text_second);

		TextView textPower = (TextView) prototypeView.findViewById(R.id
				.brick_jumping_sumo_move_backward_edit_text_power);
		textTime.setText(String.valueOf(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS / 1000));

		textPower.setText(String.valueOf(BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT));
		return prototypeView;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.JUMPING_SUMO;
	}
}
