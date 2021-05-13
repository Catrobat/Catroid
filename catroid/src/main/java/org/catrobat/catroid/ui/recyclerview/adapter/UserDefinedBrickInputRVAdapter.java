/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.recyclerview.viewholder.VariableVH;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;

import java.util.List;

import static org.catrobat.catroid.utils.NumberFormats.toMetricUnitRepresentation;

public class UserDefinedBrickInputRVAdapter extends RVAdapter<UserDefinedBrickInput> {

	UserDefinedBrickInputRVAdapter(List<UserDefinedBrickInput> items) {
		super(items);
	}

	@Override
	public CheckableVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		return new VariableVH(view);
	}

	@Override
	public void onBindViewHolder(CheckableVH holder, int position) {
		super.onBindViewHolder(holder, position);
		if (position == 0) {
			((TextView) holder.itemView.findViewById(R.id.headline)).setText(holder.itemView.getResources().getQuantityText(R.plurals.user_defined_brick_input_headline, getItemCount()));
		}

		UserDefinedBrickInput item = items.get(position);
		VariableVH variableVH = (VariableVH) holder;
		variableVH.title.setText(item.getName());

		int value;
		try {
			ProjectManager projectManager = ProjectManager.getInstance();
			Project project = projectManager.getCurrentProject();
			Sprite sprite = projectManager.getCurrentSprite();
			value = item.getValue().interpretInteger(new Scope(project, sprite, null));
		} catch (InterpretationException e) {
			value = 0;
		}
		variableVH.value.setText(toMetricUnitRepresentation(value));
	}
}
