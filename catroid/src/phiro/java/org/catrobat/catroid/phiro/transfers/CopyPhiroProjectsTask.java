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

package org.catrobat.catroid.phiro.transfers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.phiro.io.OnCopyPhiroProjectsCompleteListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;

import java.io.IOException;

public class CopyPhiroProjectsTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = CopyPhiroProjectsTask.class.getSimpleName();

	private Activity activity;
	private OnCopyPhiroProjectsCompleteListener listener;
	private LinearLayout linearLayoutProgressCircle;

	public CopyPhiroProjectsTask(Activity activity, OnCopyPhiroProjectsCompleteListener listener) {
		this.activity = activity;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		linearLayoutProgressCircle = (LinearLayout) activity.findViewById(R.id.progress_circle);
		linearLayoutProgressCircle.setVisibility(View.VISIBLE);
		linearLayoutProgressCircle.bringToFront();
	}

	@Override
	protected Boolean doInBackground(String... projects) {
		for (String project : projects) {
			if (StorageHandler.getInstance().projectExists(project)) {
				continue;
			}

			String zipFileName = project + ".zip";
			try {
				UtilFile.copyAssetProjectZipFile(activity, zipFileName, Constants.TMP_PATH);
				UtilZip.unZipFile(Constants.TMP_PATH + "/" + zipFileName, Constants.DEFAULT_ROOT + "/" + project);
				UtilFile.deleteFile(Constants.TMP_PATH + "/" + zipFileName);
			} catch (IOException exception) {
				Log.e(TAG, "Could not load phiro project " + project);
				ToastUtil.showError(activity, R.string.error_load_project);
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		linearLayoutProgressCircle.setVisibility(View.GONE);

		if (success) {
			listener.copyingCompleted();
		}
	}
}
