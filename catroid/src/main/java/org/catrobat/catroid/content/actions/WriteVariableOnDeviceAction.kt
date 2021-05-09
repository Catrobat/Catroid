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
import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.DeviceVariableAccessor
import java.io.IOException

class WriteVariableOnDeviceAction : AsynchronousAction() {
    private var userVariable: UserVariable? = null
    private var writeActionFinished = false
    override fun act(delta: Float): Boolean {
        return if (userVariable == null) {
            true
        } else super.act(delta)
    }

    override fun initialize() {
        writeActionFinished = false
        WriteTask().execute(userVariable)
    }

    override fun isFinished(): Boolean {
        return writeActionFinished
    }

    fun setUserVariable(userVariable: UserVariable?) {
        this.userVariable = userVariable
    }

    private inner class WriteTask : AsyncTask<UserVariable?, Void?, Void?>() {
        override fun doInBackground(userVariables: Array<UserVariable?>): Void? {
            val projectDirectory = ProjectManager.getInstance().currentProject.directory
            val accessor = DeviceVariableAccessor(projectDirectory)
            for (variable in userVariables) {
                try {
                    accessor.writeUserData(variable)
                } catch (e: IOException) {
                    Log.e(TAG, e.message)
                }
            }
            return null
        }

        override fun onPostExecute(o: Void?) {
            writeActionFinished = true
        }
    }

    companion object {
        private val TAG = WriteVariableOnDeviceAction::class.java.simpleName
    }
}