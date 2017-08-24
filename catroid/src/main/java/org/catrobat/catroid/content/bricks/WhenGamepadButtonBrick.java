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

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenGamepadButtonScript;

import java.util.List;

public class WhenGamepadButtonBrick extends BrickBaseType implements ScriptBrick {

	protected WhenGamepadButtonScript whenGamepadButtonScript;
	protected transient boolean checked = false;
	private static final long serialVersionUID = 1L;
	private String[] actions;
	private String action;
	private int position;

	public WhenGamepadButtonBrick(WhenGamepadButtonScript whenGamepadButtonScript) {
		this.whenGamepadButtonScript = whenGamepadButtonScript;
		actions = CatroidApplication.getAppContext().getResources().getStringArray(R.array.gamepad_buttons_array);
		if (whenGamepadButtonScript != null) {
			action = whenGamepadButtonScript.getAction();
			for (int i = 0; i < actions.length; i++) {
				if (actions[i].equals(action)) {
					position = i;
					break;
				}
			}
		}
	}

	@Override
	public int getRequiredResources() {
		return CAST_REQUIRED;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		WhenGamepadButtonBrick copyBrick = (WhenGamepadButtonBrick) clone();
		copyBrick.whenGamepadButtonScript = whenGamepadButtonScript;
		return copyBrick;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_when_gamepad_button, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_when_gamepad_button_checkbox);

		final Spinner actionSpinner = (Spinner) view.findViewById(R.id.brick_when_gamepad_button_spinner);

		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context,
				R.array.gamepad_buttons_array, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		actionSpinner.setAdapter(arrayAdapter);

		actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String actionChosen = actionSpinner.getSelectedItem().toString();
				action = actionChosen;
				WhenGamepadButtonBrick.this.position = position;
				if (whenGamepadButtonScript == null) {
					whenGamepadButtonScript = new WhenGamepadButtonScript(actionChosen);
				} else {
					whenGamepadButtonScript.setAction(actionChosen);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		actionSpinner.setSelection(position);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return getView(context, 0, null);
	}

	@Override
	public Brick clone() {
		return new WhenGamepadButtonBrick(null);
	}

	@Override
	public Script getScriptSafe() {
		if (whenGamepadButtonScript == null) {
			whenGamepadButtonScript = new WhenGamepadButtonScript();
		}

		return whenGamepadButtonScript;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}
}
