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
import org.catrobat.catroid.devices.arduino.kodey.KodeySensor;

import java.util.ArrayList;
import java.util.List;

public class KodeySensorBrick extends FormulaBrick implements NestingBrick, OnItemSelectedListener {

	private static final long serialVersionUID = 1l;
	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private int sensorSpinnerPosition = 0;
	protected transient KodeySensorElseBrick kodeySensorElseBrick;
	protected transient KodeySensorEndBrick kodeySensorEndBrick;
	private transient KodeySensorBrick copy;
	private static final String TAG = KodeySensor.class.getSimpleName();

	public KodeySensorBrick() {
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_KODEY;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		KodeySensorBrick copyBrick = (KodeySensorBrick) clone();
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_kodey_if_sensor, null);

		Spinner kodeySensorSpinner = (Spinner) prototypeView.findViewById(R.id.brick_kodey_sensor_action_spinner);
		kodeySensorSpinner.setFocusableInTouchMode(false);
		kodeySensorSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> kodeySensorSpinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_kodey_select_sensor_spinner, android.R.layout.simple_spinner_item);
		kodeySensorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		kodeySensorSpinner.setAdapter(kodeySensorSpinnerAdapter);
		kodeySensorSpinner.setSelection(sensorSpinnerPosition);

		return prototypeView;

	}


	@Override
	public boolean isInitialized() {
		if (kodeySensorElseBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		kodeySensorElseBrick = new KodeySensorElseBrick(this);
		kodeySensorEndBrick = new KodeySensorEndBrick(kodeySensorElseBrick, this);
		Log.w(TAG, "Creating if logic stuff");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		//TODO: handle sorting
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(this);
			nestingBrickList.add(kodeySensorElseBrick);
			nestingBrickList.add(kodeySensorEndBrick);
		} else {
			nestingBrickList.add(this);
			nestingBrickList.add(kodeySensorEndBrick);
		}

		return nestingBrickList;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == kodeySensorElseBrick) {
			return false;
		} else {
			return true;
		}
	}

	public KodeySensorElseBrick getKodeySensorElseBrick() {
		return kodeySensorElseBrick;
	}

	public KodeySensorEndBrick getKodeySensorEndBrick() {
		return kodeySensorEndBrick;
	}

	public KodeySensorBrick getCopy() {
		return copy;
	}

	public void setKodeySensorElseBrick(KodeySensorElseBrick kodeySensorElseBrick) {
		this.kodeySensorElseBrick = kodeySensorElseBrick;
	}

	public void setKodeySensorEndBrick(KodeySensorEndBrick kodeySensorEndBrick) {
		this.kodeySensorEndBrick = kodeySensorEndBrick;
	}

	@Override
	public Brick clone() {
		return new KodeySensorBrick();
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

		view = View.inflate(context, R.layout.brick_kodey_if_sensor, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_kodey_sensor_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner kodeySensorSpinner = (Spinner) view.findViewById(R.id.brick_kodey_sensor_action_spinner);

		ArrayAdapter<CharSequence> kodeySensorAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_kodey_select_sensor_spinner, android.R.layout.simple_spinner_item);
		kodeySensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (checkbox.getVisibility() == View.VISIBLE) {
			kodeySensorSpinner.setClickable(false);
			kodeySensorSpinner.setEnabled(false);
		} else {
			kodeySensorSpinner.setClickable(true);
			kodeySensorSpinner.setEnabled(true);
			kodeySensorSpinner.setOnItemSelectedListener(this);
		}

		kodeySensorSpinner.setAdapter(kodeySensorAdapter);
		kodeySensorSpinner.setSelection(sensorSpinnerPosition);

		kodeySensorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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
			View layout = view.findViewById(R.id.brick_kodey_sensor_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner kodeySensorSpinner = (Spinner) view.findViewById(R.id.brick_kodey_sensor_action_spinner);
			kodeySensorSpinner.getBackground().setAlpha(alphaValue);

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
		sequence.addAction(ExtendedActions.kodeySendSelectedSensor(sprite, sensorSpinnerPosition));
		return null;
	}
}