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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

public class CameraBrick extends BrickBaseType {

	private static final int OFF = 0;
	private static final int ON = 1;

	private int spinnerSelectionID;

	public CameraBrick() {
		spinnerSelectionID = ON;
	}

	public CameraBrick(int onOrOff) {
		spinnerSelectionID = onOrOff;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_video;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		Spinner videoSpinner = view.findViewById(R.id.brick_video_spinner);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		videoSpinner.setAdapter(spinnerAdapter);
		videoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				spinnerSelectionID = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});

		videoSpinner.setSelection(spinnerSelectionID);

		return view;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		String[] spinnerValues = new String[2];
		spinnerValues[OFF] = context.getString(R.string.video_brick_camera_off);
		spinnerValues[ON] = context.getString(R.string.video_brick_camera_on);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerValues);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return spinnerAdapter;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(VIDEO);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createUpdateCameraPreviewAction(spinnerSelectionID == ON));
	}
}
