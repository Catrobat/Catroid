/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class DroneSetConfigBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	protected transient AdapterView<?> adapterView;
	private String selectedMessage;
	private int spinnerPosition = 0;

	public DroneSetConfigBrick() {
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		DroneSetConfigBrick copyBrick = (DroneSetConfigBrick) clone();
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new DroneSetConfigBrick();
	}

	@Override
	public int getRequiredResources() {
		return ARDRONE_SUPPORT;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_set_config, null);
		setCheckboxView(R.id.brick_set_config_checkbox);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(DroneSetConfigBrick.this, isChecked);
			}
		});

		Spinner spinner = (Spinner) view.findViewById(R.id.brick_set_config_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);


		ArrayAdapter<CharSequence> droneSetConfigAdapter = ArrayAdapter.createFromResource(context,
				R.array.drone_config_spinner, android.R.layout.simple_spinner_item);
		droneSetConfigAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(droneSetConfigAdapter);

		if (checkbox.getVisibility() == View.VISIBLE) {
			spinner.setClickable(false);
			spinner.setEnabled(false);
		} else {
			spinner.setClickable(true);
			spinner.setEnabled(true);
		}

		spinner.setAdapter(droneSetConfigAdapter);
		spinner.setSelection(spinnerPosition);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedMessage = parent.getItemAtPosition(position).toString();
				spinnerPosition = position;
				adapterView = parent;
				Log.d("DroneSetConfigBrick", "selected message = " + selectedMessage + " an der Position: "+spinnerPosition);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_set_config, null);

		Spinner DroneConfigSpinner = (Spinner) prototypeView.findViewById(R.id.brick_set_config_spinner);
		DroneConfigSpinner.setFocusableInTouchMode(false);
		DroneConfigSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> DroneConfigSpinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.drone_config_spinner, android.R.layout.simple_spinner_item);
		DroneConfigSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		DroneConfigSpinner.setAdapter(DroneConfigSpinnerAdapter);
		DroneConfigSpinner.setSelection(spinnerPosition);

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_config_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = (alphaValue);
		}
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		adapterView = parent;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (spinnerPosition == 0) {
			sequence.addAction(ExtendedActions.droneSetConfigAction(R.string.drone_config_default));
			Log.d(getClass().getSimpleName(), "default ausgewählt");
		} else if (spinnerPosition == 1) {
			sequence.addAction(ExtendedActions.droneSetConfigAction(R.string.drone_config_outdoor));
			Log.d(getClass().getSimpleName(), "outdoor ausgewählt");
		} else {
			sequence.addAction(ExtendedActions.droneSetConfigAction(R.string.drone_config_indoor));
			Log.d(getClass().getSimpleName(), "indoor ausgewählt");
		}
		return null;
	}
}