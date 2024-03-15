/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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
package org.catrobat.catroid.test.utiltests

import org.catrobat.catroid.utils.StringFinder
import org.catrobat.catroid.utils.StringFinder.Companion.encodeSpecialChars
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StringFinderTest {
    @Rule
    @JvmField
    val exception: ExpectedException = ExpectedException.none()

    private val singleLine =
        "Mos eisley spaceport. You will never find a more wretched hive of scum and villainy."
    private val multiLine =
        """
        I must not fear.
        Fear is the mind-killer.
        Fear is the little-death that brings total obliteration.
        I will face my fear.
        I will permit it to pass over me and through me.
        And when it has gone past I will turn the inner eye to see its path.
        Where the fear has gone there will be nothing. Only I will remain.
        """.trimIndent()

    @Test
    fun testMatchBetween() {
        val start = "find"
        val end = "of"

        val stringFinder = StringFinder()
        assertEquals(" a more wretched hive ", stringFinder.findBetween(singleLine, start, end))
    }

    @Test
    fun testMatchWithNewLineChars() {
        val start = "fear.\n"
        val end = "\nFear is the little-death that brings total obliteration."

        val stringFinder = StringFinder()
        assertEquals("Fear is the mind-killer.", stringFinder.findBetween(multiLine, start, end))
    }

    @Test
    fun testMultipleStartStringMatches() {
        val start = "Fear"
        val end = "I will face my fear."

        val stringFinder = StringFinder()
        assertEquals(
            """
            | is the mind-killer.
            |Fear is the little-death that brings total obliteration.
            |
            """.trimMargin(), stringFinder.findBetween(multiLine, start, end)
        )
    }

    @Test
    fun testMultipleEndStringMatches() {
        val start = "find"
        val end = encodeSpecialChars(".")

        val stringFinder = StringFinder()
        assertEquals(
            " a more wretched hive of scum and villainy",
            stringFinder.findBetween(singleLine, start, end)
        )
    }

    @Test
    fun testNoMatchForEnd() {
        val start = "find"
        val end = "I won't be found"

        val stringFinder = StringFinder()
        assertNull(stringFinder.findBetween(singleLine, start, end))
    }
}
