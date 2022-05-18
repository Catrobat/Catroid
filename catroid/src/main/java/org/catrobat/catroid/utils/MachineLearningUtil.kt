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

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.camera.core.ImageAnalysis
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.koin.java.KoinJavaComponent
import java.lang.ref.WeakReference

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
    var facesForSensors: Array<VisualDetectionHandlerFace?>
    fun addListener(listener: SensorCustomEventListener)
    fun removeListener(listener: SensorCustomEventListener)
    fun updateFaceDetectionStatusSensorValues()
    fun updateFaceSensorValues(facePosition: Point, faceSize: Int, faceNumber: Int)
}

open class VisualDetectionHandlerFace(val id: Int, val boundingBox: Rect)

private const val MODULE_NAME = "machinelearning"
private const val MODULE_PATH = "org.catrobat.catroidfeature.$MODULE_NAME"
private const val MAX_PERCENT = 100

private enum class LoadingState {
    NOT_LOADED,
    IS_LOADING,
    LOADED
}

private enum class PermissionState {
    NOT_ANSWERED,
    PENDING,
    REJECTED,
    ACCEPTED
}

// NOTE: How to test lazy loading locally in the simulator:
// https://developer.android.com/guide/playcore/feature-delivery/on-demand#local-testing
// 1. Build: Build > Generate signed bundles
// 2. bundletool build-apks --local-testing --bundle catroid/catroid/debug/catroid-catroid-debug.aab --output app.apks
// 3. bundletool install-apks --apks app.apks

object MachineLearningUtil {
    @get:Synchronized
    @set:Synchronized
    private var loadingState: LoadingState = LoadingState.NOT_LOADED

    @get:Synchronized
    @set:Synchronized
    private var permissionState: PermissionState = PermissionState.NOT_ANSWERED

    @get:Synchronized
    @set:Synchronized
    private var activity: WeakReference<Activity>? = null

    @JvmStatic
    fun setActivity(a: Activity?) {
        activity = WeakReference<Activity>(a)
        if (a != null && permissionState == PermissionState.REJECTED) {
            permissionState = PermissionState.NOT_ANSWERED
        }
    }

    fun isLoaded() = this.loadingState == LoadingState.LOADED

    fun getCatroidImageAnalyzer() = getObjectInstance<CatroidImageAnalyzer>("CatroidImageAnalyzer")

    fun getFaceTextPoseDetectorHuawei() = getObjectInstance<FaceTextPoseDetectorHuawei>("FaceTextPoseDetectorHuawei")

    fun getObjectDetectorResults() = getObjectInstance<ObjectDetectorResults>("ObjectDetectorResults")

    fun <E> getObjectDetectorOnSuccessListener() =
        getObjectInstance<OnSuccessListener<MutableList<E>>>("ObjectDetectorOnSuccessListener")

    @JvmStatic
    fun getTextBlockUtil() = getObjectInstance<TextBlockUtil>("TextBlockUtil")

    @JvmStatic
    fun getVisualDetectionHandler() = getObjectInstance<VisualDetectionHandler>("VisualDetectionHandler")

    private fun <T> getObjectInstance(name: String): T? {
        if (!isAvailableForAndroid() && permissionState == PermissionState.NOT_ANSWERED) {
            permissionState = PermissionState.REJECTED
            showNotAvailableDialog()
            return null
        }
        if (loadingState == LoadingState.NOT_LOADED) {
            val context = activity?.get()?.applicationContext
            if (context != null) {
                val splitInstallManager = SplitInstallManagerFactory.create(context)
                if (splitInstallManager.installedModules.contains(MODULE_NAME)) {
                    initializeMachineLearningModule(context)








                }
            }
        }
        if (loadingState == LoadingState.NOT_LOADED && permissionState == PermissionState.NOT_ANSWERED) {
            showPermissionDialog { loadModule() }
            return null
        }
        if (loadingState != LoadingState.LOADED) {
            return null
        }
        return try {
            Class.forName("$MODULE_PATH.$name").kotlin.objectInstance as T?
        } catch (exception: ClassNotFoundException) {
            Log.e(javaClass.simpleName, "Could not get class for name '$MODULE_PATH.$name'", exception)
            null
        }
    }

