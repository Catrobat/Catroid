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

package org.catrobat.catroidfeature.machinelearning

import android.media.Image
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import org.catrobat.catroid.utils.ObjectDetectorResults as ObjectDetectorResultsInterface

private val objectDetectionClient by lazy {
    ObjectDetection.getClient(
        ObjectDetectorOptions.Builder().enableMultipleObjects()
            .enableClassification().build()
    )
}

class ObjectDetectorOnSuccessListener : OnSuccessListener<MutableList<DetectedObject>> {
    override fun onSuccess(detectedObjects: MutableList<DetectedObject>) {
        ObjectDetectorResults.result = detectedObjects.map { it.trackingId to it }.toMap()
    }
}

object ObjectDetector : Detector {
    override fun processImage(
        mediaImage: Image,
        inputImage: InputImage,
        onCompleteListener: DetectorsCompleteListener
    ) {
        objectDetectionClient.process(inputImage)
            .addOnSuccessListener(ObjectDetectorOnSuccessListener())
            .addOnFailureListener { exception ->
                Log.e(javaClass.simpleName, "Could not analyze image.", exception)
            }.addOnCompleteListener {
                onCompleteListener.onComplete()
            }
    }
}

object ObjectDetectorResults : ObjectDetectorResultsInterface {
    @get:Synchronized @set:Synchronized
    var result: Map<Int?, DetectedObject> = HashMap()

    override fun getIdOfDetectedObject(index: Int): Int {
        return result.keys.toList().getOrNull(index - 1) ?: 0
    }

    override fun isObjectWithIdVisible(id: Int): Boolean {
        return result[id] != null
    }
}