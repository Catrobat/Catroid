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
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BroadcastMessageBrick extends BrickBaseType implements
		BrickSpinner.OnItemSelectedListener<StringOption> {

	private transient BrickSpinner<StringOption> spinner;

	public abstract String getBroadcastMessage();

	public abstract void setBroadcastMessage(String broadcastMessage);

	@Override
	public Brick clone() throws CloneNotSupportedException {
		BroadcastMessageBrick clone = (BroadcastMessageBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<String> messages = ProjectManager.getInstance().getCurrentProject()
				.getBroadcastMessageContainer().getBroadcastMessages();

		List<Nameable> items = getSortedItemListFromMessages(context, messages);

		spinner = new BrickSpinner<>(R.id.brick_broadcast_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(getBroadcastMessage());
		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		final AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		TextInputDialog.Builder builder = new TextInputDialog.Builder(activity);

		builder.setHint(activity.getString(R.string.dialog_new_broadcast_message_name))
				.setTextWatcher(new DuplicateInputTextWatcher(new ArrayList()))
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
	public void onStringOptionSelected(Integer spinnerId, String string) {
		setBroadcastMessage(string);
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable StringOption item) {
	}

	@VisibleForTesting
	public TextInputDialog.OnClickListener getOkButtonListener(AppCompatActivity activity) {
		return (dialog, textInput) -> {
			addItem(textInput);
			notifyDataSetChanged(activity);
		};
	}

	@VisibleForTesting
	public DialogInterface.OnClickListener getNegativeButtonListener() {
		return (dialog, which) -> spinner.setSelection(getBroadcastMessage());
	}

	@VisibleForTesting
	public DialogInterface.OnCancelListener getCanceledListener() {
		return dialog -> spinner.setSelection(getBroadcastMessage());
	}

	@VisibleForTesting
	public static List<Nameable> getSortedItemListFromMessages(Context context, List<String> messages) {
		if (messages.isEmpty()) {
			String defaultValue = context.getString(R.string.brick_broadcast_default_value);
			return Collections.singletonList(new StringOption(defaultValue));
		}

		Collections.sort(messages, String::compareToIgnoreCase);

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));

		for (String message : messages) {
			items.add(new StringOption(message));
		}
		return items;
	}
}
