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
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewBroadcastMessageDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastMessageBrick extends BrickBaseType implements NewItemInterface<String>,
		BrickSpinner.OnItemSelectedListener<StringOption> {

	private transient BrickSpinner<StringOption> spinner;

	public abstract String getBroadcastMessage();

	public abstract void setBroadcastMessage(String broadcastMessage);

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		BroadcastMessageBrick clone = (BroadcastMessageBrick) super.clone();
		clone.spinner = null;
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

		if (messages.isEmpty()) {
			messages.add(context.getString(R.string.brick_broadcast_default_value));
		}

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		for (String message : messages) {
			items.add(new StringOption(message));
		}

		spinner = new BrickSpinner<>(R.id.brick_broadcast_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(getBroadcastMessage());
		return view;
	}

	@Override
	public void onNewOptionSelected() {
		new NewBroadcastMessageDialogFragment(this) {

			@Override
			public void onDismiss(DialogInterface dialog) {
				super.onDismiss(dialog);
				spinner.setSelection(getBroadcastMessage());
			}

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(getBroadcastMessage());
			}
		}.show(((Activity) view.getContext()).getFragmentManager(), NewBroadcastMessageDialogFragment.TAG);
	}

	@Override
	public void addItem(String item) {
		if (ProjectManager.getInstance().getCurrentProject().getBroadcastMessageContainer().addBroadcastMessage(item)) {
			spinner.add(new StringOption(item));
		}
		spinner.setSelection(item);
		//TODO: This should work some other way: i.e. it should not rely on the Brick being able to access its adapter.
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onStringOptionSelected(String string) {
		setBroadcastMessage(string);
	}

	@Override
	public void onItemSelected(@Nullable StringOption item) {
	}
}
