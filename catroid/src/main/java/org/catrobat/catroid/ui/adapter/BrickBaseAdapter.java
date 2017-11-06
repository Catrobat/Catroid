/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BrickBaseAdapter extends BaseAdapter {

	protected Context context;
	protected ScriptFragment scriptFragment;
	protected AddBrickFragment addBrickFragment;
	protected Button okButtonDelete;
	protected List<Brick> checkedBricks = new ArrayList<>();
	protected List<Brick> brickList;

	protected void showNewGroupBackPackDialog(final List<String> backPackedItems, final boolean backPackUserBricks) {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(context);
		builder.setTitle(R.string.new_group);
		View view = View.inflate(context, R.layout.new_group_dialog, null);
		builder.setView(view);
		final EditText groupNameEditText = (EditText) view.findViewById(R.id.new_group_dialog_group_name);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String groupName = groupNameEditText.getText().toString().trim();
				if (backPackedItems.contains(groupName)) {
					showScriptGroupNameAlreadyGivenDialog(backPackedItems, backPackUserBricks);
				} else {
					backPackScript(groupName);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				scriptFragment.clearCheckedBricksAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();

		groupNameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence groupName, int start, int before, int count) {
				if (groupName.toString().trim().isEmpty()) {
					okButtonDelete.setEnabled(false);
				} else {
					okButtonDelete.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		alertDialog.show();
		okButtonDelete = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		okButtonDelete.setEnabled(false);
	}

	private void showScriptGroupNameAlreadyGivenDialog(final List<String> backPackedItems, final boolean backPackUserBricks) {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(context);
		builder.setTitle(R.string.new_group);
		View view = View.inflate(context, R.layout.new_group_name_given_dialog, null);
		builder.setView(view);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				showNewGroupBackPackDialog(backPackedItems, backPackUserBricks);
			}
		});

		AlertDialog alertDialog = builder.create();

		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		alertDialog.show();
	}

	private void backPackScript(String groupName) {
		if (!checkedBricks.isEmpty()) {

			ScriptController scriptController = new ScriptController();
			try {
				scriptController.pack(groupName, checkedBricks);
				scriptFragment.clearCheckedBricksAndEnableButtons();
				ToastUtil.showSuccess(context, context.getString(R.string.packed_script_group));
				startBackPackActivity(BackpackActivity.FRAGMENT_SCRIPTS);
			} catch (IOException | CloneNotSupportedException e) {
				Log.e("BrickBaseAdapter", Log.getStackTraceString(e));
			}

			scriptFragment.clearCheckedBricksAndEnableButtons();
		}
	}

	private void startBackPackActivity(int fragment) {
		Intent intent = new Intent(context, BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, fragment);
		context.startActivity(intent);
	}
}
