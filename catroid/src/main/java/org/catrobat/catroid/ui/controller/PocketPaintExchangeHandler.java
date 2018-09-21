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

package org.catrobat.catroid.ui.controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;

import java.util.List;

import static org.catrobat.catroid.common.Constants.EXTRA_PICTURE_NAME_POCKET_PAINT;
import static org.catrobat.catroid.common.Constants.EXTRA_PICTURE_PATH_POCKET_PAINT;
import static org.catrobat.catroid.common.Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME;
import static org.catrobat.catroid.common.Constants.POCKET_PAINT_PACKAGE_NAME;

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

	public static Intent createPocketPaintIntent(String extraPictureName) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(POCKET_PAINT_PACKAGE_NAME, POCKET_PAINT_INTENT_ACTIVITY_NAME));

		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_PICTURE_PATH_POCKET_PAINT, "");
		bundle.putString(EXTRA_PICTURE_NAME_POCKET_PAINT, extraPictureName);
		intent.putExtras(bundle);
		intent.addCategory("android.intent.category.LAUNCHER");
		return intent;
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

	public static BroadcastReceiver createPocketPaintBroadcastReceiver(final Activity activity, final Intent pocketPaintIntent, final int requestCode) {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				String packageName = intent.getData().getEncodedSchemeSpecificPart();
				if (!packageName.equals(POCKET_PAINT_PACKAGE_NAME)) {
					return;
				}

				activity.unregisterReceiver(this);

				if (PocketPaintExchangeHandler.isPocketPaintInstalled(activity, pocketPaintIntent)) {
					ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
					activityManager.moveTaskToFront(activity.getTaskId(), 0);
					activity.startActivityForResult(pocketPaintIntent, requestCode);
				}
			}
		};
	}

	private static void showInstallPocketPaintDialog(Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle(R.string.pocket_paint_not_installed_title)
				.setMessage(R.string.pocket_paint_not_installed_message)
				.setPositiveButton(R.string.ok, null)
				.create()
				.show();
	}
}
