/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.os.AsyncTask
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.io.DeviceListAccessor
import org.catrobat.catroid.io.DeviceUserDataAccessor

class ReadListFromDeviceAction : AsynchronousAction() {
    private var userList: UserList? = null
    private var readActionFinished = false
    override fun act(delta: Float): Boolean {
        return if (userList == null) {
            true
        } else super.act(delta)
    }

    override fun initialize() {
        readActionFinished = false
        ReadTask().execute(userList)
    }

    override fun isFinished(): Boolean {
        return readActionFinished
    }

    fun setUserList(userList: UserList?) {
        this.userList = userList
    }

    private inner class ReadTask : AsyncTask<UserList?, Void?, Void?>() {
        override fun doInBackground(userList: Array<UserList?>): Void? {
            val projectDirectory = ProjectManager.getInstance().currentProject.directory
            val accessor: DeviceUserDataAccessor = DeviceListAccessor(projectDirectory)
            for (list in userList) {
                accessor.readUserData(list)
            }
            return null
        }

        override fun onPostExecute(o: Void?) {
            readActionFinished = true
        }
    }
}