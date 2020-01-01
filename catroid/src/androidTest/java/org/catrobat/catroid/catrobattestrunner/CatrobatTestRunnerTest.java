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
		exception.expectMessage("expected:<5.0> but was:<some text>");
		testAsset("testFailMismatchingTypes.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailStringNotEqual() throws Exception {
		exception.expectMessage("expected:<diff> but was:<text>");
		testAsset("testFailStringNotEqual.catrobat", "catrobatTestRunnerTests/fail");
	}

	@Test
	public void testFailDoubleNotEqual() throws Exception {
		exception.expectMessage("expected:<1.0> but was:<1.1>");
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

	private void testAsset(String assetName, String assetPath) throws Exception {
		catrobatTestRunner.assetName = assetName;
		catrobatTestRunner.assetPath = assetPath;
		catrobatTestRunner.setUp();
		catrobatTestRunner.run();
		catrobatTestRunner.tearDown();
	}
}
