 /*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser
import java.io.File
import java.io.IOException

class ProjectsViewModel : ViewModel() {
    private val projectList = MutableLiveData<List<ProjectData>>()

    fun getProjects(): LiveData<List<ProjectData>> = projectList

    private fun getProjectData(): List<ProjectData> {
        val myProjects = mutableListOf<ProjectData>()
        FlavoredConstants.DEFAULT_ROOT_DIRECTORY.listFiles()?.forEach { projectDir ->
            val xmlFile =
                File(projectDir, Constants.CODE_XML_FILE_NAME)
            if (xmlFile.exists()) {
                val metaDataParser = ProjectMetaDataParser(xmlFile)
                try {
                    myProjects.add(metaDataParser.projectMetaData)
                } catch (e: IOException) {
                    Log.e(MainMenuFragment.TAG, "Project not parseable", e)
                }
            }
        }
        return myProjects.sortedByDescending { it.lastUsed }
    }

    fun forceUpdate() {
        projectList.postValue(getProjectData())
    }
}
