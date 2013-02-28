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
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetBrightnessBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private double brightness;
	private Sprite sprite;

	private transient View view;
	private transient CheckBox checkbox;
	private transient boolean checked;

	public SetBrightnessBrick() {

	}

	public SetBrightnessBrick(Sprite sprite, double brightnessValue) {
		this.sprite = sprite;
		this.brightness = brightnessValue;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		sprite.look.setBrightnessValue((float) this.brightness / 100);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public double getBrightnessValue() {
		return brightness;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.brick_set_brightness, null);

			checkbox = (CheckBox) view.findViewById(R.id.brick_set_brightness_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});

			TextView textX = (TextView) view.findViewById(R.id.brick_set_brightness_prototype_text_view);
			EditText editX = (EditText) view.findViewById(R.id.brick_set_brightness_edit_text);
			editX.setText(String.valueOf(brightness));

			textX.setVisibility(View.GONE);
			editX.setVisibility(View.VISIBLE);

			editX.setOnClickListener(this);
		}
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_brightness, null);
	}

	@Override
	public Brick clone() {
		return new SetBrightnessBrick(getSprite(), getBrightnessValue());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_brightness_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(brightness));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					brightness = Double.parseDouble(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_set_brightness_brick");
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
		}
	}

	private transient BrickAdapter adapter;

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}
}
