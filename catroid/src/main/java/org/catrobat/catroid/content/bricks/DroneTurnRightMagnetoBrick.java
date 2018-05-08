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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class DroneTurnRightMagnetoBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	public DroneTurnRightMagnetoBrick(int durationInMilliseconds, int powerInPercent) {
		initializeBrickFields(new Formula(durationInMilliseconds / 1000.0), new Formula(powerInPercent));
	}

	private void initializeBrickFields(Formula durationInSeconds, Formula powerInPercent) {
		addAllowedBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		addAllowedBrickField(BrickField.DRONE_POWER_IN_PERCENT);
		setFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS, durationInSeconds);
		setFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT, powerInPercent);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createDroneTurnRightMagnetoAction(sprite,
				getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS),
				getFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT)));
		return null;
	}

	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_drone_turn_right_magneto_edit_text_power:
				FormulaEditorFragment.showFragment(view, this, BrickField.DRONE_POWER_IN_PERCENT);
				break;
			case R.id.brick_drone_turn_right_magneto_edit_text_second:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
				break;
		}
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (animationState) {
			return null;
		}

		view = View.inflate(context, R.layout.brick_drone_turn_right_magneto, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_drone_turn_right_magneto_checkbox);

		setSecondText(view, R.id.brick_drone_turn_right_magneto_text_second, R.id.brick_drone_turn_right_magneto_edit_text_second, BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);

		TextView editTime = (TextView) view.findViewById(R.id.brick_drone_turn_right_magneto_edit_text_second);
		getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS)
				.setTextFieldId(R.id.brick_drone_turn_right_magneto_edit_text_second);
		getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS).refreshTextField(view);
		editTime.setOnClickListener(this);

		TextView editPower = (TextView) view.findViewById(R.id.brick_drone_turn_right_magneto_edit_text_power);
		getFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT)
				.setTextFieldId(R.id.brick_drone_turn_right_magneto_edit_text_power);
		getFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT).refreshTextField(view);
		editPower.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_drone_turn_right_magneto, null);
		TextView textTime = (TextView) prototypeView.findViewById(R.id
				.brick_drone_turn_right_magneto_edit_text_second);

		TextView textPower = (TextView) prototypeView.findViewById(R.id
				.brick_drone_turn_right_magneto_edit_text_power);

		textTime.setText(formatNumberForPrototypeView(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS / 1000));

		textPower.setText(formatNumberForPrototypeView(BrickValues.DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT));

		setSecondText(context, prototypeView, R.id.brick_drone_turn_right_magneto_text_second);
		return prototypeView;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}
}
