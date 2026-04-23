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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WhenGamepadButtonBrick extends ScriptBrickBaseType implements BrickSpinner.OnItemSelectedListener<StringOption> {

	private static final long serialVersionUID = 1L;

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
}
