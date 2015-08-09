/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class LegoEv3SetLedBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	private transient LedStatus ledStatusEnum;
	private String ledStatus;
	private transient AdapterView<?> adapterView;

	public static enum LedStatus {
		LED_OFF, LED_GREEN, LED_RED, LED_ORANGE,
		LED_GREEN_FLASHING, LED_RED_FLASHING, LED_ORANGE_FLASHING,
		LED_GREEN_PULSE, LED_RED_PULSE, LED_ORANGE_PULSE
	}

	public LegoEv3SetLedBrick(LedStatus ledStatus) {
		this.ledStatusEnum = ledStatus;
		this.ledStatus = ledStatusEnum.name();
	}

	protected Object readResolve() {
		if (ledStatus != null) {
			ledStatusEnum = LedStatus.valueOf(ledStatus);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_EV3;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		LegoEv3SetLedBrick copyBrick = (LegoEv3SetLedBrick) clone();
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_ev3_set_led, null);

		Spinner ledStatusSpinner = (Spinner) prototypeView.findViewById(R.id.brick_ev3_set_led_spinner);
		ledStatusSpinner.setFocusableInTouchMode(false);
		ledStatusSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> ledStatusAdapter = ArrayAdapter.createFromResource(context,
				R.array.ev3_led_status_chooser, android.R.layout.simple_spinner_item);
		ledStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ledStatusSpinner.setAdapter(ledStatusAdapter);
		ledStatusSpinner.setSelection(ledStatusEnum.ordinal());
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LegoEv3SetLedBrick(ledStatusEnum);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_ev3_set_led, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_ev3_set_led_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		ArrayAdapter<CharSequence> ledStatusAdapter = ArrayAdapter.createFromResource(context,
				R.array.ev3_led_status_chooser, android.R.layout.simple_spinner_item);
		ledStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner ledStatusSpinner = (Spinner) view.findViewById(R.id.brick_ev3_set_led_spinner);
		ledStatusSpinner.setOnItemSelectedListener(this);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			ledStatusSpinner.setClickable(true);
			ledStatusSpinner.setEnabled(true);
		} else {
			ledStatusSpinner.setClickable(false);
			ledStatusSpinner.setEnabled(false);
		}

		ledStatusSpinner.setAdapter(ledStatusAdapter);
		ledStatusSpinner.setSelection(ledStatusEnum.ordinal());
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		ledStatusEnum = LedStatus.values()[position];
		ledStatus = ledStatusEnum.name();
		adapterView = parent;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_ev3_set_led_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textEv3SetLedLabel = (TextView) view.findViewById(R.id.brick_ev3_set_led_label);
			textEv3SetLedLabel.setTextColor(textEv3SetLedLabel.getTextColors().withAlpha(alphaValue));

			Spinner ledStatusSpinner = (Spinner) view.findViewById(R.id.brick_ev3_set_led_spinner);
			ColorStateList color = textEv3SetLedLabel.getTextColors().withAlpha(alphaValue);
			ledStatusSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoEv3SetLed(ledStatusEnum));
		return null;
	}
}
