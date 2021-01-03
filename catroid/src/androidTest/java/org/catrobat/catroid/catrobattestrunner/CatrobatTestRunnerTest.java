/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.catrobattestrunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class CatrobatTestRunnerTest {

	CatrobatTestRunner catrobatTestRunner = new CatrobatTestRunner();

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testDoubleEqual() throws Exception {
		testAsset("testSuccessDoubleEqual.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testStringEqual() throws Exception {
		testAsset("testSuccessStringEqual.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testFailMismatchingTypes() throws Exception {
		exception.expectMessage("AssertEqualsError\n"
				+ "expected: <5.0>\n"
				+ "actual:   <some text>\n"
				+ "deviation: ^");
		testAsset("testFailMismatchingTypes.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailStringNotEqual() throws Exception {
		exception.expectMessage("AssertEqualsError\n"
				+ "expected: <diff>\n"
				+ "actual:   <text>\n"
				+ "deviation: ^");
		testAsset("testFailStringNotEqual.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailDoubleNotEqual() throws Exception {
		exception.expectMessage("AssertEqualsError\n"
				+ "expected: <1.0>\n"
				+ "actual:   <1.1>\n"
				+ "deviation: --^");
		testAsset("testFailDoubleNotEqual.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailTimeout() throws Exception {
		exception.expectMessage("Timeout after 10000ms\n"
				+ "Test never got into ready state - is the AssertEqualsBrick reached?");
		testAsset("testFailTimeout.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testTapBrick() throws Exception {
		testAsset("testTapBrick.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testTapAtDuration() throws Exception {
		testAsset("testTapAtDuration.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testTapAtMultitouch() throws Exception {
		testAsset("testTapAtMultitouch.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testTapForInterrupted() throws Exception {
		testAsset("testTapForInterrupted.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testTouchAndSlide() throws Exception {
		testAsset("testTouchAndSlide.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testSuccessListEqual() throws Exception {
		testAsset("testSuccessListEqual.catrobat", "catrobatTestRunnerTests/success");
	}

	@Test
	public void testFailListDoubleNotEqual() throws Exception {
		exception.expectMessage("AssertUserListError\n"
				+ "position: 2\n"
				+ "expected: <1.1>\n"
				+ "actual:   <1.2>\n"
				+ "deviation: --^\n"
				+ "\n"
				+ "position: 3\n"
				+ "expected: <5.2>\n"
				+ "actual:   <5>\n"
				+ "deviation: -^\n");
		testAsset("testFailListDoubleNotEqual.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailListStringNotEqual() throws Exception {
		exception.expectMessage("AssertUserListError\n"
				+ "position: 1\n"
				+ "expected: <first String>\n"
				+ "actual:   <second String>\n"
				+ "deviation: ^\n"
				+ "\n"
				+ "position: 2\n"
				+ "expected: <second String>\n"
				+ "actual:   <first String>\n"
				+ "deviation: ^\n");
		testAsset("testFailListStringNotEqual.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailListMismatchingTypes() throws Exception {
		exception.expectMessage("AssertUserListError\n"
				+ "position: 1\n"
				+ "expected: <first String>\n"
				+ "actual:   <125.0>\n"
				+ "deviation: ^\n"
				+ "\n"
				+ "position: 2\n"
				+ "expected: <12.3>\n"
				+ "actual:   <second String>\n"
				+ "deviation: ^\n");
		testAsset("testFailListMismatchingTypes.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailParamMismatch() throws Exception {
		exception.expectMessage("Failed Tests:\n\n"
				+ "[3] actual = 1.0\n"
				+ "expected: <1.1>\n"
				+ "actual:   <1.0>\n"
				+ "deviation: --^\n\n"
				+ "[4] actual = String\n"
				+ "expected: <123>\n"
				+ "actual:   <String>\n"
				+ "deviation: ^\n\n"
				+ "[5] actual = 345\n"
				+ "expected: <Test String>\n"
				+ "actual:   <345>\n"
				+ "deviation: ^\n\n"
				+ "[6] actual = Actual\n"
				+ "expected: <Expected>\n"
				+ "actual:   <Actual>\n"
				+ "deviation: ^\n\n\n"
				+ "Succeeded Tests:\n\n"
				+ "[1] actual = 5.0\n"
				+ "5.0 == 5\n\n"
				+ "[2] actual = 3\n"
				+ "3 == 3.0");
		testAsset("testFailParamMismatch.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailParamStringNotEqual() throws Exception {
		exception.expectMessage("ParameterizedAssertError\n"
				+ "Failed Tests:\n\n"
				+ "[2] firstPart = puppy | secondPart = naughty\n"
				+ "expected: <puppy is not naughty>\n"
				+ "actual:   <puppy is naughty>\n"
				+ "deviation: ----------^\n\n\n"
				+ "Succeeded Tests:\n\n"
				+ "[1] firstPart = kitty | secondPart = cute\n"
				+ "kitty is cute == kitty is cute\n\n"
				+ "[3] firstPart = octopus | secondPart = intelligent\n"
				+ "octopus is intelligent == octopus is intelligent");
		testAsset("testFailParamStringNotEqual.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailParamMissingInput() throws Exception {
		exception.expectMessage("ParameterizedInitialisationError\n"
				+ "Input was not selected");
		testAsset("testFailParamMissingInput.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailParamEmptyInput() throws Exception {
		exception.expectMessage("ParameterizedInitialisationError\n"
				+ "Input list is missing elements\n"
				+ "Failed Tests:\n\n\n"
				+ "Succeeded Tests:");
		testAsset("testFailParamEmptyInput.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testSuccessParamCalculations() throws Exception {
		testAsset("testSuccessParamCalculations.catrobat", "catrobatTestRunnerTests/success");
	}

	private void testAsset(String assetName, String assetPath) throws Exception {
		catrobatTestRunner.assetName = assetName;
		catrobatTestRunner.assetPath = assetPath;
		catrobatTestRunner.setUp();
		catrobatTestRunner.run();
		catrobatTestRunner.tearDown();
	}
}
