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

import com.google.common.collect.HashBiMap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Turn")
public class FlashBrick extends BrickBaseType {

	private static final int FLASH_OFF = 0;
	private static final int FLASH_ON = 1;

	private static final String FLASH_CATLANG_PARAMETER_NAME = "flashlight";

	private static final HashBiMap<Integer, String> SPINNER_VALUE_MAP;

	static {
		Map<Integer, String> map = new HashMap<>();
		map.put(FLASH_OFF, "off");
		map.put(FLASH_ON, "on");
		SPINNER_VALUE_MAP = HashBiMap.create(map);
	}

	private int spinnerSelectionID;

	public FlashBrick() {
		spinnerSelectionID = FLASH_ON;
	}

	public FlashBrick(int spinnerSelectionID) {
		this.spinnerSelectionID = spinnerSelectionID;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_flash;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		Spinner flashSpinner = view.findViewById(R.id.brick_flash_spinner);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		flashSpinner.setAdapter(spinnerAdapter);
		flashSpinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			spinnerSelectionID = position;
			return Unit.INSTANCE;
		}));
		flashSpinner.setSelection(spinnerSelectionID);
		return view;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		String[] spinnerValues = new String[2];
		spinnerValues[FLASH_OFF] = context.getString(R.string.brick_flash_off);
		spinnerValues[FLASH_ON] = context.getString(R.string.brick_flash_on);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerValues);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return spinnerAdapter;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(CAMERA_FLASH);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createFlashAction(spinnerSelectionID == FLASH_ON));
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(FLASH_CATLANG_PARAMETER_NAME)) {
			return CatrobatLanguageUtils.getCatlangArgumentTuple(FLASH_CATLANG_PARAMETER_NAME, SPINNER_VALUE_MAP.get(spinnerSelectionID));
		} else {
			return super.getArgumentByCatlangName(name);
		}
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(FLASH_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String flashValue = arguments.get(FLASH_CATLANG_PARAMETER_NAME);
		if (flashValue != null) {
			Integer selectedFlashValue = SPINNER_VALUE_MAP.inverse().get(flashValue);
			if (selectedFlashValue != null) {
				spinnerSelectionID = selectedFlashValue;
			} else {
				throw new CatrobatLanguageParsingException("Invalid value for flashlight parameter: " + flashValue);
			}
		}
	}
}
