/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.Arrays;
import java.util.List;

public class WhenGamepadButtonBrick extends BrickBaseType implements ScriptBrick {

	private static final long serialVersionUID = 1L;
	private WhenGamepadButtonScript whenGamepadButtonScript;
	private List<String> actions = Arrays.asList(CatroidApplication.getAppContext().getResources().getStringArray(R.array.gamepad_buttons_array));

	public WhenGamepadButtonBrick(@NonNull WhenGamepadButtonScript whenGamepadButtonScript) {
		whenGamepadButtonScript.setScriptBrick(this);
		commentedOut = whenGamepadButtonScript.isCommentedOut();
		this.whenGamepadButtonScript = whenGamepadButtonScript;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		WhenGamepadButtonBrick clone = (WhenGamepadButtonBrick) super.clone();
		clone.whenGamepadButtonScript = (WhenGamepadButtonScript) whenGamepadButtonScript.clone();
		clone.whenGamepadButtonScript.setScriptBrick(clone);
		return clone;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(CAST_REQUIRED);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_gamepad_button;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);
		final Spinner actionSpinner = view.findViewById(R.id.brick_when_gamepad_button_spinner);

		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context,
				R.array.gamepad_buttons_array, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		actionSpinner.setAdapter(arrayAdapter);

		actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String actionChosen = actionSpinner.getSelectedItem().toString();
				whenGamepadButtonScript.setAction(actionChosen);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		actionSpinner.setSelection(actions.indexOf(whenGamepadButtonScript.getAction()));
		return view;
	}

	@Override
	public Script getScript() {
		return whenGamepadButtonScript;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}
}
