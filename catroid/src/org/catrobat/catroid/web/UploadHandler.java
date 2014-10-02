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

package org.catrobat.catroid.web;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.transfers.CheckTokenTask;
import org.catrobat.catroid.ui.dialogs.ExecuteOnceDialog;
import org.catrobat.catroid.ui.dialogs.LoginRegisterDialog;
import org.catrobat.catroid.ui.dialogs.UploadProjectDialog;

public final class UploadHandler implements LoadProjectTask.OnLoadProjectCompleteListener, CheckTokenTask.OnCheckTokenCompleteListener {

	private static final UploadHandler INSTANCE;

	static {
		INSTANCE = new UploadHandler();
	}

	private UploadHandler() { }

	public static UploadHandler getInstance() {
		return INSTANCE;
	}

	public void uploadProject(String projectName, FragmentActivity fragmentActivity) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		boolean isProgramExecutedAtLeastOnce = false;
		if (currentProject != null) {
			isProgramExecutedAtLeastOnce = currentProject.isProgramExecutedAtLeastOnce();
			if (isProgramExecutedAtLeastOnce == false) {
				showExecuteOnceDialog(fragmentActivity);
			}
		}

		if (currentProject == null || !currentProject.getName().equals(projectName)) {
			LoadProjectTask loadProjectTask = new LoadProjectTask(fragmentActivity, projectName, false, false);
			loadProjectTask.setOnLoadProjectCompleteListener(this);
			loadProjectTask.execute();
		}
		if (isProgramExecutedAtLeastOnce == true)
		{
			startUploadProjectProccess(fragmentActivity);
		}


	}

	private void startUploadProjectProccess(FragmentActivity fragmentActivity)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(fragmentActivity);
		String token = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = preferences.getString(Constants.USERNAME, Constants.NO_USERNAME);

		if (token.equals(Constants.NO_TOKEN) || token.length() != ServerCalls.TOKEN_LENGTH
				|| token.equals(ServerCalls.TOKEN_CODE_INVALID)) {
			showLoginRegisterDialog(fragmentActivity);
		} else {
			CheckTokenTask checkTokenTask = new CheckTokenTask(fragmentActivity, token, username);
			checkTokenTask.setOnCheckTokenCompleteListener(this);
			checkTokenTask.execute();
		}

	}

	private void showExecuteOnceDialog(FragmentActivity fragmentActivity) {

		ExecuteOnceDialog executeOnceDialog = new ExecuteOnceDialog(fragmentActivity);
		executeOnceDialog.show(fragmentActivity.getSupportFragmentManager(),ExecuteOnceDialog.DIALOG_FRAGMENT_TAG);

	}

	private void showLoginRegisterDialog(FragmentActivity fragmentActivity) {
		LoginRegisterDialog loginRegisterDialog = new LoginRegisterDialog();
		loginRegisterDialog.show(fragmentActivity.getSupportFragmentManager(), LoginRegisterDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onTokenNotValid(FragmentActivity fragmentActivity) {
		showLoginRegisterDialog(fragmentActivity);
	}

	@Override
	public void onCheckTokenSuccess(FragmentActivity fragmentActivity) {
		UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
		uploadProjectDialog.show(fragmentActivity.getSupportFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity,FragmentActivity startedFromFragmentActivity) {

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject.isProgramExecutedAtLeastOnce() == false)
		{
			showExecuteOnceDialog(startedFromFragmentActivity);
		}
		else
		{
			startUploadProjectProccess(startedFromFragmentActivity);
		}

	}

	@Override
	public void onLoadProjectFailure() {

	}


}
