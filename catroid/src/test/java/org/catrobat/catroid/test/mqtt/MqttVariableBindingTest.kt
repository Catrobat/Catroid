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

package org.catrobat.catroid.test.mqtt

import org.catrobat.catroid.content.MqttScript
import org.catrobat.catroid.devices.mqtt.MqttScriptRegistrar.Companion.bindMessageToVariables
import org.catrobat.catroid.devices.mqtt.ReceivedMqttMessage
import org.catrobat.catroid.formulaeditor.UserVariable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MqttVariableBindingTest {

    private val message = ReceivedMqttMessage("catrobat/home/light1/state", "ON")

    @Test
    fun testPayloadIsWrittenToSelectedVariable() {
        val script = MqttScript("catrobat/home/#")
        val payload = UserVariable("payload")
        script.payloadVariable = payload

        bindMessageToVariables(script, message)

        assertEquals("ON", payload.value)
    }

    @Test
    fun testTopicIsWrittenToSelectedVariable() {
        val script = MqttScript("catrobat/home/#")
        val topic = UserVariable("topic")
        script.topicVariable = topic

        bindMessageToVariables(script, message)

        assertEquals("catrobat/home/light1/state", topic.value)
    }

    @Test
    fun testWildcardScriptLearnsConcreteTopicNotItsFilter() {
        val script = MqttScript("catrobat/home/#")
        val topic = UserVariable("topic")
        script.topicVariable = topic

        bindMessageToVariables(script, message)

        assertEquals("catrobat/home/light1/state", topic.value)
    }

    @Test
    fun testBothVariablesAreWrittenTogether() {
        val script = MqttScript("catrobat/home/#")
        val payload = UserVariable("payload")
        val topic = UserVariable("topic")
        script.payloadVariable = payload
        script.topicVariable = topic

        bindMessageToVariables(script, message)

        assertEquals("ON", payload.value)
        assertEquals("catrobat/home/light1/state", topic.value)
    }

    @Test
    fun testUnsetVariablesAreToleratedWithoutCrashing() {
        bindMessageToVariables(MqttScript("catrobat/home/#"), message)
    }

    @Test
    fun testOnlyPayloadSelectedLeavesTopicUnused() {
        val script = MqttScript("catrobat/home/#")
        val payload = UserVariable("payload")
        script.payloadVariable = payload

        bindMessageToVariables(script, message)

        assertEquals("ON", payload.value)
        assertNull(script.topicVariable)
    }

    @Test
    fun testEmptyPayloadIsWrittenAsEmptyString() {
        val script = MqttScript("catrobat/home/#")
        val payload = UserVariable("payload")
        script.payloadVariable = payload

        bindMessageToVariables(script, ReceivedMqttMessage("catrobat/home/a", ""))

        assertEquals("", payload.value)
    }

    @Test
    fun testNumericPayloadIsKeptAsStringToPreserveLeadingZeros() {
        val script = MqttScript("catrobat/home/#")
        val payload = UserVariable("payload")
        script.payloadVariable = payload

        bindMessageToVariables(script, ReceivedMqttMessage("catrobat/home/id", "007"))

        assertEquals("007", payload.value)
    }

    @Test
    fun testLaterMessageOverwritesEarlierValue() {
        val script = MqttScript("catrobat/home/#")
        val payload = UserVariable("payload")
        script.payloadVariable = payload

        bindMessageToVariables(script, ReceivedMqttMessage("catrobat/home/a", "first"))
        bindMessageToVariables(script, ReceivedMqttMessage("catrobat/home/a", "second"))

        assertEquals("second", payload.value)
    }
}
