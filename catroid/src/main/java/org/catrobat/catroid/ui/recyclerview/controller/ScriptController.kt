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
package org.catrobat.catroid.ui.recyclerview.controller

import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.BrickData
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.UserDataBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.UserListBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickInterface
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserDataWrapper
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.koin.java.KoinJavaComponent
import java.io.IOException
import java.util.ArrayList

class ScriptController {
    private val lookController = LookController()
    private val soundController = SoundController()
    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)

    companion object {
        val TAG = ScriptController::class.java.simpleName
    }

    @Throws(IOException::class, CloneNotSupportedException::class)
    fun copy(
        scriptToCopy: Script,
        destinationProject: Project?,
        destinationScene: Scene?,
        destinationSprite: Sprite?
    ): Script {

        val script: Script = scriptToCopy.clone()
        val scriptFlatBrickList: List<Brick> = ArrayList()
        script.addToFlatList(scriptFlatBrickList)

        for (brick in scriptFlatBrickList) {
            when (brick) {
                is SetLookBrick ->
                    brick.look = brick.look?.let {
                        lookController.findOrCopy(it, destinationScene, destinationSprite)
                    }

                is WhenBackgroundChangesBrick ->
                    brick.look = brick.look?.let {
                        lookController.findOrCopy(it, destinationScene, destinationSprite)
                    }

                is PlaySoundBrick ->
                    brick.sound = brick.sound?.let {
                        soundController.findOrCopy(it, destinationScene, destinationSprite)
                    }

                is PlaySoundAndWaitBrick ->
                    brick.sound = brick.sound?.let {
                        soundController.findOrCopy(it, destinationScene, destinationSprite)
                    }

                is UserVariableBrickInterface ->
                    brick.userVariable?.let {
                        updateUserVariable(brick, destinationProject, destinationSprite)
                    }

                is UserListBrick ->
                    brick.userList?.let {
                        updateUserList(brick, destinationProject, destinationSprite)
                    }

                is UserDataBrick ->
                    updateUserData(brick, destinationProject, destinationSprite)
            }
        }
        return script
    }

    @Throws(CloneNotSupportedException::class)
    fun pack(groupName: String?, bricksToPack: List<Brick>?) {
        val scriptsToPack: MutableList<Script> = ArrayList()
        val userDefinedBrickListToPack: MutableList<UserDefinedBrick> = ArrayList()
        bricksToPack?.forEach() { brick ->
            if (brick is ScriptBrick) {
                if (brick is UserDefinedReceiverBrick) {
                    val userDefinedBrick = brick.userDefinedBrick
                    userDefinedBrickListToPack.add(userDefinedBrick.clone() as UserDefinedBrick)
                }
                val scriptToPack = brick.getScript()
                scriptsToPack.add(scriptToPack.clone())
            }
            if (brick is UserDefinedBrick) {
                val userDefinedScript = projectManager.currentSprite.getUserDefinedScript(brick.userDefinedBrickID)

                if (!checkIfUserDefinedBrickDefinitionIsInBricksToPack(bricksToPack, brick)) {
                    scriptsToPack.add(userDefinedScript)
                    userDefinedBrickListToPack.add(brick.clone() as UserDefinedBrick)
                }
            }
        }

        BackpackListManager.getInstance()
            .addUserDefinedBrickToBackPack(groupName, userDefinedBrickListToPack)
        BackpackListManager.getInstance().addScriptToBackPack(groupName, scriptsToPack)
        BackpackListManager.getInstance().saveBackpack()
    }

    private fun checkIfUserDefinedBrickDefinitionIsInBricksToPack(bricksToPack: List<Brick>?, userDefinedBrick: UserDefinedBrick): Boolean {
        bricksToPack?.forEach { brick ->
            if (brick is UserDefinedReceiverBrick && brick.userDefinedBrick.userDefinedBrickID.equals(userDefinedBrick.userDefinedBrickID)) {
                return true
            }
        }
        return false
    }

    @Throws(IOException::class, CloneNotSupportedException::class)
    fun packForSprite(scriptToPack: Script, destinationSprite: Sprite) {
        val script = scriptToPack.clone()
        val scriptFlatBrickList: List<Brick> = ArrayList()
        script.addToFlatList(scriptFlatBrickList)

        for (brick in scriptFlatBrickList) {
            when (brick) {
                is SetLookBrick ->
                    brick.look = brick.look?.let {
                        lookController.packForSprite(it, destinationSprite)
                    }

                is WhenBackgroundChangesBrick ->
                    brick.look = brick.look?.let {
                        lookController.packForSprite(it, destinationSprite)
                    }

                is PlaySoundBrick ->
                    brick.sound = brick.sound?.let {
                        soundController.packForSprite(it, destinationSprite)
                    }

                is PlaySoundAndWaitBrick ->
                    brick.sound = brick.sound?.let {
                        soundController.packForSprite(it, destinationSprite)
                    }
            }
        }
        destinationSprite.scriptList.add(script)
    }

    @Throws(CloneNotSupportedException::class)
    fun unpack(scriptToUnpack: Script, destinationSprite: Sprite) {
        val script = scriptToUnpack.clone()
        copyBroadcastMessages(script.scriptBrick)
        for (brick in script.brickList) {
            if (projectManager.currentProject.isCastProject &&
                CastManager.unsupportedBricks.contains(brick.javaClass)
            ) {
                Log.e(TAG, "CANNOT insert bricks into ChromeCast project")
                return
            }
            copyBroadcastMessages(brick)
        }
        destinationSprite.scriptList.add(script)
    }

    private fun copyBroadcastMessages(brick: Brick): Boolean {
        if (brick is BroadcastMessageBrick) {
            val broadcastMessage = brick.broadcastMessage
            return projectManager.currentProject.broadcastMessageContainer.addBroadcastMessage(
                broadcastMessage
            )
        }
        return false
    }

    @Throws(IOException::class, CloneNotSupportedException::class)
    fun unpackForSprite(
        scriptToUnpack: Script,
        destinationProject: Project?,
        destinationScene: Scene?,
        destinationSprite: Sprite
    ) {
        val script = scriptToUnpack.clone()
        for (brick in script.brickList) {
            when {
                projectManager.currentProject.isCastProject && CastManager.unsupportedBricks.contains(
                    brick.javaClass
                ) -> {
                    Log.e(TAG, "CANNOT insert bricks into ChromeCast project")
                    return
                }
                brick is SetLookBrick && brick.look != null ->
                    brick.look = lookController.unpackForSprite(
                        brick.look,
                        destinationScene,
                        destinationSprite
                    )

                brick is WhenBackgroundChangesBrick && brick.look != null ->
                    brick.look = lookController.unpackForSprite(
                        brick.look,
                        destinationScene,
                        destinationSprite
                    )

                brick is PlaySoundBrick && brick.sound != null ->
                    brick.sound = soundController.unpackForSprite(
                        brick.sound,
                        destinationScene,
                        destinationSprite
                    )

                brick is PlaySoundAndWaitBrick && brick.sound != null ->
                    brick.sound = soundController.unpackForSprite(
                        brick.sound,
                        destinationScene,
                        destinationSprite
                    )

                brick is UserVariableBrickInterface && brick.userVariable != null ->
                    updateUserVariable(brick, destinationProject, destinationSprite)

                brick is UserListBrick && brick.userList != null ->
                    updateUserList(brick, destinationProject, destinationSprite)

                brick is UserDataBrick ->
                    updateUserData(brick, destinationProject, destinationSprite)
            }
        }
        destinationSprite.scriptList.add(script)
    }

    private fun updateUserData(
        brick: UserDataBrick,
        destinationProject: Project?,
        destinationSprite: Sprite?
    ) {
        for (entry in brick.userDataMap.entries) {
            val previousUserData = entry.value
            var updatedUserList: UserData<*>?
            val scope = destinationSprite?.let { Scope(destinationProject, it, null) }
            updatedUserList = if (BrickData.isUserList(entry.key)) {
                UserDataWrapper.getUserList(previousUserData?.name, scope)
            } else {
                UserDataWrapper.getUserVariable(previousUserData?.name, scope)
            }
            entry.setValue(updatedUserList)
        }
    }

    private fun updateUserList(
        brick: UserListBrick,
        destinationProject: Project?,
        destinationSprite: Sprite?
    ) {
        val previousUserList = brick.userList
        val scope = destinationSprite?.let { Scope(destinationProject, it, null) }
        val updatedUserList = UserDataWrapper.getUserList(previousUserList?.name, scope)
        brick.userList = updatedUserList
    }

    private fun updateUserVariable(
        brick: UserVariableBrickInterface,
        destinationProject: Project?,
        destinationSprite: Sprite?
    ) {
        val previousUserVar = brick.userVariable
        val scope = destinationSprite?.let { Scope(destinationProject, it, null) }
        val updatedUserVar =
            UserDataWrapper.getUserVariable(previousUserVar?.name, scope)
        brick.userVariable = updatedUserVar
    }
}
