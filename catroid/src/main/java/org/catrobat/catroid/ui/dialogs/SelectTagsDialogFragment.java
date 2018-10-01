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

package org.catrobat.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.dialog.UploadProgressDialogFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SelectTagsDialogFragment extends DialogFragment {

	public static final String TAG = SelectTagsDialogFragment.class.getSimpleName();

	public static final int MAX_NUMBER_OF_TAGS_CHECKED = 3;
	public List<String> tags = new ArrayList<>();

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public SelectTagsDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isRestoringPreviouslyDestroyedActivity = savedInstanceState != null;
		if (isRestoringPreviouslyDestroyedActivity) {
			dismiss();
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle bundle) {
		final List<String> checkedTags = new ArrayList<>();
		final String[] choiceItems = tags.toArray(new String[tags.size()]);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.upload_tag_dialog_title)
				.setMultiChoiceItems(choiceItems, null, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
						if (isChecked) {
							if (checkedTags.size() >= MAX_NUMBER_OF_TAGS_CHECKED) {
								((AlertDialog) dialog).getListView().setItemChecked(indexSelected, false);
							} else {
								checkedTags.add(choiceItems[indexSelected]);
							}
						} else {
							checkedTags.remove(choiceItems[indexSelected]);
						}
					}
				})
				.setPositiveButton(getText(R.string.next), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						ProjectManager.getInstance().getCurrentProject().setTags(checkedTags);
						onPositiveButtonClick();
					}
				})
				.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Utils.invalidateLoginTokenIfUserRestricted(getActivity());
					}
				})
				.setCancelable(false)
				.create();
	}

	private void onPositiveButtonClick() {
		UploadProgressDialogFragment dialog = new UploadProgressDialogFragment();
		dialog.setArguments(getArguments());
		dialog.setCancelable(false);
		dialog.show(getFragmentManager(), UploadProgressDialogFragment.TAG);
	}
}
