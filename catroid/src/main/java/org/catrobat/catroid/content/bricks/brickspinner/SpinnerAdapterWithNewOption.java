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

package org.catrobat.catroid.content.bricks.brickspinner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.List;

public class SpinnerAdapterWithNewOption extends ArrayAdapter<String> implements SpinnerAdapter {

	@Nullable
	private OnNewOptionInDropDownClickListener onDropDownItemClickListener;

	public SpinnerAdapterWithNewOption(@NonNull Context context, @NonNull List<String> objects) {
		super(context, android.R.layout.simple_spinner_item);
		super.add(context.getString(R.string.new_broadcast_message));
		super.addAll(objects);
	}

	public void setOnDropDownItemClickListener(@Nullable OnNewOptionInDropDownClickListener onDropDownItemClickListener) {
		this.onDropDownItemClickListener = onDropDownItemClickListener;
	}

	@Override
	public View getDropDownView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext())
					.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		}

		((TextView) convertView).setText(getItem(position));
		convertView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionIndex()) {
					case MotionEvent.ACTION_DOWN:
						if (position == 0 && onDropDownItemClickListener != null) {
							return onDropDownItemClickListener.onNewOptionInDropDownClicked(v);
						}
						break;
				}
				return false;
			}
		});

		return convertView;
	}

	@Override
	public int getPosition(@Nullable String item) {
		int position = super.getPosition(item);

		if (position == -1) {
			if (getCount() > 1) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return position;
		}
	}

	public interface OnNewOptionInDropDownClickListener {

		boolean onNewOptionInDropDownClicked(View v);
	}
}
