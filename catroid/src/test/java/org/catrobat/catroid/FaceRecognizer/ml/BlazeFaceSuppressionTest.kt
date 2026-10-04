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
package org.catrobat.catroid.FaceRecognizer.ml

import android.graphics.RectF
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * BlazeFace merges overlapping boxes (weighted non-maximum suppression). Found
 * in a review: the best box left the list only by overlapping itself, and a box
 * without area (or with NaN coordinates) overlaps nothing, not even itself, so
 * the loop never ended and the face models stayed locked for good.
 */
@RunWith(RobolectricTestRunner::class)
class BlazeFaceSuppressionTest {

    @Test(timeout = TIMEOUT_MS)
    fun aBoxWithoutAreaEndsTheMerge() {
        val faces = BlazeFace.weightedNonMaxSuppression(listOf(detection(0.5f, 0.5f, 0.5f, 0.5f, 0.9f)))

        assertEquals(1, faces.size)
    }

    @Test(timeout = TIMEOUT_MS)
    fun aBoxWithNanCoordinatesEndsTheMerge() {
        val faces = BlazeFace.weightedNonMaxSuppression(
            listOf(detection(Float.NaN, 0.1f, 0.4f, 0.4f, 0.9f), detection(0.1f, 0.1f, 0.4f, 0.4f, 0.8f))
        )

        assertEquals(2, faces.size)
    }

    @Test(timeout = TIMEOUT_MS)
    fun overlappingBoxesBecomeOneFaceAndSeparateBoxesStaySeparate() {
        val faces = BlazeFace.weightedNonMaxSuppression(
            listOf(
                detection(0.10f, 0.10f, 0.40f, 0.40f, 0.95f),
                detection(0.12f, 0.11f, 0.41f, 0.42f, 0.90f),
                detection(0.60f, 0.60f, 0.90f, 0.90f, 0.85f)
            )
        )

        assertEquals(2, faces.size)
    }

    private fun detection(left: Float, top: Float, right: Float, bottom: Float, score: Float) =
        BlazeFace.Detection(RectF(left, top, right, bottom), score, FloatArray(KEYPOINT_VALUES))

    private companion object {
        const val TIMEOUT_MS = 5000L
        const val KEYPOINT_VALUES = 12
    }
}
