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

public class RobotAlbertRgbLedEyeBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private String eye;
	private transient Eye eyeEnum;
	private transient TextView editRedValue;
	private transient TextView editGreenValue;
	private transient TextView editBlueValue;

	public RobotAlbertRgbLedEyeBrick(Eye eye, int red, int green, int blue) {
		this.eyeEnum = eye;
		this.eye = eyeEnum.name();
		initializeBrickFields(new Formula(red), new Formula(green), new Formula(blue));
	}

	public RobotAlbertRgbLedEyeBrick(Eye eye, Formula red, Formula green, Formula blue) {
		this.eyeEnum = eye;
		this.eye = eyeEnum.name();
		initializeBrickFields(red, green, blue);

	}

	protected Object readResolve() {
		if (eye != null) {
			eyeEnum = Eye.valueOf(eye);
		}
		return this;
	}

	private void initializeBrickFields(Formula red, Formula green, Formula blue) {
		addAllowedBrickField(BrickField.ROBOT_ALBERT_RGB_BLUE);
		addAllowedBrickField(BrickField.ROBOT_ALBERT_RGB_GREEN);
		addAllowedBrickField(BrickField.ROBOT_ALBERT_RGB_RED);

		setFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_RED, red);
		setFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_GREEN, green);
		setFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_BLUE, blue);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ROBOT_ALBERT;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_robot_albert_rgb_eye_action, null);
		TextView textRed = (TextView) prototypeView.findViewById(R.id.robot_albert_rgb_led_red_prototype_text_view);
		textRed.setText(String.valueOf((BrickValues.ROBOT_ALBERT_RGB_LED_EYE_RED)));
		TextView textGreen = (TextView) prototypeView.findViewById(R.id.robot_albert_rgb_led_green_prototype_text_view);
		textGreen.setText(String.valueOf(BrickValues.ROBOT_ALBERT_RGB_LED_EYE_GREEN));
		TextView textBlue = (TextView) prototypeView.findViewById(R.id.robot_albert_rgb_led_blue_prototype_text_view);
		textBlue.setText(String.valueOf(BrickValues.ROBOT_ALBERT_RGB_LED_EYE_BLUE));
		Spinner eyeSpinner = (Spinner) prototypeView.findViewById(R.id.robot_albert_eye_spinner);
		eyeSpinner.setFocusableInTouchMode(false);
		eyeSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> eyeAdapter = ArrayAdapter.createFromResource(context,
				R.array.robot_albert_eye_chooser, android.R.layout.simple_spinner_item);
		eyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		eyeSpinner.setAdapter(eyeAdapter);
		eyeSpinner.setSelection(eyeEnum.ordinal());

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

		view = View.inflate(context, R.layout.brick_robot_albert_rgb_eye_action, null);
		setCheckboxView(R.id.brick_robot_albert_rgb_led_action_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textRed = (TextView) view.findViewById(R.id.robot_albert_rgb_led_red_prototype_text_view);
		editRedValue = (TextView) view.findViewById(R.id.robot_albert_rgb_led_action_red_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_RED).setTextFieldId(R.id.robot_albert_rgb_led_action_red_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_RED).refreshTextField(view);

		textRed.setVisibility(View.GONE);
		editRedValue.setVisibility(View.VISIBLE);

		editRedValue.setOnClickListener(this);

		TextView textGreen = (TextView) view.findViewById(R.id.robot_albert_rgb_led_green_prototype_text_view);
		editGreenValue = (TextView) view.findViewById(R.id.robot_albert_rgb_led_action_green_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_GREEN).setTextFieldId(R.id.robot_albert_rgb_led_action_green_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_GREEN).refreshTextField(view);

		textGreen.setVisibility(View.GONE);
		editGreenValue.setVisibility(View.VISIBLE);

		editGreenValue.setOnClickListener(this);

		TextView textBlue = (TextView) view.findViewById(R.id.robot_albert_rgb_led_blue_prototype_text_view);
		editBlueValue = (TextView) view.findViewById(R.id.robot_albert_rgb_led_action_blue_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_BLUE).setTextFieldId(R.id.robot_albert_rgb_led_action_blue_edit_text);
		getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_BLUE).refreshTextField(view);

		textBlue.setVisibility(View.GONE);
		editBlueValue.setVisibility(View.VISIBLE);

		editBlueValue.setOnClickListener(this);

		//update color of the current rgb-selection
		int r = 0;
		int g = 0;
		int b = 0;
		try {
			r = getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_RED).interpretInteger(ProjectManager.getInstance().getCurrentSprite());
			g = getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_GREEN).interpretInteger(ProjectManager.getInstance().getCurrentSprite());
			b = getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_BLUE).interpretInteger(ProjectManager.getInstance().getCurrentSprite());
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
		}

		if (r > 255) {
			editRedValue.setText("" + 255);
		} else if (r < 0) {
			editRedValue.setText("" + 0);
		}
		if (g > 255) {
			editGreenValue.setText("" + 255);
		} else if (g < 0) {
			editGreenValue.setText("" + 0);
		}
		if (b > 255) {
			editBlueValue.setText("" + 255);
		} else if (b < 0) {
			editBlueValue.setText("" + 0);
		}

		ArrayAdapter<CharSequence> eyeAdapter = ArrayAdapter.createFromResource(context,
				R.array.robot_albert_eye_chooser, android.R.layout.simple_spinner_item);
		eyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner eyeSpinner = (Spinner) view.findViewById(R.id.robot_albert_eye_spinner);
		eyeSpinner.setClickable(true);
		eyeSpinner.setEnabled(true);
		eyeSpinner.setAdapter(eyeAdapter);
		eyeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				eyeEnum = Eye.values()[position];
				eye = eyeEnum.name();
				adapterView = arg0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		eyeSpinner.setSelection(eyeEnum.ordinal());

		return view;
	}

	@Override
	public void onClick(View view) {

		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.robot_albert_rgb_led_action_red_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ROBOT_ALBERT_RGB_RED);
				break;

			case R.id.robot_albert_rgb_led_action_green_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ROBOT_ALBERT_RGB_GREEN);
				break;

			case R.id.robot_albert_rgb_led_action_blue_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ROBOT_ALBERT_RGB_BLUE);
				break;
		}
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {

	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_robot_albert_rgb_led_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textAlbertEyeLabel = (TextView) view.findViewById(R.id.brick_robot_albert_rgb_led_action_label);
			TextView textAlbertEyeRed = (TextView) view.findViewById(R.id.robot_albert_rgb_led_red_text_view);
			TextView editRed = (TextView) view.findViewById(R.id.robot_albert_rgb_led_action_red_edit_text);

			TextView textAlbertEyeRedView = (TextView) view.findViewById(R.id.robot_albert_rgb_led_red_text_view);

			textAlbertEyeLabel.setTextColor(textAlbertEyeLabel.getTextColors().withAlpha(alphaValue));
			textAlbertEyeRed.setTextColor(textAlbertEyeRed.getTextColors().withAlpha(alphaValue));

			Spinner eyeSpinner = (Spinner) view.findViewById(R.id.robot_albert_eye_spinner);
			ColorStateList color = textAlbertEyeRedView.getTextColors().withAlpha(alphaValue);
			eyeSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			editRed.setTextColor(editRed.getTextColors().withAlpha(alphaValue));
			editRed.getBackground().setAlpha(alphaValue);

			//green
			TextView textAlbertEyeGreen = (TextView) view.findViewById(R.id.robot_albert_rgb_led_green_text_view);
			TextView editGreen = (TextView) view.findViewById(R.id.robot_albert_rgb_led_action_green_edit_text);
			TextView textAlbertEyeGreenView = (TextView) view.findViewById(R.id.robot_albert_rgb_led_green_text_view);
			editGreen.setTextColor(editGreen.getTextColors().withAlpha(alphaValue));
			editGreen.getBackground().setAlpha(alphaValue);
			ColorStateList color2 = textAlbertEyeGreenView.getTextColors().withAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color2);
			}
			textAlbertEyeGreen.setTextColor(textAlbertEyeGreen.getTextColors().withAlpha(alphaValue));

			//blue
			TextView textAlbertEyeBlue = (TextView) view.findViewById(R.id.robot_albert_rgb_led_blue_text_view);
			TextView editBlue = (TextView) view.findViewById(R.id.robot_albert_rgb_led_action_blue_edit_text);
			TextView textAlbertEyeBlueView = (TextView) view.findViewById(R.id.robot_albert_rgb_led_blue_text_view);
			editBlue.setTextColor(editGreen.getTextColors().withAlpha(alphaValue));
			editBlue.getBackground().setAlpha(alphaValue);
			ColorStateList color3 = textAlbertEyeBlueView.getTextColors().withAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color3);
			}
			textAlbertEyeBlue.setTextColor(textAlbertEyeBlue.getTextColors().withAlpha(alphaValue));

			this.alphaValue = (alphaValue);
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.robotAlbertRgbLedEye(sprite, eye, eyeEnum, getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_RED),
				getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_GREEN), getFormulaWithBrickField(BrickField.ROBOT_ALBERT_RGB_BLUE)));
		return null;
	}

	public static enum Eye {
		Left, Right, Both
	}
}
