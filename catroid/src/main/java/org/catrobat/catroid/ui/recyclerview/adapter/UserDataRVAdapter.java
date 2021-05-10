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

package org.catrobat.catroid.ui.recyclerview.adapter;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.recyclerview.viewholder.VariableVH;

import java.util.List;

import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class UserDataRVAdapter<T extends UserData> extends RVAdapter<T> {

	UserDataRVAdapter(List<T> items) {
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

		UserData item = items.get(position);
		VariableVH variableVH = (VariableVH) holder;
		variableVH.title.setText(item.getName());
		variableVH.value.setText(trimTrailingCharacters(item.getValue().toString()));
		variableVH.arrowButton.setOnClickListener(v -> {

			View alertView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_variable, null);
			AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
			TextView title = alertView.findViewById(R.id.variable_name);
			TextView value = alertView.findViewById(R.id.variable_value);
			Button okButton = alertView.findViewById(R.id.ok_btn);
			title.setText(variableVH.title.getText().toString());
			value.setText(variableVH.value.getText().toString());
			alert.setView(alertView);
			//Show alert
			final AlertDialog alertDialog = alert.show();
			alertDialog.setCanceledOnTouchOutside(true);
			okButton.setOnClickListener(v1 -> alertDialog.dismiss());
		});
	}
}
