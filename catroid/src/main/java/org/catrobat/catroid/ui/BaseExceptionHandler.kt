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
package org.catrobat.catroid.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Process
import android.preference.PreferenceManager
import android.util.Log
import kotlin.system.exitProcess

private const val EXIT_CODE = 10

open class BaseExceptionHandler(context: Context) : Thread.UncaughtExceptionHandler {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        Log.e(TAG, "uncaughtException: ", exception)
        preferences.edit()
            .putBoolean(RECOVERED_FROM_CRASH, true)
            .apply()
        exit()
    }

    protected fun exit() {
        Process.killProcess(Process.myPid())
        exitProcess(EXIT_CODE)
    }

    companion object {
        private val TAG = BaseExceptionHandler::class.java.simpleName
    }
}
