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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.TemplateData;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.transfers.DownloadTemplateTask;
import org.catrobat.catroid.ui.BaseSettingsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.utils.OnProjectTranslatedListener;
import org.catrobat.catroid.utils.ProjectTranslationTask;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

public class OrientationDialog extends DialogFragment implements
		LoadProjectTask.OnLoadProjectCompleteListener,
		DownloadTemplateTask.OnDownloadTemplateCompleteListener,
		OnProjectTranslatedListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_orientation_project";

	private static final String TAG = OrientationDialog.class.getSimpleName();
	private static final float GREYED_VALUE = 0.25f;

	private Dialog orientationDialog;
	private String projectName;
	private RadioButton landscapeMode;
	private RadioButton cast;
	private RadioButton portraitMode;

	private boolean createEmptyProject;
	private boolean createLandscapeProject = false;
	private boolean createCastProject = false;

	private boolean openedFromProjectList = false;
	private boolean openedFromTemplatesList = false;

	private TemplateData templateData;
	private Activity activity;
	private String baseUrlForTemplates;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_orientation_new_project, null);
		boolean castEnabled = BaseSettingsActivity.isCastSharedPreferenceEnabled(getActivity());
		int title = castEnabled ? R.string.project_select_screen_title : R.string.project_orientation_title;

		orientationDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		orientationDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}
				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});
				TextSizeUtil.enlargeViewGroup((ViewGroup) dialogView.getRootView());
			}
		});

		cast = (RadioButton) dialogView.findViewById(R.id.cast);

		if (castEnabled) {
			cast.setVisibility(View.VISIBLE);
		}

		landscapeMode = (RadioButton) dialogView.findViewById(R.id.landscape_mode);
		portraitMode = (RadioButton) dialogView.findViewById(R.id.portrait);

		if (templateData != null) {
			if (templateData.getLandscape() == null) {
				landscapeMode.setEnabled(false);
				landscapeMode.setAlpha(GREYED_VALUE);
				portraitMode.setChecked(true);
			}
			if (templateData.getPortrait() == null) {
				portraitMode.setEnabled(false);
				portraitMode.setAlpha(GREYED_VALUE);
				landscapeMode.setChecked(true);
			}
		}

		return orientationDialog;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	protected void handleOkButtonClick() {
		createLandscapeProject = landscapeMode.isChecked();
		createCastProject = cast.isChecked();

		if (isOpenedFromTemplatesList()) {
			downloadTemplateFile();
		} else {
			try {
				ProjectManager.getInstance().initializeNewProject(projectName, getActivity(), createEmptyProject, false, createLandscapeProject, createCastProject, false);
			} catch (IllegalArgumentException illegalArgumentException) {
				Utils.showErrorDialog(getActivity(), R.string.error_project_exists);
				return;
			} catch (IOException ioException) {
				Utils.showErrorDialog(getActivity(), R.string.error_new_project);
				Log.e(TAG, Log.getStackTraceString(ioException));
				dismiss();
				return;
			}
			startProjectActivity();
		}
		dismiss();
	}

	private void downloadTemplateFile() {
		String zipFileUrl = baseUrlForTemplates;
		zipFileUrl += createLandscapeProject ? templateData.getLandscape() : templateData.getPortrait();

		DownloadTemplateTask downloadTask = new DownloadTemplateTask(getActivity(), templateData, zipFileUrl, this);
		downloadTask.execute();
	}

	public boolean isOpenedFromProjectList() {
		return openedFromProjectList;
	}

	public void setOpenedFromProjectList(boolean openedFromProjectList) {
		this.openedFromProjectList = openedFromProjectList;
	}

	public boolean isOpenedFromTemplatesList() {
		return openedFromTemplatesList;
	}

	public void setOpenedFromTemplatesList(boolean openedFromTemplatesList) {
		this.openedFromTemplatesList = openedFromTemplatesList;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setCreateEmptyProject(boolean isChecked) {
		this.createEmptyProject = isChecked;
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity) {
		ProjectManager.getInstance().initializeTemplateProject(projectName, activity);
		Utils.getTrackingUtilProxy().trackUseTemplate(templateData.getName(), createLandscapeProject);
		new ProjectTranslationTask(activity, this).execute();
	}

	@Override
	public void onLoadProjectFailure() {
		ToastUtil.showError(activity, activity.getString(R.string.error_load_project));
	}

	private void startProjectActivity() {
		Intent intent = new Intent(activity, ProjectActivity.class);
		intent.putExtra(Constants.PROJECTNAME_TO_LOAD, projectName);
		if (isOpenedFromProjectList()) {
			intent.putExtra(Constants.PROJECT_OPENED_FROM_PROJECTS_LIST, true);
		} else if (isOpenedFromTemplatesList()) {
			intent.putExtra(Constants.PROJECT_OPENED_FROM_TEMPLATES_LIST, true);
		}
		activity.startActivity(intent);
	}

	public void setTemplateData(TemplateData templateData) {
		this.templateData = templateData;
	}

	public void setBaseUrlForTemplates(String baseUrlForTemplates) {
		this.baseUrlForTemplates = baseUrlForTemplates;
	}

	@Override
	public void onDownloadTemplateComplete() {
		ProjectManager.getInstance().loadStageProject(templateData, activity, projectName, this);
	}

	@Override
	public void onProjectTranslated() {
		startProjectActivity();
	}
}
