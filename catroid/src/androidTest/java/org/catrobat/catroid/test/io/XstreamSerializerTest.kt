/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.io

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.base.Charsets
import com.google.common.io.Files
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.LegoNXTSetting
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Setting
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ComeToFrontBrick
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.HideBrick
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.ShowBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.hamcrest.Matchers
import org.hamcrest.number.OrderingComparison
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class XstreamSerializerTest {
    private val storageHandler: XstreamSerializer
    private val projectName = "testProject"
    private var currentProjectBuffer: Project? = null

    init {
        storageHandler = XstreamSerializer.getInstance()
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestUtils.deleteProjects(projectName)
        currentProjectBuffer = ProjectManager.getInstance().currentProject
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ProjectManager.getInstance().currentProject = currentProjectBuffer
        TestUtils.deleteProjects(projectName)
    }

    @Test
    @Throws(Exception::class)
    fun testSerializeProject() {
        val xPosition = 457
        val yPosition = 598
        val size = 0.8f
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val firstSprite = Sprite("first")
        val secondSprite = Sprite("second")
        val thirdSprite = Sprite("third")
        val fourthSprite = Sprite("fourth")
        val testScript: Script = StartScript()
        val otherScript: Script = StartScript()
        val hideBrick = HideBrick()
        val showBrick = ShowBrick()
        val setSizeToBrick = SetSizeToBrick(size)
        val comeToFrontBrick = ComeToFrontBrick()
        val placeAtBrick = PlaceAtBrick(xPosition, yPosition)
        testScript.addBrick(hideBrick)
        testScript.addBrick(showBrick)
        testScript.addBrick(setSizeToBrick)
        testScript.addBrick(comeToFrontBrick)
        otherScript.addBrick(placeAtBrick)
        firstSprite.addScript(testScript)
        secondSprite.addScript(otherScript)
        project.defaultScene.addSprite(firstSprite)
        project.defaultScene.addSprite(secondSprite)
        project.defaultScene.addSprite(thirdSprite)
        project.defaultScene.addSprite(fourthSprite)
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
        val loadedProject = XstreamSerializer.getInstance()
            .loadProject(project.directory, ApplicationProvider.getApplicationContext())
        val preScene = project.defaultScene
        val postScene = loadedProject.defaultScene
        val preSpriteList = project.defaultScene.spriteList as ArrayList<Sprite>
        val postSpriteList = loadedProject.defaultScene.spriteList as ArrayList<Sprite>
        Assert.assertEquals(preScene.name, postScene.name)
        Assert.assertEquals(preSpriteList[0].name, postSpriteList[0].name)
        Assert.assertEquals(preSpriteList[1].name, postSpriteList[1].name)
        Assert.assertEquals(preSpriteList[2].name, postSpriteList[2].name)
        Assert.assertEquals(preSpriteList[3].name, postSpriteList[3].name)
        Assert.assertEquals(preSpriteList[4].name, postSpriteList[4].name)
        Assert.assertEquals(project.name, loadedProject.name)
        val actualXPosition = (postSpriteList[2].getScript(0).brickList[0] as FormulaBrick)
            .getFormulaWithBrickField(Brick.BrickField.X_POSITION)
        val actualYPosition = (postSpriteList[2].getScript(0).brickList[0] as FormulaBrick)
            .getFormulaWithBrickField(Brick.BrickField.Y_POSITION)
        val actualSize = (postSpriteList[1].getScript(0).brickList[2] as FormulaBrick)
            .getFormulaWithBrickField(Brick.BrickField.SIZE)
        Assert.assertEquals(size, actualSize.interpretFloat(null))
        Assert.assertEquals(xPosition, actualXPosition.interpretFloat(null).toInt())
        Assert.assertEquals(yPosition, actualYPosition.interpretFloat(null).toInt())
    }

    @Test
    @Throws(IOException::class)
    fun testSanityCheck() {
        val xPosition = 457
        val yPosition = 598
        val size = 0.8f
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val firstSprite = Sprite("first")
        val secondSprite = Sprite("second")
        val thirdSprite = Sprite("third")
        val fourthSprite = Sprite("fourth")
        val testScript: Script = StartScript()
        val otherScript: Script = StartScript()
        val hideBrick = HideBrick()
        val showBrick = ShowBrick()
        val setSizeToBrick = SetSizeToBrick(size)
        val comeToFrontBrick = ComeToFrontBrick()
        val placeAtBrick = PlaceAtBrick(xPosition, yPosition)
        testScript.addBrick(hideBrick)
        testScript.addBrick(showBrick)
        testScript.addBrick(setSizeToBrick)
        testScript.addBrick(comeToFrontBrick)
        otherScript.addBrick(placeAtBrick)
        firstSprite.addScript(testScript)
        secondSprite.addScript(otherScript)
        project.defaultScene.addSprite(firstSprite)
        project.defaultScene.addSprite(secondSprite)
        project.defaultScene.addSprite(thirdSprite)
        project.defaultScene.addSprite(fourthSprite)
        val tmpCodeFile = File(project.directory, Constants.TMP_CODE_XML_FILE_NAME)
        val currentCodeFile = File(project.directory, Constants.CODE_XML_FILE_NAME)
        Assert.assertFalse(tmpCodeFile.exists())
        Assert.assertFalse(currentCodeFile.exists())
        storageHandler.saveProject(project)
        Assert.assertTrue(currentCodeFile.exists())
        org.junit.Assert.assertThat(
            currentCodeFile.length(),
            Matchers.`is`(OrderingComparison.greaterThan(0L))
        )

        // simulate 1st Option: tmp_code.xml exists but code.xml doesn't exist
        // --> saveProject process will restore from tmp_code.xml
        Assert.assertTrue(tmpCodeFile.createNewFile())
        StorageOperations.transferData(currentCodeFile, tmpCodeFile)
        val currentCodeFileXml = Files.toString(currentCodeFile, Charsets.UTF_8)
        Assert.assertTrue(currentCodeFile.delete())
        storageHandler.saveProject(project)
        Assert.assertTrue(currentCodeFile.exists())
        org.junit.Assert.assertThat(
            currentCodeFile.length(),
            Matchers.`is`(OrderingComparison.greaterThan(0L))
        )
        Assert.assertEquals(currentCodeFileXml, Files.toString(currentCodeFile, Charsets.UTF_8))

        // simulate 2nd Option: tmp_code.xml and code.xml exist
        // --> saveProject process will discard tmp_code.xml and use code.xml
        Assert.assertTrue(tmpCodeFile.createNewFile())
        storageHandler.saveProject(project)
        Assert.assertFalse(tmpCodeFile.exists())
    }

    @Test
    fun testGetRequiredResources() {
        val resources = generateMultiplePermissionsProject().requiredResources
        Assert.assertTrue(resources.contains(Brick.FACE_DETECTION))
        Assert.assertTrue(resources.contains(Brick.BLUETOOTH_LEGO_NXT))
        Assert.assertTrue(resources.contains(Brick.TEXT_TO_SPEECH))
    }

    @Test
    fun testPermissionFileRemoved() {
        val project = generateMultiplePermissionsProject()
        ProjectManager.getInstance().currentProject = project
        XstreamSerializer.getInstance().saveProject(project)
        val permissionsFile = File(project.directory, Constants.PERMISSIONS_FILE_NAME)
        Assert.assertFalse(permissionsFile.exists())
    }

    @Test
    fun testSerializeSettings() {
        val sensorMapping = arrayOf(
            NXTSensor.Sensor.TOUCH,
            NXTSensor.Sensor.SOUND,
            NXTSensor.Sensor.LIGHT_INACTIVE,
            NXTSensor.Sensor.ULTRASONIC
        )
        var project = generateMultiplePermissionsProject()
        ProjectManager.getInstance().currentProject = project
        SettingsFragment.setLegoMindstormsNXTSensorMapping(
            ApplicationProvider.getApplicationContext(),
            sensorMapping
        )
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
        var setting = project.settings[0]
        Assert.assertTrue(setting is LegoNXTSetting)
        var nxtSetting = setting as LegoNXTSetting
        var actualSensorMapping = nxtSetting.sensorMapping
        Assert.assertEquals(4, actualSensorMapping.size)
        Assert.assertEquals(sensorMapping[0], actualSensorMapping[0])
        Assert.assertEquals(sensorMapping[1], actualSensorMapping[1])
        Assert.assertEquals(sensorMapping[2], actualSensorMapping[2])
        Assert.assertEquals(sensorMapping[3], actualSensorMapping[3])
        val changedSensorMapping = sensorMapping.clone()
        changedSensorMapping[0] = NXTSensor.Sensor.LIGHT_ACTIVE
        SettingsFragment
            .setLegoMindstormsNXTSensorMapping(
                ApplicationProvider.getApplicationContext(),
                changedSensorMapping
            )
        Assert.assertTrue(
            loadProject(
                project.directory,
                ApplicationProvider.getApplicationContext()
            )
        )
        actualSensorMapping =
            SettingsFragment.getLegoNXTSensorMapping(ApplicationProvider.getApplicationContext())
        Assert.assertEquals(4, actualSensorMapping.size)
        Assert.assertEquals(sensorMapping[0], actualSensorMapping[0])
        Assert.assertEquals(sensorMapping[1], actualSensorMapping[1])
        Assert.assertEquals(sensorMapping[2], actualSensorMapping[2])
        Assert.assertEquals(sensorMapping[3], actualSensorMapping[3])
        project = ProjectManager.getInstance().currentProject
        setting = project.settings[0]
        nxtSetting = setting as LegoNXTSetting
        org.junit.Assert.assertThat<Setting>(
            setting, Matchers.instanceOf(
                LegoNXTSetting::class.java
            )
        )
        actualSensorMapping = nxtSetting.sensorMapping
        Assert.assertEquals(4, actualSensorMapping.size)
        Assert.assertEquals(sensorMapping[0], actualSensorMapping[0])
        Assert.assertEquals(sensorMapping[1], actualSensorMapping[1])
        Assert.assertEquals(sensorMapping[2], actualSensorMapping[2])
        Assert.assertEquals(sensorMapping[3], actualSensorMapping[3])
    }

    private fun generateMultiplePermissionsProject(): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val motorBrick = LegoNxtMotorMoveBrick(
            LegoNxtMotorMoveBrick.Motor.MOTOR_A, SET_SPEED_INITIALLY
        )
        val setSizeToBrick = SetSizeToBrick(
            Formula(
                FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.FACE_SIZE.name, null)
            )
        )
        val moveBrick: Brick = DroneMoveForwardBrick(
            DEFAULT_MOVE_TIME_IN_MILLISECONDS,
            DEFAULT_MOVE_POWER_IN_PERCENT
        )
        val firstSprite = Sprite("first")
        val testScript: Script = StartScript()
        testScript.addBrick(HideBrick())
        testScript.addBrick(ShowBrick())
        testScript.addBrick(SpeakBrick(""))
        testScript.addBrick(motorBrick)
        firstSprite.addScript(testScript)
        val secondSprite = Sprite("second")
        val otherScript: Script = StartScript()
        otherScript.addBrick(setSizeToBrick)
        otherScript.addBrick(moveBrick)
        secondSprite.addScript(otherScript)
        project.defaultScene.addSprite(firstSprite)
        project.defaultScene.addSprite(secondSprite)
        return project
    }

    @Test
    fun testExtractDefaultSceneNameFromXml() {
        val firstSceneName = "First Scene"
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.sceneList[0].name = firstSceneName
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
        Assert.assertEquals(
            firstSceneName,
            XstreamSerializer.extractDefaultSceneNameFromXml(project.directory)
        )
    }

    companion object {
        private const val SET_SPEED_INITIALLY = -70
        private const val DEFAULT_MOVE_TIME_IN_MILLISECONDS = 2000
        private const val DEFAULT_MOVE_POWER_IN_PERCENT = 20
    }
}
