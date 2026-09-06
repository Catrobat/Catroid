/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.runner

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.StrictMode
import androidx.test.runner.AndroidJUnitRunner
import org.catrobat.catroid.UiTestCatroidApplication

class UiTestApplicationRunner : AndroidJUnitRunner() {

    override fun onCreate(arguments: Bundle) {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        super.onCreate(arguments)
    }

    override fun onStart() {
        dismissImmersiveModeCling()
        super.onStart()
    }

    /**
     * The first time an app goes full screen, Android shows the "Viewing full screen" cling on
     * top of it. It swallows the next back press, which makes tests that leave the stage fail on
     * a device that has not seen the cling yet.
     */
    private fun dismissImmersiveModeCling() {
        val command = "settings put secure immersive_mode_confirmations confirmed"
        ParcelFileDescriptor.AutoCloseInputStream(uiAutomation.executeShellCommand(command))
            .use { it.readBytes() }
    }

    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application = super.newApplication(cl, UiTestCatroidApplication::class.java.name, context)
}
