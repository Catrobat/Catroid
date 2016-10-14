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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class ChooseCameraBrick extends BrickBaseType {

	private transient View prototypeView;

	private String[] spinnerValues;
	private int spinnerSelectionID;
	private static final int BACK = 0;
	private static final int FRONT = 1;

	public ChooseCameraBrick() {
		spinnerValues = new String[2];
		spinnerSelectionID = FRONT;
	}

	public ChooseCameraBrick(int frontOrBack) {
		spinnerValues = new String[2];
		spinnerSelectionID = frontOrBack;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_choose_camera, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_choose_camera__prototype_text_view),
				context.getString(R.string.category_looks));

		setCheckboxView(R.id.brick_choose_camera_checkbox);
		Spinner videoSpinner = (Spinner) view.findViewById(R.id.brick_choose_camera_spinner);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);

		videoSpinner.setAdapter(spinnerAdapter);

		videoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				spinnerSelectionID = position;

				TextView spinnerText = (TextView) adapterView.getChildAt(0);
				TextSizeUtil.enlargeTextView(spinnerText);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});

		videoSpinner.setSelection(spinnerSelectionID);

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_choose_camera, null);

		Spinner setVideoSpinner = (Spinner) prototypeView.findViewById(R.id.brick_choose_camera_spinner);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		setVideoSpinner.setAdapter(spinnerAdapter);
		setVideoSpinner.setSelection(spinnerSelectionID);

		return prototypeView;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		spinnerValues[BACK] = context.getString(R.string.choose_camera_back);
		spinnerValues[FRONT] = context.getString(R.string.choose_camera_front);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerValues);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return spinnerAdapter;
	}

	@Override
	public int getRequiredResources() {
		if (spinnerSelectionID == FRONT) {
			return CAMERA_FRONT;
		}
		return CAMERA_BACK;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		if (spinnerSelectionID == FRONT) {
			sequence.addAction(sprite.getActionFactory().createSetFrontCameraAction());
			return null;
		}
		sequence.addAction(sprite.getActionFactory().createSetBackCameraAction());
		return null;
	}
}
