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

package org.catrobat.catroid.test.formulaeditor.sensor

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.XmlHeader
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(ProjectManager::class)
class SensorStageSensorsTest {

    @Before
    fun setup() {
        mockXmlHeader.virtualScreenHeight = expectedHeight
        mockXmlHeader.virtualScreenWidth = expectedWidth
        PowerMockito.mockStatic(ProjectManager::class.java)
        PowerMockito.`when`(ProjectManager.getInstance()).thenReturn(mockManager)
        PowerMockito.`when`(mockManager.currentProject).thenReturn(mockCurrentProject)
        PowerMockito.`when`(mockCurrentProject.xmlHeader).thenReturn(mockXmlHeader)
    }

    @Test
    fun stageHeightTest() {
        compareToSensor(expectedHeight, Sensors.STAGE_HEIGHT)
    }

    @Test
    fun stageWidthTest() {
        compareToSensor(expectedWidth, Sensors.STAGE_WIDTH)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
        private const val expectedHeight: Int = 1080
        private const val expectedWidth: Int = 720

        private var mockManager = mock(ProjectManager::class.java)
        private var mockCurrentProject = mock(Project::class.java)
        private var mockXmlHeader = mock(XmlHeader::class.java)
    }
}
