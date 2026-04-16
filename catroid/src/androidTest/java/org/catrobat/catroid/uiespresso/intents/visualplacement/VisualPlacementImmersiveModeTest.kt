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
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.visualplacement.VisualPlacementActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression test for CATROID-1648:
 * "Place Visually" coordinate misalignment caused by the system navigation bar
 * appearing when the activity launched, shifting the visible viewport upward.
 *
 * The activity must hide both the status bar and the navigation bar so the
 * visible area and the coordinate system stay aligned.
 */
@RunWith(AndroidJUnit4::class)
class VisualPlacementImmersiveModeTest {

    private lateinit var scenario: ActivityScenario<VisualPlacementActivity>

    @Before
    fun setUp() {
        UiTestUtils.createProjectAndGetStartScript(
            VisualPlacementImmersiveModeTest::class.java.simpleName
        )
        val launchIntent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            VisualPlacementActivity::class.java
        ).apply {
            putExtra(EXTRA_X_TRANSFORM, 0)
            putExtra(EXTRA_Y_TRANSFORM, 0)
        }
        scenario = ActivityScenario.launch(launchIntent)
    }

    @After
    fun tearDown() {
        scenario.close()
        org.catrobat.catroid.test.utils.TestUtils.deleteProjects(
            VisualPlacementImmersiveModeTest::class.java.simpleName
        )
    }

    @Test
    fun testNavigationBarIsHiddenOnLaunch() {
        scenario.onActivity { activity ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = activity.window.insetsController
                assertNotNull("WindowInsetsController must be available", controller)
                assertEquals(
                    "Navigation bar should use transient-swipe behavior (CATROID-1648)",
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE,
                    controller?.systemBarsBehavior
                )
            } else {
                @Suppress("DEPRECATION")
                val visibility = activity.window.decorView.systemUiVisibility
                assertTrue(
                    "SYSTEM_UI_FLAG_HIDE_NAVIGATION must be set to suppress the nav bar (CATROID-1648)",
                    visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0
                )
                assertTrue(
                    "SYSTEM_UI_FLAG_IMMERSIVE_STICKY must be set to keep the nav bar hidden (CATROID-1648)",
                    visibility and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY != 0
                )
            }
        }
    }

    @Test
    fun testNavigationBarRemainsHiddenAfterFocusChange() {
        scenario.onActivity { activity ->
            // Simulate losing and regaining window focus (e.g. after a dialog).
            activity.onWindowFocusChanged(false)
            activity.onWindowFocusChanged(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = activity.window.insetsController
                assertNotNull(controller)
                assertEquals(
                    "hideSystemUI() must re-apply transient-swipe behaviour after focus returns (CATROID-1648)",
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE,
                    controller?.systemBarsBehavior
                )
            } else {
                @Suppress("DEPRECATION")
                val visibility = activity.window.decorView.systemUiVisibility
                assertTrue(
                    "Navigation bar should remain hidden after focus is restored (CATROID-1648)",
                    visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0
                )
            }
        }
    }
}
