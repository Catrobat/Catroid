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
package org.catrobat.catroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.parrot.freeflight.settings.ApplicationSettings;

import org.catrobat.catroid.ui.Multilingual;

import java.util.Locale;

public class CatroidApplication extends MultiDexApplication {

	private static final String TAG = CatroidApplication.class.getSimpleName();

	private ApplicationSettings settings;
	private static Context context;
	public static String defaultSystemLanguage;
	public static SharedPreferences languageSharedPreferences;

	public static final String OS_ARCH = System.getProperty("os.arch");

	public static boolean parrotLibrariesLoaded = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "CatroidApplication onCreate");
		settings = new ApplicationSettings(this);
		CatroidApplication.context = getApplicationContext();
		defaultSystemLanguage = Locale.getDefault().getLanguage();
		// open the App in the last chosen language
		languageSharedPreferences = getSharedPreferences("For_language",getApplicationContext().MODE_PRIVATE);
		String langTag = languageSharedPreferences.getString("Nur","");
		if (langTag.equals("")){
			Multilingual.setContextLocale(getApplicationContext(),defaultSystemLanguage);}
		if (langTag.equals("ar")){
			Multilingual.setContextLocale(getApplicationContext(),"ar");}
		if (langTag.equals("az-rAZ")){
			Multilingual.setContextLocale(getApplicationContext(),"az-rAZ");}
		if (langTag.equals("bs")){
			Multilingual.setContextLocale(getApplicationContext(),"bs");}
		if (langTag.equals("ca-rES")){
			Multilingual.setContextLocale(getApplicationContext(),"ca-rES");}
		if (langTag.equals("cs-rCZ")){
			Multilingual.setContextLocale(getApplicationContext(),"cs-rCZ");}
		if (langTag.equals("da")){
			Multilingual.setContextLocale(getApplicationContext(),"da");}
		if (langTag.equals("de")){
			Multilingual.setContextLocale(getApplicationContext(),"de");}
		if (langTag.equals("en-rAU")){
			Multilingual.setContextLocale(getApplicationContext(),"en-rAU");}
		if (langTag.equals("en-rCA")){
			Multilingual.setContextLocale(getApplicationContext(),"en-rCA");}
		if (langTag.equals("en-rGB")){
			Multilingual.setContextLocale(getApplicationContext(),"en-rGB");}
		if (langTag.equals("es")){
			Multilingual.setContextLocale(getApplicationContext(),"es");}
		if (langTag.equals("fa")){
			Multilingual.setContextLocale(getApplicationContext(),"fa");}
		if (langTag.equals("fr")){
			Multilingual.setContextLocale(getApplicationContext(),"fr");}
		if (langTag.equals("gl-rES")){
			Multilingual.setContextLocale(getApplicationContext(),"gl-rES");}
		if (langTag.equals("gu")){
			Multilingual.setContextLocale(getApplicationContext(),"gu");}
		if (langTag.equals("he")){
			Multilingual.setContextLocale(getApplicationContext(),"he");}
		if (langTag.equals("hi")){
			Multilingual.setContextLocale(getApplicationContext(),"hi");}
		if (langTag.equals("hr")){
			Multilingual.setContextLocale(getApplicationContext(),"hr");}
		if (langTag.equals("hu")){
			Multilingual.setContextLocale(getApplicationContext(),"hu");}
		if (langTag.equals("id")){
			Multilingual.setContextLocale(getApplicationContext(),"id");}
		if (langTag.equals("it")){
			Multilingual.setContextLocale(getApplicationContext(),"it");}
		if (langTag.equals("ja")){
			Multilingual.setContextLocale(getApplicationContext(),"ja");}
		if (langTag.equals("ko")){
			Multilingual.setContextLocale(getApplicationContext(),"ko");}
		if (langTag.equals("mk")){
			Multilingual.setContextLocale(getApplicationContext(),"mk");}
		if (langTag.equals("ml")){
			Multilingual.setContextLocale(getApplicationContext(),"ml");}
		if (langTag.equals("ms")){
			Multilingual.setContextLocale(getApplicationContext(),"ms");}
		if (langTag.equals("nl")){
			Multilingual.setContextLocale(getApplicationContext(),"nl");}
		if (langTag.equals("no")){
			Multilingual.setContextLocale(getApplicationContext(),"no");}
		if (langTag.equals("pl")){
			Multilingual.setContextLocale(getApplicationContext(),"pl");}
		if (langTag.equals("ps")){
			Multilingual.setContextLocale(getApplicationContext(),"ps");}
		if (langTag.equals("pt")){
			Multilingual.setContextLocale(getApplicationContext(),"pt");}
		if (langTag.equals("pt-rBR")){
			Multilingual.setContextLocale(getApplicationContext(),"pt-rBR");}
		if (langTag.equals("ro")){
			Multilingual.setContextLocale(getApplicationContext(),"ro");}
		if (langTag.equals("ru")){
			Multilingual.setContextLocale(getApplicationContext(),"ru");}
		if (langTag.equals("sd")){
			Multilingual.setContextLocale(getApplicationContext(),"sd");}
		if (langTag.equals("sl")){
			Multilingual.setContextLocale(getApplicationContext(),"sl");}
		if (langTag.equals("sq")){
			Multilingual.setContextLocale(getApplicationContext(),"sq");}
		if (langTag.equals("sr-rCS")){
			Multilingual.setContextLocale(getApplicationContext(),"sr-rCS");}
		if (langTag.equals("sr-rSP")){
			Multilingual.setContextLocale(getApplicationContext(),"sr-rSP");}
		if (langTag.equals("sv")){
			Multilingual.setContextLocale(getApplicationContext(),"sv");}
		if (langTag.equals("ta")){
			Multilingual.setContextLocale(getApplicationContext(),"ta");}
		if (langTag.equals("te")){
			Multilingual.setContextLocale(getApplicationContext(),"te");}
		if (langTag.equals("th")){
			Multilingual.setContextLocale(getApplicationContext(),"th");}
		if (langTag.equals("tr")){
			Multilingual.setContextLocale(getApplicationContext(),"tr");}
		if (langTag.equals("ur")){
			Multilingual.setContextLocale(getApplicationContext(),"ur");}
		if (langTag.equals("vi")){
			Multilingual.setContextLocale(getApplicationContext(),"vi");}
		if (langTag.equals("zh-rCN")){
			Multilingual.setContextLocale(getApplicationContext(),"zh-rCN");}
		if (langTag.equals("zh-rTW")){
			Multilingual.setContextLocale(getApplicationContext(),"zh-rTW");}


	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public ApplicationSettings getParrotApplicationSettings() {
		return settings;
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
}
