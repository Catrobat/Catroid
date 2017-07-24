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

package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.GetTagsTask;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UploadProjectTagsDialog extends DialogFragment implements GetTagsTask.AsyncResponse {

	public static final String DIALOG_TAGGING_FRAGMENT_TAG = "dialog_upload_project_tags";

	public static final int MAX_NUMBER_OF_TAGS_CHECKED = 3;
	public List<String> tags;

	@Override
	public Dialog onCreateDialog(final Bundle bundle) {

		final List<String> checkedTags = new ArrayList<>();
		final String[] tagChoices = tags.toArray(new String[tags.size()]);

		final Dialog tagDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.upload_tag_dialog_title)
				.setMultiChoiceItems(tagChoices, null, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
						if (isChecked) {
							if (checkedTags.size() >= MAX_NUMBER_OF_TAGS_CHECKED) {
								((AlertDialog) dialog).getListView().setItemChecked(indexSelected, false);
							} else {
								checkedTags.add(tagChoices[indexSelected]);
							}
						} else {
							checkedTags.remove(tagChoices[indexSelected]);
						}
					}
				}).setPositiveButton(getText(R.string.next), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						ProjectManager.getInstance().getCurrentProject().setTags(checkedTags);
						handleOKButton();
					}
				}).setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						handleCancelButtonClick();
					}
				}).create();

		tagDialog.setCanceledOnTouchOutside(false);
		tagDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tagDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		tagDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				TextSizeUtil.enlargeViewGroup((ViewGroup) tagDialog.getWindow().getDecorView().getRootView());
			}
		});

		return tagDialog;
	}

	private void handleOKButton() {
		UploadProgressDialog progressDialog = new UploadProgressDialog();
		progressDialog.setProjectName(getArguments().getString(Constants.PROJECT_UPLOAD_NAME));
		progressDialog.setProjectDescription(getArguments().getString(Constants.PROJECT_UPLOAD_DESCRIPTION));
		progressDialog.show(getFragmentManager(), UploadProgressDialog.DIALOG_PROGRESS_FRAGMENT_TAG);
	}

	@Override
	public void onTagsReceived(List<String> tags) {
		this.tags = tags;
	}

	private void handleCancelButtonClick() {
		Utils.invalidateLoginTokenIfUserRestricted(getActivity());
		dismiss();
	}
}
