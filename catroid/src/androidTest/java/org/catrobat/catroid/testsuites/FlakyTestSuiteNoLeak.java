/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.testsuites;

import org.catrobat.catroid.uiespresso.content.brick.app.InsertItemToUserListTest;
import org.catrobat.catroid.uiespresso.content.brick.app.PhiroColorBrickFormulaTest;
import org.catrobat.catroid.uiespresso.content.brick.app.PhiroColorBrickNumberTest;
import org.catrobat.catroid.uiespresso.content.brick.app.PlaySoundAndWaitBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.app.PlaySoundBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.app.PointToBrickAdditionalTest;
import org.catrobat.catroid.uiespresso.content.brick.app.ReplaceItemInUserListTest;
import org.catrobat.catroid.uiespresso.content.brick.app.SetBackgroundBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.app.SetLookBrickTest;
import org.catrobat.catroid.uiespresso.content.brick.app.SetPenColorBrickFormulaTest;
import org.catrobat.catroid.uiespresso.content.brick.app.SetPenColorBrickNumberTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses({
		/*
		FlakyTestTest.class,
		AddUserListToActiveFormulaUITest.class,
		ChangeVariableTest.class,
		DeleteItemOfUserListBrickTest.class,
		DeleteUserDefinedReceiverBrickTest.class,
		EmptyEventBrickTest.class
		// only one screen leak
		 */
		InsertItemToUserListTest.class,
		PhiroColorBrickFormulaTest.class,
		PhiroColorBrickNumberTest.class,
		PlaySoundAndWaitBrickTest.class,
		PlaySoundBrickTest.class,
		PointToBrickAdditionalTest.class,
		ReplaceItemInUserListTest.class,
		SetBackgroundBrickTest.class,
		SetLookBrickTest.class,
		SetPenColorBrickFormulaTest.class,
		SetPenColorBrickNumberTest.class
		// 4 Leaks
		// many Window leaks
})
public class FlakyTestSuiteNoLeak {
}
