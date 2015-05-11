/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class RobotAlbertMotorBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;

	public static enum Motor {
		Left, Right, Both
	}

	private String motor;
	public static Motor motorEnum;
	private transient TextView editSpeed;

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	public RobotAlbertMotorBrick(Motor motor, int speedValue) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
		initializeBrickFields(new Formula(speedValue));
	}

	private void initializeBrickFields(Formula brightness) {
		addAllowedBrickField(BrickField.ROBOT_ALBERT_SPEED);
		setFormulaWithBrickField(BrickField.ROBOT_ALBERT_SPEED, brightness);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ROBOT_ALBERT;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_robot_albert_motor_action, null);
		TextView textSpeed = (TextView) prototypeView
				.findViewById(R.id.robot_albert_motor_action_speed_prototype_text_view);
		textSpeed.setText(String.valueOf(BrickValues.ROBOT_ALBERT_SPEED));

		Spinner motorSpinner = (Spinner) prototypeView.findViewById(R.id.robot_albert_motor_spinner);
		motorSpinner.setFocusableInTouchMode(false);
		motorSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.robot_albert_motor_chooser, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setSelection(motorEnum.ordinal());

		return prototypeView;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		this.motorEnum = Motor.valueOf(this.motor);

		view = View.inflate(context, R.layout.brick_robot_albert_motor_action, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_robot_albert_motor_action_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textSpeed = (TextView) view.findViewById(R.id.robot_albert_motor_action_speed_prototype_text_view);
		editSpeed = (TextView) view.findViewById(R.id.robot_albert_motor_action_speed_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_SPEED).setTextFieldId(R.id.robot_albert_motor_action_speed_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_SPEED).refreshTextField(view);

		textSpeed.setVisibility(View.GONE);
		editSpeed.setVisibility(View.VISIBLE);

		editSpeed.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.robot_albert_motor_chooser, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner motorSpinner = (Spinner) view.findViewById(R.id.robot_albert_motor_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			motorSpinner.setClickable(true);
			motorSpinner.setEnabled(true);
		} else {
			motorSpinner.setClickable(false);
			motorSpinner.setEnabled(false);
		}

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				motorEnum = Motor.values()[position];
				motor = motorEnum.name();
				adapterView = arg0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		motorSpinner.setSelection(motorEnum.ordinal());

		int val = 0;
		try {
			val = getFormulaWithBrickField(BrickField.ROBOT_ALBERT_SPEED).interpretInteger(ProjectManager.getInstance().getCurrentSprite());
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
		}
		if (val > 100) {
			editSpeed.setText("" + 100);
		} else if (val < -100) {
			editSpeed.setText("" + -100);
		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, BrickField.ROBOT_ALBERT_SPEED);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {

	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_robot_albert_motor_action_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textAlbertMotorActionLabel = (TextView) view.findViewById(R.id.robot_albert_motor_action_label);
			TextView textAlbertMotorActionSpeed = (TextView) view
					.findViewById(R.id.robot_albert_motor_action_speed_text_view);
			TextView editSpeed = (TextView) view.findViewById(R.id.robot_albert_motor_action_speed_edit_text);

			TextView textAlbertMotorActionPercent = (TextView) view
					.findViewById(R.id.robot_albert_motor_action_percent);
			TextView textAlbertMotorActionLabelSpeedView = (TextView) view
					.findViewById(R.id.robot_albert_motor_action_speed_text_view);

			textAlbertMotorActionLabel.setTextColor(textAlbertMotorActionLabel.getTextColors().withAlpha(alphaValue));
			textAlbertMotorActionSpeed.setTextColor(textAlbertMotorActionSpeed.getTextColors().withAlpha(alphaValue));
			textAlbertMotorActionPercent.setTextColor(textAlbertMotorActionPercent.getTextColors()
					.withAlpha(alphaValue));
			textAlbertMotorActionLabelSpeedView.setTextColor(textAlbertMotorActionLabelSpeedView.getTextColors()
					.withAlpha(alphaValue));
			Spinner motorSpinner = (Spinner) view.findViewById(R.id.robot_albert_motor_spinner);
			ColorStateList color = textAlbertMotorActionLabelSpeedView.getTextColors().withAlpha(alphaValue);
			motorSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			editSpeed.setTextColor(editSpeed.getTextColors().withAlpha(alphaValue));
			editSpeed.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;

	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite,SequenceAction sequence) {
		sequence.addAction(ExtendedActions.robotAlbertMotor(sprite,Motor.valueOf(motor), getFormulaWithBrickField(BrickField.ROBOT_ALBERT_SPEED)));
		return null;
	}
}
