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

package org.catrobat.catroid.test.content.bricks

import com.thoughtworks.xstream.io.xml.DomDriver
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.SimulateGamepadStickBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.XstreamSerializer
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.StringReader

@RunWith(JUnit4::class)
class SimulateGamepadStickBrickTest {

    private val xstream = XstreamSerializer.getInstance().xstream

    @Test
    fun `new brick moves the left stick to the center`() {
        val brick = SimulateGamepadStickBrick()

        assertEquals("LEFT", brick.stick)
        assertEquals(0.0, xOf(brick), 0.0)
        assertEquals(0.0, yOf(brick), 0.0)
    }

    @Test
    fun `clone keeps stick and position and is independent of the original brick`() {
        val brick = SimulateGamepadStickBrick("RIGHT", Formula(30), Formula(-40))

        val clone = brick.clone() as SimulateGamepadStickBrick
        clone.stick = "LEFT"

        assertNotSame(brick, clone)
        assertEquals("RIGHT", brick.stick)
        assertEquals(30.0, xOf(clone), 0.0)
        assertEquals(-40.0, yOf(clone), 0.0)
    }

    @Test
    fun `brick is serialized with the brick alias and the stable stick ID`() {
        val xml = xstream.toXML(SimulateGamepadStickBrick("RIGHT", Formula(30), Formula(-40)))

        assertThat(xml, startsWith("<brick type=\"SimulateGamepadStickBrick\">"))
        assertThat(xml, containsString("<stick>RIGHT</stick>"))
    }

    @Test
    fun `deserialized brick keeps stick and position`() {
        val xml = xstream.toXML(SimulateGamepadStickBrick("RIGHT", Formula(30), Formula(-40)))

        val brick = fromXml(xml) as SimulateGamepadStickBrick

        assertEquals("RIGHT", brick.stick)
        assertEquals(30.0, xOf(brick), 0.0)
        assertEquals(-40.0, yOf(brick), 0.0)
    }

    @Test
    fun `deserialized brick without stick moves the left stick`() {
        val brick = fromXml("<brick type=\"SimulateGamepadStickBrick\"/>") as SimulateGamepadStickBrick

        assertEquals("LEFT", brick.stick)
    }

    private fun xOf(brick: SimulateGamepadStickBrick): Double =
        brick.getFormulaWithBrickField(BrickField.X_POSITION).interpretDouble(null)

    private fun yOf(brick: SimulateGamepadStickBrick): Double =
        brick.getFormulaWithBrickField(BrickField.Y_POSITION).interpretDouble(null)

    private fun fromXml(xml: String): Any =
        xstream.unmarshal(DomDriver().createReader(StringReader(xml)))
}
