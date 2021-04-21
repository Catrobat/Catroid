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
package org.catrobat.catroid.common

import android.content.Context
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.common.defaultprojectcreators.ChromeCastProjectCreator
import org.catrobat.catroid.common.defaultprojectcreators.DefaultExampleProject
import org.catrobat.catroid.common.defaultprojectcreators.ProjectCreator
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.XstreamSerializer
import java.io.IOException

class DefaultProjectHandler private constructor() {
    enum class ProjectCreatorType {
        PROJECT_CREATOR_DEFAULT, PROJECT_CREATOR_CAST
    }

    private var defaultProjectCreator: ProjectCreator? = null
    fun setDefaultProjectCreator(type: ProjectCreatorType?) {
        when (type) {
            ProjectCreatorType.PROJECT_CREATOR_DEFAULT -> defaultProjectCreator =
                DefaultExampleProject()
            ProjectCreatorType.PROJECT_CREATOR_CAST -> if (BuildConfig.FEATURE_CAST_ENABLED) {
                defaultProjectCreator = ChromeCastProjectCreator()
            }
        }
    }

    companion object {
        @JvmStatic
		var instance: DefaultProjectHandler? = null
            get() {
                if (field == null) {
                    field = DefaultProjectHandler()
                }
                return field
            }
            private set

        @JvmStatic
        @Throws(IOException::class)
        fun createAndSaveDefaultProject(context: Context): Project? {
            val name = context.getString(instance!!.defaultProjectCreator!!.defaultProjectNameID)
            return createAndSaveDefaultProject(name, context, false)
        }

        @JvmStatic
		@Throws(IOException::class)
        fun createAndSaveDefaultProject(
            name: String?,
            context: Context?,
            landscapeMode: Boolean
        ): Project? {
            return instance!!.defaultProjectCreator!!.createDefaultProject(
                name,
                context,
                landscapeMode
            )
        }

        @JvmStatic
		@Throws(IOException::class)
        fun createAndSaveEmptyProject(
            name: String?,
            context: Context?,
            landscapeMode: Boolean,
            isCastEnabled: Boolean
        ): Project {
            val project = Project(context, name, landscapeMode, isCastEnabled)
            if (project.directory.exists()) {
                throw IOException(
                    "Cannot create new project at "
                        + project.directory.absolutePath
                        + ", directory already exists."
                )
            }
            XstreamSerializer.getInstance().saveProject(project)
            return project
        }
    }

    init {
        setDefaultProjectCreator(ProjectCreatorType.PROJECT_CREATOR_DEFAULT)
    }
}