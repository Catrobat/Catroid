/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
package org.catrobat.catroid.test.stage

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.sensing.ColorDetection
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorDetectionViewportTest {
    private class Detection(scope: Scope) : ColorDetection(scope, null) {
        fun viewport(project: Project) = createViewport(project, 480f, 800f, OrthographicCamera())
        override fun setBufferParameters() = Unit
        override fun isParameterInvalid(parameter: Any?) = false
        override fun getLooksOfRelevantSprites(): MutableList<Look>? = null
        override fun createProjectionMatrix(project: Project) = Matrix4()
    }

    @Test
    fun testViewportHasTheProjectsSizeInEveryScreenMode() {
        val project = Project(MockUtil.mockContextForProject(), "testProject")
        ProjectManager.getInstance().currentProject = project
        val detection = Detection(Scope(project, Sprite("sprite"), SequenceAction()))
        for (mode in listOf(ScreenModes.STRETCH, ScreenModes.MAXIMIZE)) {
            project.screenMode = mode
            val viewport = detection.viewport(project)
            assertEquals(480f, viewport.worldWidth)
            assertEquals(800f, viewport.worldHeight)
        }
    }
}
