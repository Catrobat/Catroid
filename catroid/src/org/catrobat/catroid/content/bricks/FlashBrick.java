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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class FlashBrick extends BrickBaseType {

	private static final int FLASH_OFF = 0;
	private static final int FLASH_ON = 1;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private String[] spinnerValues;
	private int spinnerSelectionID;

	public FlashBrick() {
		spinnerValues = new String[2];
		spinnerSelectionID = FLASH_ON;
	}

	public FlashBrick(int onOrOff) {
		spinnerValues = new String[2];
		spinnerSelectionID = onOrOff;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		final Brick brickInstance = this;
		view = View.inflate(context, R.layout.brick_flash, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_flash_checkbox);
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner flashSpinner = (Spinner) view.findViewById(R.id.brick_flash_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			flashSpinner.setClickable(true);
			flashSpinner.setEnabled(true);
		} else {
			flashSpinner.setClickable(false);
			flashSpinner.setEnabled(false);
		}

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
		setFlashSpinner.setFocusableInTouchMode(false);
		setFlashSpinner.setFocusable(false);
		setFlashSpinner.setEnabled(false);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		setFlashSpinner.setAdapter(spinnerAdapter);
		setFlashSpinner.setSelection(spinnerSelectionID);

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_flash_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner flashBrickSpinner = (Spinner) view.findViewById(R.id.brick_flash_spinner);
			TextView flashBrickTextView = (TextView) view.findViewById(R.id.brick_flash_prototype_text_view);

			ColorStateList color = flashBrickTextView.getTextColors().withAlpha(alphaValue);
			flashBrickTextView.setTextColor(color);
			flashBrickSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			this.alphaValue = alphaValue;
		}
		return view;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
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
			sequence.addAction(ExtendedActions.turnFlashOn());
			return null;
		}
		sequence.addAction(ExtendedActions.turnFlashOff());
		return null;
	}
}
