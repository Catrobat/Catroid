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

package org.catrobat.catroid.test.embroidery

import com.badlogic.gdx.graphics.Color
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.DSTPatternManager
import org.catrobat.catroid.embroidery.DSTStitchCommand
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class EmbroideryExportIsolationTest {
    @Test
    @Throws(Exception::class)
    fun testExportStreamIsNotAffectedByMutatingRenderedPatternList() {
        val patternManager = DSTPatternManager()
        val sprite = Mockito.mock(Sprite::class.java)

        patternManager.addStitchCommand(DSTStitchCommand(0f, 0f, 0, sprite, Color.BLACK))
        patternManager.addStitchCommand(DSTStitchCommand(20f, 30f, 0, sprite, Color.BLACK))

        val renderedPattern = patternManager.embroideryPatternList
        val originalExportPoints = patternManager.embroideryStream.pointList.map { it.x to it.y }

        Reflection.setPrivateField(renderedPattern.first(), "xCoord", 999f)
        Reflection.setPrivateField(renderedPattern.first(), "yCoord", -999f)

        val exportPointsAfterMutation = patternManager.embroideryStream.pointList.map { it.x to it.y }

        assertEquals(originalExportPoints, exportPointsAfterMutation)
    }
}
