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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhiroIfLogicBeginBrick extends IfLogicBeginBrick implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	protected transient IfLogicElseBrick ifElseBrick;
	protected transient IfLogicEndBrick ifEndBrick;
	private transient PhiroIfLogicBeginBrick copy;
	private int sensorSpinnerPosition = 0;

	public PhiroIfLogicBeginBrick() {
		addAllowedBrickField(BrickField.IF_PHIRO_SENSOR_CONDITION);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_PHIRO;
	}

	public IfLogicElseBrick getIfElseBrick() {
		return ifElseBrick;
	}

	public IfLogicEndBrick getIfEndBrick() {
		return ifEndBrick;
	}

	public PhiroIfLogicBeginBrick getCopy() {
		return copy;
	}

	public void setIfElseBrick(IfLogicElseBrick elseBrick) {
		this.ifElseBrick = elseBrick;
	}

	public void setIfEndBrick(IfLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public Brick clone() {
		return new PhiroIfLogicBeginBrick();
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

		view = View.inflate(context, R.layout.brick_phiro_if_sensor, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_phiro_sensor_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner phiroProSensorSpinner = (Spinner) view.findViewById(R.id.brick_phiro_sensor_action_spinner);

		ArrayAdapter<CharSequence> phiroProSensorAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_phiro_select_sensor_spinner, android.R.layout.simple_spinner_item);
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
			View layout = view.findViewById(R.id.brick_phiro_sensor_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner phiroProSensorSpinner = (Spinner) view.findViewById(R.id.brick_phiro_sensor_action_spinner);
			phiroProSensorSpinner.getBackground().setAlpha(alphaValue);

			TextView ifLabel = (TextView) view.findViewById(R.id.brick_phiro_sensor_label);
			TextView ifLabelEnd = (TextView) view.findViewById(R.id.phiro_sensor_label_second_part);

			ifLabel.setTextColor(ifLabel.getTextColors().withAlpha(alphaValue));
			ifLabelEnd.setTextColor(ifLabelEnd.getTextColors().withAlpha(alphaValue));
			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_phiro_if_sensor, null);

		Spinner phiroProSensorSpinner = (Spinner) prototypeView.findViewById(R.id.brick_phiro_sensor_action_spinner);
		phiroProSensorSpinner.setFocusableInTouchMode(false);
		phiroProSensorSpinner.setFocusable(false);
		phiroProSensorSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> phiroProSensorSpinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_phiro_select_sensor_spinner, android.R.layout.simple_spinner_item);
		phiroProSensorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		phiroProSensorSpinner.setAdapter(phiroProSensorSpinnerAdapter);
		phiroProSensorSpinner.setSelection(sensorSpinnerPosition);

		return prototypeView;
	}

	@Override
	public boolean isInitialized() {
		return ifElseBrick != null;
	}

	@Override
	public void initialize() {
		ifElseBrick = new IfLogicElseBrick(this);
		ifEndBrick = new IfLogicEndBrick(ifElseBrick, this);
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		//TODO: handle sorting
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(this);
			nestingBrickList.add(ifElseBrick);
			nestingBrickList.add(ifEndBrick);
		} else {
			nestingBrickList.add(this);
			nestingBrickList.add(ifEndBrick);
		}

		return nestingBrickList;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		return ifElseBrick != null;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		SequenceAction ifAction = ExtendedActions.sequence();
		SequenceAction elseAction = ExtendedActions.sequence();
		Action action = ExtendedActions.phiroSendSelectedSensor(sprite, sensorSpinnerPosition, ifAction, elseAction);
		sequence.addAction(action);

		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add(elseAction);
		returnActionList.add(ifAction);

		return returnActionList;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		//ifEndBrick and ifElseBrick will be set in the copyBrickForSprite method of IfLogicEndBrick
		PhiroIfLogicBeginBrick copyBrick = (PhiroIfLogicBeginBrick) clone(); //Using the clone method because of its flexibility if new fields are added
		copyBrick.ifElseBrick = null; //if the Formula gets a field sprite, a separate copy method will be needed
		copyBrick.ifEndBrick = null;
		this.copy = copyBrick;
		return copyBrick;
	}
}
