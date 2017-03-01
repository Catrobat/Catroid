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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.DynamicTextSizeArrayAdapter;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class StopScriptBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private int spinnerSelection;

	public StopScriptBrick() {
	}

	public StopScriptBrick(int spinnerSelection) {
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

		IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_stop_script_label),
				context.getString(R.string.category_control));

		setCheckboxView(R.id.brick_stop_script_checkbox);
		Spinner stopScriptSpinner = (Spinner) view.findViewById(R.id.brick_stop_script_spinner);

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
		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {

		View prototypeView = View.inflate(context, R.layout.brick_stop_script, null);

		Spinner stopSctiptSpinner = (Spinner) prototypeView.findViewById(R.id.brick_stop_script_spinner);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		stopSctiptSpinner.setAdapter(spinnerAdapter);
		stopSctiptSpinner.setSelection(spinnerSelection);

		return prototypeView;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		String[] spinnerValue = new String[3];
		spinnerValue[BrickValues.STOP_THIS_SCRIPT] = context.getString(R.string.brick_stop_this_script);
		spinnerValue[BrickValues.STOP_ALL_SCRIPTS] = context.getString(R.string.brick_stop_all_scripts);
		spinnerValue[BrickValues.STOP_OTHER_SCRIPTS] = context.getString(R.string.brick_stop_other_scripts);

		DynamicTextSizeArrayAdapter<String> spinnerAdapter = new DynamicTextSizeArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
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
