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

package org.catrobat.catroid.sensing;

import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;

public class GatherCollisionInformationTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = GatherCollisionInformationTask.class.getSimpleName();
	private OnPolygonLoadedListener listener;

	public GatherCollisionInformationTask(OnPolygonLoadedListener listener) {
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		getCollisionInformation();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		Log.i(TAG, "Finished task");
		listener.onFinished();
	}

	private void getCollisionInformation() {
		Log.i(TAG, "Waiting for all calculation threads to finish...");
		for (Sprite s : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			for (LookData lookData : s.getLookDataList()) {
				if (lookData.getCollisionInformation().collisionPolygonCalculationThread == null) {
					continue;
				}
				try {
					lookData.getCollisionInformation().collisionPolygonCalculationThread.join();
				} catch (InterruptedException e) {
					Log.i(TAG, "Thread got interupted");
				}
			}
		}

		for (Sprite s : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			if (s.hasCollision()) {
				for (LookData l : s.getLookDataList()) {
					l.getCollisionInformation().loadOrCreateCollisionPolygon();
				}
			}
		}
	}

	public interface OnPolygonLoadedListener {
		void onFinished();
	}
}
