/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class LegoNxtMotorTurnAngleBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_A_C
	}

	private String motor;
	private transient Motor motorEnum;
	private Formula degrees;
	private transient EditText editSpeed;

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	public LegoNxtMotorTurnAngleBrick(Sprite sprite, Motor motor, int degrees) {
		this.sprite = sprite;
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		this.degrees = new Formula(degrees);
	}

	public LegoNxtMotorTurnAngleBrick(Sprite sprite, Motor motor, Formula degreesFormula) {
		this.sprite = sprite;
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		this.degrees = degreesFormula;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		LegoNxtMotorTurnAngleBrick copyBrick = (LegoNxtMotorTurnAngleBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_nxt_motor_turn_angle, null);
		TextView textX = (TextView) prototypeView.findViewById(R.id.motor_turn_angle_text_view);
		textX.setText(String.valueOf(degrees.interpretInteger(sprite)));
		//TODO set the motorname
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LegoNxtMotorTurnAngleBrick(getSprite(), motorEnum, degrees.clone());
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_nxt_motor_turn_angle, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_nxt_motor_turn_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textSpeed = (TextView) view.findViewById(R.id.motor_turn_angle_text_view);
		editSpeed = (EditText) view.findViewById(R.id.motor_turn_angle_edit_text);
		degrees.setTextFieldId(R.id.motor_turn_angle_edit_text);
		degrees.refreshTextField(view);

		textSpeed.setVisibility(View.GONE);
		editSpeed.setVisibility(View.VISIBLE);

		editSpeed.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.nxt_motor_chooser,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner motorSpinner = (Spinner) view.findViewById(R.id.motor_spinner);

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
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		motorSpinner.setSelection(motorEnum.ordinal());

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, degrees);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_nxt_motor_turn_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoNxtMotorTurnAngle(sprite, motorEnum, degrees));
		return null;
	}
}
