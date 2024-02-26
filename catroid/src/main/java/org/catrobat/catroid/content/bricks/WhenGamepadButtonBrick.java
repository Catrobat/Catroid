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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.apache.commons.lang3.NotImplementedException;
import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@CatrobatLanguageBrick(command = "When tapped")
public class WhenGamepadButtonBrick extends ScriptBrickBaseType
		implements BrickSpinner.OnItemSelectedListener<StringOption> {

	private static final long serialVersionUID = 1L;
	private static final String GAMEPAD_BUTTON_CATLANG_PARAMETER_NAME = "gamepad button";
	private static BiMap<Integer, String> GAMEPAD_BUTTON_CATLANG_VALUES = HashBiMap.create(new HashMap<Integer, String>()
	{{
		put(0, "A");
		put(1, "B");
		put(2, "up");
		put(3, "down");
		put(4, "left");
		put(5, "right");
	}});

	private static BiMap<Integer, Integer> GAMEPAD_BUTTON_VALUES = HashBiMap.create(new HashMap<Integer, Integer>()
	{{
		put(0, R.string.cast_gamepad_A);
		put(1, R.string.cast_gamepad_B);
		put(2, R.string.cast_gamepad_up);
		put(3, R.string.cast_gamepad_down);
		put(4, R.string.cast_gamepad_left);
		put(5, R.string.cast_gamepad_right);
	}});

	private WhenGamepadButtonScript script;

	public WhenGamepadButtonBrick() {
		script = new WhenGamepadButtonScript();
	}

	public WhenGamepadButtonBrick(@NonNull WhenGamepadButtonScript script) {
		script.setScriptBrick(this);
		commentedOut = script.isCommentedOut();
		this.script = script;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		WhenGamepadButtonBrick clone = (WhenGamepadButtonBrick) super.clone();
		clone.script = (WhenGamepadButtonScript) script.clone();
		clone.script.setScriptBrick(clone);
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_gamepad_button;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new StringOption(context.getString(R.string.cast_gamepad_A)));
		items.add(new StringOption(context.getString(R.string.cast_gamepad_B)));
		items.add(new StringOption(context.getString(R.string.cast_gamepad_up)));
		items.add(new StringOption(context.getString(R.string.cast_gamepad_down)));
		items.add(new StringOption(context.getString(R.string.cast_gamepad_left)));
		items.add(new StringOption(context.getString(R.string.cast_gamepad_right)));

		BrickSpinner<StringOption> spinner = new BrickSpinner<>(R.id.brick_when_gamepad_button_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(script.getAction());
		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
		script.setAction(string);
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable StringOption item) {
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(CAST_REQUIRED);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(GAMEPAD_BUTTON_CATLANG_PARAMETER_NAME)) {
			Context context = CatroidApplication.getAppContext();
			String selectedAction = script.getAction();
			Map<Integer, Integer> resourceToValue = GAMEPAD_BUTTON_VALUES.inverse();
			for (Map.Entry<Integer, Integer> entry : resourceToValue.entrySet()) {
				if (context.getString(entry.getKey()).equals(selectedAction)) {
					return CatrobatLanguageUtils.getCatlangArgumentTuple(name, GAMEPAD_BUTTON_CATLANG_VALUES.get(entry.getValue()));
				}
			}
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, GAMEPAD_BUTTON_CATLANG_VALUES.get(0));
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String selectedItem = arguments.get(GAMEPAD_BUTTON_CATLANG_PARAMETER_NAME);
		if (selectedItem == null) {
			throw new CatrobatLanguageParsingException("No gamepad button selected");
		}
		Integer selectedItemValue = GAMEPAD_BUTTON_CATLANG_VALUES.inverse().get(selectedItem);
		if (selectedItemValue == null) {
			throw new CatrobatLanguageParsingException("Invalid gamepad button selected");
		}
		script.setAction(context.getString(GAMEPAD_BUTTON_VALUES.get(selectedItemValue)));
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(GAMEPAD_BUTTON_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}
}
