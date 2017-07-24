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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.DynamicTextSizeArrayAdapter;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class LegoEv3SetLedBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	private transient LedStatus ledStatusEnum;
	private String ledStatus;

	public enum LedStatus {
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
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_ev3_set_led, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_ev3_set_led_checkbox);

		DynamicTextSizeArrayAdapter<CharSequence> ledStatusAdapter = new DynamicTextSizeArrayAdapter(context,
				android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array
				.ev3_led_status_chooser));
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
		if (ledStatusEnum == null) {
			readResolve();
		}
		ledStatusSpinner.setSelection(ledStatusEnum.ordinal());

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		ledStatusEnum = LedStatus.values()[position];
		ledStatus = ledStatusEnum.name();

		TextView spinnerText = (TextView) parent.getChildAt(0);
		TextSizeUtil.enlargeTextView(spinnerText);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoEv3SetLedAction(ledStatusEnum));
		return null;
	}
}
