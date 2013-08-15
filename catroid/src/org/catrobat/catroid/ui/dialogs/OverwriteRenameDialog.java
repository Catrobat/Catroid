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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.Utils;

public class OverwriteRenameDialog extends DialogFragment implements OnClickListener {
	protected RadioButton replaceButton, renameButton;
	protected String programName, url;
	protected Context context;
	protected EditText projectText;

	public static final String DIALOG_FRAGMENT_TAG = "overwrite_rename_look";

	public OverwriteRenameDialog(Context context, String programName, String url) {
		super();
		this.programName = programName;
		this.url = url;
		this.context = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_overwrite_project, null);

		replaceButton = (RadioButton) dialogView.findViewById(R.id.dialog_overwrite_project_radio_replace);
		replaceButton.setOnClickListener(this);
		renameButton = (RadioButton) dialogView.findViewById(R.id.dialog_overwrite_project_radio_rename);
		renameButton.setOnClickListener(this);
		projectText = (EditText) dialogView.findViewById(R.id.dialog_overwrite_project_edit);
		projectText.setText(programName);

		Dialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setTitle(R.string.overwrite_text)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(context, R.string.notification_download_project_cancel, Toast.LENGTH_SHORT)
								.show();
					}
				}).create();

		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						handleOkButton();
					}
				});
			}
		});

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					boolean okButtonResult = handleOkButton();
					if (okButtonResult) {
						dismiss();
					}
					return okButtonResult;
				} else if (keyCode == KeyEvent.KEYCODE_BACK) {
					Toast.makeText(context, R.string.notification_download_project_cancel, Toast.LENGTH_SHORT).show();
					return true;
				}

				return false;
			}
		});

		return dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_overwrite_project_radio_replace:
				projectText.setVisibility(EditText.GONE);
				break;

			case R.id.dialog_overwrite_project_radio_rename:
				projectText.setVisibility(EditText.VISIBLE);
				break;

			default:
				break;
		}
	}

	private boolean handleOkButton() {
		if (replaceButton.isChecked()) {
			DownloadUtil.getInstance().startDownload(context, url, programName);
		} else if (renameButton.isChecked()) {
			String newProgramName = projectText.getText().toString();
			if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(newProgramName)) {
				return false;
			}

			DownloadUtil.getInstance().startDownload(context, url, newProgramName);
		}
		dismiss();

		return true;
	}
}
