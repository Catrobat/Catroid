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

package org.catrobat.catroid.stage

import android.graphics.PointF
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.RunningStitchType
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify

class StageLifeCycleControllerTest {

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testStageDestroyDoesNotCrashWhenStageListenerIsNull() {
        val mockStageActivity = mock(StageActivity::class.java)
        val mockProjectManager = mock(ProjectManager::class.java)
        val mockProject = mock(Project::class.java)

        val pmMock = mockStatic(ProjectManager::class.java)
        val rpMock = mockStatic(RequiresPermissionTask::class.java)
        val srhMock = mockStatic(StageResourceHolder::class.java)
        val spMock = mockStatic(ServiceProvider::class.java)

        try {
            val mockBluetoothDeviceService = mock(BluetoothDeviceService::class.java)
            spMock.`when`<Any> {
                ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
            }.thenReturn(mockBluetoothDeviceService)

            pmMock.`when`<Any> {
                ProjectManager.getInstance()
            }.thenReturn(mockProjectManager)
            org.mockito.Mockito.`when`(mockProjectManager.currentProject).thenReturn(mockProject)
            org.mockito.Mockito.`when`(mockProjectManager.currentlyEditedScene).thenReturn(null)

            srhMock.`when`<Any> {
                StageResourceHolder.getProjectsRuntimePermissionList(anyInt())
            }.thenReturn(emptyList<String>())
            rpMock.`when`<Any> {
                RequiresPermissionTask.checkPermission(any(), any())
            }.thenReturn(true)

            StageActivity.stageListener = null

            StageLifeCycleController.stageDestroy(mockStageActivity)

            verify(mockStageActivity).manageLoadAndFinish()
            verify(mockBluetoothDeviceService).destroy()
            assertNull(StageActivity.stageListener)
        } finally {
            pmMock.close()
            rpMock.close()
            srhMock.close()
            spMock.close()
        }
    }

    @Test
    fun testStageDestroyResetsDrawingStateForNextFreshStart() {
        val mockStageActivity = mock(StageActivity::class.java)
        val mockProjectManager = mock(ProjectManager::class.java)
        val project = Project()
        val scene = Scene("scene", project)
        val sprite = Sprite("sprite")
        scene.addSprite(sprite)
        project.sceneList.add(scene)

        val previousPenConfiguration = sprite.penConfiguration
        val previousPlot = sprite.plot
        val previousRunningStitch = sprite.runningStitch

        sprite.penConfiguration.setPenDown(true)
        sprite.penConfiguration.addQueue()
        sprite.penConfiguration.addPosition(PointF(1f, 2f))

        sprite.plot.startNewPlotLine(PointF(0f, 0f))
        sprite.plot.resumePlot()
        sprite.plot.startNewCutLine(PointF(1f, 1f))
        sprite.plot.resumeCut()
        sprite.plot.startNewEngraveLine(PointF(2f, 2f))
        sprite.plot.resumeEngrave()

        sprite.runningStitch.activateStitching(sprite, mock(RunningStitchType::class.java))

        val pmMock = mockStatic(ProjectManager::class.java)
        val rpMock = mockStatic(RequiresPermissionTask::class.java)
        val srhMock = mockStatic(StageResourceHolder::class.java)
        val spMock = mockStatic(ServiceProvider::class.java)

        try {
            val mockBluetoothDeviceService = mock(BluetoothDeviceService::class.java)
            spMock.`when`<Any> {
                ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
            }.thenReturn(mockBluetoothDeviceService)

            pmMock.`when`<Any> {
                ProjectManager.getInstance()
            }.thenReturn(mockProjectManager)
            org.mockito.Mockito.`when`(mockProjectManager.currentProject).thenReturn(project)
            org.mockito.Mockito.`when`(mockProjectManager.currentlyEditedScene).thenReturn(scene)

            srhMock.`when`<Any> {
                StageResourceHolder.getProjectsRuntimePermissionList(anyInt())
            }.thenReturn(emptyList<String>())
            rpMock.`when`<Any> {
                RequiresPermissionTask.checkPermission(any(), any())
            }.thenReturn(true)

            StageActivity.stageListener = mock(StageListener::class.java)

            StageLifeCycleController.stageDestroy(mockStageActivity)

            assertNotSame(previousPenConfiguration, sprite.penConfiguration)
            assertFalse(sprite.penConfiguration.isPenDown)
            assertTrue(sprite.penConfiguration.positions.isEmpty)

            assertNotSame(previousPlot, sprite.plot)
            assertFalse(sprite.plot.isPlotting())
            assertFalse(sprite.plot.isCutting())
            assertFalse(sprite.plot.isEngraving())
            assertTrue(sprite.plot.data().isEmpty())
            assertTrue(sprite.plot.cutDataPointLists.isEmpty())
            assertTrue(sprite.plot.engraveDataPointLists.isEmpty())

            assertNotSame(previousRunningStitch, sprite.runningStitch)
            assertFalse(sprite.runningStitch.isRunning)
            assertNull(StageActivity.stageListener)
        } finally {
            pmMock.close()
            rpMock.close()
            srhMock.close()
            spMock.close()
        }
    }
}
