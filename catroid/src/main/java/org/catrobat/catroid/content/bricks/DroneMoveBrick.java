/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public abstract class DroneMoveBrick extends FormulaBrick {
	private static final String TAG = DroneMoveBrick.class.getSimpleName();

	protected transient View prototypeView;
	private static final long serialVersionUID = 1L;

	public DroneMoveBrick() {
		addAllowedBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		addAllowedBrickField(BrickField.DRONE_POWER_IN_PERCENT);
	}

	public DroneMoveBrick(int durationInMilliseconds, int powerInPercent) {
		initializeBrickFields(new Formula(durationInMilliseconds / 1000.0), new Formula(powerInPercent));
	}

	public DroneMoveBrick(Formula durationInSeconds, Formula powerInPercent) {
		initializeBrickFields(durationInSeconds, powerInPercent);
	}

	private void initializeBrickFields(Formula durationInSeconds, Formula powerInPercent) {
		addAllowedBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		addAllowedBrickField(BrickField.DRONE_POWER_IN_PERCENT);
		setFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS, durationInSeconds);
		setFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT, powerInPercent);
	}

	public void setPower(Formula powerInPercent) {
		setFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT, powerInPercent);
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		setFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS, timeToWaitInSeconds);
	}

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_drone_move, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_drone_move_checkbox);

		TextView editTime = (TextView) view.findViewById(R.id.brick_drone_move_edit_text_second);
		getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS)
				.setTextFieldId(R.id.brick_drone_move_edit_text_second);
		getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS).refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_drone_move_text_view_second);

		if (getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS).isSingleNumberFormula()) {
			try {
				times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS)
								.interpretDouble(ProjectManager.getInstance().getCurrentSprite()))));
			} catch (InterpretationException interpretationException) {
				Log.d(TAG, "Formula interpretation for this specific Brick failed.", interpretationException);
			}
		} else {
			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		TextView label = (TextView) view.findViewById(R.id.brick_drone_move_label);
		label.setText(getBrickLabel(view));

		editTime.setOnClickListener(this);

		TextView editPower = (TextView) view.findViewById(R.id.brick_drone_move_edit_text_power);
		getFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT)
				.setTextFieldId(R.id.brick_drone_move_edit_text_power);
		getFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT).refreshTextField(view);
		TextView textPercent = (TextView) view.findViewById(R.id.brick_set_power_to_percent);
		String textPercentString = "% ".concat(view.getResources().getString(R.string.brick_drone_percent_power));
		textPercent.setText(textPercentString);

		editPower.setOnClickListener(this);

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_drone_move, null);
		TextView label = (TextView) prototypeView.findViewById(R.id.brick_drone_move_label);
		label.setText(getBrickLabel(prototypeView));
		TextView textTime = (TextView) prototypeView.findViewById(R.id.brick_drone_move_edit_text_second);
		TextView textPercent = (TextView) prototypeView.findViewById(R.id.brick_set_power_to_percent);
		String textPercentString = "% ".concat(prototypeView.getResources().getString(R.string.brick_drone_percent_power));
		textPercent.setText(textPercentString);
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_drone_move_text_view_second);
		TextView textPower = (TextView) prototypeView.findViewById(R.id.brick_drone_move_edit_text_power);
		textTime.setText(String.valueOf(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS / 1000));

		textPower.setText(String.valueOf(BrickValues.DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT * 100));
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS / 1000)));
		return prototypeView;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_drone_move_edit_text_second:
				FormulaEditorFragment.showFragment(view, this, BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
				break;

			case R.id.brick_drone_move_edit_text_power:
				FormulaEditorFragment.showFragment(view, this, BrickField.DRONE_POWER_IN_PERCENT);
				break;
		}
	}

	protected abstract String getBrickLabel(View view);
}
