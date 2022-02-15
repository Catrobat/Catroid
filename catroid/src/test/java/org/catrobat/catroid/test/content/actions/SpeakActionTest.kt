/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
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
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.test.PowerMockUtil.Companion.mockStaticAppContextAndInitializeStaticSingletons
import org.catrobat.catroid.test.utils.Reflection.getPrivateField
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import java.io.File

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(
    File::class,
    SpeakAction::class,
    Constants::class,
    FlavoredConstants::class,
    CatroidApplication::class
)
class SpeakActionTest(
    private val name: String,
    private val formula: Formula?,
    private val expectedValue: String
) {
    lateinit var sprite: Sprite
    private var scope: Scope? = null
    private val temporaryFolder = TemporaryFolder()
    lateinit var mobileServiceAvailability: MobileServiceAvailability
    lateinit var contextMock: Context

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

    @Before
    @Throws(Exception::class)
    fun setUp() {
        contextMock = mockStaticAppContextAndInitializeStaticSingletons()
        temporaryFolder.create()
        val temporaryCacheFolder = temporaryFolder.newFolder("SpeakTest")
        Mockito.`when`(contextMock.cacheDir)
            .thenAnswer { temporaryCacheFolder }
        mobileServiceAvailability = Mockito.mock(MobileServiceAvailability::class.java)
        Mockito.`when`(mobileServiceAvailability.isGmsAvailable(contextMock)).thenReturn(true)
        sprite = Sprite("testSprite")
        scope = Scope(ProjectManager.getInstance().currentProject, sprite, SequenceAction())
        val project = Project(MockUtil.mockContextForProject(), "Project")
        ProjectManager.getInstance().currentProject = project
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
        val synthesizer = SpeechSynthesizer(scope, formula)
        action.speechSynthesizer = synthesizer
        action.mobileServiceAvailability = mobileServiceAvailability
        action.context = contextMock
        action.initialize()
        assertEquals(expectedValue, getPrivateField(synthesizer, "interpretedText").toString())
    }
}
