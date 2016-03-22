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

package org.catrobat.catroid.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;

public class AdvancedConfigSeekbar {

	private final FormulaBrick formulaBrick;

	private final Brick.BrickField altitudeLimit;
	private final Brick.BrickField verticalSpeedLimit;
	private final Brick.BrickField rotationSpeedLimit;
	private final Brick.BrickField tiltAngleLimit;

	private TextView formulaEditorEditTextAltitude;
	private TextView formulaEditorEditTextVerticalSpeed;
	private TextView formulaEditorEditTextRotationSpeed;
	private TextView formulaEditorEditTextTiltAngle;

	private View seekbarView;

	public AdvancedConfigSeekbar(FormulaBrick formulaBrick, Brick.BrickField altitudeLimit,
			Brick.BrickField verticalSpeedLimit, Brick.BrickField rotationSpeedLimit,
			Brick.BrickField tiltAngleLimit) {
		this.formulaBrick = formulaBrick;
		this.altitudeLimit = altitudeLimit;
		this.verticalSpeedLimit = verticalSpeedLimit;
		this.rotationSpeedLimit = rotationSpeedLimit;
		this.tiltAngleLimit = tiltAngleLimit;
	}

	public View getView(Context context) {
		seekbarView = View.inflate(context, R.layout.fragment_drone_config_cooser, null);

		seekbarView.setFocusableInTouchMode(true);
		seekbarView.requestFocus();

		formulaEditorEditTextAltitude = (TextView) seekbarView.findViewById(R.id.altitude_limit_value);
		formulaBrick.getFormulaWithBrickField(altitudeLimit).setTextFieldId(R.id.altitude_limit_value);
		formulaBrick.getFormulaWithBrickField(altitudeLimit).refreshTextField(seekbarView);

		formulaEditorEditTextRotationSpeed = (TextView) seekbarView.findViewById(R.id.vertical_speed_limit_value);
		formulaBrick.getFormulaWithBrickField(verticalSpeedLimit).setTextFieldId(R.id.vertical_speed_limit_value);
		formulaBrick.getFormulaWithBrickField(verticalSpeedLimit).refreshTextField(seekbarView);

		formulaEditorEditTextVerticalSpeed = (TextView) seekbarView.findViewById(R.id.rotation_speed_limit_value);
		formulaBrick.getFormulaWithBrickField(rotationSpeedLimit).setTextFieldId(R.id.rotation_speed_limit_value);
		formulaBrick.getFormulaWithBrickField(rotationSpeedLimit).refreshTextField(seekbarView);

		formulaEditorEditTextTiltAngle = (TextView) seekbarView.findViewById(R.id.tilt_angle_limit_value);
		formulaBrick.getFormulaWithBrickField(tiltAngleLimit).setTextFieldId(R.id.tilt_angle_limit_value);
		formulaBrick.getFormulaWithBrickField(tiltAngleLimit).refreshTextField(seekbarView);

		SeekBar altitudeSeekBar = (SeekBar) seekbarView.findViewById(R.id.altitude_limit_seekbar);
		SeekBar verticalSpeedSeekBar = (SeekBar) seekbarView.findViewById(R.id.vertical_speed_limit_seekbar);
		SeekBar rotationSpeedSeekBar = (SeekBar) seekbarView.findViewById(R.id.rotation_speed_limit_seekbar);
		SeekBar tiltAngleSeekBar = (SeekBar) seekbarView.findViewById(R.id.tilt_angle_limit_seekbar);

		altitudeSeekBar.setMax(BrickValues.DRONE_ALTITUDE_MAX);
		verticalSpeedSeekBar.setMax(BrickValues.DRONE_VERTICAL_MAX);
		rotationSpeedSeekBar.setMax(BrickValues.DRONE_ROTATION_MAX);
		tiltAngleSeekBar.setMax(BrickValues.DRONE_TILT_MAX);

		altitudeSeekBar.setProgress(getCurrentBrickFieldValue(altitudeLimit));
		verticalSpeedSeekBar.setProgress(getCurrentBrickFieldValue(verticalSpeedLimit));
		rotationSpeedSeekBar.setProgress(getCurrentBrickFieldValue(rotationSpeedLimit));
		tiltAngleSeekBar.setProgress(getCurrentBrickFieldValue(tiltAngleLimit));

		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				switch (seekBar.getId()) {
					case R.id.altitude_limit_seekbar:
						if (seekBar.getProgress() + BrickValues.DRONE_ALTITUDE_MIN < BrickValues.DRONE_ALTITUDE_MAX) {
							formulaEditorEditTextAltitude.setText(String.valueOf(seekBar.getProgress() + BrickValues.DRONE_ALTITUDE_MIN));
						} else {
							formulaEditorEditTextAltitude.setText(String.valueOf(BrickValues.DRONE_ALTITUDE_MAX));
						}
						break;
					case R.id.vertical_speed_limit_seekbar:
						if (seekBar.getProgress() + BrickValues.DRONE_VERTICAL_MIN < BrickValues.DRONE_VERTICAL_MAX) {
							formulaEditorEditTextRotationSpeed.setText(String.valueOf(seekBar.getProgress() + BrickValues.DRONE_VERTICAL_MIN));
						} else {
							formulaEditorEditTextRotationSpeed.setText(String.valueOf(BrickValues.DRONE_VERTICAL_MAX));
						}
						break;
					case R.id.rotation_speed_limit_seekbar:
						if (seekBar.getProgress() + BrickValues.DRONE_ROTATION_MIN < BrickValues.DRONE_ROTATION_MAX) {
							formulaEditorEditTextVerticalSpeed.setText(String.valueOf(seekBar.getProgress() + BrickValues.DRONE_ROTATION_MIN));
						} else {
							formulaEditorEditTextVerticalSpeed.setText(String.valueOf(BrickValues.DRONE_ROTATION_MAX));
						}
						break;
					case R.id.tilt_angle_limit_seekbar:
						if (seekBar.getProgress() + BrickValues.DRONE_TILT_MIN < BrickValues.DRONE_TILT_MAX) {
							formulaEditorEditTextTiltAngle.setText(String.valueOf(seekBar.getProgress() + BrickValues.DRONE_TILT_MIN));
						} else {
							formulaEditorEditTextTiltAngle.setText(String.valueOf(BrickValues.DRONE_TILT_MAX));
						}
						break;
					default:
						break;
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				Brick.BrickField changedBrickField = null;

				switch (seekBar.getId()) {
					case R.id.altitude_limit_seekbar:
						FormulaEditorFragment.changeInputField(seekbarView, altitudeLimit);
						changedBrickField = altitudeLimit;
						if (seekBar.getProgress() < BrickValues.DRONE_ALTITUDE_MIN) {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(BrickValues.DRONE_ALTITUDE_MIN));
						} else if (seekBar.getProgress() + BrickValues.DRONE_ALTITUDE_MIN > BrickValues.DRONE_ALTITUDE_MAX) {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(BrickValues.DRONE_ALTITUDE_MAX));
						} else {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(seekBar.getProgress() + BrickValues.DRONE_ALTITUDE_MIN));
						}
						break;
					case R.id.vertical_speed_limit_seekbar:
						FormulaEditorFragment.changeInputField(seekbarView, verticalSpeedLimit);
						changedBrickField = verticalSpeedLimit;
						if (seekBar.getProgress() + BrickValues.DRONE_VERTICAL_MIN < BrickValues.DRONE_VERTICAL_MAX) {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(seekBar.getProgress() + BrickValues.DRONE_VERTICAL_MIN));
						} else {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(BrickValues.DRONE_VERTICAL_MAX));
						}
						break;
					case R.id.rotation_speed_limit_seekbar:
						FormulaEditorFragment.changeInputField(seekbarView, rotationSpeedLimit);
						changedBrickField = rotationSpeedLimit;
						if (seekBar.getProgress() + BrickValues.DRONE_ROTATION_MIN < BrickValues.DRONE_ROTATION_MAX) {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(seekBar.getProgress() + BrickValues.DRONE_ROTATION_MIN));
						} else {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(BrickValues.DRONE_ROTATION_MAX));
						}
						break;
					case R.id.tilt_angle_limit_seekbar:
						FormulaEditorFragment.changeInputField(seekbarView, tiltAngleLimit);
						changedBrickField = tiltAngleLimit;
						if (seekBar.getProgress() + BrickValues.DRONE_TILT_MIN < BrickValues.DRONE_TILT_MAX) {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(seekBar.getProgress() + BrickValues.DRONE_TILT_MIN));
						} else {
							FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(BrickValues.DRONE_TILT_MAX));
						}
						break;
					default:
						break;
				}
//                FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(seekBar.getProgress()));
				// ToDo: this is a hack for saving the value immediately
				FormulaEditorFragment.changeInputField(seekbarView, getOtherField(changedBrickField));
				FormulaEditorFragment.changeInputField(seekbarView, changedBrickField);
				// end of hack
			}
		};

		altitudeSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		verticalSpeedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		rotationSpeedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		tiltAngleSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

		return seekbarView;
	}

	private Brick.BrickField getOtherField(Brick.BrickField brickField) {
		if (brickField == rotationSpeedLimit) {
			return altitudeLimit;
		}
		return rotationSpeedLimit;
	}

	private int getCurrentBrickFieldValue(Brick.BrickField brickField) {
		String stringValue = formulaBrick.getFormulaWithBrickField(brickField)
				.getDisplayString(seekbarView.getContext());
		return Double.valueOf(stringValue.replace(",", ".")).intValue();
	}
}
