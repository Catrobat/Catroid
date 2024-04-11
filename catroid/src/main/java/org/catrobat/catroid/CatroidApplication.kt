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
package org.catrobat.catroid

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.multidex.MultiDex
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.mlsdk.common.MLApplication
import org.catrobat.catroid.koin.myModules
import org.catrobat.catroid.koin.start
import org.catrobat.catroid.utils.Utils
import java.util.Locale

open class CatroidApplication : Application() {
    @TargetApi(29)
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "CatroidApplication onCreate")
        Log.d(TAG, "git commit info: " + BuildConfig.GIT_COMMIT_INFO)
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build()
            )
        }
        appContext = applicationContext
        start(this, myModules)
        Utils.fetchSpeechRecognitionSupportedLanguages(this)
        defaultSystemLanguage = Locale.getDefault().toLanguageTag()
        googleAnalytics = GoogleAnalytics.getInstance(this)
        googleAnalytics!!.setDryRun(BuildConfig.DEBUG)
        setupHuaweiMobileServices()
    }

    private fun setupHuaweiMobileServices() {
        if (AGConnectInstance.getInstance() == null) {
            AGConnectInstance.initialize(this)
        }
        val apiKey = AGConnectServicesConfig.fromContext(this).getString("client/api_key")
        MLApplication.getInstance().apiKey = apiKey
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    @get:Synchronized
    val defaultTracker: Tracker?
        get() {
            if (googleTracker == null) {
                googleTracker = googleAnalytics!!.newTracker(R.xml.global_tracker)
            }
            return googleTracker
        }

    companion object {
        private val TAG = CatroidApplication::class.java.simpleName
        var appContext: Context? = null

        @JvmField
        var defaultSystemLanguage: String? = null
        private var googleAnalytics: GoogleAnalytics? = null
        private var googleTracker: Tracker? = null
        @JvmStatic
        fun getAppContext(): Context {
            return appContext ?: throw IllegalStateException("Application context not initialized yet.")
        }
    }

}