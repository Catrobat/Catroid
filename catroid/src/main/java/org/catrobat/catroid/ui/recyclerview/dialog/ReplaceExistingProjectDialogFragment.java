/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ViewUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.InputWatcher;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.web.GlobalProjectDownloadQueue;
import org.catrobat.catroid.web.ProjectDownloader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ReplaceExistingProjectDialogFragment extends DialogFragment {

	public static final String TAG = ReplaceExistingProjectDialogFragment.class.getSimpleName();

	public static final String BUNDLE_KEY_PROGRAM_NAME = "programName";
	public static final String BUNDLE_KEY_DOWNLOADER = "downloader";

	public static ReplaceExistingProjectDialogFragment newInstance(String programName, ProjectDownloader downloader) {
		ReplaceExistingProjectDialogFragment dialog = new ReplaceExistingProjectDialogFragment();

		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_KEY_PROGRAM_NAME, programName);
		bundle.putSerializable(BUNDLE_KEY_DOWNLOADER, downloader);
		dialog.setArguments(bundle);

		return dialog;
	}

	public static Boolean projectExistsInDirectory(String projectName) {
		String projectDirectoryName = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName);
		return new File(DEFAULT_ROOT_DIRECTORY, projectDirectoryName).exists();
	}

	@NotNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String programName = getArguments().getString(BUNDLE_KEY_PROGRAM_NAME);
		final ProjectDownloader downloader = (ProjectDownloader) getArguments().getSerializable(BUNDLE_KEY_DOWNLOADER);

		View view = View.inflate(getActivity(), R.layout.dialog_overwrite_project, null);

		final TextInputLayout inputLayout = view.findViewById(R.id.input);
		final RadioGroup radioGroup = view.findViewById(R.id.radio_group);

		final InputWatcher.TextWatcher textWatcher = new InputWatcher.TextWatcher() {

			@Override
			protected boolean isNameUnique(String name) {
				return !projectExistsInDirectory(name) && !GlobalProjectDownloadQueue.INSTANCE.getQueue().alreadyInQueue(name);
			}
		};

		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext())
				.setText(programName)
				.setTextWatcher(textWatcher)
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					Context context = getContext();
					if (context == null) {
						return;
					}

					switch (radioGroup.getCheckedRadioButtonId()) {
						case R.id.rename:
							downloader.downloadOverwriteExistingProject(context, textInput);
							break;
						case R.id.replace:

							ProjectManager.getInstance().setCurrentProject(null);
							downloader.downloadOverwriteExistingProject(context, textInput);
							break;
						default:
							throw new IllegalStateException(TAG + ": Cannot find RadioButton.");
					}
				});

		final AlertDialog alertDialog = builder
				.setTitle(R.string.overwrite_title)
				.setView(view)
				.setNegativeButton(R.string.notification_download_project_cancel, null)
				.create();

		radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			switch (checkedId) {
				case R.id.replace:
					inputLayout.setVisibility(TextView.GONE);
					ViewUtils.hideKeyboard(inputLayout.getEditText());
					alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
					break;
				case R.id.rename:
					inputLayout.setVisibility(TextView.VISIBLE);
					ViewUtils.showKeyboard(inputLayout.getEditText());
					alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
							.setEnabled(textWatcher.validateInput(inputLayout.getEditText().getText().toString(), getContext()) == null);
					break;
			}
		});

		return alertDialog;
	}
}
