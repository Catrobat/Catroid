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

package org.catrobat.catroid.utils

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ListSelectorBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.koin.java.KoinJavaComponent.inject

object UserDataUtil {
    @JvmStatic
    fun containedInListSelector(dateName: String): Boolean {
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        return projectManager.currentProject.sceneList.any { scene ->
            scene.spriteList.any { sprite ->
                sprite.scriptList.any { script ->
                    val flatList = mutableListOf<Brick>()
                    script.addToFlatList(flatList)
                    flatList.filterIsInstance<ListSelectorBrick>().any { brick ->
                        brick.userLists.any { it.name == dateName }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun renameUserData(item: UserData<*>, name: String) {
        if (containedInListSelector(item.name)) {
            val previousName: String = item.name
            val rename = { data: UserData<*> ->
                if (data.name == previousName) {
                    data.name = name
                }
            }
            val projectManager: ProjectManager by inject(ProjectManager::class.java)
            projectManager.currentProject?.userLists?.forEach(rename)
            projectManager.currentSprite?.userLists?.forEach(rename)
            projectManager.currentProject?.userVariables?.forEach(rename)
            projectManager.currentSprite?.userVariables?.forEach(rename)
        } else {
            item.name = name
        }
    }
}
