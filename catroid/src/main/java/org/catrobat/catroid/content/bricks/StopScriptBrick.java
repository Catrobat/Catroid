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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import kotlin.Unit;

public class StopScriptBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private int spinnerSelection;

	public StopScriptBrick() {
	}

	public StopScriptBrick(int spinnerSelection) {
		this.spinnerSelection = spinnerSelection;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_stop_script;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		Spinner spinner = view.findViewById(R.id.brick_stop_script_spinner);
		spinner.setAdapter(createArrayAdapter(context));
		spinner.setSelection(spinnerSelection);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			spinnerSelection = position;
			return Unit.INSTANCE;
		}));
		return view;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		String[] values = new String[3];
		values[BrickValues.STOP_THIS_SCRIPT] = context.getString(R.string.brick_stop_this_script);
		values[BrickValues.STOP_ALL_SCRIPTS] = context.getString(R.string.brick_stop_all_scripts);
		values[BrickValues.STOP_OTHER_SCRIPTS] = context.getString(R.string.brick_stop_other_scripts);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, values);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return spinnerAdapter;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStopScriptAction(spinnerSelection,
				sequence.getScript(), sprite));
	}
}
