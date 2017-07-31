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
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import org.catrobat.catroid.common.Constants;

import java.util.List;

public final class PocketPaintExchangeHandler {

	private PocketPaintExchangeHandler() {
	}

	public static boolean isPocketPaintInstalled(Activity activity, Intent intent) {
		List<ResolveInfo> packages = activity.getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		return !packages.isEmpty();
	}

	public static void installPocketPaintAndRegister(BroadcastReceiver receiver, Activity activity) {

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addDataScheme("package");
		activity.registerReceiver(receiver, intentFilter);

		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POCKET_PAINT_DOWNLOAD_LINK));
		downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(downloadIntent);
	}
}