    private fun loadModule() {
        val context = activity?.get()?.applicationContext ?: return
        if (loadingState != LoadingState.NOT_LOADED) {
            return
        }
        loadingState = LoadingState.IS_LOADING
        val request = SplitInstallRequest
            .newBuilder()
            .addModule(MODULE_NAME)
            .build()

        val statusBarNotificationManager = StatusBarNotificationManager(context)
        val notificationData = statusBarNotificationManager.createMLModuleDownloadNotification(context)
        statusBarNotificationManager.showOrUpdateNotification(context, notificationData, 0, null)

        val splitInstallManager = SplitInstallManagerFactory.create(context)
        val listener = SplitInstallStateUpdatedListener { state ->
            when (state.status()) {
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    // NOTE: only necessary for feature modules > 150MB (https://issuetracker.google.com/issues/171501820#comment6)
                    // Implementation details:
                    //  - https://developer.android.com/reference/com/google/android/play/core/splitinstall/SplitInstallManager.html#startConfirmationDialogForResult(com.google.android.play.core.splitinstall.SplitInstallSessionState,%20android.app.Activity,%20int)
                    //  - https://developer.android.com/guide/playcore/feature-delivery/on-demand#obtain_confirmation
                    //  - https://medium.com/swlh/dynamic-feature-module-integration-android-a315194a4801
                }
                SplitInstallSessionStatus.DOWNLOADING -> {
                    val size = state.totalBytesToDownload()
                    val downloaded = state.bytesDownloaded()
                    val percentage = (downloaded * MAX_PERCENT / size).toInt()
                    statusBarNotificationManager.showOrUpdateNotification(context, notificationData, percentage, null)
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    statusBarNotificationManager.showOrUpdateNotification(context, notificationData, MAX_PERCENT, null)
                    initializeMachineLearningModule(context)
                }
                SplitInstallSessionStatus.UNKNOWN,
                SplitInstallSessionStatus.CANCELED,
                SplitInstallSessionStatus.FAILED ->
                    statusBarNotificationManager.abortProgressNotificationWithMessage(
                        context,
                        notificationData,
                        R.string.download_ml_module_error_message
                    )
                SplitInstallSessionStatus.CANCELING,
                SplitInstallSessionStatus.DOWNLOADED,
                SplitInstallSessionStatus.INSTALLING,
                SplitInstallSessionStatus.PENDING -> {
                }
            }
        }
        splitInstallManager.registerListener(listener)
        splitInstallManager
            .startInstall(request)
            .addOnFailureListener { exception ->
                loadingState = LoadingState.NOT_LOADED
                Log.e(javaClass.simpleName, "Could not load module.", exception)
            }
    }

    fun initializeMachineLearningModule(context: Context) {
        if (loadingState == LoadingState.LOADED) {
            return
        }
        try {
            val machineLearningModule =
                Class.forName("$MODULE_PATH.MachineLearningModule").kotlin.objectInstance as MachineLearningModule?
            machineLearningModule?.init(context)
            loadingState = LoadingState.LOADED
            permissionState = PermissionState.ACCEPTED
        } catch (exception: ClassNotFoundException) {
            Log.e(javaClass.simpleName, "Could not initialize module.", exception)
        }
    }

    private fun showPermissionDialog(positiveButtonHandler: () -> Unit) {
        val activityNotNull = activity?.get() ?: return
        permissionState = PermissionState.PENDING
        activityNotNull.runOnUiThread {
            AlertDialog.Builder(ContextThemeWrapper(activityNotNull, R.style.Theme_AppCompat_Dialog))
                .setTitle(R.string.download_ml_module_permission_dialog_title)
                .setMessage(R.string.download_ml_module_permission_dialog_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    positiveButtonHandler()
                    permissionState = PermissionState.ACCEPTED
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    permissionState = PermissionState.REJECTED
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    private fun showNotAvailableDialog() {
        val activityNotNull = activity?.get() ?: return
        activityNotNull.runOnUiThread {
            AlertDialog.Builder(ContextThemeWrapper(activityNotNull, R.style.Theme_AppCompat_Dialog))
                .setTitle(R.string.download_ml_module_not_available_dialog_title)
                .setMessage(R.string.download_ml_module_not_available_dialog_message)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    private fun isAvailableForAndroid(): Boolean {
        val activityNotNull = activity?.get() ?: return false
        val mobileServiceAvailability = KoinJavaComponent.get(MobileServiceAvailability::class.java)
        val status = mobileServiceAvailability.getGmsAvailabilityStatus(activityNotNull)
        if (status != ConnectionResult.SUCCESS) {
            Log.w(javaClass.simpleName, "machine learning features not available (status: $status)")
            return false
        }
        return true
    }
}
