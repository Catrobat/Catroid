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

import android.content.Context
import android.graphics.Rect
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.stage.StageActivity
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Found on the phone: ML Kit's face detection reports asynchronously, and a
 * result that arrives after the stage has closed made
 * VisualDetectionHandler.translateToStageCoordinates() call isCameraFacingFront
 * on a missing camera manager. The NullPointerException ended the app.
 */
@RunWith(RobolectricTestRunner::class)
class VisualDetectionHandlerAfterStageTest {

    private val writes = mutableListOf<Sensors>()
    private val listener = SensorCustomEventListener { event -> writes.add(event.sensor) }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        ProjectManager.getInstance().currentProject = Project(context, "VisualDetectionHandlerAfterStageTest")
        ScreenValues.setToDefaultScreenSize()
        StageActivity.activeStageActivity = null
        VisualDetectionHandler.addListener(listener)
    }

    @After
    fun tearDown() {
        VisualDetectionHandler.removeListener(listener)
    }

    @Test
    fun aFaceReportedAfterTheStageClosedDoesNotEndTheApp() {
        val face = VisualDetectionHandlerFace(FACE_ID, Rect(100, 100, 300, 300))
        VisualDetectionHandler.handleAlreadyExistingFaces(listOf(face))
        VisualDetectionHandler.handleNewFaces(listOf(face))

        VisualDetectionHandler.updateAllFaceSensorValues(IMAGE_WIDTH, IMAGE_HEIGHT)

        assertTrue("The face sensors are still written", writes.contains(Sensors.FACE_X))
    }

    private companion object {
        const val FACE_ID = 4711
        const val IMAGE_WIDTH = 640
        const val IMAGE_HEIGHT = 480
    }
}
