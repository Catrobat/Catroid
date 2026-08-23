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

package org.catrobat.catroid.test.io

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.MqttScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.PublishMqttMessageBrick
import org.catrobat.catroid.content.bricks.WhenMqttMessageReceivedBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MqttBrickSerializationTest {

    private lateinit var project: Project

    @Before
    fun setUp() {
        project = Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)

        val payloadVariable = UserVariable("payload", "")
        project.addUserVariable(payloadVariable)

        val receiveScript = MqttScript(TOPIC_FILTER)
        receiveScript.payloadVariable = payloadVariable
        receiveScript.scriptBrick = WhenMqttMessageReceivedBrick(receiveScript)
        sprite.addScript(receiveScript)

        val startScript = StartScript()
        startScript.addBrick(PublishMqttMessageBrick(MESSAGE, PUBLISH_TOPIC))
        sprite.addScript(startScript)

        ProjectManager.getInstance().currentProject = project
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(PROJECT_NAME)
    }

    private fun reload(): Sprite = XstreamSerializer.getInstance()
        .loadProject(project.directory, ApplicationProvider.getApplicationContext())
        .defaultScene.spriteList.first { it.name == "testSprite" }

    @Test
    fun testReceiveBrickSurvivesSaveAndLoad() {
        val script = reload().scriptList.filterIsInstance<MqttScript>().firstOrNull()
        assertNotNull("MqttScript was not restored from code.xml", script)
        assertEquals(TOPIC_FILTER, script!!.topic)
        assertTrue(script.scriptBrick is WhenMqttMessageReceivedBrick)
    }

    @Test
    fun testReceiveBrickKeepsItsPayloadVariable() {
        val script = reload().scriptList.filterIsInstance<MqttScript>().first()
        assertEquals("payload", script.payloadVariable?.name)
    }

    @Test
    fun testPublishBrickSurvivesSaveAndLoad() {
        val brick = reload().scriptList
            .flatMap { it.brickList }
            .filterIsInstance<PublishMqttMessageBrick>()
            .firstOrNull()
        assertNotNull("PublishMqttMessageBrick was not restored from code.xml", brick)
        assertEquals(
            MESSAGE,
            brick!!.getFormulaWithBrickField(Brick.BrickField.MQTT_MESSAGE).root.value
        )
    }

    @Test
    fun testPublishBrickKeepsItsTopic() {
        val brick = reload().scriptList
            .flatMap { it.brickList }
            .filterIsInstance<PublishMqttMessageBrick>()
            .first()
        assertEquals(
            PUBLISH_TOPIC,
            brick.getFormulaWithBrickField(Brick.BrickField.MQTT_TOPIC).root.value
        )
    }

    @Test
    fun testReloadedProjectStillRequiresTheMqttResource() {
        val reloaded = XstreamSerializer.getInstance()
            .loadProject(project.directory, ApplicationProvider.getApplicationContext())
        assertTrue(reloaded.requiredResources.contains(Brick.MQTT_CONNECTION))
    }

    companion object {
        private const val PROJECT_NAME = "MqttBrickSerializationTest"
        private const val TOPIC_FILTER = "catrobat/home/+/state"
        private const val PUBLISH_TOPIC = "catrobat/home/light1/set"
        private const val MESSAGE = "ON"
    }
}
