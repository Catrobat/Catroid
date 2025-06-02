/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2025 The Catrobat Team
 * (<http://developer.catrobat.org/>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.catrobatlanguage;

import org.catrobat.catroid.test.catrobatlanguage.util.CatrobatTestFile;
import org.catrobat.catroid.test.catrobatlanguage.util.CatrobatLanguageTest;
import org.junit.Rule;
import org.junit.Test;

public class FinishStageBrickTest {

	@Rule
	public CatrobatLanguageTest rule = new CatrobatLanguageTest(CatrobatTestFile.FINISH_STAGE_BRICK_TEST);

	@Test
	public void testFinishStageBrick() {
		rule.testActions();
	}
}
