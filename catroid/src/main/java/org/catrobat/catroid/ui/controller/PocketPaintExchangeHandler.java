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

package org.catrobat.catroid.ui.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

import java.util.List;

public final class PocketPaintExchangeHandler {

	private PocketPaintExchangeHandler() {
	}

	private static BroadcastReceiver registeredReceiver = null;
	private static Activity activityReg = null;

	public static boolean isPocketPaintInstalled(Activity activity, Intent intent) {
		List<ResolveInfo> packages = activity.getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		return !packages.isEmpty();
	}

	public static void installPocketPaintAndRegister(BroadcastReceiver receiver, Activity activity) {

		if (registeredReceiver != null) {
			activityReg.unregisterReceiver(registeredReceiver);
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addDataScheme("package");
		activity.registerReceiver(receiver, intentFilter);
		registeredReceiver = receiver;
		activityReg = activity;

		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POCKET_PAINT_DOWNLOAD_LINK));
		downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			activity.startActivity(downloadIntent);
		} catch (Exception e) {
			showInstallPocketPaintDialog(activity);
		}
	}

	private static void showInstallPocketPaintDialog(Activity activity) {
		Dialog dialog = new CustomAlertDialogBuilder(activity).setTitle(R.string.pocket_paint_not_installed_title)
				.setMessage(R.string.pocket_paint_not_installed_message)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();

		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
}
