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
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenStageExitedBrick
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.xmlpull.v1.XmlPullParser

/**
 * "When stage is exited" is an event script: it belongs to the Event category, is always offered
 * there and looks like the other event "when" bricks. The Testing category offers it as well.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class WhenStageExitedBrickCategoryTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

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
    fun `event category offers the brick once, directly after delete this clone, by default`() {
        val eventBricks = brickClassesOf(R.string.category_event)

        val deleteCloneIndex = eventBricks.indexOf(DeleteThisCloneBrick::class.java)
        assertEquals(WhenStageExitedBrick::class.java, eventBricks[deleteCloneIndex + 1])
        assertEquals(1, eventBricks.count { it == WhenStageExitedBrick::class.java })
    }

    @Test
    fun `event category offers the brick for the background too`() {
        val backgroundEventBricks = CategoryBricksFactory()
            .getBricks(context.getString(R.string.category_event), true, context)
            .map { it.javaClass }

        assertEquals(1, backgroundEventBricks.count { it == WhenStageExitedBrick::class.java })
    }

    @Test
    fun `event category offers the brick before the NFC brick`() {
        sharedPreferences.edit().putBoolean(SettingsFragment.SETTINGS_SHOW_NFC_BRICKS, true).commit()

        val eventBricks = brickClassesOf(R.string.category_event)

        val brickIndex = eventBricks.indexOf(WhenStageExitedBrick::class.java)
        assertEquals(WhenNfcBrick::class.java, eventBricks[brickIndex + 1])
    }

    @Test
    fun `testing category offers the brick as a secondary category`() {
        assertEquals(1, brickClassesOf(R.string.category_assertions).count { it == WhenStageExitedBrick::class.java })
    }

    @Test
    fun `brick belongs to the event category`() {
        val category = CategoryBricksFactoryWithoutMotionBricks()
            .getBrickCategory(WhenStageExitedBrick(), false, context)

        assertEquals(context.getString(R.string.category_event), category)
    }

    @Test
    fun `brick has the shape and color of the event when bricks`() {
        val layout = layoutOf(WhenStageExitedBrick().viewResource)

        assertEquals(R.style.BrickContainer_Event_Small, layout.brickContainerStyle)
        assertEquals(listOf(R.layout.icon_brick_category_event), layout.includedLayouts)
    }

    /** The style of the brick container and the included layouts of a brick layout resource. */
    private data class BrickLayoutDefinition(val brickContainerStyle: Int, val includedLayouts: List<Int>)

    private fun layoutOf(layoutResId: Int): BrickLayoutDefinition {
        var brickContainerStyle = 0
        val includedLayouts = mutableListOf<Int>()
        context.resources.getLayout(layoutResId).use { parser ->
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                when {
                    parser.name.endsWith(".BrickLayout") -> brickContainerStyle = parser.styleAttribute
                    parser.name == "include" ->
                        includedLayouts += parser.getAttributeResourceValue(null, "layout", 0)
                }
            }
        }
        return BrickLayoutDefinition(brickContainerStyle, includedLayouts)
    }

    private fun brickClassesOf(categoryResId: Int): List<Class<out Brick>> =
        CategoryBricksFactory().getBricks(context.getString(categoryResId), false, context)
            .map { it.javaClass }

    /**
     * Looks up brick categories without creating the Motion bricks, whose physics defaults load the
     * native libGDX library that JVM unit tests do not have.
     */
    private class CategoryBricksFactoryWithoutMotionBricks : CategoryBricksFactory() {
        override fun setupMotionCategoryList(context: Context?, isBackgroundSprite: Boolean): List<Brick> =
            emptyList()
    }
}
