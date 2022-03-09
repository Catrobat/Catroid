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
import org.catrobat.catroid.formulaeditor.Functions.ABS
import org.catrobat.catroid.formulaeditor.Functions.ARCCOS
import org.catrobat.catroid.formulaeditor.Functions.ARCSIN
import org.catrobat.catroid.formulaeditor.Functions.ARCTAN
import org.catrobat.catroid.formulaeditor.Functions.CEIL
import org.catrobat.catroid.formulaeditor.Functions.COS
import org.catrobat.catroid.formulaeditor.Functions.EXP
import org.catrobat.catroid.formulaeditor.Functions.FLOOR
import org.catrobat.catroid.formulaeditor.Functions.LN
import org.catrobat.catroid.formulaeditor.Functions.LOG
import org.catrobat.catroid.formulaeditor.Functions.ROUND
import org.catrobat.catroid.formulaeditor.Functions.SIN
import org.catrobat.catroid.formulaeditor.Functions.SQRT
import org.catrobat.catroid.formulaeditor.Functions.TAN
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType.NUMBER
import org.catrobat.catroid.formulaeditor.InternTokenType.STRING
import org.catrobat.catroid.formulaeditor.Operators.PLUS
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.buildBinaryOperator
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.testSingleParameterFunction
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import java.lang.Math.abs
import java.lang.Math.acos
import java.lang.Math.asin
import java.lang.Math.atan
import java.lang.Math.ceil
import java.lang.Math.cos
import java.lang.Math.exp
import java.lang.Math.floor
import java.lang.Math.log
import java.lang.Math.log10
import java.lang.Math.round
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.lang.Math.tan
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import java.util.Collections

@RunWith(Parameterized::class)
class SingleParameterFunctionParserTest(
    val name: String,
    private val function: Functions,
    private val associatedFunction: AssociatedFunction,
    private val parameterValue: Double
) {

    companion object {
        private const val NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf("SIN", SIN, AssociatedFunction { par -> sin(toRadians(par)) }, 90.0),
                arrayOf("COS", COS, AssociatedFunction { par -> cos(toRadians(par)) }, 180.0),
                arrayOf("TAN", TAN, AssociatedFunction { par -> tan(toRadians(par)) }, 30.0),
                arrayOf("LN", LN, AssociatedFunction { par -> log(par) }, 30.0),
                arrayOf("LOG", LOG, AssociatedFunction { par -> log10(par) }, 30.0),
                arrayOf("SQRT", SQRT, AssociatedFunction { par -> sqrt(par) }, 900.0),
                arrayOf("ROUND", ROUND, AssociatedFunction { par -> round(par).toDouble() }, 3.5),
                arrayOf("FLOOR", FLOOR, AssociatedFunction { par -> floor(par) }, 3.5),
                arrayOf("CEIL", CEIL, AssociatedFunction { par -> ceil(par) }, 3.5),
                arrayOf("ABS", ABS, AssociatedFunction { par -> abs(par) }, -4.0),
                arrayOf("ARCSIN", ARCSIN, AssociatedFunction { par -> toDegrees(asin(par)) }, 0.66),
                arrayOf("ARCCOS", ARCCOS, AssociatedFunction { par -> toDegrees(acos(par)) }, 0.66),
                arrayOf("ARCTAN", ARCTAN, AssociatedFunction { par -> toDegrees(atan(par)) }, 45.66),
                arrayOf("EXP", EXP, AssociatedFunction { par -> exp(par) }, 45.66)
                )
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
        sprite = Sprite("testSprite")
        scope = Scope(project, sprite!!, SequenceAction())
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
    }

    @Test
    fun testNumberParameter() {
        val internToken = InternToken(NUMBER, parameterValue.toString())
        val expectedValue = associatedFunction.function(parameterValue)
        testSingleParameterFunction(function, listOf(internToken), expectedValue, null)
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testStringParameter() {
        val internToken = InternToken(STRING, parameterValue.toString())
        val expectedValue = associatedFunction.function(parameterValue)
        testSingleParameterFunction(function, listOf(internToken), expectedValue, null)
    }

    @Test
    fun testEmptyParameter() {
        val internToken = InternToken(STRING, "")
        val expectedValue = 0.0
        testSingleParameterFunction(function, listOf(internToken), expectedValue, null)
    }

    @Test
    fun testNotNumericalStringParameter() {
        val internToken = InternToken(STRING, NOT_NUMERICAL_STRING)
        val expectedValue = 0.0
        testSingleParameterFunction(function, listOf(internToken), expectedValue, null)
    }

    @Test
    fun testInvalidFormulaParameter() {
        val parameter = buildBinaryOperator(NUMBER, "15.0", PLUS, STRING, NOT_NUMERICAL_STRING)
        val expectedValue = associatedFunction.function(Double.NaN)
        testSingleParameterFunction(function, parameter, expectedValue, scope)
    }

    data class AssociatedFunction(val function: (Double) -> Double)
}
