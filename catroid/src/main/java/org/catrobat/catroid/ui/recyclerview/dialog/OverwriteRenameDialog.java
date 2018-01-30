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
package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DialogInputWatcher;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

public class OverwriteRenameDialog extends DialogFragment {

	public static final String TAG = OverwriteRenameDialog.class.getSimpleName();

	private RadioGroup radioGroup;
	private TextInputLayout inputLayout;
	private String programName;
	private String url;

	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public void setURL(String url) {
		this.url = url;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View root = View.inflate(getActivity(), R.layout.dialog_overwrite_project, null);

		inputLayout = root.findViewById(R.id.input);
		radioGroup = root.findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.replace:
						inputLayout.setVisibility(TextView.GONE);
						break;
					case R.id.rename:
						inputLayout.setVisibility(TextView.VISIBLE);
						break;
				}
			}
		});
		inputLayout.getEditText().setText(programName);

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.overwrite_text)
				.setView(root)
				.setPositiveButton(R.string.ok, null)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ToastUtil.showError(getActivity(), R.string.notification_download_project_cancel);
						DownloadUtil.getInstance().downloadCanceled(url);
					}
				})
				.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (handlePositiveButtonClick()) {
							dismiss();
						}
					}
				});
				inputLayout.getEditText()
						.addTextChangedListener(new DialogInputWatcher(inputLayout, buttonPositive, false));
			}
		});
		return alertDialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		ToastUtil.showError(getActivity(), R.string.notification_download_project_cancel);
		DownloadUtil.getInstance().downloadCanceled(url);
	}

	private boolean handlePositiveButtonClick() {
		switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.rename:
				String newProgramName = inputLayout.getEditText().getText().toString().trim();
				if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(newProgramName)) {
					inputLayout.setError(getString(R.string.name_already_exists));
					return false;
				}
				DownloadUtil.getInstance().startDownload(getActivity(), url, newProgramName, true);
				break;
			case R.id.replace:
				ProjectManager.getInstance().setProject(null);
				DownloadUtil.getInstance().startDownload(getActivity(), url, programName, false);
				break;
			default:
				throw new IllegalStateException(TAG + ": Cannot find RadioButton.");
		}
		return true;
	}
}
