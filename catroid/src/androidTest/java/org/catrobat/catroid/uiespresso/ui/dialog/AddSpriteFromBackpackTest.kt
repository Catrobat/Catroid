/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.dialog

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddSpriteFromBackpackTest {

    private var currentProject: Project? = null
    private var controller = SpriteController()

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val projectName = "newProject"
        currentProject = UiTestUtils.createDefaultTestProject(projectName)
        currentProject!!.defaultScene.addSprite(Sprite("Sprite1"))
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(currentProject?.name)
    }

    @Test
    fun emptyBackpackTest() {
        val oldSize = currentProject!!.defaultScene.spriteList.size
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_backpack))
            .perform(ViewActions.click())
        Assert.assertEquals(oldSize, currentProject!!.defaultScene.spriteList.size)

        Assert.assertEquals(0, BackpackListManager.getInstance().backpack.backpackedSprites.size)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpack.backpackedLooks.size)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpack.backpackedScenes.size)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpack.backpackedScripts.size)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpack.backpackedSounds.size)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpack
            .backpackedUserDefinedBricks.size)
    }

    @Test
    fun noSpritesBackpackTest() {
        val oldSize = currentProject!!.defaultScene.spriteList.size
        val scriptGroup = currentProject!!.defaultScene.spriteList[1].scriptList
        BackpackListManager.getInstance().addScriptToBackPack("Skript1", scriptGroup)
        BackpackListManager.getInstance().saveBackpack()

        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_backpack))
            .perform(ViewActions.click())

        Assert.assertEquals(oldSize, currentProject!!.defaultScene.spriteList.size)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpack.backpackedSprites.size)
        Assert.assertNotEquals(0, BackpackListManager.getInstance().backpack.backpackedScripts.size)
    }

    @Test
    fun navigateToBackpack() {
        val sprite = currentProject!!.defaultScene.spriteList[1]
        BackpackListManager.getInstance().sprites.add(controller.pack(sprite))
        BackpackListManager.getInstance().saveBackpack()

        Assert.assertNotEquals(0, BackpackListManager.getInstance().sprites.size)

        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_backpack))
            .perform(ViewActions.click())

        Espresso.onView(withText(R.string.backpack_title)).check(ViewAssertions.matches(isDisplayed()))
    }
}
