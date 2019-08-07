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

package org.catrobat.catroid;

import org.catrobat.catroid.catrobattestrunner.CatrobatTestRunner;
import org.catrobat.catroid.catrobattestrunner.CatrobatTestRunnerTest;
import org.catrobat.catroid.testsuites.AllHeadlessTestsSuite;
import org.catrobat.catroid.testsuites.UiEspressoPullRequestTriggerSuite;
import org.catrobat.catroid.testsuites.UiEspressoQuarantineTestSuite;
import org.catrobat.catroid.testsuites.UiEspressoRtlTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		UiEspressoPullRequestTriggerSuite.class,
		CatrobatTestRunner.class,
		CatrobatTestRunnerTest.class,
		AllHeadlessTestsSuite.class,
		UiEspressoRtlTestSuite.class,
		UiEspressoQuarantineTestSuite.class
})

public class AllEmulatorTestsSuite {
}
