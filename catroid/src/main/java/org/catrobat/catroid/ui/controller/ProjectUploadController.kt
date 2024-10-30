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
package org.catrobat.catroid.ui.controller

import android.content.Context
import android.content.Intent
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.transfers.project.ProjectUploadService
import org.catrobat.catroid.transfers.project.ResultReceiverWrapper

open class ProjectUploadController(private val projectUploadInterface: ProjectUploadInterface) {
    interface ProjectUploadInterface {
        fun getResultReceiverWrapper(): ResultReceiverWrapper
        fun getContext(): Context
        fun startUploadService(intent: Intent?)
    }

    private val context: Context = projectUploadInterface.getContext()
    private fun createUploadIntent(
        projectName: String,
        projectDescription: String,
        project: Project
    ): Intent {
        val intent = Intent(context, ProjectUploadService::class.java)
        val sceneNames = project.sceneNames.toTypedArray()
        val resultReceiverWrapper = projectUploadInterface.getResultReceiverWrapper()
        return intent.apply {
            putExtra(Constants.EXTRA_RESULT_RECEIVER, resultReceiverWrapper)
            putExtra(Constants.EXTRA_UPLOAD_NAME, projectName)
            putExtra(Constants.EXTRA_PROJECT_DESCRIPTION, projectDescription)
            putExtra(Constants.EXTRA_PROJECT_PATH, project.directory.absolutePath)
            putExtra(Constants.EXTRA_PROVIDER, Constants.NO_OAUTH_PROVIDER)
            putExtra(Constants.EXTRA_SCENE_NAMES, sceneNames)
        }
    }

    open fun startUpload(
        projectName: String,
        projectDescription: String,
        projectNotesAndCredits: String?,
        project: Project
    ) {
        project.description = projectDescription
        project.notesAndCredits = projectNotesAndCredits
        project.setDeviceData(context)
        project.setListeningLanguageTag()
        saveProjectSerial(project, context)
        val uploadIntent = createUploadIntent(projectName, projectDescription, project)
        projectUploadInterface.startUploadService(uploadIntent)
    }
}
