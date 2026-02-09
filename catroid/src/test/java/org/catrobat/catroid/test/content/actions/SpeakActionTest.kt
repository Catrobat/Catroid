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

import android.content.Context
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SpeakAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.STRING
import org.catrobat.catroid.formulaeditor.Functions.JOIN
import org.catrobat.catroid.stage.SpeechSynthesizer
import org.catrobat.catroid.test.mockutils.MockUtil
import org.catrobat.catroid.test.utils.Reflection.getPrivateField
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SpeakActionTest(
    private val name: String,
    private val formula: Formula?,
    private val expectedValue: String
) {

    companion object {
        private const val SPEAK = "hello world!"
        private val helloStringFormulaElement = FormulaElement(STRING, "hello ", null)
        private val worldStringFormulaElement = FormulaElement(STRING, "world!", null)
        private val textFunction = Formula(
            FormulaElement(
                FUNCTION,
                JOIN.name, null, helloStringFormulaElement, worldStringFormulaElement
            )
        )

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("Null formula", null, ""),
            arrayOf("Not a number formula", Formula(Double.NaN), "NaN"),
            arrayOf("Number formula", Formula(888.88), "888.88"),
            arrayOf("String formula", Formula(SPEAK), "hello world!"),
            arrayOf("Function formula", textFunction, "hello world!")
        )
    }

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var mobileServiceAvailabilityMock: MobileServiceAvailability

    private lateinit var contextMock: Context

    private lateinit var testSprite: Sprite
    private lateinit var testScope: Scope

    @Before
    @Throws(Exception::class)
    fun setUp() {
        contextMock = MockUtil.getApplicationContextMock()
        val project = Project(contextMock, "Project")
        ProjectManager.getInstance().currentProject = project

        every { mobileServiceAvailabilityMock.isGmsAvailable(contextMock) }.returns(true)

        testSprite = Sprite("testSprite")
        testScope = Scope(ProjectManager.getInstance().currentProject, testSprite, SequenceAction())
    }

    @Test
    fun testRequirements() {
        formula ?: return

        val speakBrick = SpeakBrick(formula)
        val resourcesSet = ResourcesSet()
        speakBrick.addRequiredResources(resourcesSet)

        assertTrue(resourcesSet.contains(Brick.TEXT_TO_SPEECH))
    }

    @Test
    @Throws(Exception::class)
    fun testFormulaInterpretation() {
        val action = SpeakAction()
        val synthesizer = SpeechSynthesizer(testScope, formula)
        action.speechSynthesizer = synthesizer
        action.mobileServiceAvailability = mobileServiceAvailabilityMock
        action.context = contextMock
        action.initialize()

        assertEquals(expectedValue, getPrivateField(synthesizer, "interpretedText").toString())
    }

    @After
    fun tearDown() {
        MockUtil.cleanUpFiles()
    }
}
