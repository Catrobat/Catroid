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
package org.catrobat.catroid.catrobattestrunner

import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatrobatTestRunnerTest {
    private var catrobatTestRunner = CatrobatTestRunner()

    @Rule
    @JvmField
    val exception: ExpectedException = ExpectedException.none()

    @Test
    @Throws(Exception::class)
    fun testDoubleEqual() {
        testAsset("testSuccessDoubleEqual.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testStringEqual() {
        testAsset("testSuccessStringEqual.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testFailMismatchingTypes() {
        exception.expectMessage(
            """
            AssertEqualsError
            expected: <5.0>
            actual:   <some text>
            deviation: ^
            """.trimIndent()
        )
        testAsset("testFailMismatchingTypes.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailStringNotEqual() {
        exception.expectMessage(
            """
            AssertEqualsError
            expected: <diff>
            actual:   <text>
            deviation: ^
            """.trimIndent()
        )
        testAsset("testFailStringNotEqual.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailDoubleNotEqual() {
        exception.expectMessage(
            """
            AssertEqualsError
            expected: <1.0>
            actual:   <1.1>
            deviation: --^
            """.trimIndent()
        )
        testAsset("testFailDoubleNotEqual.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailTimeout() {
        exception.expectMessage(
            """
            Timeout after 10000ms
            Test never got into ready state - is the AssertEqualsBrick reached?
            """.trimIndent()
        )
        testAsset("testFailTimeout.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testTapBrick() {
        testAsset("testTapBrick.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testTapAtDuration() {
        testAsset("testTapAtDuration.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testTapAtMultitouch() {
        testAsset("testTapAtMultitouch.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testTapForInterrupted() {
        testAsset("testTapForInterrupted.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testTouchAndSlide() {
        testAsset("testTouchAndSlide.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testSuccessListEqual() {
        testAsset("testSuccessListEqual.catrobat", "catrobatTestRunnerTests/success")
    }

    @Test
    @Throws(Exception::class)
    fun testFailListDoubleNotEqual() {
        exception.expectMessage(
            """
            AssertUserListError
            position: 2
            expected: <1.1>
            actual:   <1.2>
            deviation: --^
            
            position: 3
            expected: <5.2>
            actual:   <5>
            deviation: -^
            
            """.trimIndent()
        )
        testAsset("testFailListDoubleNotEqual.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailListStringNotEqual() {
        exception.expectMessage(
            """
            AssertUserListError
            position: 1
            expected: <first String>
            actual:   <second String>
            deviation: ^
            
            position: 2
            expected: <second String>
            actual:   <first String>
            deviation: ^
            
            """.trimIndent()
        )
        testAsset("testFailListStringNotEqual.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailListMismatchingTypes() {
        exception.expectMessage(
            """
            AssertUserListError
            position: 1
            expected: <first String>
            actual:   <125.0>
            deviation: ^
            
            position: 2
            expected: <12.3>
            actual:   <second String>
            deviation: ^
            
            """.trimIndent()
        )
        testAsset("testFailListMismatchingTypes.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailParamMismatch() {
        exception.expectMessage(
            """
            Failed Tests:
            
            [3] actual = 1.0
            expected: <1.1>
            actual:   <1.0>
            deviation: --^
            
            [4] actual = String
            expected: <123>
            actual:   <String>
            deviation: ^
            
            [5] actual = 345
            expected: <Test String>
            actual:   <345>
            deviation: ^
            
            [6] actual = Actual
            expected: <Expected>
            actual:   <Actual>
            deviation: ^
            
            
            Succeeded Tests:
            
            [1] actual = 5.0
            5.0 == 5
            
            [2] actual = 3
            3 == 3.0
            """.trimIndent()
        )
        testAsset("testFailParamMismatch.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailParamStringNotEqual() {
        exception.expectMessage(
            """
            ParameterizedAssertError
            Failed Tests:
            
            [2] firstPart = puppy | secondPart = naughty
            expected: <puppy is not naughty>
            actual:   <puppy is naughty>
            deviation: ----------^
            
            
            Succeeded Tests:
            
            [1] firstPart = kitty | secondPart = cute
            kitty is cute == kitty is cute
            
            [3] firstPart = octopus | secondPart = intelligent
            octopus is intelligent == octopus is intelligent
            """.trimIndent()
        )
        testAsset("testFailParamStringNotEqual.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailParamMissingInput() {
        exception.expectMessage(
            """
            ParameterizedInitialisationError
            Input was not selected
            """.trimIndent()
        )
        testAsset("testFailParamMissingInput.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testFailParamEmptyInput() {
        exception.expectMessage(
            """
            ParameterizedInitialisationError
            Input list is missing elements
            Failed Tests:
            
            
            Succeeded Tests:
            """.trimIndent()
        )
        testAsset("testFailParamEmptyInput.catrobat", "catrobatTestRunnerTests/fail")
    }

    @Test
    @Throws(Exception::class)
    fun testSuccessParamCalculations() {
        testAsset("testSuccessParamCalculations.catrobat", "catrobatTestRunnerTests/success")
    }

    @Throws(Exception::class)
    private fun testAsset(assetName: String, assetPath: String) {
        catrobatTestRunner.assetName = assetName
        catrobatTestRunner.assetPath = assetPath
        catrobatTestRunner.retries = 1
        catrobatTestRunner.setUp()
        catrobatTestRunner.run()
        catrobatTestRunner.tearDown()
    }
}
