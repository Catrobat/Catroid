/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.camera

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.UiThread
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.catrobat.catroid.utils.ToastUtil
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.koin.java.KoinJavaComponent.get

class CameraManager(private val activity: Activity) : LifecycleOwner {
    private val cameraProvider = ProcessCameraProvider.getInstance(activity).get()
    private val lifecycle = LifecycleRegistry(this)
    val previewView = PreviewView(activity).apply {
        visibility = View.INVISIBLE
    }

    private val previewUseCase = Preview.Builder().build()
    private val analysisUseCase = ImageAnalysis.Builder().build()

    private var currentCamera: Camera? = null
    private val defaultCameraSelector: CameraSelector
    private var currentCameraSelector: CameraSelector

    val hasFrontCamera = cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
    val hasBackCamera = cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
    val hasFlash = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    var previewVisible = false
        private set

    var detectionOn = false
        private set

    var flashOn = false
        private set

    companion object {
        private val TAG = CameraManager::class.java.simpleName
    }

    init {
        if (hasFrontCamera || hasBackCamera) {
            activity.addContentView(
                previewView,
                FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            )
        }

        defaultCameraSelector = if (hasFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        currentCameraSelector = defaultCameraSelector
        lifecycle.currentState = Lifecycle.State.CREATED
    }

    val isCameraFacingFront: Boolean
        get() = currentCameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA

    val isCameraActive: Boolean
        get() = lifecycle.currentState in listOf(Lifecycle.State.STARTED, Lifecycle.State.RESUMED) &&
            (cameraProvider.isBound(previewUseCase) || cameraProvider.isBound(analysisUseCase))

    @Synchronized
    fun reset() {
        flashOn = false
        previewVisible = false
        detectionOn = false
        unbindPreview()
        switchToDefaultCamera()
    }

    @Synchronized
    fun destroy() {
        lifecycle.currentState = Lifecycle.State.DESTROYED
    }

    @Synchronized
    fun pause() {
        lifecycle.currentState = Lifecycle.State.CREATED
    }

    @Synchronized
    fun resume() {
        lifecycle.currentState = Lifecycle.State.RESUMED
        currentCamera?.cameraControl?.enableTorch(flashOn)
    }

    @Synchronized
    fun switchToFrontCamera() {
        if (hasFrontCamera) {
            runInMainThreadAndWait { switchCamera(CameraSelector.DEFAULT_FRONT_CAMERA) }
        }
    }

    @Synchronized
    fun switchToBackCamera() {
        if (hasBackCamera) {
            runInMainThreadAndWait { switchCamera(CameraSelector.DEFAULT_BACK_CAMERA) }
        }
    }

    private fun switchToDefaultCamera() = switchCamera(defaultCameraSelector)

    private fun switchCamera(cameraSelector: CameraSelector): Boolean {
        if (currentCameraSelector != cameraSelector) {
            currentCameraSelector = cameraSelector
            currentCamera = null
            cameraProvider.unbindAll()
            bindPreview()
            bindFaceAndTextDetector()
            return true
        }
        return false
    }

    @Synchronized
    fun startPreview() {
        if (previewVisible.not()) {
            previewVisible = true
            runInMainThreadAndWait {
                previewView.visibility = View.VISIBLE
                if (cameraProvider.isBound(previewUseCase).not()) {
                    bindPreview()
                }
            }
        }
    }

    @Synchronized
    fun stopPreview() {
        if (previewVisible) {
            previewVisible = false
            runInMainThreadAndWait {
                if (flashOn.not()) {
                    unbindPreview()
                }
                previewView.visibility = View.INVISIBLE
            }
        }
    }

    @Synchronized
    fun enableFlash() {
        if (flashOn.not()) {
            flashOn = true
            if (currentCamera?.cameraInfo?.hasFlashUnit()?.not() != false && isCameraFacingFront) {
                switchToBackCamera()
            }
            if (cameraProvider.isBound(previewUseCase).not()) {
                runInMainThreadAndWait { bindPreview() }
            } else {
                currentCamera?.cameraControl?.enableTorch(true)
            }
        }
    }

    @Synchronized
    fun disableFlash() {
        if (flashOn) {
            flashOn = false
            currentCamera?.cameraControl?.enableTorch(false)
            if (previewVisible.not()) {
                runInMainThreadAndWait { unbindPreview() }
            }
        }
    }

    @Synchronized
    fun startDetection(): Boolean {
        if (detectionOn.not()) {
            detectionOn = true
            bindFaceAndTextDetector()
        }
        return true
    }

    private fun bindPreview(): Boolean {
        previewView.visibility = View.VISIBLE
        return bindUseCase(previewUseCase).also {
            previewUseCase.surfaceProvider = previewView.createSurfaceProvider()
            if (previewVisible.not()) {
                previewView.visibility = View.INVISIBLE
            }
        }
    }

    @UiThread
    private fun unbindPreview() {
        cameraProvider.unbind(previewUseCase)
        if (cameraProvider.isBound(analysisUseCase).not()) {
            currentCamera = null
        }
    }

    @UiThread
    private fun bindFaceAndTextDetector() = bindUseCase(analysisUseCase).also {
        val mobileServiceAvailability = get(MobileServiceAvailability::class.java)
        if (mobileServiceAvailability.isGmsAvailable(activity)) {
            CatdroidImageAnalyzer.setActiveDetectorsWithContext(this.activity, isCameraFacingFront)
            analysisUseCase.setAnalyzer(Executors.newSingleThreadExecutor(), CatdroidImageAnalyzer)
        } else if (mobileServiceAvailability.isHmsAvailable(activity)) {
            FaceTextPoseDetectorHuawei.setFrontCamera(isCameraFacingFront)
            analysisUseCase.setAnalyzer(Executors.newSingleThreadExecutor(), FaceTextPoseDetectorHuawei)
        }
    }

    @UiThread
    private fun bindUseCase(useCase: UseCase): Boolean {
        if (cameraProvider.isBound(useCase)) {
            cameraProvider.unbind(useCase)
        }
        return try {
            currentCamera = cameraProvider.bindToLifecycle(
                this,
                currentCameraSelector,
                useCase
            )
            currentCamera?.cameraControl?.enableTorch(flashOn)
            lifecycle.currentState = Lifecycle.State.STARTED
            true
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "Could not bind use case.", exception)
            handleError()
            false
        } catch (exception: IllegalArgumentException) {
            Log.e(TAG, "No suitable camera found.", exception)
            handleError()
            false
        }
    }

    private fun runInMainThreadAndWait(runnable: Runnable) {
        val executionLatch = CountDownLatch(1)
        activity.runOnUiThread {
            runnable.run()
            executionLatch.countDown()
        }
        executionLatch.await()
    }

    private fun handleError() {
        ToastUtil.showError(activity, activity.getString(R.string.camera_error_generic))
        destroy()
    }

    override fun getLifecycle() = lifecycle
}
