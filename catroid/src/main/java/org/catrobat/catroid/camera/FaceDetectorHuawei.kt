/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.camera.VisualDetectionHandler.handleAlreadyExistingFaces
import org.catrobat.catroid.camera.VisualDetectionHandler.handleNewFaces
import org.catrobat.catroid.camera.VisualDetectionHandler.translateHuaweiFaceToVisualDetectionFace
import org.catrobat.catroid.camera.VisualDetectionHandler.updateAllFaceSensorValues
import org.catrobat.catroid.stage.StageActivity

object FaceDetectorHuawei : ImageAnalysis.Analyzer {

    private const val QUADRANT_DEGREES = 90
    private val analyzer = MLAnalyzerFactory.getInstance().faceAnalyzer

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val mlFrame = MLFrame.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees / QUADRANT_DEGREES
            )

            val task = analyzer.asyncAnalyseFrame(mlFrame)
            task.addOnSuccessListener { mlFaces ->
                val faces = translateHuaweiFaceToVisualDetectionFace(mlFaces)
                handleAlreadyExistingFaces(faces)
                handleNewFaces(faces)
                updateAllFaceSensorValues(mediaImage.width, mediaImage.height)
                imageProxy.close()
            }.addOnFailureListener { e ->
                val context = CatroidApplication.getAppContext()
                StageActivity.messageHandler.obtainMessage(
                    StageActivity.SHOW_TOAST,
                    arrayListOf(context.getString(R.string.camera_error_face_detection))
                ).sendToTarget()
                Log.e(javaClass.simpleName, "Could not analyze image.", e)
            }
        }
    }
}
