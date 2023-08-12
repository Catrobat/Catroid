/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.apache.commons.lang3.NotImplementedException;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.io.catlang.CatrobatLanguageBrick;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Set EV3")
public class LegoEv3SetLedBrick extends BrickBaseType implements UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;

	private String ledStatus;
	private int spinnerSelectionIndex;

	public enum LedStatus {
		LED_OFF, LED_GREEN, LED_RED, LED_ORANGE,
		LED_GREEN_FLASHING, LED_RED_FLASHING, LED_ORANGE_FLASHING,
		LED_GREEN_PULSE, LED_RED_PULSE, LED_ORANGE_PULSE
	}

	public LegoEv3SetLedBrick() {
		ledStatus = LedStatus.LED_GREEN.name();
	}

	public LegoEv3SetLedBrick(LedStatus ledStatusEnum) {
		ledStatus = ledStatusEnum.name();
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_ev3_set_led;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(context, R.array.ev3_led_status_chooser, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.brick_ev3_set_led_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			ledStatus = LedStatus.values()[position].name();
			spinnerSelectionIndex = position;
			return Unit.INSTANCE;
		}));
		spinner.setSelection(LedStatus.valueOf(ledStatus).ordinal());
		return view;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_LEGO_EV3);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoEv3SetLedAction(LedStatus.valueOf(ledStatus)));
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		if (itemIndex >= 0 && itemIndex < LedStatus.values().length) {
			ledStatus = LedStatus.values()[itemIndex].name();
			spinnerSelectionIndex = itemIndex;
		}
	}

	@Override
	protected String getCatrobatLanguageSpinnerValue(int spinnerIndex) {
		switch (spinnerIndex) {
			case 0:
				return "off";
			case 1:
				return "green";
			case 2:
				return "red";
			case 3:
				return "orange";
			case 4:
				return "green flash";
			case 5:
				return "red flash";
			case 6:
				return "orange flash";
			case 7:
				return "green pulse";
			case 8:
				return "red pulse";
			case 9:
				return "orange pulse";
			default:
				throw new NotImplementedException("Invalid spinnerIndex");
		}
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		return getCatrobatLanguageSpinnerCall(indentionLevel, "status", spinnerSelectionIndex);
	}
}
