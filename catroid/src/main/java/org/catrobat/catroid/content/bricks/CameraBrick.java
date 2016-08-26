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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class CameraBrick extends BrickBaseType {

	private static final int OFF = 0;
	private static final int ON = 1;

	private transient View prototypeView;

	private String[] spinnerValues;
	private int spinnerSelectionID;

	public CameraBrick() {
		spinnerValues = new String[2];
		spinnerSelectionID = ON;
	}

	public CameraBrick(int onOrOff) {
		spinnerValues = new String[2];
		spinnerSelectionID = onOrOff;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		final Brick brickInstance = this;
		view = View.inflate(context, R.layout.brick_video, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_video_checkbox);
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner videoSpinner = (Spinner) view.findViewById(R.id.brick_video_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			videoSpinner.setClickable(true);
			videoSpinner.setEnabled(true);
		} else {
			videoSpinner.setClickable(false);
			videoSpinner.setEnabled(false);
		}

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

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_video, null);

		Spinner setVideoSpinner = (Spinner) prototypeView.findViewById(R.id.brick_video_spinner);
		setVideoSpinner.setFocusableInTouchMode(false);
		setVideoSpinner.setFocusable(false);
		setVideoSpinner.setEnabled(false);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		setVideoSpinner.setAdapter(spinnerAdapter);
		setVideoSpinner.setSelection(spinnerSelectionID);

		return prototypeView;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		spinnerValues[OFF] = context.getString(R.string.video_brick_camera_off);
		spinnerValues[ON] = context.getString(R.string.video_brick_camera_on);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerValues);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return spinnerAdapter;
	}

	@Override
	public int getRequiredResources() {
		return Brick.VIDEO;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createUpdateCameraPreviewAction(getCameraStateFromSpinner()));
		return null;
	}

	private CameraManager.CameraState getCameraStateFromSpinner() {
		if (spinnerSelectionID == OFF) {
			return CameraManager.CameraState.stopped;
		}

		return CameraManager.CameraState.prepare;
	}
}
