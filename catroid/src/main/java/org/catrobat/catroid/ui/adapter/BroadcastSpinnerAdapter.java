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

package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;

public class BroadcastSpinnerAdapter extends ArrayAdapter<String> {

	public BroadcastSpinnerAdapter(@NonNull Context context) {
		super(context, android.R.layout.simple_spinner_item);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		update();
	}

	public void update() {
		clear();
		add(getContext().getString(R.string.new_broadcast_message));
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		addAll(currentProject.getBroadcastMessageContainer().getBroadcastMessages());
		if (getCount() < 2) {
			add(getContext().getString(R.string.brick_broadcast_default_value));
		}
	}

	@Override
	public void insert(@Nullable String object, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(@Nullable String object) {
		int position = getPosition(object);
		if (position < 0) {
			if (!getContext().getString(R.string.new_broadcast_message).equals(object)) {
				Project currentProject = ProjectManager.getInstance().getCurrentProject();
				currentProject.getBroadcastMessageContainer().addBroadcastMessage(object);
			}
			super.add(object);
		}
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}
}
