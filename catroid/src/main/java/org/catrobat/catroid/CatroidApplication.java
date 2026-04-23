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
package org.catrobat.catroid;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;

import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.utils.Utils;

import java.util.Locale;

import androidx.multidex.MultiDex;

public class CatroidApplication extends Application {

	private static final String TAG = CatroidApplication.class.getSimpleName();

	private static Context context;
	public static String defaultSystemLanguage;

	private static GoogleAnalytics googleAnalytics;
	private static Tracker googleTracker;

	@TargetApi(31)
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "CatroidApplication onCreate");
		Log.d(TAG, "git commit info: " + BuildConfig.GIT_COMMIT_INFO);

		if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectNonSdkApiUsage()
					.penaltyLog()
					.build());
		}

		context = getApplicationContext();

		CatroidKoinHelperKt.start(this, CatroidKoinHelperKt.getMyModules());

		Utils.fetchSpeechRecognitionSupportedLanguages(this);

		defaultSystemLanguage = Locale.getDefault().toLanguageTag();

		googleAnalytics = GoogleAnalytics.getInstance(this);
		googleAnalytics.setDryRun(BuildConfig.DEBUG);

		setupHuaweiMobileServices();
	}

	private void setupHuaweiMobileServices() {
		if (AGConnectInstance.getInstance() == null) {
			AGConnectInstance.initialize(this);
		}

		String apiKey = AGConnectServicesConfig.fromContext(this).getString("client/api_key");
		MLApplication.getInstance().setApiKey(apiKey);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public synchronized Tracker getDefaultTracker() {
		if (googleTracker == null) {
			googleTracker = googleAnalytics.newTracker(R.xml.global_tracker);
		}

		return googleTracker;
	}

	public static Context getAppContext() {
		return CatroidApplication.context;
	}
}
