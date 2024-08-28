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
package org.catrobat.catroid.test.content.actions

import android.graphics.PointF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.OrthographicCamera
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.plot.Plot
import org.catrobat.catroid.plot.SVGPlotGenerator
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.File

@RunWith(AndroidJUnit4::class)
class GeneratePlotSVGTest {

    private val camera: OrthographicCamera = Mockito.spy(OrthographicCamera())
    private val projectName = "testProject"
    private lateinit var plotFile: File

    @Before
    @Throws(Exception::class)
    fun setUp() {
        Mockito.doNothing().`when`(camera).update()
        createTestProject()
        plotFile = File(Constants.CACHE_DIRECTORY, "$projectName.svg")
        if (plotFile.exists()) {
            plotFile.delete()
        }
        if (!Constants.CACHE_DIRECTORY.exists()) {
            Constants.CACHE_DIRECTORY.mkdirs()
        }
        plotFile.createNewFile()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
        plotFile.exists() && plotFile.delete()
    }

    @Test
    fun testNullPlot() {
        val generator = SVGPlotGenerator(null)
        generator.writeToSVGFile(plotFile)
    }

    @Test
    fun testNoData() {
        val generator = SVGPlotGenerator(Plot())
        generator.writeToSVGFile(plotFile)
        Assert.assertFalse(plotFile.readText().contains("path"))
    }

    @Test
    fun testData() {
        val plot = Plot()
        plot.startNewPlotLine(PointF(0.0f, 0.0f))
        plot.addPoint(PointF(0.0f, 1.0f))
        val generator = SVGPlotGenerator(plot)
        generator.writeToSVGFile(plotFile)
        Assert.assertTrue(plotFile.readText().contains("path"))
        var count = 0
        for(line in plotFile.readLines())
        {
            if(line.contains("path"))
                count++
        }
        Assert.assertEquals(count,1)
    }

    @Test
    fun testMultiLineData() {
        val plot = Plot()
        plot.startNewPlotLine(PointF(0.0f, 0.0f))
        plot.addPoint(PointF(0.0f, 1.0f))
        plot.startNewPlotLine(PointF(1.0f, 0.0f))
        plot.addPoint(PointF(0.0f, 1.0f))
        val generator = SVGPlotGenerator(plot)
        generator.writeToSVGFile(plotFile)
        Assert.assertTrue(plotFile.readText().contains("path"))
        var count = 0
        for(line in plotFile.readLines())
        {
            if(line.contains("path"))
                count++
        }
        Assert.assertEquals(count,2)
    }



    private fun createTestProject() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
    }
}