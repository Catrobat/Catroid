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

package org.catrobat.catroid.uiespresso.content.brick.app

import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.MqttScript
import org.catrobat.catroid.content.bricks.PublishMqttMessageBrick
import org.catrobat.catroid.content.bricks.WhenMqttMessageReceivedBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MqttBrickViewTest {

    @Rule
    @JvmField
    var baseActivityTestRule = BaseActivityTestRule(ProjectActivity::class.java, true, false)

    @Before
    fun setUp() {
        val project = UiTestUtils.createDefaultTestProject(PROJECT_NAME)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = UiTestUtils.getDefaultTestSprite(project)
        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(PROJECT_NAME)
    }

    private fun activity() = baseActivityTestRule.activity

    @Test
    fun testPublishBrickInflates() {
        val view = PublishMqttMessageBrick("ON", "catrobat/home/light1/set").getView(activity())
        assertNotNull(view)
    }

    @Test
    fun testPublishBrickShowsMessageTopicQosAndRetainedFields() {
        val view = PublishMqttMessageBrick("ON", "catrobat/home/light1/set").getView(activity())
        assertNotNull(view.findViewById(R.id.brick_publish_mqtt_message_edit_text))
        assertNotNull(view.findViewById(R.id.brick_publish_mqtt_topic_edit_text))
        assertNotNull(view.findViewById<Spinner>(R.id.brick_publish_mqtt_qos_spinner))
        assertNotNull(view.findViewById<CheckBox>(R.id.brick_publish_mqtt_retained_checkbox))
    }

    @Test
    fun testPublishBrickQosSpinnerOffersTheThreeQosLevels() {
        val view = PublishMqttMessageBrick("ON", "catrobat/home/light1/set").getView(activity())
        val spinner = view.findViewById<Spinner>(R.id.brick_publish_mqtt_qos_spinner)
        assertEquals(3, spinner.adapter.count)
    }

    @Test
    fun testReceiveBrickInflates() {
        val brick = WhenMqttMessageReceivedBrick(MqttScript("catrobat/home/+/state"))
        assertNotNull(brick.getView(activity()))
    }

    @Test
    fun testReceiveBrickShowsTopicAndBothVariableSpinners() {
        val view = WhenMqttMessageReceivedBrick(MqttScript("catrobat/home/+/state"))
            .getView(activity())
        assertNotNull(view.findViewById<EditText>(R.id.brick_when_mqtt_topic_edit_text))
        assertNotNull(view.findViewById<Spinner>(R.id.brick_when_mqtt_payload_spinner))
        assertNotNull(view.findViewById<Spinner>(R.id.brick_when_mqtt_topic_spinner))
    }

    @Test
    fun testReceiveBrickShowsItsConfiguredTopic() {
        val view = WhenMqttMessageReceivedBrick(MqttScript("catrobat/home/+/state"))
            .getView(activity())
        val topicField = view.findViewById<EditText>(R.id.brick_when_mqtt_topic_edit_text)
        assertEquals("catrobat/home/+/state", topicField.text.toString())
    }

    @Test
    fun testReceiveBrickWithEmptyTopicStillInflates() {
        val view = WhenMqttMessageReceivedBrick(MqttScript()).getView(activity())
        val topicField = view.findViewById<EditText>(R.id.brick_when_mqtt_topic_edit_text)
        assertEquals("", topicField.text.toString())
    }

    companion object {
        private const val PROJECT_NAME = "MqttBrickViewTest"
    }

    @Test
    fun testRenderingDoesNotBindVariablesTheUserDidNotChoose() {
        val script = MqttScript("catrobat/home/+/state")
        val brick = WhenMqttMessageReceivedBrick(script)

        brick.getView(activity())

        assertNull("rendering bound a payload variable on its own", script.payloadVariable)
        assertNull("rendering bound a topic variable on its own", script.topicVariable)
    }

    @Test
    fun testRenderingKeepsAnExistingBinding() {
        val chosen = UserVariable("chosenPayload", "")
        ProjectManager.getInstance().currentProject.addUserVariable(chosen)
        val script = MqttScript("catrobat/home/+/state")
        script.payloadVariable = chosen
        val brick = WhenMqttMessageReceivedBrick(script)

        brick.getView(activity())

        assertEquals(chosen.name, script.payloadVariable?.name)
        assertNull("the topic binding was left unset and must stay unset", script.topicVariable)
    }
}
