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

import android.os.Process;
import android.util.Log;

import org.catrobat.catroid.common.LookData;

public class CollisionPolygonCreationTask implements Runnable {

	private static final String TAG = CollisionPolygonCreationTask.class.getSimpleName();
	private LookData lookdata;

	public CollisionPolygonCreationTask(LookData lookdata) {
		this.lookdata = lookdata;
	}

	@Override
	public void run() {
		android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		long startTime = System.currentTimeMillis();
		Log.i(TAG, "Creating polygon in runnable for " + lookdata.getLookFileName());
		lookdata.getCollisionInformation().loadOrCreateCollisionPolygon();
		if (lookdata.getCollisionInformation().isCalculationCancelled()) {
			return;
		}
		long stopTime = System.currentTimeMillis();
		long time = (stopTime - startTime) / 1000;
		Log.i(TAG, "Finished Creating polygon in runnable for " + lookdata.getLookFileName() + " in "
				+ "" + time + " seconds.");
	}
}
