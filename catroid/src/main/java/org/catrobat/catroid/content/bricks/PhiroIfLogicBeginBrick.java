/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhiroIfLogicBeginBrick extends BrickBaseType implements IfElseLogicBeginBrick {

	private static final long serialVersionUID = 1L;

	private int sensorSpinnerPosition = 0;

	private transient IfLogicElseBrick ifElseBrick;
	private transient IfLogicEndBrick ifEndBrick;

	public PhiroIfLogicBeginBrick() {
	}

	public IfLogicElseBrick getIfElseBrick() {
		return ifElseBrick;
	}

	public void setIfElseBrick(IfLogicElseBrick elseBrick) {
		this.ifElseBrick = elseBrick;
	}

	public IfLogicEndBrick getIfEndBrick() {
		return ifEndBrick;
	}

	public void setIfEndBrick(IfLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		PhiroIfLogicBeginBrick clone = (PhiroIfLogicBeginBrick) super.clone();
		clone.ifElseBrick = null;
		clone.ifEndBrick = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_phiro_if_sensor;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		Spinner spinner = view.findViewById(R.id.brick_phiro_sensor_action_spinner);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(),
				R.array.brick_phiro_select_sensor_spinner,
				android.R.layout.simple_spinner_item);

		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(sensorSpinnerPosition);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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
	public Brick getFirstBrick() {
		return this;
	}

	@Override
	public Brick getLastBrick() {
		return ifEndBrick;
	}

	@Override
	public List<Brick> getAllParts() {
		List<Brick> parts = new ArrayList<>();
		parts.add(this);
		parts.add(ifElseBrick);
		parts.add(ifEndBrick);
		return parts;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_PHIRO);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction ifAction = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());
		ScriptSequenceAction elseAction = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());
		Action action = sprite.getActionFactory().createPhiroSendSelectedSensorAction(sprite, sensorSpinnerPosition,
				ifAction, elseAction);
		sequence.addAction(action);

		LinkedList<ScriptSequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(elseAction);
		returnActionList.add(ifAction);

		return returnActionList;
	}
}
