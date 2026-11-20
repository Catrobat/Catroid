/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.test.ui.fragment

import android.content.Context
import android.os.Build
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.FinishStageBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.SimulateGamepadButtonBrick
import org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick
import org.catrobat.catroid.content.bricks.TapAtBrick
import org.catrobat.catroid.content.bricks.TapForBrick
import org.catrobat.catroid.content.bricks.TouchAndSlideBrick
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TestingCategoryBricksTest {
    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun `testing category offers simulate gamepad button after touch and slide`() {
        val testingBricks: List<Class<out Brick>> = CategoryBricksFactory()
            .getBricks(context.getString(R.string.category_assertions), false, context)
            .map { it.javaClass }

        assertEquals(expectedTestingBrickClasses, testingBricks)
    }

    private companion object {
        val expectedTestingBrickClasses: List<Class<out Brick>> = listOf(
            AssertEqualsBrick::class.java,
            AssertUserListsBrick::class.java,
            ParameterizedBrick::class.java,
            WaitTillIdleBrick::class.java,
            TapAtBrick::class.java,
            TapForBrick::class.java,
            TouchAndSlideBrick::class.java,
            SimulateGamepadButtonBrick::class.java,
            FinishStageBrick::class.java,
            StoreCSVIntoUserListBrick::class.java,
            WebRequestBrick::class.java
        )
    }
}
