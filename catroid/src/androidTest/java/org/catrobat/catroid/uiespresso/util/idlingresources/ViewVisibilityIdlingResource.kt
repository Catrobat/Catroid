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

package org.catrobat.catroid.uiespresso.util.idlingresources

import android.view.View
import androidx.test.espresso.IdlingResource
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage

class ViewVisibilityIdlingResource(private val viewId: Int, private val expectedVisibility: Int) : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    private var isIdle = false

    override fun getName(): String = "ViewVisibilityIdlingResource"

    override fun isIdleNow(): Boolean {
        val checkVisibility = {
            var view: View? = null
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                val currentActivity = resumedActivities.iterator().next()
                view = currentActivity.findViewById(viewId)
            }
            view != null && view?.visibility == expectedVisibility
        }

        val currentlyIdle = if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            checkVisibility()
        } else {
            var result = false
            getInstrumentation().runOnMainSync {
                result = checkVisibility()
            }
            result
        }

        if (currentlyIdle && !isIdle) {
            resourceCallback?.onTransitionToIdle()
        }
        isIdle = currentlyIdle
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}
