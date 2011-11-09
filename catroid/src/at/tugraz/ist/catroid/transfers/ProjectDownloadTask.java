/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.transfers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class ProjectDownloadTask extends AsyncTask<Void, Void, Boolean> implements OnClickListener {
	private Activity activity;
	private String projectName;
	private String zipFileString;
	private String url;
	private ProgressDialog progressDialog;
	private boolean result;

	// mock object testing
	protected ConnectionWrapper createConnection() {
		return new ConnectionWrapper();
	}

	public ProjectDownloadTask(Activity activity, String url, String projectName) {
		this.activity = activity;
		this.projectName = projectName;
		this.zipFileString = Consts.TMP_PATH + "/down" + Consts.CATROID_EXTENTION;
		this.url = url;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		String title = activity.getString(R.string.please_wait);
		String message = activity.getString(R.string.loading);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			ServerCalls.getInstance().downloadProject(url, zipFileString);

			result = UtilZip.unZipFile(zipFileString, Consts.DEFAULT_ROOT + "/" + projectName + "/");
			return result;
		} catch (WebconnectionException e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!result) {
			//Toast.makeText(mActivity, R.string.error_project_download, Toast.LENGTH_SHORT).show();
			showDialog(R.string.error_project_download);
			return;
		}

		if (activity == null) {
			return;
		}
		Toast.makeText(activity, R.string.success_project_download, Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(activity, MainMenuActivity.class);
		activity.startActivity(intent);

	}

	private void showDialog(int messageId) {
		if (activity == null) {
			return;
		}
		//TODO: refactor to use strings.xml
		new Builder(activity).setMessage(messageId).setPositiveButton("OK", null).show();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (!result) {
			activity.finish();
		}
	}

}
