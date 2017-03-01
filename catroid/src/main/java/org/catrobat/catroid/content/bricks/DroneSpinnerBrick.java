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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.DynamicTextSizeArrayAdapter;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.ArrayList;

public abstract class DroneSpinnerBrick extends BrickBaseType {
	private static final String TAG = DroneSpinnerBrick.class.getSimpleName();

	protected transient AdapterView<?> adapterView;
	protected String selectedMessage;
	protected int spinnerPosition = 0;

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_drone_spinner, null);

		IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_drone_spinner_label),
				context.getString(R.string.category_drone));

		setCheckboxView(R.id.brick_drone_spinner_checkbox);

		Spinner spinner = (Spinner) view.findViewById(R.id.brick_drone_spinner_ID);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);

		DynamicTextSizeArrayAdapter<String> arrayAdapter = new DynamicTextSizeArrayAdapter<String>(view.getContext(),
				android.R.layout.simple_spinner_item, getSpinnerItems(view));
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(arrayAdapter);

		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(spinnerPosition);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedMessage = parent.getItemAtPosition(position).toString();
				spinnerPosition = position;
				Log.d(TAG, "selected message = "
						+ selectedMessage + " on position: " + spinnerPosition);

				TextView spinnerText = (TextView) adapterView.getChildAt(0);
				TextSizeUtil.enlargeTextView(spinnerText);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		TextView label = (TextView) view.findViewById(R.id.brick_drone_spinner_label);
		label.setText(getBrickLabel(view));

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_drone_spinner, null);

		Spinner spinner = (Spinner) prototypeView.findViewById(R.id.brick_drone_spinner_ID);

		DynamicTextSizeArrayAdapter<String> arrayAdapter = new DynamicTextSizeArrayAdapter<String>(prototypeView.getContext(),
				android.R.layout.simple_spinner_item, getSpinnerItems(prototypeView));
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(spinnerPosition);

		TextView label = (TextView) prototypeView.findViewById(R.id.brick_drone_spinner_label);
		label.setText(getBrickLabel(prototypeView));

		return prototypeView;
	}

	public void setSpinnerPosition(int spinnerPosition) {
		this.spinnerPosition = spinnerPosition;
	}

	@Override
	public int getRequiredResources() {
		return ARDRONE_SUPPORT;
	}

	protected abstract String getBrickLabel(View view);

	protected abstract ArrayList<String> getSpinnerItems(View view);
}
