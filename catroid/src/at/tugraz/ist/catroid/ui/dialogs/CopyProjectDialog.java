/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class CopyProjectDialog extends TextDialog {

	private class CopyProjectAsyncTask extends AsyncTask<String, Void, Boolean> {
		boolean screenOrientationChanged = false;
		boolean copyProcessFinished = false;
		FragmentActivity activityAsync = getActivity();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... array) {
			String newProjectName = array[0];
			if (isCancelled()) {
				return false;
			}

			try {
				File oldProjectRootDirectory = new File(Utils.buildProjectPath(oldProjectName));
				File newProjectRootDirectory = new File(Utils.buildProjectPath(newProjectName));

				copyDirectory(newProjectRootDirectory, oldProjectRootDirectory);
				if (isCancelled()) {
					return false;
				}

				Project copiedProject = StorageHandler.getInstance().loadProject(newProjectName);
				copiedProject.setName(newProjectName);
				StorageHandler.getInstance().saveProject(copiedProject);

			} catch (IOException exception) {
				UtilFile.deleteDirectory(new File(Utils.buildProjectPath(newProjectName)));
				Log.e("CATROID", "Error while copying project, destroy newly created directories.", exception);
				return false;
			}
			copyProcessFinished = true;
			return true;
		}

		private void copyDirectory(File destinationFile, File sourceFile) throws IOException {
			if (isCancelled()) {
				throw new IOException();
			}
			if (sourceFile.isDirectory()) {

				destinationFile.mkdirs();
				for (String subDirectoryName : sourceFile.list()) {
					copyDirectory(new File(destinationFile, subDirectoryName), new File(sourceFile, subDirectoryName));
				}
			} else {
				UtilFile.copyFile(destinationFile, sourceFile, null);
			}
		}

		@Override
		protected void onCancelled() {
			if (screenOrientationChanged && !copyProcessFinished) {
				Log.d("Catroid", "Copy process cancelled");
				Utils.displayErrorMessageFragment(activityAsync.getSupportFragmentManager(),
						activityAsync.getString(R.string.error_orientation_change_copy_project));
				currentCopyProjectAsyncTask = null;

			} else if (!screenOrientationChanged && !copyProcessFinished) {
				Log.d("CATROID", "Copy process cancelled");
				Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
						getActivity().getString(R.string.copy_process_canceled));

			} else if (copyProcessFinished && !screenOrientationChanged) {
				if (onCopyProjectListener != null) {
					onCopyProjectListener.onCopyProject(screenOrientationChanged);
					dismiss();
				}
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			if (result) {
				if (onCopyProjectListener != null) {
					onCopyProjectListener.onCopyProject(screenOrientationChanged);
					dismiss();
				}
			} else {
				Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
						getString(R.string.error_copy_project));
				return;
			}
		}
	}

	private static final String BUNDLE_ARGUMENTS_OLD_PROJECT_NAME = "old_project_name";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_copy_project";

	private OnCopyProjectListener onCopyProjectListener;

	private String oldProjectName;
	private CopyProjectAsyncTask currentCopyProjectAsyncTask;
	ProgressDialog progressDialog;

	public CopyProjectDialog() {

	}

	public static CopyProjectDialog newInstance(String oldProjectName) {
		CopyProjectDialog dialog = new CopyProjectDialog();

		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_OLD_PROJECT_NAME, oldProjectName);
		dialog.setArguments(arguments);
		return dialog;
	}

	public void setOnCopyProjectListener(OnCopyProjectListener listener) {
		onCopyProjectListener = listener;
	}

	@Override
	protected void initialize() {
		oldProjectName = getArguments().getString(BUNDLE_ARGUMENTS_OLD_PROJECT_NAME);
		input.setText(oldProjectName);
	}

	@Override
	protected boolean handleOkButton() {
		String newProjectName = (input.getText().toString()).trim();

		if (newProjectName.equalsIgnoreCase("")) {
			Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
					getString(R.string.notification_invalid_text_entered));
			return false;
		} else if (StorageHandler.getInstance().projectExistsIgnoreCase(newProjectName)) {
			Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
					getString(R.string.error_project_exists));
			return false;
		}

		if (newProjectName != null && !newProjectName.equalsIgnoreCase("")) {

			String title = getString(R.string.please_wait);
			String message = getString(R.string.copying);
			progressDialog = ProgressDialog.show(getActivity(), title, message);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					currentCopyProjectAsyncTask.cancel(true);
				}
			});

			currentCopyProjectAsyncTask = new CopyProjectAsyncTask();
			currentCopyProjectAsyncTask.execute(newProjectName);
		} else {
			Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
					getString(R.string.notification_invalid_text_entered));
			return false;
		}
		return false;
	}

	@Override
	protected String getTitle() {
		return getString(R.string.copy_project);
	}

	@Override
	protected String getHint() {
		return null;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (currentCopyProjectAsyncTask != null) {
			currentCopyProjectAsyncTask.screenOrientationChanged = true;
			currentCopyProjectAsyncTask.cancel(true);
		}
	}

	public interface OnCopyProjectListener {

		public void onCopyProject(boolean orientationChangedWhileCopying);

	}
}
