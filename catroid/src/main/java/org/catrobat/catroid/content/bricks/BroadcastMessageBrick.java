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
package org.catrobat.catroid.content.bricks;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerAdapterWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewBroadcastMessageDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.List;

public abstract class BroadcastMessageBrick extends BrickBaseType implements NewItemInterface<String>,
		SpinnerAdapterWithNewOption.OnNewOptionInDropDownClickListener {

	private transient SpinnerAdapterWithNewOption spinnerAdapter;

	public abstract String getBroadcastMessage();

	public abstract void setBroadcastMessage(String broadcastMessage);

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		BroadcastMessageBrick clone = (BroadcastMessageBrick) super.clone();
		clone.spinnerAdapter = null;
		return clone;
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<String> messages = ProjectManager.getInstance().getCurrentProject()
				.getBroadcastMessageContainer().getBroadcastMessages();

		Spinner spinner = view.findViewById(R.id.brick_broadcast_spinner);
		spinnerAdapter = new SpinnerAdapterWithNewOption(context, messages);
		spinnerAdapter.setOnDropDownItemClickListener(this);

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position != 0) {
					setBroadcastMessage(spinnerAdapter.getItem(position));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setSelection(spinnerAdapter.getPosition(getBroadcastMessage()));
		return view;
	}

	@Override
	public boolean onNewOptionInDropDownClicked(View v) {
		new NewBroadcastMessageDialogFragment(this)
				.show(((Activity) v.getContext()).getFragmentManager(), NewBroadcastMessageDialogFragment.TAG);
		return false;
	}

	@Override
	public void addItem(String item) {
		ProjectManager.getInstance().getCurrentProject().getBroadcastMessageContainer().addBroadcastMessage(item);
		spinnerAdapter.add(item);
		setBroadcastMessage(item);
		Spinner spinner = view.findViewById(R.id.brick_broadcast_spinner);
		spinner.setSelection(spinnerAdapter.getPosition(getBroadcastMessage()));
	}
}
