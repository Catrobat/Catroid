/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class StopScriptBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private String[] spinnerValue;
	private int spinnerSelection;

	public StopScriptBrick() {
		this.spinnerValue = new String[3];
	}

	public StopScriptBrick(int spinnerSelection) {
		this.spinnerValue = new String[3];
		this.spinnerSelection = spinnerSelection;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		return clone();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_stop_script, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_stop_script_checkbox);
		Spinner stopScriptSpinner = (Spinner) view.findViewById(R.id.brick_stop_script_spinner);

		if (!(checkbox.getVisibility() == view.VISIBLE)) {
			stopScriptSpinner.setClickable(true);
			stopScriptSpinner.setEnabled(true);
		} else {
			stopScriptSpinner.setClickable(false);
			stopScriptSpinner.setEnabled(false);
		}

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		stopScriptSpinner.setAdapter(spinnerAdapter);

		stopScriptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				spinnerSelection = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		stopScriptSpinner.setSelection(spinnerSelection);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {

		View prototypeView = View.inflate(context, R.layout.brick_stop_script, null);

		Spinner stopSctiptSpinner = (Spinner) prototypeView.findViewById(R.id.brick_stop_script_spinner);
		stopSctiptSpinner.setEnabled(false);
		stopSctiptSpinner.setFocusable(false);
		stopSctiptSpinner.setFocusableInTouchMode(false);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		stopSctiptSpinner.setAdapter(spinnerAdapter);
		stopSctiptSpinner.setSelection(spinnerSelection);

		return prototypeView;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		spinnerValue[BrickValues.STOP_THIS_SCRIPT] = context.getString(R.string.brick_stop_this_script);
		spinnerValue[BrickValues.STOP_ALL_SCRIPTS] = context.getString(R.string.brick_stop_all_scripts);
		spinnerValue[BrickValues.STOP_OTHER_SCRIPTS] = context.getString(R.string.brick_stop_other_scripts);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
				spinnerValue);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return spinnerAdapter;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStopScriptAction(spinnerSelection, sequence));
		return null;
	}

	@Override
	public Brick clone() {
		return new StopScriptBrick(this.spinnerSelection);
	}
}
