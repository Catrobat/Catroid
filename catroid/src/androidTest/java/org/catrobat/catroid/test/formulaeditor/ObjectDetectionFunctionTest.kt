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

package org.catrobat.catroid.test.formulaeditor

import android.graphics.Rect
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.objects.DetectedObject
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternFormulaParser
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.utils.MachineLearningUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.LinkedList

@RunWith(Parameterized::class)
class ObjectDetectionFunctionTest(
    val name: String,
    private val function: Functions,
    private val parameterValue: Double,
    private val expectedReturnValue: Double
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf("Get first ID", Functions.ID_OF_DETECTED_OBJECT, 1, 1),
                arrayOf("Get second ID", Functions.ID_OF_DETECTED_OBJECT, 2, 5),
                arrayOf("Get invalid ID", Functions.ID_OF_DETECTED_OBJECT, 100, 0),
                arrayOf("Get invalid ID", Functions.ID_OF_DETECTED_OBJECT, 0, 0),
                arrayOf("Get invalid ID", Functions.ID_OF_DETECTED_OBJECT, -1, 0),
                arrayOf("Object is visible", Functions.OBJECT_WITH_ID_VISIBLE, 5, 1),
                arrayOf("Object is not visible", Functions.OBJECT_WITH_ID_VISIBLE, 100, 0)
            )
        }
    }

    private var successListener: OnSuccessListener<MutableList<DetectedObject>>? = null
    private var detectedObjects: MutableList<DetectedObject> = mutableListOf(
        DetectedObject(
            Rect(0, 0, 0, 0),
            1,
            listOf(DetectedObject.Label("Book", 0.9F, 1))
        ),
        DetectedObject(
            Rect(0, 0, 0, 0),
            5,
            listOf(DetectedObject.Label("Book", 0.9F, 1))
        )
    )

    @Before
    fun setUp() {
        MachineLearningUtil.initializeMachineLearningModule(ApplicationProvider.getApplicationContext())
        successListener = MachineLearningUtil.getObjectDetectorOnSuccessListener()
    }

    @Test
    fun testNumberParameter() {
        Assert.assertTrue(MachineLearningUtil.isLoaded())
        successListener?.onSuccess(detectedObjects)
        val internToken = InternToken(InternTokenType.NUMBER, parameterValue.toString())
        testSingleParameterFunction(function, listOf(internToken), expectedReturnValue)
    }

    private fun testSingleParameterFunction(function: Functions, internTokenList: List<InternToken>, expected: Any) {
        val tokenList = buildSingleParameterFunction(function, internTokenList)
        val parseTree = InternFormulaParser(tokenList).parseFormula(null)
        Assert.assertNotNull(parseTree)
        Assert.assertEquals(expected, parseTree.interpretRecursive(null))
    }

    private fun buildSingleParameterFunction(
        function: Functions,
        internTokenList: List<InternToken>
    ): List<InternToken> {
        val tokenList: MutableList<InternToken> = LinkedList()
        tokenList.add(InternToken(InternTokenType.FUNCTION_NAME, function.name))
        tokenList.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN))
        tokenList.addAll(internTokenList)
        tokenList.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE))
        return tokenList
    }
}
