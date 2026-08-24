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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetPenColorActionTest {

    private lateinit var sprite: Sprite

    @Before
    @Throws(Exception::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
    }

    @Test
    fun testSetPenColor() {
        val red = 100
        val green = 150
        val blue = 200

        sprite.actionFactory.createSetPenColorAction(
            sprite, SequenceAction(), Formula(red), Formula(green), Formula(blue)
        ).act(1.0f)

        val penColor = sprite.penConfiguration.penColor
        assertEquals("Red color mismatch", red / 255f, penColor.r, 0.01f)
        assertEquals("Green color mismatch", green / 255f, penColor.g, 0.01f)
        assertEquals("Blue color mismatch", blue / 255f, penColor.b, 0.01f)
    }

    @Test
    fun testSetPenColorWithNullFormulas() {
        sprite.actionFactory.createSetPenColorAction(
            sprite, SequenceAction(), null, null, null
        ).act(1.0f)

        val penColor = sprite.penConfiguration.penColor
        assertEquals("Red color mismatch", 0f, penColor.r, 0.01f)
        assertEquals("Green color mismatch", 0f, penColor.g, 0.01f)
        assertEquals("Blue color mismatch", 0f, penColor.b, 0.01f)
    }
}
