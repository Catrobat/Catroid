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

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.NonEmptyStringTextWatcher;

import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastMessageBrick extends BrickBaseType implements
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
		final SpriteActivity activity = (SpriteActivity) UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}
		TextInputDialog.Builder builder = new TextInputDialog.Builder(activity);

		builder.setHint(activity.getString(R.string.dialog_new_broadcast_message_name))
				.setTextWatcher(new NonEmptyStringTextWatcher())
				.setPositiveButton(activity.getString(R.string.ok), getOkButtonListener(activity))
				.setTitle(R.string.dialog_new_broadcast_message_title)
				.setNegativeButton(R.string.cancel, getNegativeButtonListener())
				.setOnCancelListener(getCanceledListener())
				.show();
	}

	public void addItem(String item) {
		if (ProjectManager.getInstance().getCurrentProject().getBroadcastMessageContainer().addBroadcastMessage(item)) {
			spinner.add(new StringOption(item));
		}
		spinner.setSelection(item);
	}

	@Override
	public void onStringOptionSelected(String string) {
		setBroadcastMessage(string);
	}

	@Override
	public void onItemSelected(@Nullable StringOption item) {
	}

	@VisibleForTesting
	public TextInputDialog.OnClickListener getOkButtonListener(final SpriteActivity activity) {
		return new TextInputDialog.OnClickListener() {
			@Override
			public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
				addItem(textInput);
				notifyDataSetChanged(activity);
			}
		};
	}

	@VisibleForTesting
	public DialogInterface.OnClickListener getNegativeButtonListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				spinner.setSelection(getBroadcastMessage());
			}
		};
	}

	@VisibleForTesting
	public DialogInterface.OnCancelListener getCanceledListener() {
		return new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				spinner.setSelection(getBroadcastMessage());
			}
		};
	}
}
