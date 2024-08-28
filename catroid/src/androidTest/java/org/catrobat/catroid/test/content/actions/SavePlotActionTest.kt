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

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SavePlotAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.File

@RunWith(AndroidJUnit4::class)
class SavePlotActionTest {
    private val xMovement = Formula(X_MOVEMENT)
    private var sprite: Sprite? = null
    private val camera: OrthographicCamera = Mockito.spy(OrthographicCamera())
    private lateinit var plotFile : File
    
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = Sprite("testSprite")
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
    }

    @Test(expected = NullPointerException::class)
    fun testNull() {
        val action = sprite!!.actionFactory.createSavePlotAction(null, null, null)
        Assert.assertTrue(action is SavePlotAction)
    }


    @Test
    fun testSaveNoPositionChange() {
        Assert.assertEquals(0f, sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite!!.look.yInUserInterfaceDimensionUnit)

        sprite!!.actionFactory.createStartPlotAction(sprite).act(1.0f)
        val action = sprite!!.actionFactory.createSavePlotAction(sprite, SequenceAction(), FILE)

        Assert.assertTrue(action is SavePlotAction)
        if(action is SavePlotAction)
            action.writeToFile(plotFile)
        Assert.assertTrue(plotFile.readText().isNotEmpty())
    }

    @Test
    fun testSaveOnePositionChangeOpen() {
        Assert.assertEquals(0f, sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite!!.look.yInUserInterfaceDimensionUnit)

        sprite!!.actionFactory.createStartPlotAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        val action = sprite!!.actionFactory.createSavePlotAction(sprite, SequenceAction(), FILE)

        Assert.assertTrue(action is SavePlotAction)
        if(action is SavePlotAction)
            action.writeToFile(plotFile)
        Assert.assertTrue(plotFile.readText().isNotEmpty())
    }
    @Test
    fun testSaveOnePositionChangeClosed() {
        Assert.assertEquals(0f, sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite!!.look.yInUserInterfaceDimensionUnit)

        sprite!!.actionFactory.createStartPlotAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        sprite!!.actionFactory.createStopPlotAction(sprite).act(1.0f)
        val action = sprite!!.actionFactory.createSavePlotAction(sprite, SequenceAction(), FILE)

        Assert.assertTrue(action is SavePlotAction)
        if(action is SavePlotAction)
            action.writeToFile(plotFile)
        Assert.assertTrue(plotFile.readText().isNotEmpty())
    }

    @Test
    fun testSaveMultiLineChange() {
        Assert.assertEquals(0f, sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite!!.look.yInUserInterfaceDimensionUnit)

        sprite!!.actionFactory.createStartPlotAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        sprite!!.actionFactory.createStopPlotAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        sprite!!.actionFactory.createStartPlotAction(sprite).act(1.0f)
        sprite!!.actionFactory.createChangeXByNAction(sprite, SequenceAction(), xMovement).act(1.0f)
        sprite!!.actionFactory.createStopPlotAction(sprite).act(1.0f)
        val action = sprite!!.actionFactory.createSavePlotAction(sprite, SequenceAction(), FILE)

        Assert.assertTrue(action is SavePlotAction)
        if(action is SavePlotAction)
            action.writeToFile(plotFile)
        Assert.assertTrue(plotFile.readText().isNotEmpty())
    }

    private fun createTestProject() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
    }

    companion object {
        private const val X_MOVEMENT = 100.0f
        private const val projectName = "testProject"
        private const val FILENAME = "$projectName.svg"
        private val FILE = Formula(FILENAME)
    }
}