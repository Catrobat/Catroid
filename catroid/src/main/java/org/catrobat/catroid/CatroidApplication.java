/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.parrot.freeflight.settings.ApplicationSettings;
import com.zed.bdsclient.controller.BDSClientController;
import com.zed.bdsclient.environments.BDSClientEnvironments;

import org.catrobat.catroid.ui.SettingsActivity;

public class CatroidApplication extends MultiDexApplication {

	private static final String TAG = CatroidApplication.class.getSimpleName();

	protected static Context context;

	public static final String OS_ARCH = System.getProperty("os.arch");

	public static boolean parrotLibrariesLoaded = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "CatroidApplication onCreate");
		CatroidApplication.context = getApplicationContext();
		BDSClientController controller = BDSClientController.init(context,
				BDSClientEnvironments.PRO, appId, appPassword, 1, 0);
		SettingsActivity.applyAccessibilitySettings(context);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public static synchronized boolean loadNativeLibs() {
		if (parrotLibrariesLoaded) {
			return true;
		}

		try {
			System.loadLibrary("avutil");
			System.loadLibrary("swscale");
			System.loadLibrary("avcodec");
			System.loadLibrary("avfilter");
			System.loadLibrary("avformat");
			System.loadLibrary("avdevice");
			System.loadLibrary("adfreeflight");
			parrotLibrariesLoaded = true;
		} catch (UnsatisfiedLinkError e) {
			Log.e(TAG, Log.getStackTraceString(e));
			parrotLibrariesLoaded = false;
		}
		return parrotLibrariesLoaded;
	}

	public static Context getAppContext() {
		return CatroidApplication.context;
	}

	public static void restartApplication(Context context) {
		int delay = 100;
		Intent restartIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		PendingIntent intent = PendingIntent.getActivity(context, 0, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
		System.exit(2);
	}
}
