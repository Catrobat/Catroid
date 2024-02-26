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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.apache.commons.lang3.NotImplementedException;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Set EV3 LED")
public class LegoEv3SetLedBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private static final String LED_STATUS_CATLANG_PARAMETER_NAME = "status";

	private static final BiMap<LedStatus, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<LedStatus, String>()
	{{
		put(LedStatus.LED_OFF, "off");
		put(LedStatus.LED_GREEN, "green");
		put(LedStatus.LED_RED, "red");
		put(LedStatus.LED_ORANGE, "orange");
		put(LedStatus.LED_GREEN_FLASHING, "green flashing");
		put(LedStatus.LED_RED_FLASHING, "red flashing");
		put(LedStatus.LED_ORANGE_FLASHING, "orange flashing");
		put(LedStatus.LED_GREEN_PULSE, "green pulse");
		put(LedStatus.LED_RED_PULSE, "red pulse");
		put(LedStatus.LED_ORANGE_PULSE, "orange pulse");
	}});

	private String ledStatus;

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
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(LED_STATUS_CATLANG_PARAMETER_NAME))
			return new HashMap.SimpleEntry<>(LED_STATUS_CATLANG_PARAMETER_NAME, CATLANG_SPINNER_VALUES.get(LedStatus.valueOf(ledStatus)));
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(LED_STATUS_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);

		String ledStatus = arguments.get(LED_STATUS_CATLANG_PARAMETER_NAME);
		if (ledStatus != null) {
			LedStatus selectedLedStatus = CATLANG_SPINNER_VALUES.inverse().get(ledStatus);
			if (selectedLedStatus != null) {
				this.ledStatus = selectedLedStatus.name();
			} else {
				throw new CatrobatLanguageParsingException("Invalid LED status: " + ledStatus);
			}
		}
	}
}
