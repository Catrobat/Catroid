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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.catrobat.catroid.R;

public abstract class DroneBasicLookBrick extends BrickBaseType {

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_drone_look, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_drone_basic_look_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView label = (TextView) view.findViewById(R.id.ValueTextViewLook);
		label.setText(getBrickLabel(view));

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_drone_look, null);

		TextView label = (TextView) prototypeView.findViewById(R.id.ValueTextViewLook);
		label.setText(getBrickLabel(prototypeView));

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_drone_basic_look_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = alphaValue;

			TextView label = (TextView) view.findViewById(R.id.ValueTextViewLook);
			label.setText(getBrickLabel(view));
		}
		return view;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}

	protected abstract String getBrickLabel(View view);
}
