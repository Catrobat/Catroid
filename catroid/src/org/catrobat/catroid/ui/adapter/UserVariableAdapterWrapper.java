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
package org.catrobat.catroid.ui.adapter;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserVariable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserVariableAdapterWrapper extends BaseAdapter {

	UserVariableAdapter userVariableAdapter;
	Context context;
	private boolean isTouchInDropDownView;

	public UserVariableAdapterWrapper(Context context, UserVariableAdapter userVariableAdapter) {
		this.context = context;
		this.userVariableAdapter = userVariableAdapter;
	}

	@Override
	public int getCount() {
		return userVariableAdapter.getCount() + 1;
	}

	@Override
	public UserVariable getItem(int position) {
		if (position == 0) {
			return null;
		}
		return userVariableAdapter.getItem(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getPositionOfItem(UserVariable item) {
		return userVariableAdapter.getPositionOfItem(item) + 1;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		TextView text1;
		if (position == 0) {
			if (view == null) {
				view = View.inflate(context, userVariableAdapter.getItemLayout(), null);
				text1 = (TextView) view.findViewById(android.R.id.text1);
				view.setTag(text1);
			} else {
				text1 = (TextView) view.findViewById(android.R.id.text1);
			}
			text1.setText(context.getString(R.string.brick_variable_spinner_create_new_variable));

		} else {
			view = userVariableAdapter.getView(position - 1, convertView, parent);
		}
		return view;
	}

	@Override
	public View getDropDownView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		TextView text1;
		if (position == 0) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
				text1 = (TextView) view.findViewById(android.R.id.text1);
				view.setTag(text1);
			} else {
				text1 = (TextView) view.findViewById(android.R.id.text1);
			}
			text1.setText(context.getString(R.string.brick_variable_spinner_create_new_variable));

		} else {
			view = userVariableAdapter.getDropDownView(position - 1, convertView, parent);
		}

		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
				isTouchInDropDownView = true;
				return false;
			}
		});
		return view;
	}

	public void setItemLayout(int itemLayout, int textViewId) {
		userVariableAdapter.setItemLayout(itemLayout, textViewId);
	}

	public boolean isTouchInDropDownView() {
		return isTouchInDropDownView;
	}

	public void resetIsTouchInDropDownView() {
		isTouchInDropDownView = false;
	}

}
