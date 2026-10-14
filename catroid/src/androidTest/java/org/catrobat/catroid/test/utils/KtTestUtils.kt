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

package org.catrobat.catroid.test.utils

import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.io.XstreamSerializer
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import kotlin.getValue

class KtTestUtils private constructor(){
    companion object {
        private val projectManager by inject(ProjectManager::class.java)

        @JvmStatic
        fun createProjectAndGetStartScriptandFile(projectName: String?): Triple<Script, File,
            Project> {
            val project = Project(ApplicationProvider.getApplicationContext(), projectName)
            val sprite = Sprite(TestUtils.DEFAULT_TEST_SPRITE_NAME)
            val script: Script = StartScript()
            sprite.addScript(script)
            project.defaultScene.addSprite(sprite)
            projectManager.currentProject = project
            projectManager.currentSprite = sprite
            projectManager.currentlyEditedScene = project.defaultScene
            XstreamSerializer.getInstance().saveProject(project)
            return Triple(script, project.getDirectory(), project)
        }
    }
}