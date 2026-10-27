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
package org.catrobat.catroid.test.robolectric

import android.os.Build
import android.os.Bundle
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.ui.SpriteActivity
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The script editor is rebuilt from its saved state both after a configuration change and
 * after Android stopped the app in the background, for example while the photo picker of
 * the face training was open. Only in the first case is the project still in memory.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SpriteActivityRecreationTest {

    private lateinit var savedState: Bundle

    @Before
    fun setUp() {
        val creator = Robolectric.buildActivity(SpriteActivity::class.java)
        createProject(creator.get())
        creator.setup()
        savedState = Bundle()
        creator.saveInstanceState(savedState)
        creator.pause().stop().destroy()
    }

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
        ProjectManager.getInstance().currentProject = null
    }

    @Test
    fun theScriptEditorClosesWhenTheAppWasStoppedInTheBackground() {
        ProjectManager.getInstance().resetProjectManager()
        ProjectManager.getInstance().currentProject = null

        val activity = Robolectric.buildActivity(SpriteActivity::class.java).create(savedState).get()

        assertTrue(activity.isFinishing)
    }

    @Test
    fun theScriptEditorStaysAfterAConfigurationChange() {
        val activity = Robolectric.buildActivity(SpriteActivity::class.java).create(savedState).get()

        assertFalse(activity.isFinishing)
    }

    private fun createProject(activity: SpriteActivity) {
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }
}
