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

package org.catrobat.catroid.utils

import android.content.Context
import android.graphics.Point
import android.util.Log
import androidx.camera.core.ImageAnalysis
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener

interface MachineLearningModule {
    fun init(context: Context)
}

interface CatroidImageAnalyzer : ImageAnalysis.Analyzer {
    fun setActiveDetectorsWithContext(context: Context?)
}

interface FaceTextPoseDetectorHuawei : ImageAnalysis.Analyzer

interface ObjectDetectorResults {
    fun getIdOfDetectedObject(index: Int): Int
    fun isObjectWithIdVisible(id: Int): Boolean
}

interface TextBlockUtil {
    fun getTextBlock(arg: Int): String
    fun getTextBlockLanguage(arg: Int): String
    fun getCenterCoordinates(arg: Int): Point
    fun getSize(arg: Int): Double
}

interface VisualDetectionHandler {
    fun addListener(listener: SensorCustomEventListener)
    fun removeListener(listener: SensorCustomEventListener)
}

private const val MODULE_NAME = "machinelearning"
private const val MODULE_PATH = "org.catrobat.catroidfeature.$MODULE_NAME"

object MachineLearningUtil {
    private var isLoaded = false
    private var isLoading = false

    // TODO how to test the lazy loading??
    // https://developer.android.com/guide/playcore/feature-delivery/on-demand#local-testing
    // 1. Build: Build > Generate signed bundles
    // 2. bundletool build-apks --local-testing --bundle catroid/catroid/debug/catroid-catroid-debug.aab --output app.apks
    // 3. bundletool install-apks --apks my_app.apks

    // 		context = getApplicationContext();

    @JvmStatic
    fun loadModule(context: Context?) {
        if (context == null || isLoaded || isLoading) {
            Log.d("BULLSHIT", "is already loaded")
            return
        }
        Log.d("BULLSHIT", "start loading")
        isLoading = true
        val request = SplitInstallRequest
            .newBuilder()
            .addModule(MODULE_NAME)
            .build()
        val splitInstallManager = SplitInstallManagerFactory.create(context)
        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener { sessionId ->
                try {
                    val machineLearningModule =
                        Class.forName("$MODULE_PATH.MachineLearningModule").kotlin.objectInstance as MachineLearningModule?
                    // TODO can be null
                    machineLearningModule?.init(context)
                    Log.d("BULLSHIT", "is loaded")
                    isLoaded = true
                } catch (exception: ClassNotFoundException) {
                    Log.e(javaClass.simpleName, "Could not initialize module.", exception)
                    Log.e("BULLSHIT", "could not init", exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(javaClass.simpleName, "Could not load module.", exception)
                Log.e("BULLSHIT", "could not load", exception)
            }
            .addOnCompleteListener {
                isLoading = false
            }
    }

    fun getCatroidImageAnalyzer(): CatroidImageAnalyzer? =
        getObjectInstance<CatroidImageAnalyzer>("CatroidImageAnalyzer")

    fun getFaceTextPoseDetectorHuawei(): FaceTextPoseDetectorHuawei? =
        getObjectInstance<FaceTextPoseDetectorHuawei>("FaceTextPoseDetectorHuawei")

    fun getObjectDetectorResults(): ObjectDetectorResults? =
        getObjectInstance<ObjectDetectorResults>("ObjectDetectorResults")

    @JvmStatic
    fun getTextBlockUtil(): TextBlockUtil? = getObjectInstance<TextBlockUtil>("TextBlockUtil")

    @JvmStatic
    fun getVisualDetectionHandler(): VisualDetectionHandler? =
        getObjectInstance<VisualDetectionHandler>("VisualDetectionHandler")

    private fun <T> getObjectInstance(name: String): T? {
        if (!isLoaded) {
            return null
        }
        return try {
            Class.forName("$MODULE_PATH.$name").kotlin.objectInstance as T?
        } catch (e: ClassNotFoundException) {
            null
        }
    }
}
