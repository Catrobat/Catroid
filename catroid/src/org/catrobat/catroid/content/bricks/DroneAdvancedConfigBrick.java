/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/caltitudeits>)
 *
 * This program is free software: you can altitudeistribute it and/or modify
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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.AdvancedConfigSeekbar;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class DroneAdvancedConfigBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient AdvancedConfigSeekbar advancedConfigSeekbar = new AdvancedConfigSeekbar(this, BrickField.DRONE_ALTITUDE_LIMIT,
			BrickField.DRONE_VERTICAL_SPEED_MAX, BrickField.DRONE_ROTATION_MAX, BrickField.DRONE_TILT_ANGLE);

	public DroneAdvancedConfigBrick() {
		addAllowedBrickField(BrickField.DRONE_ALTITUDE_LIMIT);
		addAllowedBrickField(BrickField.DRONE_VERTICAL_SPEED_MAX);
		addAllowedBrickField(BrickField.DRONE_ROTATION_MAX);
		addAllowedBrickField(BrickField.DRONE_TILT_ANGLE);
	}

	public DroneAdvancedConfigBrick(Formula altitude, Formula vertical, Formula rotation, Formula tilt) {
		initializeBrickFields(altitude, vertical, rotation, tilt);
	}

	private void initializeBrickFields(Formula altitude, Formula vertical, Formula rotation, Formula tilt) {
		addAllowedBrickField(BrickField.DRONE_ALTITUDE_LIMIT);
		addAllowedBrickField(BrickField.DRONE_VERTICAL_SPEED_MAX);
		addAllowedBrickField(BrickField.DRONE_ROTATION_MAX);
		addAllowedBrickField(BrickField.DRONE_TILT_ANGLE);
		setFormulaWithBrickField(BrickField.DRONE_ALTITUDE_LIMIT, altitude);
		setFormulaWithBrickField(BrickField.DRONE_VERTICAL_SPEED_MAX, vertical);
		setFormulaWithBrickField(BrickField.DRONE_ROTATION_MAX, rotation);
		setFormulaWithBrickField(BrickField.DRONE_TILT_ANGLE, tilt);
	}

	@Override
	public int getRequiredResources() {
		return ARDRONE_SUPPORT;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_drone_advanced_config, null);

		TextView textValueAltitude = (TextView) prototypeView.findViewById(R.id.brick_drone_advanced_config_altitude_prototype_text_view);
		textValueAltitude.setText(String.valueOf(BrickValues.DRONE_ALTITUDE_DEFAULT));

		TextView textValueVertical = (TextView) prototypeView.findViewById(R.id.brick_drone_advanced_config_vertical_speed_prototype_text_view);
		textValueVertical.setText(String.valueOf(BrickValues.DRONE_VERTICAL_DEFAULT));

		TextView textValueRotation = (TextView) prototypeView.findViewById(R.id.brick_drone_advanced_config_rotation_prototype_text_view);
		textValueRotation.setText(String.valueOf(BrickValues.DRONE_ROTATION_DEFAULT));

		TextView textValueTilt = (TextView) prototypeView.findViewById(R.id.brick_drone_advanced_config_tilt_prototype_text_view);
		textValueTilt.setText(String.valueOf(BrickValues.DRONE_TILT_DEFAULT));

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new DroneAdvancedConfigBrick(getFormulaWithBrickField(BrickField.DRONE_ALTITUDE_LIMIT).clone(),
				getFormulaWithBrickField(BrickField.DRONE_VERTICAL_SPEED_MAX).clone(),
				getFormulaWithBrickField(BrickField.DRONE_ROTATION_MAX).clone(),
				getFormulaWithBrickField(BrickField.DRONE_TILT_ANGLE).clone());
	}

	@Override
	public View getCustomView(Context context, int brickId, BaseAdapter baseAdapter) {
		return advancedConfigSeekbar.getView(context);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {

		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_drone_advanced_config, null);
		setCheckboxView(R.id.brick_drone_advanced_config_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textAltitude = (TextView) view.findViewById(R.id.brick_drone_advanced_config_altitude_prototype_text_view);
		TextView editAltitudeValue = (TextView) view.findViewById(R.id.brick_drone_advanced_config_altitude_edit_text);
//        editAltitudeValue.setText(String.valueOf(BrickValues.DRONE_ALTITUDE_DEFAULT));
		getFormulaWithBrickField(BrickField.DRONE_ALTITUDE_LIMIT).setTextFieldId(R.id.brick_drone_advanced_config_altitude_edit_text);
		getFormulaWithBrickField(BrickField.DRONE_ALTITUDE_LIMIT).refreshTextField(view);

		textAltitude.setVisibility(View.GONE);
		editAltitudeValue.setVisibility(View.VISIBLE);

		editAltitudeValue.setOnClickListener(this);

		TextView textVertical = (TextView) view.findViewById(R.id.brick_drone_advanced_config_vertical_speed_prototype_text_view);
		TextView editVerticalValue = (TextView) view.findViewById(R.id.brick_drone_advanced_config_vertical_speed_edit_text);
//        editVerticalValue.setText(String.valueOf(BrickValues.DRONE_VERTICAL_DEFAULT));
		getFormulaWithBrickField(BrickField.DRONE_VERTICAL_SPEED_MAX).setTextFieldId(R.id.brick_drone_advanced_config_vertical_speed_edit_text);
		getFormulaWithBrickField(BrickField.DRONE_VERTICAL_SPEED_MAX).refreshTextField(view);

		textVertical.setVisibility(View.GONE);
		editVerticalValue.setVisibility(View.VISIBLE);

		editVerticalValue.setOnClickListener(this);

		TextView textRotation = (TextView) view.findViewById(R.id.brick_drone_advanced_config_rotation_prototype_text_view);
		TextView editRotationValue = (TextView) view.findViewById(R.id.brick_drone_advanced_config_rotation_edit_text);
//        editRotationValue.setText(String.valueOf(BrickValues.DRONE_ROTATION_DEFAULT));
		getFormulaWithBrickField(BrickField.DRONE_ROTATION_MAX).setTextFieldId(R.id.brick_drone_advanced_config_rotation_edit_text);
		getFormulaWithBrickField(BrickField.DRONE_ROTATION_MAX).refreshTextField(view);

		textRotation.setVisibility(View.GONE);
		editRotationValue.setVisibility(View.VISIBLE);

		editRotationValue.setOnClickListener(this);

		TextView textTilt = (TextView) view.findViewById(R.id.brick_drone_advanced_config_tilt_prototype_text_view);
		TextView editTiltValue = (TextView) view.findViewById(R.id.brick_drone_advanced_config_tilt_edit_text);
//        editTiltValue.setText(String.valueOf(BrickValues.DRONE_TILT_DEFAULT));
		getFormulaWithBrickField(BrickField.DRONE_TILT_ANGLE).setTextFieldId(R.id.brick_drone_advanced_config_tilt_edit_text);
		getFormulaWithBrickField(BrickField.DRONE_TILT_ANGLE).refreshTextField(view);

		textTilt.setVisibility(View.GONE);
		editTiltValue.setVisibility(View.VISIBLE);

		editTiltValue.setOnClickListener(this);

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {

		BrickField clickedBrickField = getClickedBrickField(view);
		if (clickedBrickField == null) {
			return;
		}

		FormulaEditorFragment.showCustomFragment(view, this, getClickedBrickField(view));
	}

	private BrickField getClickedBrickField(View view) {
		switch (view.getId()) {
			case R.id.brick_drone_advanced_config_altitude_edit_text:
				return BrickField.DRONE_ALTITUDE_LIMIT;
			case R.id.brick_drone_advanced_config_vertical_speed_edit_text:
				return BrickField.DRONE_VERTICAL_SPEED_MAX;
			case R.id.brick_drone_advanced_config_rotation_edit_text:
				return BrickField.DRONE_ROTATION_MAX;
			case R.id.brick_drone_advanced_config_tilt_edit_text:
				return BrickField.DRONE_TILT_ANGLE;
		}

		return null;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_drone_advanced_config_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textPhiroProLabel = (TextView) view.findViewById(R.id.brick_drone_advanced_config_label);
			TextView textPhiroProEyeRed = (TextView) view.findViewById(R.id.brick_drone_advanced_config_altitude_text_view);
			TextView editRed = (TextView) view.findViewById(R.id.brick_drone_advanced_config_altitude_edit_text);

			//altitude
			textPhiroProLabel.setTextColor(textPhiroProLabel.getTextColors().withAlpha(alphaValue));
			textPhiroProEyeRed.setTextColor(textPhiroProEyeRed.getTextColors().withAlpha(alphaValue));
			editRed.setTextColor(editRed.getTextColors().withAlpha(alphaValue));
			editRed.getBackground().setAlpha(alphaValue);

			//vertical
			TextView textPhiroProEyeGreen = (TextView) view.findViewById(R.id.brick_drone_advanced_config_vertical_speed_text_view);
			TextView editGreen = (TextView) view.findViewById(R.id.brick_drone_advanced_config_vertical_speed_edit_text);
			editGreen.setTextColor(editGreen.getTextColors().withAlpha(alphaValue));
			editGreen.getBackground().setAlpha(alphaValue);
			textPhiroProEyeGreen.setTextColor(textPhiroProEyeGreen.getTextColors().withAlpha(alphaValue));

			//rotation
			TextView textPhiroProEyeBlue = (TextView) view.findViewById(R.id.brick_drone_advanced_config_rotation_text_view);
			TextView editBlue = (TextView) view.findViewById(R.id.brick_drone_advanced_config_rotation_edit_text);
			editBlue.setTextColor(editGreen.getTextColors().withAlpha(alphaValue));
			editBlue.getBackground().setAlpha(alphaValue);
			textPhiroProEyeBlue.setTextColor(textPhiroProEyeBlue.getTextColors().withAlpha(alphaValue));

			this.alphaValue = (alphaValue);
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.droneSetAltitudeAction(sprite,
				getFormulaWithBrickField(BrickField.DRONE_ALTITUDE_LIMIT),
				getFormulaWithBrickField(BrickField.DRONE_VERTICAL_SPEED_MAX),
				getFormulaWithBrickField(BrickField.DRONE_ROTATION_MAX),
				getFormulaWithBrickField(BrickField.DRONE_TILT_ANGLE)));
		return null;
	}
}