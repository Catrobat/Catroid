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

package org.catrobat.catroid.test.content.actions

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SavePlotAction
import org.catrobat.catroid.plot.Plot
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.File

@RunWith(AndroidJUnit4::class)
class SavePlotActionTest {

    private lateinit var action: SavePlotAction
    private lateinit var testFile: File

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockProject = Mockito.mock(Project::class.java)
        val mockSprite = Mockito.mock(Sprite::class.java)
        val realPlot = Plot()

        val testLine = arrayListOf(android.graphics.PointF(0f, 0f), android.graphics.PointF(10f, 10f))
        realPlot.data().add(testLine)
        mockSprite.plot = realPlot

        action = SavePlotAction()
        action.scope = Scope(mockProject, mockSprite, null)
        testFile = File(context.cacheDir, "test_plot_output.svg")
    }

    @Test
    fun testHandleFileWorkGeneratesSvgContent() {
        val result = action.handleFileWork(testFile)

        assertTrue("handleFileWork should return true on success", result)
        assertTrue("SVG file should have been created", testFile.exists())
        assertTrue("SVG file should contain data", testFile.length() > 0)

        testFile.delete()
    }

    @Test
    fun testCheckIfDataIsReadyReturnsTrueWhenDataExists() {
        assertTrue("Action should be ready when plot data exists",
                   action.checkIfDataIsReady())
    }

    @Test
    fun testCheckIfDataIsReadyReturnsFalseWhenNoDataExists() {
        action.scope!!.sprite.plot.data().clear()

        org.junit.Assert.assertFalse("Action should NOT be ready when plot data is empty",
                                     action.checkIfDataIsReady())
    }
}