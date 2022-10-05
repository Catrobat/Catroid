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

package org.catrobat.catroid.test.merge

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class MergeTestUtils {
    fun assertSuccessfulSpriteImport(
        currentProject: Project,
        sourceProject: Project,
        spriteToImport: Sprite,
        importedSprite: Sprite,
        wasVisuallyPlaced: Boolean
    ) {
        Assert.assertNotNull(importedSprite)
        assertTrue(currentProject.spriteListWithClones.contains(importedSprite))
        assertTrue(currentProject.userVariables.containsAll(sourceProject.userVariables))
        assertTrue(currentProject.userLists.containsAll(sourceProject.userLists))
        assertTrue(
            currentProject.broadcastMessageContainer.broadcastMessages.containsAll(
                sourceProject.broadcastMessageContainer.broadcastMessages
            )
        )

        assertEquals(importedSprite.soundList.size, spriteToImport.soundList.size)
        assertEquals(importedSprite.lookList.size, spriteToImport.lookList.size)
        if (wasVisuallyPlaced) {
            assertEquals(importedSprite.scriptList.size, spriteToImport.scriptList.size + 1)
        } else {
            assertEquals(importedSprite.scriptList.size, spriteToImport.scriptList.size)
        }
        assertTrue(importedSprite.userVariables.containsAll(spriteToImport.userVariables))
        assertTrue(importedSprite.userLists.containsAll(spriteToImport.userLists))

        Assert.assertFalse(TestUtils.checkForDuplicates(currentProject.userLists as List<Any>?))
        Assert.assertFalse(TestUtils.checkForDuplicates(currentProject.userVariables as List<Any>?))
        Assert.assertFalse(TestUtils.checkForDuplicates(currentProject.broadcastMessageContainer.broadcastMessages as List<Any>?))
    }

    fun assertRejectedImport(currentProject: Project, projectBeforeImport: ProjectMergeData) {
        assertEquals(currentProject.userVariables, projectBeforeImport.userVariables)
        assertEquals(currentProject.userLists, projectBeforeImport.userLists)
        assertEquals(
            currentProject.broadcastMessageContainer.broadcastMessages,
            projectBeforeImport.broadcastMessages
        )
        assertEquals(currentProject.spriteListWithClones, projectBeforeImport.spriteListWithClones)
    }

    data class ProjectMergeData(
        val userVariables: List<UserVariable>,
        val userLists: List<UserList>,
        val spriteListWithClones: List<Sprite>,
        val broadcastMessages: List<String>
    )

    fun getOriginalProjectData(project: Project): ProjectMergeData {
        val originalUserVariables = ArrayList<UserVariable>()
        originalUserVariables.addAll(project.userVariables)
        val originalUserLists = ArrayList<UserList>()
        originalUserLists.addAll(project.userLists)
        val originalSprites = ArrayList<Sprite>()
        originalSprites.addAll(project.spriteListWithClones)
        val originalBroadCastMessages = ArrayList<String>()
        originalBroadCastMessages.addAll(project.broadcastMessageContainer.broadcastMessages)
        return ProjectMergeData(
            originalUserVariables,
            originalUserLists,
            originalSprites,
            originalBroadCastMessages
        )
    }
}
