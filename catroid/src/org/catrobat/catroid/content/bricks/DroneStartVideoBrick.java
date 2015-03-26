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
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import java.util.List;

/**
 * Created by Lukas on 24.03.2015.
 */



public class DroneStartVideoBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;
	private transient AdapterView<?> adapterView;
	private int cameraId = 0;

	public DroneStartVideoBrick() {

	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		DroneStartVideoBrick copyBrick = (DroneStartVideoBrick) clone();
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new DroneStartVideoBrick();
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_drone_start_video, null);
		Spinner spin = (Spinner) view.findViewById(R.id.brick_drone_start_video_spinner);
		spin.setFocusableInTouchMode(false);
		spin.setFocusable(false);
		SpinnerAdapter adapter = getArrayAdapterVideoCameras(context);
		spin.setAdapter(adapter);
		setSpinnerSelection(spin);
		return view;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_drone_start_video, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_drone_start_video_checkbox);

		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(DroneStartVideoBrick.this, isChecked);
			}
		});


		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_drone_start_video_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);



		final ArrayAdapter<String> spinnerAdapter = getArrayAdapterVideoCameras(context);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(spinnerAdapter);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			spinner.setClickable(true);
			spinner.setEnabled(true);
		}
		else {
			spinner.setClickable(false);
			spinner.setEnabled(false);
		}

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cameraId = (int) parent.getSelectedItemId();

				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(spinner);
		return view;
	}

	private ArrayAdapter<String> getArrayAdapterVideoCameras(Context context)
	{
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter.add(context.getString(R.string.drone_front_facing_camera));
		arrayAdapter.add(context.getString(R.string.drone_bottom_facing_camera));

		return arrayAdapter;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {

			View layout = view.findViewById(R.id.brick_drone_start_video_layout);
			layout.getBackground().setAlpha(alphaValue);

			TextView txt = (TextView) view.findViewById(R.id.brick_drone_start_video_label);
			txt.setTextColor(txt.getTextColors().withAlpha(alphaValue));
			Spinner spin = (Spinner) view.findViewById(R.id.brick_drone_start_video_spinner);
			ColorStateList color = txt.getTextColors().withAlpha(alphaValue);
			spin.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.droneStartVideo(sprite, cameraId));
		return null;
	}


	private void setSpinnerSelection(Spinner spinner) {

		spinner.setSelection(cameraId, true);
	}
}

