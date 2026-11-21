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
import android.preference.PreferenceManager
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonPressedBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class WhenGamepadButtonPressedBrickCategoryTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val categoryBricksFactory = CategoryBricksFactory()

    @Before
    fun setUp() {
        val project = Project(context, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun `device category offers the brick directly after when touch down with the setting on`() {
        setPhysicalGamepadSetting(true)

        val deviceBricks = brickClassesOf(R.string.category_device)

        val touchDownIndex = deviceBricks.indexOf(WhenTouchDownBrick::class.java)
        assertTrue(touchDownIndex >= 0)
        assertEquals(WhenGamepadButtonPressedBrick::class.java, deviceBricks[touchDownIndex + 1])
        assertEquals(1, deviceBricks.count { it == WhenGamepadButtonPressedBrick::class.java })
    }

    @Test
    fun `device category offers the brick before the NFC bricks`() {
        setPhysicalGamepadSetting(true)
        sharedPreferences.edit().putBoolean(SettingsFragment.SETTINGS_SHOW_NFC_BRICKS, true).commit()

        val deviceBricks = brickClassesOf(R.string.category_device)

        val gamepadIndex = deviceBricks.indexOf(WhenGamepadButtonPressedBrick::class.java)
        assertEquals(WhenTouchDownBrick::class.java, deviceBricks[gamepadIndex - 1])
        assertEquals(WhenNfcBrick::class.java, deviceBricks[gamepadIndex + 1])
    }

    @Test
    fun `device category does not offer the brick with the setting off`() {
        setPhysicalGamepadSetting(false)

        assertFalse(brickClassesOf(R.string.category_device).contains(WhenGamepadButtonPressedBrick::class.java))
    }

    @Test
    fun `device category does not offer the brick by default`() {
        assertFalse(brickClassesOf(R.string.category_device).contains(WhenGamepadButtonPressedBrick::class.java))
    }

    @Test
    fun `event category does not offer the brick`() {
        setPhysicalGamepadSetting(true)

        assertFalse(brickClassesOf(R.string.category_event).contains(WhenGamepadButtonPressedBrick::class.java))
    }

    @Test
    fun `brick belongs to the device category with the setting off`() {
        setPhysicalGamepadSetting(false)

        assertEquals(DEVICE_CATEGORY, brickCategoryOf(WhenGamepadButtonPressedBrick()))
    }

    @Test
    fun `brick belongs to the device category with the setting on`() {
        setPhysicalGamepadSetting(true)

        assertEquals(DEVICE_CATEGORY, brickCategoryOf(WhenGamepadButtonPressedBrick()))
    }

    private fun setPhysicalGamepadSetting(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, enabled)
            .commit()
    }

    private fun brickClassesOf(categoryResId: Int): List<Class<out Brick>> =
        categoryBricksFactory.getBricks(context.getString(categoryResId), false, context)
            .map { it.javaClass }

    private fun brickCategoryOf(brick: Brick): String =
        CategoryBricksFactoryWithoutMotionBricks().getBrickCategory(brick, false, context)

    /**
     * Looks up brick categories without creating the Motion bricks, whose physics defaults load the
     * native libGDX library that JVM unit tests do not have.
     */
    private class CategoryBricksFactoryWithoutMotionBricks : CategoryBricksFactory() {
        override fun setupMotionCategoryList(context: Context?, isBackgroundSprite: Boolean): List<Brick> =
            emptyList()
    }

    private companion object {
        const val DEVICE_CATEGORY = "Device"
    }
}
