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

package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.graphics.Color
import org.catrobat.catroid.content.PenConfiguration
import org.catrobat.catroid.content.PenColor
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.actions.SetPenColorAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SetPenColorActionTest {

    private lateinit var action: SetPenColorAction
    private lateinit var scope: Scope
    private lateinit var redFormula: Formula
    private lateinit var greenFormula: Formula
    private lateinit var blueFormula: Formula

    @Before
    fun setUp() {
        action = SetPenColorAction()
        scope = mock(Scope::class.java)
        action.setScope(scope)
        redFormula = mock(Formula::class.java)
        greenFormula = mock(Formula::class.java)
        blueFormula = mock(Formula::class.java)
        action.setRed(redFormula)
        action.setGreen(greenFormula)
        action.setBlue(blueFormula)
    }

    @Test
    fun testUpdate() {
        `when`(redFormula.interpretInteger(scope)).thenReturn(255)
        `when`(greenFormula.interpretInteger(scope)).thenReturn(128)
        `when`(blueFormula.interpretInteger(scope)).thenReturn(64)

        val penConfiguration = mock(PenConfiguration::class.java)
        `when`(scope.sprite.penConfiguration).thenReturn(penConfiguration)

        action.update(1f)

        verify(penConfiguration).setPenColor(PenColor(1f, 0.5f, 0.25f, 1f))
    }

    @Test
    fun testUpdateWithInterpretationException() {
        `when`(redFormula.interpretInteger(scope)).thenThrow(InterpretationException::class.java)

        action.update(1f)

        
        verifyZeroInteractions(scope.sprite.penConfiguration)
    }
}