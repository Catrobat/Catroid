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
package org.catrobat.catroid.ui.filepicker

import android.os.AsyncTask
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import java.io.File
import java.lang.ref.WeakReference
import java.util.ArrayList

class ListProjectFilesTask(listener: OnListProjectFilesListener) :
    AsyncTask<File?, Void?, List<File>>() {
    private val weakListenerReference: WeakReference<OnListProjectFilesListener> = WeakReference(listener)
    @SuppressWarnings("SpreadOperator")
    override fun doInBackground(vararg params: File?): List<File> = task(*params as Array<out File>)

    override fun onPostExecute(files: List<File>) {
        val listener = weakListenerReference.get()
        listener?.onListProjectFilesComplete(files)
    }

    interface OnListProjectFilesListener {
        fun onListProjectFilesComplete(files: List<File>?)
    }

    companion object {
        @JvmStatic
        fun task(vararg startDir: File): List<File> {
            val files: MutableList<File> = ArrayList()
            for (dir in startDir) {
                findProjectFiles(dir, files)
            }
            getAllProjectsFromPocketCodeFolder(files)
            return files
        }

        private fun findProjectFiles(dir: File, projectFiles: MutableList<File>) {
            // this check will prevent a future crash on android 11
            if (dir.canRead() && dir.listFiles() != null) {
                dir.listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        findProjectFiles(file, projectFiles)
                    }
                    if (file.name.endsWith(Constants.CATROBAT_EXTENSION)) {
                        projectFiles.add(file)
                    }
                }
            }
        }
        @SuppressWarnings("ComplexCondition")
        private fun getAllProjectsFromPocketCodeFolder(projectFiles: MutableList<File>) {
            if (FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY.listFiles() == null) {
                return
            }
            FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY.listFiles()?.forEach { dir ->
                if (dir.name != Constants.BACKPACK_DIRECTORY_NAME && dir.name != Constants.TMP_DIRECTORY_NAME && dir.isDirectory && File(dir, Constants.CODE_XML_FILE_NAME).exists()) {
                    projectFiles.add(dir)
                }
            }
        }
    }
}
