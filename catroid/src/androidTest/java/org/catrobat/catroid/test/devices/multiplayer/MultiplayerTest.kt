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
package org.catrobat.catroid.test.devices.multiplayer

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.apache.commons.lang3.SerializationUtils
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.devices.multiplayer.MultiplayerInterface
import org.catrobat.catroid.devices.multiplayer.Multiplayer
import org.catrobat.catroid.devices.multiplayer.MultiplayerVariableMessage
import org.catrobat.catroid.formulaeditor.UserVariable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class MultiplayerTest {
    private var multiplayer: MultiplayerInterface? = null
    private var logger: ConnectionDataLogger? = null

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @Before
    fun setUp() {
        multiplayer = Multiplayer()
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        multiplayer?.setConnection(logger?.connectionProxy)
        multiplayer?.setStreams(logger?.connectionProxy?.inputStream, logger?.connectionProxy?.outputStream)
    }

    @After
    fun tearDown() {
        multiplayer?.disconnect()
        logger?.disconnectAndDestroy()
    }

    @Test
    fun testSendChangedMultiplayerVariables() {
        val multiplayerVariable = UserVariable(MULTIVARIABLE_NAME, VALUE)
        multiplayer?.sendChangedMultiplayerVariables(multiplayerVariable)
        val message = SerializationUtils.deserialize<MultiplayerVariableMessage>(logger?.nextSentMessage)
        assertEquals(MULTIVARIABLE_NAME, message.name)
        assertEquals(VALUE, message.value)
    }

    @Test
    fun testGetChangedMultiplayerVariables() {
        val project = Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME)
        var multiplayerVariable = UserVariable(MULTIVARIABLE_NAME, INITIAL_VALUE)
        project.addMultiplayerVariable(multiplayerVariable)
        multiplayerVariable = UserVariable(MULTIVARIABLE_NAME_SECOND, INITIAL_VALUE)
        project.addMultiplayerVariable(multiplayerVariable)
        projectManager.currentProject = project
        val message = MultiplayerVariableMessage(MULTIVARIABLE_NAME, VALUE)
        val byteArray = SerializationUtils.serialize(message)
        multiplayer?.getChangedMultiplayerVariables(byteArray)
        assertEquals(project.getMultiplayerVariable(MULTIVARIABLE_NAME).value, VALUE)
        assertEquals(project.getMultiplayerVariable(MULTIVARIABLE_NAME_SECOND).value, INITIAL_VALUE)
    }

    @Test
    fun testGetUnchangedMultiplayerVariables() {
        val project = Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME)
        val multiplayerVariable = UserVariable(MULTIVARIABLE_NAME, INITIAL_VALUE)
        project.addMultiplayerVariable(multiplayerVariable)
        projectManager.currentProject = project
        val message = MultiplayerVariableMessage(MULTIVARIABLE_NAME_SECOND, VALUE)
        val byteArray = SerializationUtils.serialize(message)
        multiplayer?.getChangedMultiplayerVariables(byteArray)
        assertEquals(project.getMultiplayerVariable(MULTIVARIABLE_NAME).value, INITIAL_VALUE)
    }

    @Test
    fun testIsAlive() {
        assertEquals(true, multiplayer?.isAlive)

        multiplayer?.disconnect()
        assertEquals(false, multiplayer?.isAlive)
    }

    companion object {
        private val PROJECT_NAME = MultiplayerTest::class.simpleName
        private const val MULTIVARIABLE_NAME = "MultiVariable"
        private const val MULTIVARIABLE_NAME_SECOND = "MultiVariable2"
        private const val INITIAL_VALUE = 0.0
        private const val VALUE = 2.0
    }
}
