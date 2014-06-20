/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.PreStageActivity;


public class ExecuteOnceDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "DIALOG_EXECUTED_ONCE";
	public static final String STARTED_FROM_DIALOG = "STARTED_FROM_DIALOG";

	private Dialog executedOnceDialog;
	FragmentActivity fragmentActivity = null;

	public ExecuteOnceDialog(FragmentActivity fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_execute_once, null);

		executedOnceDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.execute_once_title)
				.setPositiveButton(R.string.execute_once_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		executedOnceDialog.setCanceledOnTouchOutside(true);
		executedOnceDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		executedOnceDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		executedOnceDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						handleExecuteButtonClick();
					}
				});

				Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
				negativeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						dismiss();
					}
				});
			}
		});

		return executedOnceDialog;
	}

	protected void handleExecuteButtonClick() {

		ProjectManager.getInstance().getCurrentProject().getUserVariables().resetAllUserVariables();
		Intent intent = new Intent(fragmentActivity, PreStageActivity.class);
		intent.putExtra(STARTED_FROM_DIALOG, DIALOG_FRAGMENT_TAG);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
		dismiss();
	}
}




