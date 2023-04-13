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
package org.catrobat.catroid.content.actions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.io.DeviceListAccessor

class ReadListFromDeviceAction(private val scope: CoroutineScope) : AsynchronousAction() {
    private var userList: UserList? = null
    private var readActionFinished = false

    override fun act(delta: Float): Boolean = userList == null || super.act(delta)

    override fun initialize() {
        readActionFinished = false
        scope.launch {
            readUserListFromDevice()
            readActionFinished = true
        }
    }

    override fun isFinished(): Boolean = readActionFinished

    fun setUserList(userList: UserList?) {
        this.userList = userList
    }

    private suspend fun readUserListFromDevice() {
        val currentProject = ProjectManager.getInstance().currentProject
        currentProject?.directory?.let { projectDirectory ->
            val accessor = DeviceListAccessor(projectDirectory)
            userList?.let { list ->
                accessor.readUserData(list)
            }
        }
    }
}
