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

package org.catrobat.catroid.camera.mlkitdetectors

import android.media.Image
import android.util.Log
/*import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions*/
import org.catrobat.catroid.camera.CatdroidImageAnalyzer
import org.catrobat.catroid.camera.DetectorsCompleteListener
import org.catrobat.catroid.camera.VisualDetectionHandler

private val poseDetectionClient by lazy {
    /*PoseDetection.getClient(
        PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
    )*/
}

object PoseDetector : Detector {

    override fun processImage(
        mediaImage: Image,
        //inputImage: InputImage,
        onCompleteListener: DetectorsCompleteListener
    ) {
        /*poseDetectionClient.process(inputImage)
            .addOnSuccessListener { pose ->
                VisualDetectionHandler.updateAllPoseSensorValues(
                    pose,
                    mediaImage.width,
                    mediaImage.height
                )
            }
            .addOnFailureListener { exception ->
                Log.e(
                    javaClass.simpleName,
                    CatdroidImageAnalyzer.DETECTION_PROCESS_ERROR_MESSAGE,
                    exception
                )
            }.addOnCompleteListener {
                onCompleteListener.onComplete()
            }*/
    }
}
