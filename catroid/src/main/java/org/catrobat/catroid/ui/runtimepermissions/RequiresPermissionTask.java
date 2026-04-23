/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.runtimepermissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class RequiresPermissionTask {

	private final int permissionRequestId;
	private final @StringRes int rationaleString;
	private List<String> permissions;
	public static final String TAG = RequiresPermissionTask.class.getSimpleName();

	public abstract void task();

	protected RequiresPermissionTask(int permissionRequestId, List<String> permissions, @StringRes int rationaleString) {
		this.permissionRequestId = permissionRequestId;
		this.permissions = permissions;
		this.rationaleString = rationaleString;
	}

	public int getPermissionRequestId() {
		return permissionRequestId;
	}

	public int getRationaleString() {
		return rationaleString;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public void execute(Activity activity) {
		if (checkPermission(activity, permissions)) {
			task();
		} else {
			if (activity instanceof PermissionHandlingActivity) {
				((PermissionHandlingActivity) activity).addToRequiresPermissionTaskList(this);
			} else {
				Log.d(TAG, "This has to be called from a PermissionHandlingActivity to have your task be executed on premissionResult");
			}
			ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), permissionRequestId);
		}
	}

	public static boolean checkPermission(Activity activity, List<String> permissions) {
		for (String permission : permissions) {
			boolean granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
			if (!granted) {
				return false;
			}
		}
		return true;
	}
}

