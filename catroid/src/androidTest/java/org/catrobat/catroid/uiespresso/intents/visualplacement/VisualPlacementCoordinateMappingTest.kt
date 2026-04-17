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

package org.catrobat.catroid.uiespresso.intents.visualplacement

import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.visualplacement.VisualPlacementActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VisualPlacementCoordinateMappingTest {

    @get:Rule
    val activityRule = IntentsTestRule(VisualPlacementActivity::class.java, false, false)

    @Before
    fun setUp() {
        UiTestUtils.createProjectAndGetStartScript(
            VisualPlacementCoordinateMappingTest::class.java.simpleName
        )
        ProjectManager.getInstance().currentProject.setScreenMode(ScreenModes.STRETCH)
    }

    @After
    fun tearDown() {
        activityRule.finishActivity()
        TestUtils.deleteProjects(VisualPlacementCoordinateMappingTest::class.java.simpleName)
    }

    @Test
    fun testInitialSpritePositionScalesToCurrentFrameHeight() {
        val currentProject = ProjectManager.getInstance().currentProject
        currentProject.xmlHeader.virtualScreenHeight -= PROJECT_HEIGHT_DELTA

        activityRule.launchActivity(
            Intent().apply {
                putExtra(EXTRA_X_TRANSFORM, 0)
                putExtra(EXTRA_Y_TRANSFORM, INITIAL_PROJECT_Y)
            }
        )
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        var actualImageTop = 0f
        var expectedImageTop = 0f
        var imageViewFound: ImageView? = null
        activityRule.activity.runOnUiThread {
            val frameLayout = activityRule.activity.findViewById<FrameLayout>(R.id.frame_container)
            imageViewFound = (0 until frameLayout.childCount)
                .map(frameLayout::getChildAt)
                .filterIsInstance<ImageView>()
                .firstOrNull()
            expectedImageTop = -INITIAL_PROJECT_Y * frameLayout.height.toFloat() /
                currentProject.xmlHeader.virtualScreenHeight.toFloat()
            actualImageTop = imageViewFound?.y ?: 0f
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertNotNull("Visual placement preview image must be present", imageViewFound)
        assertEquals(
            "Visual placement must scale the initial sprite Y position to the actual frame height (CATROID-1648)",
            expectedImageTop,
            actualImageTop,
            POSITION_TOLERANCE
        )
    }

    @Test
    fun testReturnedYCoordinateUsesActualFrameHeightWhenFrameIsShorterThanRequestedHeight() {
        activityRule.launchActivity(
            Intent().apply {
                putExtra(EXTRA_X_TRANSFORM, 0)
                putExtra(EXTRA_Y_TRANSFORM, 0)
            }
        )

        val frameLayout = activityRule.activity.findViewById<FrameLayout>(R.id.frame_container)
        val actualFrameHeight = shrinkFrameLayoutHeight(frameLayout)
        val projectHeight = ProjectManager.getInstance().currentProject.xmlHeader.virtualScreenHeight
        val actualHeightRatio = actualFrameHeight.toFloat() / projectHeight.toFloat()

        activityRule.activity.runOnUiThread {
            activityRule.activity.setYCoordinate(TARGET_PROJECT_Y * actualHeightRatio)
            val finishWithResult = VisualPlacementActivity::class.java.getDeclaredMethod("finishWithResult")
            finishWithResult.isAccessible = true
            finishWithResult.invoke(activityRule.activity)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val resultIntent = activityRule.activityResult.resultData
        assertEquals(
            "Visual placement must convert the saved Y coordinate using the actual frame height (CATROID-1648)",
            TARGET_PROJECT_Y,
            resultIntent.getIntExtra(
                VisualPlacementActivity.Y_COORDINATE_BUNDLE_ARGUMENT,
                Int.MIN_VALUE
            )
        )
    }

    private fun shrinkFrameLayoutHeight(frameLayout: FrameLayout): Int {
        var newHeight = 0
        activityRule.activity.runOnUiThread {
            val requestedWidth = frameLayout.width
            newHeight = frameLayout.height - ACTUAL_FRAME_HEIGHT_DELTA
            frameLayout.layoutParams = frameLayout.layoutParams.apply {
                height = newHeight
            }
            frameLayout.measure(
                View.MeasureSpec.makeMeasureSpec(requestedWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY)
            )
            frameLayout.layout(
                frameLayout.left,
                frameLayout.top,
                frameLayout.left + requestedWidth,
                frameLayout.top + newHeight
            )
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        return newHeight
    }

    companion object {
        private const val ACTUAL_FRAME_HEIGHT_DELTA = 120
        private const val INITIAL_PROJECT_Y = 240
        private const val POSITION_TOLERANCE = 1f
        private const val PROJECT_HEIGHT_DELTA = 400
        private const val TARGET_PROJECT_Y = 240
    }
}
