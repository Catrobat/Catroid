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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.stage

import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.utils.Resolution
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Method

class StageListenerResizeTest {

    @After
    fun tearDown() {
        ScreenValues.setToDefaultScreenSize()
    }

    @Test
    fun resizeUpdatesStageMetricsToActualSurfaceHeight() {
        val project = Project().apply {
            xmlHeader.virtualScreenWidth = 480
            xmlHeader.virtualScreenHeight = 800
            screenMode = ScreenModes.MAXIMIZE
        }
        val stageListener = StageListener()

        ScreenValues.currentScreenResolution = Resolution(1080, 2400)

        setField(stageListener, "project", project)
        setField(stageListener, "maxViewPort", Resolution(1080, 1800, 0, 300))

        invokeMethod(stageListener, "updateStageSurfaceResolution", 1080, 2280)

        assertEquals(1080, ScreenValues.currentScreenResolution.width)
        assertEquals(2280, ScreenValues.currentScreenResolution.height)

        val maxViewPort = getField(stageListener, "maxViewPort") as Resolution
        assertEquals(1080, maxViewPort.width)
        assertEquals(1800, maxViewPort.height)
        assertEquals(240, maxViewPort.offsetY)
    }

    private fun setField(target: Any, name: String, value: Any?) {
        val field: Field = target.javaClass.getDeclaredField(name)
        field.isAccessible = true
        field.set(target, value)
    }

    private fun getField(target: Any, name: String): Any? {
        val field: Field = target.javaClass.getDeclaredField(name)
        field.isAccessible = true
        return field.get(target)
    }

    private fun invokeMethod(target: Any, name: String, vararg args: Any) {
        val method: Method = target.javaClass.getDeclaredMethod(
            name,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )
        method.isAccessible = true
        method.invoke(target, *args)
    }
}
