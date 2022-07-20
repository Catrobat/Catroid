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

package org.catrobat.catroid.content.actions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.io.DeviceUserDataAccessor

class WriteUserDataOnDeviceAction : AsynchronousAction() {
    var userData: UserData<Any>? = null
    var accessor: DeviceUserDataAccessor? = null
    private var writeActionFinished = false
    private val scopeIO = CoroutineScope(Dispatchers.IO)

    override fun initialize() {
        writeActionFinished = false
        userData?.let { executeWriteTask(it) }
    }

    override fun isFinished(): Boolean = writeActionFinished || userData == null

    private fun executeWriteTask(userData: UserData<Any>) {
        scopeIO.launch {
            accessor?.writeUserData(userData)

            withContext(Dispatchers.Main) {
                writeActionFinished = true
            }
        }
    }
}
