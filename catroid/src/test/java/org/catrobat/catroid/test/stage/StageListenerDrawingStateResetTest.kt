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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.stage

import android.graphics.PointF
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.embroidery.RunningStitchType
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.ui.dialogs.StageDialog
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import java.lang.reflect.Field

class StageListenerDrawingStateResetTest {

    private val projectManagerInstanceField = ProjectManager::class.java.getDeclaredField("instance").apply {
        isAccessible = true
    }
    private val previousProjectManager = projectManagerInstanceField.get(null)

    @After
    fun tearDown() {
        projectManagerInstanceField.set(null, previousProjectManager)
    }

    @Test
    fun testReloadProjectResetsDrawingStateForEverySprite() {
        val sprite = Sprite("sprite")
        val project = Project()
        val scene = Scene("scene", project).apply {
            addSprite(sprite)
            firstStart = false
        }
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

        val projectManager = mock(ProjectManager::class.java).apply {
            org.mockito.Mockito.`when`(getCurrentProject()).thenReturn(project)
            org.mockito.Mockito.`when`(getStartScene()).thenReturn(scene)
        }
        projectManagerInstanceField.set(null, projectManager)

        val stageListener = StageListener()
        setField(stageListener, "scene", scene)
        setField(stageListener, "sprites", mutableListOf<Sprite>())
        stageListener.embroideryPatternManager = mock(EmbroideryPatternManager::class.java)

        stageListener.reloadProject(mock(StageDialog::class.java))

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
    }

    private fun setField(target: Any, name: String, value: Any?) {
        val field: Field = target.javaClass.getDeclaredField(name)
        field.isAccessible = true
        field.set(target, value)
    }
}
