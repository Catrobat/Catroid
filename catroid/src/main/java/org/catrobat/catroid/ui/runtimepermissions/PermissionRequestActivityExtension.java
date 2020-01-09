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

package org.catrobat.catroid.ui.runtimepermissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class PermissionRequestActivityExtension {
	private final List<RequiresPermissionTask> waitingForResponsePermissionTaskList;

	public PermissionRequestActivityExtension() {
		waitingForResponsePermissionTaskList = new ArrayList<>();
	}

	public void addToRequiresPermissionTaskList(RequiresPermissionTask task) {
		waitingForResponsePermissionTaskList.add(task);
	}

	public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		RequiresPermissionTask task = popAllWithSameIdRequiredPermissionTask(requestCode);

		if (permissions.length == 0) {
			return;
		}

		List<String> deniedPermissions = new ArrayList<>();
		for (int resultIndex = 0; resultIndex < permissions.length; resultIndex++) {
			if (grantResults[resultIndex] == PackageManager.PERMISSION_DENIED) {
				deniedPermissions.add(permissions[resultIndex]);
			}
		}

		if (task != null) {
			if (deniedPermissions.isEmpty()) {
				task.execute(activity);
			} else {
				task.setPermissions(deniedPermissions);
				showPermissionRationale(activity, task);
			}
		}
	}

	private RequiresPermissionTask popAllWithSameIdRequiredPermissionTask(int requestCode) {
		RequiresPermissionTask matchedTask = null;
		for (RequiresPermissionTask task : new ArrayList<>(waitingForResponsePermissionTaskList)) {
			if (task.getPermissionRequestId() == requestCode) {
				matchedTask = task;
				waitingForResponsePermissionTaskList.remove(task);
			}
		}
		return matchedTask;
	}

	@TargetApi(23)
	private void showPermissionRationale(final Activity activity, final RequiresPermissionTask task) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				&& activity.shouldShowRequestPermissionRationale(task.getPermissions().get(0))) {
			showAlertOKCancel(activity, activity.getResources().getString(task.getRationaleString()), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((PermissionHandlingActivity) activity).addToRequiresPermissionTaskList(task);
					activity.requestPermissions(task.getPermissions().toArray(new String[0]), task.getPermissionRequestId());
				}
			});
		}
	}

	private void showAlertOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
		new android.app.AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton(R.string.ok, okListener)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}
}
