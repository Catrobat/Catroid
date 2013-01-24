/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeVolumeByNBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private double volume;

	private transient View view;
	private transient View prototype;

	public ChangeVolumeByNBrick() {

	}

	public ChangeVolumeByNBrick(Sprite sprite, double changeVolume) {
		this.sprite = sprite;
		this.volume = changeVolume;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		float currentVolume = SoundManager.getInstance().getVolume();
		currentVolume += volume;
		if (currentVolume < 0.0f) {
			currentVolume = 0.0f;
		} else if (currentVolume > 100.0f) {
			currentVolume = 100.0f;
		}
		SoundManager.getInstance().setVolume(currentVolume);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public double getVolume() {
		return volume;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_change_volume_by, null);

		TextView text = (TextView) view.findViewById(R.id.brick_change_volume_by_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_change_volume_by_edit_text);
		edit.setText(String.valueOf(volume));

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return prototype;
	}

	@Override
	public Brick clone() {
		return new ChangeVolumeByNBrick(getSprite(), getVolume());
	}

	@Override
	public void setDefaultValues(Context context) {
		prototype = View.inflate(context, R.layout.brick_change_volume_by, null);
		TextView textSetVolumenTo = (TextView) prototype.findViewById(R.id.brick_change_volume_by_prototype_text_view);
		textSetVolumenTo.setText(volume + "");
	}

	@Override
	public void onClick(View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(volume));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
						| InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					volume = Float.parseFloat(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_change_volume_by_brick");
	}
}
