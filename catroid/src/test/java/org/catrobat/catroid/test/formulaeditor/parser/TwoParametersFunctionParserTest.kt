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

package org.catrobat.catroid.test.formulaeditor.parser

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Functions.ARCTAN2
import org.catrobat.catroid.formulaeditor.Functions.MAX
import org.catrobat.catroid.formulaeditor.Functions.MIN
import org.catrobat.catroid.formulaeditor.Functions.MOD
import org.catrobat.catroid.formulaeditor.Functions.RAND
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.testDoubleParameterFunction
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import java.lang.Math.max
import java.lang.Math.min
import java.lang.Math.toDegrees
import java.util.Collections
import kotlin.math.atan2

@RunWith(Parameterized::class)
class TwoParametersFunctionParserTest(
    val name: String,
    private val function: Functions,
    private val associatedFunction: AssociatedFunction,
    private val firstParameterValue: Double,
    private val secondParameterValue: Double
) {

    companion object {
        private const val NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf("MOD", MOD, AssociatedFunction { firPar, secPar -> firPar % secPar }, 9.0, 2.0),
                arrayOf("RAND", RAND, AssociatedFunction { _, _ -> 0.0 }, 0.0, 0.0),
                arrayOf("ARCTAN2", ARCTAN2, AssociatedFunction { firPar, secPar -> toDegrees(atan2(firPar, secPar)) }, 9.0, 3.0),
                arrayOf("MAX", MAX, AssociatedFunction { firPar, secPar -> max(firPar, secPar) }, 9.0, 3.0),
                arrayOf("MIN", MIN, AssociatedFunction { firPar, secPar -> min(firPar, secPar) }, 9.0, 3.0))
        }
    }

    private var sprite: Sprite? = null
    private var scope: Scope? = null

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    @Before
    fun setUp() {
        val context = MockUtil.mockContextForProject(dependencyModules)
        val project = Project(context, "Project")
        sprite = Sprite("sprite")
        scope = Scope(project, sprite!!, SequenceAction())
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testNumberParameters() {
        val firstParameter = listOf(InternToken(InternTokenType.NUMBER, firstParameterValue.toString()))
        val secondParameter = listOf(InternToken(InternTokenType.NUMBER, secondParameterValue.toString()))
        val expectedValue = associatedFunction.function(firstParameterValue, secondParameterValue)
        testDoubleParameterFunction(function, firstParameter, secondParameter, expectedValue, scope)
    }

    @Test
    fun testStringParameters() {
        val firstParameter = listOf(InternToken(InternTokenType.STRING, firstParameterValue.toString()))
        val secondParameter = listOf(InternToken(InternTokenType.STRING, secondParameterValue.toString()))
        val expectedValue = associatedFunction.function(firstParameterValue, secondParameterValue)
        testDoubleParameterFunction(function, firstParameter, secondParameter, expectedValue, scope)
    }

    @Test
    fun testEmptyParameters() {
        val firstParameter = listOf(InternToken(InternTokenType.STRING, ""))
        val secondParameter = listOf(InternToken(InternTokenType.STRING, ""))
        val expectedValue = 0.0
        testDoubleParameterFunction(function, firstParameter, secondParameter, expectedValue, scope)
    }

    @Test
    fun testEmptyFirstParameter() {
        val firstParameter = listOf(InternToken(InternTokenType.STRING, ""))
        val secondParameter = listOf(InternToken(InternTokenType.STRING, secondParameterValue.toString()))
        val expectedValue = 0.0
        testDoubleParameterFunction(function, firstParameter, secondParameter, expectedValue, scope)
    }

    @Test
    fun testEmptySecondParameter() {
        val firstParameter = listOf(InternToken(InternTokenType.STRING, firstParameterValue.toString()))
        val secondParameter = listOf(InternToken(InternTokenType.STRING, ""))
        val expectedValue = 0.0
        testDoubleParameterFunction(function, firstParameter, secondParameter, expectedValue, scope)
    }

    @Test
    fun testNotNumericalStringParameters() {
        val firstParameter = listOf(InternToken(InternTokenType.STRING, NOT_NUMERICAL_STRING))
        val secondParameter = listOf(InternToken(InternTokenType.STRING, NOT_NUMERICAL_STRING))
        val expectedValue = 0.0
        testDoubleParameterFunction(function, firstParameter, secondParameter, expectedValue, scope)
    }

    @Test
    fun testInvalidFormulaParameter() {
        val firstParameter = FormulaEditorTestUtil.buildBinaryOperator(
            InternTokenType.NUMBER, "15.0",
            Operators.PLUS,
            InternTokenType.STRING, NOT_NUMERICAL_STRING
        )
        val secondParameter = listOf(InternToken(InternTokenType.STRING, secondParameterValue.toString()))
        val expectedValue = Double.NaN
        testDoubleParameterFunction(function, firstParameter, secondParameter, expectedValue, scope)
    }

    data class AssociatedFunction(val function: (Double, Double) -> Double)
}
