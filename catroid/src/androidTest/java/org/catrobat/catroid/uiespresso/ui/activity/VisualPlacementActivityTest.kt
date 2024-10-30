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

package org.catrobat.catroid.uiespresso.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import androidx.test.espresso.intent.rule.IntentsTestRule
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils
import org.catrobat.catroid.uiespresso.intents.visualplacement.VisualPlacementActivityTest
import org.catrobat.catroid.visualplacement.VisualPlacementActivity
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VisualPlacementActivityTest {
    private val XPOS = -200
    private val YPOS = 500

    @get:Rule
    val baseActivityTestRule = IntentsTestRule(
        VisualPlacementActivity::class.java, false, false
    )
    @Before
    fun setUp() {
        BrickTestUtils.createProjectAndGetStartScript(VisualPlacementActivity::class.java.simpleName)
        val intent = Intent()
        intent.putExtra(SpriteActivity.EXTRA_X_TRANSFORM, XPOS)
        intent.putExtra(SpriteActivity.EXTRA_Y_TRANSFORM, YPOS)
        baseActivityTestRule.launchActivity(intent)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(VisualPlacementActivityTest::class.java.simpleName)
    }

    @Test
    fun testDrawAxes() {
        val width = baseActivityTestRule.activity.window.decorView.rootView.width
        val height = baseActivityTestRule.activity.window.decorView.rootView.height
        val emptyBitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val backgroundBitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBitmap)
        baseActivityTestRule.activity.window.decorView.rootView.draw(canvas)
        Assert.assertFalse(emptyBitmap.sameAs(backgroundBitmap))
    }
}
