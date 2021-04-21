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
package org.catrobat.catroid.common

import org.catrobat.catroid.utils.FileMetaDataExtractor
import java.io.File
import java.io.Serializable

class ProjectData(
    private val name: String,
    private val directory: File,
    val languageVersion: Double,
    private val hasScenes: Boolean
) : Nameable, Serializable, Comparable<ProjectData> {
    override fun getName(): String {
        return name
    }

    override fun setName(name: String) {
        throw RuntimeException("Do not set the project name through this. TODO: refactor nameable interface.")
    }

    fun hasScenes(): Boolean {
        return hasScenes
    }

    val lastUsed: Long
        get() = File(directory, Constants.CODE_XML_FILE_NAME).lastModified()

    fun getDirectory(): File {
        return File(
            FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(getName())
        )
    }

    override fun compareTo(projectData: ProjectData): Int {
        return name.compareTo(projectData.getName())
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}