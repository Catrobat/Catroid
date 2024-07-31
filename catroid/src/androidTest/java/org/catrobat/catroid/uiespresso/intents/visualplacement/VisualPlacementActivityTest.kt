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
package org.catrobat.catroid.uiespresso.intents.visualplacement

import android.content.Intent
import android.content.res.Resources
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.utils.AndroidCoordinates
import org.catrobat.catroid.visualplacement.VisualPlacementActivity
import org.catrobat.catroid.visualplacement.VisualPlacementViewModel.Companion.EXTRA_TEXT
import org.catrobat.catroid.visualplacement.VisualPlacementViewModel.Companion.EXTRA_X_COORDINATE
import org.catrobat.catroid.visualplacement.VisualPlacementViewModel.Companion.EXTRA_Y_COORDINATE
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class VisualPlacementActivityTest {
    @JvmField
    @Rule
    val baseActivityTestRule = IntentsTestRule(VisualPlacementActivity::class.java, false, false)

    val projectManager by inject(ProjectManager::class.java)
    private var screenWith: Int = 0
    private var screenHeight: Int = 0

    @Before
    fun setUp() {
        UiTestUtils.createProjectAndGetStartScript(VisualPlacementActivity::class.java.simpleName)
        screenHeight = Resources.getSystem().displayMetrics.heightPixels
        screenWith = Resources.getSystem().displayMetrics.widthPixels
        assertEquals(screenHeight, projectManager.currentProject.xmlHeader.virtualScreenHeight)
        assertEquals(screenWith, projectManager.currentProject.xmlHeader.virtualScreenWidth)
        val intent = Intent()
        intent.putExtra(EXTRA_X_COORDINATE, X_POS)
        intent.putExtra(EXTRA_Y_COORDINATE, Y_POS)
        intent.putExtra(EXTRA_TEXT, "test")
        baseActivityTestRule.launchActivity(intent)
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(VisualPlacementActivityTest::class.java.simpleName)
    }

    @Test
    fun testResultWhenConfirmClicked() {
        onView(withId(R.id.confirm)).perform(click())
        assertTrue(baseActivityTestRule.activity.isFinishing)
        val resultIntent = baseActivityTestRule.activityResult.resultData
        assertEquals(X_POS, resultIntent.extras!![EXTRA_X_COORDINATE])
        assertEquals(Y_POS, resultIntent.extras!![EXTRA_Y_COORDINATE])
    }

    @Test
    fun testResultWithChangedCoordinates() {
        baseActivityTestRule.activity.runOnUiThread {
            baseActivityTestRule.activity.setCoordinates(returnPos)
        }
        onView(withId(R.id.confirm)).perform(click())
        assertTrue(baseActivityTestRule.activity.isFinishing)
        val resultIntent = baseActivityTestRule.activityResult.resultData
        val expectedX = returnPos.x.toInt() - screenWith / 2
        val expectedY = -(returnPos.y.toInt() - screenHeight / 2)
        assertEquals(expectedX, resultIntent.extras!![EXTRA_X_COORDINATE])
        assertEquals(expectedY, resultIntent.extras!![EXTRA_Y_COORDINATE])
    }

    @Test
    fun testResultWhenBackAndSaveClicked() {
        baseActivityTestRule.activity.runOnUiThread {
            baseActivityTestRule.activity.setCoordinates(returnPos)
        }
        pressBack()
        onView(withText(R.string.save)).perform(click())
        assertTrue(baseActivityTestRule.activity.isFinishing)
        val resultIntent = baseActivityTestRule.activityResult.resultData
        val expectedX = returnPos.x.toInt() - screenWith / 2
        val expectedY = -(returnPos.y.toInt() - screenHeight / 2)
        assertEquals(expectedX, resultIntent.extras!![EXTRA_X_COORDINATE])
        assertEquals(expectedY, resultIntent.extras!![EXTRA_Y_COORDINATE])
    }

    @Test
    fun testResultWhenDiscarded() {

        baseActivityTestRule.activity.runOnUiThread {
            baseActivityTestRule.activity.setCoordinates(returnPos)
        }
        pressBack()
        onView(withText(R.string.discard))
            .perform(click())
        assertTrue(baseActivityTestRule.activity.isFinishing)
        val resultIntent = baseActivityTestRule.activityResult.resultData
        assertNull(resultIntent)
    }

    private companion object {
        private const val X_POS = -200
        private const val Y_POS = 500
        private const val X_RETURN_POS = 42
        private const val Y_RETURN_POS = 666
        private val returnPos = AndroidCoordinates(X_RETURN_POS.toFloat(), Y_RETURN_POS.toFloat())
    }
}
