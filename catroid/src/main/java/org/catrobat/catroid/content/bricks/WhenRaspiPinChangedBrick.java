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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "When Raspberry Pi pin changes to")
public class WhenRaspiPinChangedBrick extends ScriptBrickBaseType implements UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;
	private static final String PIN_CATLANG_PARAMETER_NAME = "pin";
	private static final String POSITION_CATLANG_PARAMETER_NAME = "position";

	private RaspiInterruptScript script;

	public WhenRaspiPinChangedBrick() {
		script = new RaspiInterruptScript();
	}

	public WhenRaspiPinChangedBrick(RaspiInterruptScript script) {
		script.setScriptBrick(this);
		commentedOut = script.isCommentedOut();
		this.script = script;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		WhenRaspiPinChangedBrick clone = (WhenRaspiPinChangedBrick) super.clone();
		clone.script = (RaspiInterruptScript) script.clone();
		clone.script.setScriptBrick(clone);
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_raspi_pin_changed;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		setupValueSpinner(context);
		setupPinSpinner(context);
		return view;
	}

	private void setupPinSpinner(Context context) {
		final Spinner pinSpinner = view.findViewById(R.id.brick_raspi_when_pinspinner);

		String revision = SettingsFragment.getRaspiRevision(context);
		ArrayList<Integer> availableGPIOs = RaspberryPiService.getInstance().getGpioList(revision);
		ArrayAdapter<String> messageAdapter2 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		messageAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (Integer gpio : availableGPIOs) {
			messageAdapter2.add(gpio.toString());
		}
		pinSpinner.setAdapter(messageAdapter2);
		pinSpinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			String selectedMessage = pinSpinner.getSelectedItem().toString();
			script.setPin(selectedMessage);
			return Unit.INSTANCE;
		}));
		pinSpinner.setSelection(messageAdapter2.getPosition(script.getPin()), true);
	}

	private void setupValueSpinner(final Context context) {
		final Spinner valueSpinner = view.findViewById(R.id.brick_raspi_when_valuespinner);

		ArrayAdapter<String> valueAdapter = getValueSpinnerArrayAdapter(context);
		valueSpinner.setAdapter(valueAdapter);
		valueSpinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			if (position < BrickValues.RASPI_EVENTS.length) {
				script.setEventValue(BrickValues.RASPI_EVENTS[position]);
			}
			return Unit.INSTANCE;
		}));

		for (int i = 0; i < BrickValues.RASPI_EVENTS.length; i++) {
			if (BrickValues.RASPI_EVENTS[i].equals(script.getEventValue())) {
				valueSpinner.setSelection(i, true);
				break;
			}
		}
	}

	private ArrayAdapter<String> getValueSpinnerArrayAdapter(Context context) {
		ArrayAdapter<String> messageAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		messageAdapter.add(context.getString(R.string.brick_raspi_pressed_text));
		messageAdapter.add(context.getString(R.string.brick_raspi_released_text));
		return messageAdapter;
	}

	@Override
	public RaspiInterruptScript getScript() {
		return script;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(SOCKET_RASPI);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		if (script != null) {
			if (spinnerId == R.id.brick_raspi_when_pinspinner) {
				script.setPin(itemName);
			} else if (spinnerId == R.id.brick_raspi_when_valuespinner) {
				script.setEventValue(BrickValues.RASPI_EVENTS[itemIndex]);
			}
		}
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(PIN_CATLANG_PARAMETER_NAME)) {
			String pin = script.getPin() == null ? "" : script.getPin();
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, pin);
		}
		if (name.equals(POSITION_CATLANG_PARAMETER_NAME)) {
			String position = script.getEventValue() == null ? "" : script.getEventValue().equals(BrickValues.RASPI_EVENTS[0]) ? "high" : "low";
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, position);
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(PIN_CATLANG_PARAMETER_NAME);
		requiredArguments.add(POSITION_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		
	}
}
