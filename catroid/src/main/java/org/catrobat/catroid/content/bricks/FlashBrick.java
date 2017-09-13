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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class FlashBrick extends BrickBaseType {

	private static final int FLASH_OFF = 0;
	private static final int FLASH_ON = 1;

	private transient View prototypeView;

	private int spinnerSelectionID;

	public FlashBrick() {
		spinnerSelectionID = FLASH_ON;
	}

	public FlashBrick(int onOrOff) {
		spinnerSelectionID = onOrOff;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_flash, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_flash_checkbox);
		Spinner flashSpinner = (Spinner) view.findViewById(R.id.brick_flash_spinner);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);

		flashSpinner.setAdapter(spinnerAdapter);

		flashSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				spinnerSelectionID = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});

		flashSpinner.setSelection(spinnerSelectionID);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_flash, null);

		Spinner setFlashSpinner = (Spinner) prototypeView.findViewById(R.id.brick_flash_spinner);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		setFlashSpinner.setAdapter(spinnerAdapter);
		setFlashSpinner.setSelection(spinnerSelectionID);

		return prototypeView;
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
	public int getRequiredResources() {
		return CAMERA_FLASH;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (spinnerSelectionID == FLASH_ON) {
			sequence.addAction(sprite.getActionFactory().createTurnFlashOnAction());
			return null;
		}
		sequence.addAction(sprite.getActionFactory().createTurnFlashOffAction());
		return null;
	}
}
