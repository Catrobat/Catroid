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
package org.catrobat.catroid.uiespresso.ui.dialog

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.OpenUrlBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WarningForWebAccessBricksDownloadedProjectTest{

    private lateinit var project: Project
    private lateinit var projectName: String
    private lateinit var sprite: Sprite
    private lateinit var script: StartScript

    @get:Rule
    var baseActivityTestRule: BaseActivityTestRule<SpriteActivity> = BaseActivityTestRule(
        SpriteActivity::class.java)

    @Before
    fun setUp() {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        ProjectManager.getInstance().addNewDownloadedProject(projectName)
        ProjectManager.getInstance().currentProject = project
        sprite = Sprite()
        script = StartScript()
        project.defaultScene.addSprite(sprite)
        sprite.addScript(script)
        project.xmlHeader.remixParentsUrlString = "url"
    }

    @Test
    fun showWarningForWebRequestBrick() {
        val brick = WebRequestBrick()
        script.addBrick(brick)
        val intent = Intent()
        baseActivityTestRule.launchActivity(intent)
        Espresso.onView(ViewMatchers.withText(R.string.warning))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.ok)).perform(ViewActions.click())


        /*Espresso.onView(ViewMatchers.withText(R.string.security_warning_dialog_msg_web_access))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))*/

    }

    @Test
    fun showWarningForStartListeningBrick() {
        val brick = StartListeningBrick()
        script.addBrick(brick)
        val intent = Intent()
        baseActivityTestRule.launchActivity(intent)
    }

    @Test
    fun showWarningForLookRequestBrick() {
        val brick = LookRequestBrick()
        script.addBrick(brick)
        val intent = Intent()
        baseActivityTestRule.launchActivity(intent)
    }

    @Test
    fun showWarningForBackgroundRequestBrick() {
        val brick = BackgroundRequestBrick()
        script.addBrick(brick)
        val intent = Intent()
        baseActivityTestRule.launchActivity(intent)
    }

    @Test
    fun showWarningForOpenUrlBrick() {
        val brick = OpenUrlBrick()
        script.addBrick(brick)
        val intent = Intent()
        baseActivityTestRule.launchActivity(intent)
    }
}