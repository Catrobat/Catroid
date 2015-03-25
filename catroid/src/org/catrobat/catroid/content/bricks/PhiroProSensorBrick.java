/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import java.util.ArrayList;
import java.util.List;

public class PhiroProSensorBrick extends FormulaBrick implements NestingBrick, OnItemSelectedListener {

	private static final long serialVersionUID = 1l;
	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private int sensorSpinnerPosition = 0;
	protected transient PhiroProSensorElseBrick phiroProSensorElseBrick;
	protected transient PhiroProSensorEndBrick phiroProSensorEndBrick;
	private transient PhiroProSensorBrick copy;
	private static final String TAG = PhiroProSensorBrick.class.getSimpleName();

	public PhiroProSensorBrick() {
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_PHIRO_PRO;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		PhiroProSensorBrick copyBrick = (PhiroProSensorBrick) clone();
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_phiro_pro_if_sensor, null);

		Spinner phiroProSensorSpinner = (Spinner) prototypeView.findViewById(R.id.brick_phiro_pro_sensor_action_spinner);
		phiroProSensorSpinner.setFocusableInTouchMode(false);
		phiroProSensorSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> phiroProSensorSpinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_phiro_pro_select_sensor_spinner, android.R.layout.simple_spinner_item);
		phiroProSensorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		phiroProSensorSpinner.setAdapter(phiroProSensorSpinnerAdapter);
		phiroProSensorSpinner.setSelection(sensorSpinnerPosition);

		return prototypeView;

	}


	@Override
	public boolean isInitialized() {
		if (phiroProSensorElseBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		phiroProSensorElseBrick = new PhiroProSensorElseBrick(this);
		phiroProSensorEndBrick = new PhiroProSensorEndBrick(phiroProSensorElseBrick, this);
		Log.w(TAG, "Creating if logic stuff");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		//TODO: handle sorting
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(this);
			nestingBrickList.add(phiroProSensorElseBrick);
			nestingBrickList.add(phiroProSensorEndBrick);
		} else {
			nestingBrickList.add(this);
			nestingBrickList.add(phiroProSensorEndBrick);
		}

		return nestingBrickList;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == phiroProSensorElseBrick) {
			return false;
		} else {
			return true;
		}
	}

	public PhiroProSensorElseBrick getPhiroProSensorElseBrick() {
		return phiroProSensorElseBrick;
	}

	public PhiroProSensorEndBrick getPhiroProSensorEndBrick() {
		return phiroProSensorEndBrick;
	}

	public PhiroProSensorBrick getCopy() {
		return copy;
	}

	public void setPhiroProSensorElseBrick(PhiroProSensorElseBrick phiroProSensorElseBrick) {
		this.phiroProSensorElseBrick = phiroProSensorElseBrick;
	}

	public void setPhiroProSensorEndBrick(PhiroProSensorEndBrick phiroProSensorEndBrick) {
		this.phiroProSensorEndBrick = phiroProSensorEndBrick;
	}

	@Override
	public Brick clone() {
		return new PhiroProSensorBrick();
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {

	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_phiro_pro_if_sensor, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_phiro_pro_sensor_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner phiroProSensorSpinner = (Spinner) view.findViewById(R.id.brick_phiro_pro_sensor_action_spinner);

		ArrayAdapter<CharSequence> phiroProSensorAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_phiro_pro_select_sensor_spinner, android.R.layout.simple_spinner_item);
		phiroProSensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (checkbox.getVisibility() == View.VISIBLE) {
			phiroProSensorSpinner.setClickable(false);
			phiroProSensorSpinner.setEnabled(false);
		} else {
			phiroProSensorSpinner.setClickable(true);
			phiroProSensorSpinner.setEnabled(true);
			phiroProSensorSpinner.setOnItemSelectedListener(this);
		}

		phiroProSensorSpinner.setAdapter(phiroProSensorAdapter);
		phiroProSensorSpinner.setSelection(sensorSpinnerPosition);

		phiroProSensorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				sensorSpinnerPosition = position;
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_phiro_pro_sensor_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner phiroProSensorSpinner = (Spinner) view.findViewById(R.id.brick_phiro_pro_sensor_action_spinner);
			phiroProSensorSpinner.getBackground().setAlpha(alphaValue);

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
		sequence.addAction(ExtendedActions.phiroProSendSelectedSensor(sprite, sensorSpinnerPosition));
		return null;
	}
}