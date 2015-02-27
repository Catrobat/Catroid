/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public abstract class DroneMoveBrick extends FormulaBrick implements OnClickListener {

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

	protected abstract String getBrickLabel(View view);

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_drone_move, null);

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
				Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
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

		editPower.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		if (!clickAllowed()) {
			return;
		}

		switch (view.getId()) {
			case R.id.brick_drone_move_edit_text_second:
				FormulaEditorFragment.showFragment(view, this,
						getFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS));
				break;

			case R.id.brick_drone_move_edit_text_power:
				FormulaEditorFragment.showFragment(view, this,
						getFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT));
				break;

			default:
				return;
		}
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}
}
