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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.ui.fragment

import android.content.Context
import android.os.Build
import junit.framework.Assert.assertEquals
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ArcBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.GoThroughBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class PeripheralMotionCategoryBricksTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val categoryBricksFactory = CategoryBricksFactory()

    @Test
    fun testEmbroideryCategoryIncludesRequestedMotionBricks() {
        assertRequestedMotionBrickOrder(context.getString(R.string.category_embroidery))
    }

    @Test
    fun testLaserCategoryIncludesRequestedMotionBricks() {
        assertRequestedMotionBrickOrder(context.getString(R.string.category_laser))
    }

    @Test
    fun testPlotCategoryIncludesRequestedMotionBricks() {
        assertRequestedMotionBrickOrder(context.getString(R.string.category_plot))
    }

    private fun assertRequestedMotionBrickOrder(category: String) {
        val actualMotionBricks: List<Class<out Brick>> = categoryBricksFactory
            .getBricks(category, false, context)
            .map { it.javaClass }
            .filter { requestedMotionBrickClasses.contains(it) }

        assertEquals(requestedMotionBrickClasses, actualMotionBricks)
    }

    companion object {
        private val requestedMotionBrickClasses: List<Class<out Brick>> = listOf(
            PlaceAtBrick::class.java,
            SetXBrick::class.java,
            SetYBrick::class.java,
            ChangeXByNBrick::class.java,
            ChangeYByNBrick::class.java,
            MoveNStepsBrick::class.java,
            TurnLeftBrick::class.java,
            TurnRightBrick::class.java,
            PointInDirectionBrick::class.java,
            ArcBrick::class.java,
            GoThroughBrick::class.java
        )
    }
}
