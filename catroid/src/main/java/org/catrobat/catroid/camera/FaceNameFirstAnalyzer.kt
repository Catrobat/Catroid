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

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.catrobat.catroid.camera.mlkitdetectors.FaceNameDetector

/**
 * Face name detection where [CatdroidImageAnalyzer] (ML Kit, which needs Google
 * Play services) is not used: before the Huawei analyser, or alone on a device
 * with neither. Face names need no mobile services: the face models are plain
 * TensorFlow Lite.
 *
 * The frame is read synchronously, then handed to [next], which releases it;
 * without [next] it is released here.
 */
class FaceNameFirstAnalyzer(private val next: ImageAnalysis.Analyzer?) : ImageAnalysis.Analyzer {
    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        try {
            imageProxy.image?.let { FaceNameDetector.analyseFrame(it, imageProxy.imageInfo.rotationDegrees) }
        } finally {
            if (next != null) {
                next.analyze(imageProxy)
            } else {
                imageProxy.close()
            }
        }
    }
}
