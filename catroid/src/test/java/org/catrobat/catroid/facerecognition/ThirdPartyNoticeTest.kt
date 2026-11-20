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
package org.catrobat.catroid.facerecognition

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The two face models are licensed under the Apache License 2.0, which asks
 * every copy of the app to carry the license text and the attribution. The
 * notice ships as an asset next to the models.
 */
@RunWith(RobolectricTestRunner::class)
class ThirdPartyNoticeTest {

    @Test
    fun theAppShipsTheNoticeForBothFaceModels() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val notice = context.assets.open(NOTICE).bufferedReader().use { it.readText() }

        for (required in listOf(
            "face_detection_front.tflite", "Copyright Google LLC",
            "mobile_facenet.tflite", "foamliu",
            "Apache License", "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION"
        )) {
            assertTrue("The notice must name: $required", notice.contains(required))
        }
    }

    private companion object {
        const val NOTICE = "third_party_notices_face_recognition.txt"
    }
}
