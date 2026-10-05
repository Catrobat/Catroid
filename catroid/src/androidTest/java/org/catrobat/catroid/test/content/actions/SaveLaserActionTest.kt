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

package org.catrobat.catroid.test.content.actions

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SaveLaserAction
import org.catrobat.catroid.plot.Plot
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.File
import android.graphics.PointF

@RunWith(AndroidJUnit4::class)
class SaveLaserActionTest {

    private lateinit var action: SaveLaserAction
    private lateinit var testFile: File
    private lateinit var realPlot: Plot

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockProject = Mockito.mock(Project::class.java)
        val mockSprite = Mockito.mock(Sprite::class.java)
        realPlot = Plot()

        val engraveLine = arrayListOf(PointF(0f, 0f), PointF(5f, 5f))
        realPlot.engraveDataPointLists.add(engraveLine)


        val cutLine = arrayListOf(PointF(10f, 10f), PointF(20f, 20f))
        realPlot.cutDataPointLists.add(cutLine)
        mockSprite.plot = realPlot

        action = SaveLaserAction()
        action.scope = Scope(mockProject, mockSprite, null)
        testFile = File(context.cacheDir, "test_laser_output.svg")
    }

    @Test
    fun testHandleFileWorkCombinesBothPaths() {
        realPlot.engraveDataPointLists.add(arrayListOf(PointF(0f, 0f), PointF(1f, 1f)))
        realPlot.cutDataPointLists.add(arrayListOf(PointF(2f, 2f), PointF(3f, 3f)))

        val result = action.handleFileWork(testFile)
        assertTrue("handleFileWork should return true on success", result)

        val svgContent = testFile.readText()
        assertTrue("Blue color for engraving is missing", svgContent.contains("blue") || svgContent.contains("#0000FF"))
        assertTrue("Red color for cutting is missing", svgContent.contains("red") || svgContent.contains("#FF0000"))
    }

    @Test
    fun testCheckIfDataIsReady() {
        realPlot.engraveDataPointLists.clear()
        realPlot.cutDataPointLists.clear()
        assertFalse("Should be false when no data lists exist",
                    action.checkIfDataIsReady())

        realPlot.engraveDataPointLists.add(arrayListOf(PointF(0f, 0f)))
        assertFalse("Should be false when only one point is provided",
                    action.checkIfDataIsReady())

        realPlot.engraveDataPointLists.clear()
        realPlot.engraveDataPointLists.add(arrayListOf(PointF(0f, 0f), PointF(1f, 1f)))
        assertTrue("Should be true when at least one path has two or more points",
                   action.checkIfDataIsReady())
    }
}