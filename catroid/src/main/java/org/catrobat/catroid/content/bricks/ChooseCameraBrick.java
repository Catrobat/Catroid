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
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class ChooseCameraBrick extends BrickBaseType {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({BACK, FRONT})
	@interface CameraSelection {}
	private static final int BACK = 0;
	private static final int FRONT = 1;

	@CameraSelection
	private int spinnerSelectionID;

	public ChooseCameraBrick() {
		spinnerSelectionID = FRONT;
	}

	public ChooseCameraBrick(@CameraSelection int frontOrBack) {
		spinnerSelectionID = frontOrBack;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_choose_camera;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		Spinner spinner = view.findViewById(R.id.brick_choose_camera_spinner);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdapter.add(context.getString(R.string.choose_camera_back));
		spinnerAdapter.add(context.getString(R.string.choose_camera_front));

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				spinnerSelectionID = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});

		spinner.setSelection(spinnerSelectionID);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public int getRequiredResources() {
		if (spinnerSelectionID == FRONT) {
			return CAMERA_FRONT;
		}
		return CAMERA_BACK;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (spinnerSelectionID == FRONT) {
			sequence.addAction(sprite.getActionFactory().createSetFrontCameraAction());
		} else {
			sequence.addAction(sprite.getActionFactory().createSetBackCameraAction());
		}
		return null;
	}
}
