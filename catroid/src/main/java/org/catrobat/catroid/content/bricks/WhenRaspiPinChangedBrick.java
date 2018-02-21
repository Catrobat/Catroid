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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class WhenRaspiPinChangedBrick extends BrickBaseType implements ScriptBrick {
	private static final long serialVersionUID = 1L;

	private RaspiInterruptScript script;

	public WhenRaspiPinChangedBrick(RaspiInterruptScript script) {
		this.script = script;
		if (script.isCommentedOut()) {
			setCommentedOut(true);
		}
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_raspi_pin_changed, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_raspi_when_checkbox);

		setupValueSpinner(context);
		setupPinSpinner(context);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_raspi_pin_changed, null);

		Spinner pinSpinner = (Spinner) prototypeView.findViewById(R.id.brick_raspi_when_pinspinner);

		ArrayAdapter<String> messageAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		messageAdapter.add(script.getPin());
		pinSpinner.setAdapter(messageAdapter);

		Spinner valueSpinner = (Spinner) prototypeView.findViewById(R.id.brick_raspi_when_valuespinner);

		valueSpinner.setAdapter(getValueSpinnerArrayAdapter(context));
		return prototypeView;
	}

	private void setupPinSpinner(Context context) {
		final Spinner pinSpinner = (Spinner) view.findViewById(R.id.brick_raspi_when_pinspinner);

		String revision = SettingsActivity.getRaspiRevision(context);
		ArrayList<Integer> availableGPIOs = RaspberryPiService.getInstance().getGpioList(revision);
		ArrayAdapter<String> messageAdapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		messageAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (Integer gpio : availableGPIOs) {
			messageAdapter2.add(gpio.toString());
		}
		pinSpinner.setAdapter(messageAdapter2);

		pinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = pinSpinner.getSelectedItem().toString();
				script.setPin(selectedMessage);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		pinSpinner.setSelection(messageAdapter2.getPosition(script.getPin()), true);
	}

	private void setupValueSpinner(final Context context) {

		final Spinner valueSpinner = (Spinner) view.findViewById(R.id.brick_raspi_when_valuespinner);

		ArrayAdapter<String> valueAdapter = getValueSpinnerArrayAdapter(context);
		valueSpinner.setAdapter(valueAdapter);
		valueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = getEventFromLanguageSpecificSpinnerSelection(valueSpinner.getSelectedItem().toString(), context);
				script.setEventValue(selectedMessage);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		valueSpinner.setSelection(valueAdapter.getPosition(getLanguageSpecificSpinnerSelectionFromEvent(script.getEventValue(), context)), true);
	}

	private ArrayAdapter<String> getValueSpinnerArrayAdapter(Context context) {
		ArrayAdapter<String> messageAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		messageAdapter.add(context.getString(R.string.brick_raspi_pressed_text));
		messageAdapter.add(context.getString(R.string.brick_raspi_released_text));
		return messageAdapter;
	}

	private String getEventFromLanguageSpecificSpinnerSelection(String spinnerSelection, Context context) {
		if (spinnerSelection.equals(context.getString(R.string.brick_raspi_pressed_text))) {
			return BrickValues.RASPI_PRESSED_EVENT;
		} else {
			return BrickValues.RASPI_RELEASED_EVENT;
		}
	}

	private String getLanguageSpecificSpinnerSelectionFromEvent(String eventString, Context context) {
		if (eventString.equals(BrickValues.RASPI_PRESSED_EVENT)) {
			return context.getString(R.string.brick_raspi_pressed_text);
		} else {
			return context.getString(R.string.brick_raspi_released_text);
		}
	}

	@Override
	public Brick clone() {
		return new WhenRaspiPinChangedBrick(script);
	}

	@Override
	public RaspiInterruptScript getScriptSafe() {
		return script;
	}

	@Override
	public int getRequiredResources() {
		return Brick.SOCKET_RASPI;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		script.setCommentedOut(commentedOut);
	}
}
