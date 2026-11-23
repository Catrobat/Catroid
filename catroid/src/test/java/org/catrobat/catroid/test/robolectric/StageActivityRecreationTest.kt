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
import org.catrobat.catroid.stage.StageActivity
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.ref.WeakReference

/**
 * Android rebuilds the stage when the app returns after Android stopped it in the
 * background, for example while the photo picker of the face training was open.
 * The project is gone then, so the stage closes in onCreate, before the game
 * engine (libGDX) starts. Destroying that stage must not crash the app.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class StageActivityRecreationTest {

    private var savedStage: WeakReference<StageActivity>? = null

    @Before
    fun setUp() {
        savedStage = StageActivity.activeStageActivity
        forgetTheProject()
    }

    @After
    fun tearDown() {
        StageActivity.activeStageActivity = savedStage
        forgetTheProject()
    }

    /** The state after Android stopped the app: no project in memory, also none from another test. */
    private fun forgetTheProject() {
        ProjectManager.getInstance().resetProjectManager()
        ProjectManager.getInstance().currentProject = null
    }

    @Test
    fun aStageRebuiltWithoutTheProjectClosesAndIsDestroyedWithoutACrash() {
        val controller = Robolectric.buildActivity(StageActivity::class.java).create(Bundle())

        assertTrue(controller.get().isFinishing)

        controller.destroy()
    }
}
