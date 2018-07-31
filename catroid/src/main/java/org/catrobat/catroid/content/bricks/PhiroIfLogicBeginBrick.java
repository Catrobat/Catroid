/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import java.util.LinkedList;
import java.util.List;

public class PhiroIfLogicBeginBrick extends IfLogicBeginBrick implements OnItemSelectedListener {

	private static final long serialVersionUID = 1L;

	private int sensorSpinnerPosition = 0;

	public PhiroIfLogicBeginBrick() {
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_PHIRO;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_phiro_if_sensor;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		Spinner phiroProSensorSpinner = prototypeView.findViewById(R.id.brick_phiro_sensor_action_spinner);

		ArrayAdapter<CharSequence> phiroProSensorSpinnerAdapter = ArrayAdapter
				.createFromResource(prototypeView.getContext(),
						R.array.brick_phiro_select_sensor_spinner,
						android.R.layout.simple_spinner_item);

		phiroProSensorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		phiroProSensorSpinner.setAdapter(phiroProSensorSpinnerAdapter);
		phiroProSensorSpinner.setSelection(sensorSpinnerPosition);
		return prototypeView;
	}

	@Override
	protected void onSuperGetViewCalled(Context context) {
		Spinner phiroProSensorSpinner = view.findViewById(R.id.brick_phiro_sensor_action_spinner);

		ArrayAdapter<CharSequence> phiroProSensorAdapter = ArrayAdapter.createFromResource(view.getContext(),
				R.array.brick_phiro_select_sensor_spinner,
				android.R.layout.simple_spinner_item);

		phiroProSensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
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
