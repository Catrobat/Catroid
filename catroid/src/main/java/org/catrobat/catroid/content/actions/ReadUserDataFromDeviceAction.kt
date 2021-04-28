/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.content.actions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.io.DeviceUserDataAccessor

class ReadUserDataFromDeviceAction : AsynchronousAction() {
    var userData: UserData<Any>? = null
    var deviceUserDataAccessor: DeviceUserDataAccessor? = null
    private var readActionFinished = false

    override fun act(delta: Float): Boolean {
        return if (userData == null) {
            true
        } else {
            super.act(delta)
        }
    }

    override fun initialize() {
        readActionFinished = false
        userData?.let {
            readUserData(it)
        }
    }

    override fun isFinished(): Boolean = readActionFinished

    private fun readUserData(
        userData: UserData<Any>,
        onReadComplete: (Boolean) -> Unit = {},
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        dispatcherOnFinished: CoroutineDispatcher = Dispatchers.Main
    ) {
        scope.launch {
            val isSuccessful = deviceUserDataAccessor?.readUserData(userData) ?: false
            withContext(dispatcherOnFinished) {
                readActionFinished = true
                onReadComplete(isSuccessful)
            }
        }
    }
}
