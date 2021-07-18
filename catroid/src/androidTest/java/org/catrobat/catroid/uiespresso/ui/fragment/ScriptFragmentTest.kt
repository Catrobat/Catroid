/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.ui.fragment

import android.view.View
import android.widget.AbsListView
import android.widget.RelativeLayout
import androidx.camera.core.impl.Observable
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import junit.framework.Assert.assertTrue
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.dragndrop.BrickListView
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.CustomActions.wait
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.core.Is
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.util.Objects

class ScriptFragmentTest {
    private val projectManager = inject(
        ProjectManager::class.java
    )
    private lateinit var defaultProject: Project

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        baseActivityTestRule.activity.finish()
    }

    @Test
    fun testCancelInsertingBrickOnBackPressed() {
        onView(allOf(withId(R.id.button_add), isDisplayed())).perform(ViewActions.click())
        onData(
            allOf(
                Is.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Is.`is`(UiTestUtils.getResourcesString(R.string.category_motion))
            )
        )
            .inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
            .perform(ViewActions.click())

        onData(Is.`is`(Matchers.instanceOf(SetYBrick::class.java)))
            .inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
            .perform(ViewActions.click())

        val isInsertedBrickHovering = (baseActivityTestRule.activity.supportFragmentManager
            .findFragmentByTag(ScriptFragment.TAG) as ScriptFragment?)!!.isCurrentlyMoving

        assertTrue(isInsertedBrickHovering)
        Espresso.pressBack()
        onView(withText(R.string.brick_set_y)).check(doesNotExist())
    }

    @Test
    fun testHighlightInsertingBrickOnHomeButtonPressed() {
        onView(allOf(withId(R.id.button_add), isDisplayed())).perform(ViewActions.click())
        onData(
            allOf(
                Is.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Is.`is`(UiTestUtils.getResourcesString(R.string.category_motion))
            )
        )
            .inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
            .perform(ViewActions.click())

        onData(Is.`is`(Matchers.instanceOf(SetYBrick::class.java)))
            .inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
            .perform(ViewActions.click())

        var isInsertedBrickHovering = (Objects.requireNonNull(
            baseActivityTestRule.activity.supportFragmentManager
                .findFragmentByTag(ScriptFragment.TAG)
        ) as ScriptFragment).isCurrentlyMoving

        assertTrue(isInsertedBrickHovering)
        onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
            .perform(ViewActions.click())

        isInsertedBrickHovering = (Objects.requireNonNull(
            baseActivityTestRule.activity.supportFragmentManager
                .findFragmentByTag(ScriptFragment.TAG)
        ) as ScriptFragment).isCurrentlyMoving

        assertTrue(isInsertedBrickHovering)
    }

    @Test
    fun testCommentOutScript() {
        BrickDataInteractionWrapper.onBrickAtPosition(0).performClick()
        onView(withText(R.string.brick_context_dialog_comment_out_script)).perform(ViewActions.click())
        Espresso.openContextualActionModeOverflowMenu()
        onView(withText(R.string.comment_in_out)).perform(ViewActions.click())
        BrickDataInteractionWrapper.onBrickAtPosition(0)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(2)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(3)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(4)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(5)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
    }

    @Test
    fun testCheckControlStructure() {
        Espresso.openContextualActionModeOverflowMenu()
        onView(withText(R.string.comment_in_out)).perform(ViewActions.click())

        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .checkShowsText(R.string.brick_if_begin)
            .checkShowsText(R.string.brick_if_begin_second_part)

        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().perform(ViewActions.click())

        BrickDataInteractionWrapper.onBrickAtPosition(0)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(2)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(3)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(4)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(5)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().perform(ViewActions.click())
        BrickDataInteractionWrapper.onBrickAtPosition(0)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(2)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(3)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(4)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(5)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(5)
            .checkShowsText(R.string.brick_if_end)
        BrickDataInteractionWrapper.onBrickAtPosition(5)
            .onCheckBox().perform(ViewActions.click())
        BrickDataInteractionWrapper.onBrickAtPosition(0)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(2)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(3)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(4)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(5)
            .onCheckBox().check(
                matches(
                    allOf(
                        ViewMatchers.isChecked(),
                        IsNot.not(ViewMatchers.isEnabled())
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().perform(ViewActions.click())
        BrickDataInteractionWrapper.onBrickAtPosition(0)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(2)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(3)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(4)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        BrickDataInteractionWrapper.onBrickAtPosition(5)
            .onCheckBox().check(
                matches(
                    allOf(
                        IsNot.not(ViewMatchers.isChecked()),
                        ViewMatchers.isEnabled()
                    )
                )
            )
    }

    private fun createProject() {
        defaultProject = DefaultProjectHandler
            .createAndSaveDefaultProject(
                "Example Project",
                ApplicationProvider.getApplicationContext(),
                false
            )
        val sprite = Sprite("testSprite")
        defaultProject.defaultScene.spriteList.add(1, sprite)

        val startScript: Script = StartScript()
        val ifBrick = IfLogicBeginBrick()
        ifBrick.addBrickToIfBranch(SetXBrick())
        ifBrick.addBrickToElseBranch(ChangeXByNBrick())
        startScript.addBrick(ifBrick)
        startScript.setParents()
        //Create longer script
        sprite.addScript(startScript)
        sprite.addScript(defaultProject.defaultScene.spriteList.get(2).getScript(0))
        XstreamSerializer.getInstance().saveProject(defaultProject)

        projectManager.value.currentProject = defaultProject
        projectManager.value.currentSprite = sprite
    }

    @Test
    @Throws(InterruptedException::class)
    fun testHideFloatingActionButtonsOnScroll() {
        val fabLayout = baseActivityTestRule.activity.findViewById<RelativeLayout>(R.id.bottom_bar)
        val listView = baseActivityTestRule.activity.findViewById<BrickListView>(android.R.id.list)

        onView(withId(R.id.bottom_bar)).check(matches(isDisplayed()))


        onView(withId(R.id.bottom_bar)).check(matches(not(isDisplayed())))


        wait(10)
        onView(withId(R.id.bottom_bar)).check(matches(isDisplayed()))
        onView(withId(android.R.id.list)).perform(swipeUpSlow())
        while (listView.isCurrentlyScrolledThrough)
            onView(withId(R.id.bottom_bar)).check(matches(not(isDisplayed())))
        wait(10)
        onView(withId(R.id.bottom_bar)).check(matches(isDisplayed()))
    }



    private fun swipeDownSlow() : ViewAction? {
        return ViewActions.actionWithAssertions(
            GeneralSwipeAction(Swipe.SLOW, GeneralLocation.BOTTOM_CENTER, GeneralLocation
                .TOP_CENTER, Press.FINGER)
        )
    }

    private fun swipeUpSlow() : ViewAction? {
        return ViewActions.actionWithAssertions(
            GeneralSwipeAction(Swipe.SLOW, GeneralLocation.TOP_CENTER, GeneralLocation
                .BOTTOM_CENTER, Press.THUMB)
        )
    }

    companion object {
        private const val TAG = "ScriptFragmentTest"
    }
}
