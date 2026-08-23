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

package org.catrobat.catroid.uiespresso.stage

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.MqttScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.PublishMqttMessageBrick
import org.catrobat.catroid.content.bricks.WhenMqttMessageReceivedBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MqttEndToEndTest {

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var receivedPayload: UserVariable
    private lateinit var receivedTopic: UserVariable
    private val probes = mutableListOf<MqttClient>()
    private val publishDelivered = CountDownLatch(1)
    private var publishedPayload: String? = null

    @Rule
    @JvmField
    var baseActivityTestRule = BaseActivityTestRule(StageActivity::class.java, true, false)

    @Before
    fun setUp() {
        assumeBrokerReachable()
        writeMqttSettings()
        createProject()
        subscribeToOutgoingTopic()
        baseActivityTestRule.launchActivity(null)
    }

    private fun subscribeToOutgoingTopic() {
        val subscriber = newProbe("probe-sub")
        subscriber.subscribe(OUTGOING_TOPIC) { _, message ->
            publishedPayload = String(message.payload, Charsets.UTF_8)
            publishDelivered.countDown()
        }
    }

    @After
    fun tearDown() {
        probes.forEach { probe ->
            try {
                probe.takeIf { it.isConnected }?.disconnect()
                probe.close()
            } catch (ignored: Exception) {
            }
        }
        probes.clear()
        TestUtils.deleteProjects(PROJECT_NAME)
    }

    @Test
    fun testPublishBrickDeliversMessageToBroker() {
        assertTrue(
            "Publish brick did not reach the broker within ${TIMEOUT_SECONDS}s",
            publishDelivered.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        )
        assertEquals(PUBLISHED_MESSAGE, publishedPayload)
    }

    @Test
    fun testReceiveBrickBindsPayloadAndTopicToVariables() {
        val publisher = newProbe("probe-pub")
        publisher.publish(
            INCOMING_TOPIC,
            MqttMessage("GO".toByteArray(Charsets.UTF_8)).apply { qos = 1 }
        )

        assertTrue(
            "Receive script did not set the payload variable within ${TIMEOUT_SECONDS}s",
            waitForVariable(receivedPayload, "GO")
        )
        assertEquals("GO", receivedPayload.value)
        assertEquals(INCOMING_TOPIC, receivedTopic.value)
    }

    @Test
    fun testWildcardSubscriptionReceivesNestedTopic() {
        val publisher = newProbe("probe-wildcard")
        publisher.publish(
            "$WILDCARD_ROOT/kitchen/light",
            MqttMessage("ON".toByteArray(Charsets.UTF_8)).apply { qos = 1 }
        )

        assertTrue(
            "Wildcard script did not receive the nested topic within ${TIMEOUT_SECONDS}s",
            waitForVariable(receivedPayload, "ON")
        )
        assertEquals("$WILDCARD_ROOT/kitchen/light", receivedTopic.value)
    }

    private fun waitForVariable(variable: UserVariable, expected: String): Boolean {
        val deadline = System.currentTimeMillis() + TIMEOUT_SECONDS * 1000
        while (System.currentTimeMillis() < deadline) {
            if (variable.value == expected) {
                return true
            }
            Thread.sleep(POLL_INTERVAL_MILLIS)
        }
        return false
    }

    private fun newProbe(clientId: String): MqttClient {
        val client = MqttClient(BROKER_URL, clientId, MemoryPersistence())
        client.connect(MqttConnectOptions().apply { isCleanSession = true; connectionTimeout = 5 })
        probes.add(client)
        return client
    }

    private fun writeMqttSettings() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean(SettingsFragment.SETTINGS_SHOW_MQTT_BRICKS, true)
            .putString(SettingsFragment.MQTT_HOST, BROKER_HOST)
            .putString(SettingsFragment.MQTT_PORT, BROKER_PORT.toString())
            .putBoolean(SettingsFragment.MQTT_TLS, false)
            .putString(SettingsFragment.MQTT_USERNAME, "")
            .putString(SettingsFragment.MQTT_CLIENT_ID, "catroid-e2e")
            .commit()
    }

    private fun createProject() {
        project = UiTestUtils.createProjectWithCustomScript(PROJECT_NAME, StartScript())
        sprite = UiTestUtils.getDefaultTestSprite(project)

        receivedPayload = UserVariable("receivedPayload", "")
        receivedTopic = UserVariable("receivedTopic", "")
        project.addUserVariable(receivedPayload)
        project.addUserVariable(receivedTopic)

        UiTestUtils.getDefaultTestScript(project)
            .addBrick(PublishMqttMessageBrick(PUBLISHED_MESSAGE, OUTGOING_TOPIC))

        sprite.addScript(receiveScript(INCOMING_TOPIC))
        sprite.addScript(receiveScript("$WILDCARD_ROOT/#"))

        ProjectManager.getInstance().currentProject = project
    }

    private fun receiveScript(topic: String) = MqttScript(topic).also {
        it.payloadVariable = receivedPayload
        it.topicVariable = receivedTopic
        it.scriptBrick = WhenMqttMessageReceivedBrick(it)
    }

    private fun assumeBrokerReachable() {
        try {
            MqttClient(BROKER_URL, "probe-reachability", MemoryPersistence()).use { client ->
                client.connect(MqttConnectOptions().apply { connectionTimeout = 3 })
                client.disconnect()
            }
        } catch (exception: Exception) {
            org.junit.Assume.assumeNoException(
                "No MQTT broker at $BROKER_URL, skipping end to end test", exception
            )
        }
    }

    private inline fun MqttClient.use(block: (MqttClient) -> Unit) {
        try {
            block(this)
        } finally {
            close(true)
        }
    }

    companion object {
        private const val BROKER_HOST = "10.0.2.2"
        private const val BROKER_PORT = 1883
        private const val BROKER_URL = "tcp://$BROKER_HOST:$BROKER_PORT"
        private const val PROJECT_NAME = "MqttEndToEndTest"
        private const val OUTGOING_TOPIC = "catrobat/e2e/out"
        private const val INCOMING_TOPIC = "catrobat/e2e/cmd"
        private const val WILDCARD_ROOT = "catrobat/e2e/home"
        private const val PUBLISHED_MESSAGE = "hello-from-catroid"
        private const val TIMEOUT_SECONDS = 15L
        private const val POLL_INTERVAL_MILLIS = 100L
    }
}
