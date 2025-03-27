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

package org.catrobat.catroid.content.backwardcompatibility

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.ProjectData
import java.io.File
import java.io.IOException

class ProjectRepository {

    enum class SortBy {
        LAST_USED_DESC,
        NAME_ASC
    }

    suspend fun fetchProjectData(sortBy: SortBy = SortBy.LAST_USED_DESC): List<ProjectData> =
        withContext(Dispatchers.IO) {
            val projectDirs = FlavoredConstants.DEFAULT_ROOT_DIRECTORY.listFiles() ?: emptyArray()
            val projectDeferred = projectDirs.map { projectDir ->
                async {
                    val xmlFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
                    if (!xmlFile.exists()) {
                        null
                    } else {
                        val metaDataParser = ProjectMetaDataParser(xmlFile)
                        try {
                            metaDataParser.projectMetaData
                        } catch (exception: IOException) {
                            Log.e(javaClass.simpleName, "Project not parsable", exception)
                            null
                        }
                    }
                }
            }
            val projects = projectDeferred.awaitAll().filterNotNull()
            when (sortBy) {
                SortBy.LAST_USED_DESC -> projects.sortedByDescending { it.lastUsed }
                SortBy.NAME_ASC -> projects.sortedBy { it.name }
            }
        }
}