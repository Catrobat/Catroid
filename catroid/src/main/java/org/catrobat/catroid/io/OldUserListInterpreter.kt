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
package org.catrobat.catroid.io

import android.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.UUID

const val NUMBER_OF_OLD_USER_LIST_ARGUMENTS = 4
const val USER_VARIABLE_STRING = "userVariable"
const val USER_LIST_STRING = "userList"
const val USER_VARIABLES_STRING = "userVariables"
const val USER_LISTS_STRING = "userLists"
const val NAME = "name"
const val DEVICE_VALUE_KEY = "deviceValueKey"
const val DEVICE_LIST_KEY = "deviceListKey"
const val INITIAL_INDEX = "initialIndex"
const val IS_LIST = "isList"
const val VALUE = "value"
const val REFERENCE = "reference"
const val DEFAULT = "default"

class OldUserListInterpreter(private val outcomeDocument: Document) {
    fun interpret(): Document {
        moveUserListReferencesToVariableReferences()
        val userListNodes = getProgramsUserLists() ?: return outcomeDocument

        userListNodes.filterNot { isNewUserList(it) }
            .forEach { oldUserList ->
                if (oldUserList.childNodes.length !in 1..NUMBER_OF_OLD_USER_LIST_ARGUMENTS) {
                    throw OldUserListInterpretationException(
                        "unlikely userList in " + outcomeDocument.documentURI + " found"
                    )
                }
                try {
                    createNewUserLists(oldUserList as Element)
                } catch (e: Exception) {
                    Log.d(
                        javaClass.simpleName,
                        "UserList interpretation for this project failed.",
                        e
                    )
                }
            }
        return outcomeDocument
    }

    private fun getProgramsUserLists(): ArrayList<Node>? {
        val userListWithNewFunction = outcomeDocument.getElementsByTagName(USER_LIST_STRING)
        val userListList = ArrayList<Node>()
        for (i in 0 until userListWithNewFunction.length) {
            val userList = userListWithNewFunction.item(i)
            if (userList.childNodes.length != 0) {
                userListList.add(userList)
            } else if ((userList as Element).hasAttribute(REFERENCE)) {
                updateUserListReference(userList)
            }
            outcomeDocument.renameNode(userList, null, USER_VARIABLE_STRING)
        }
        updateAllUserListReferences()
        return if (userListList.isEmpty()) null else userListList
    }

    private fun updateAllUserListReferences() {
        val userDataList = outcomeDocument.getElementsByTagName("userData")
        for (i in 0 until userDataList.length) {
            updateUserListReference(userDataList.item(i) as Element)
        }
    }

    private fun updateUserListReference(userList: Element) {
        val reference = userList.getAttribute(REFERENCE)
        val referenceParts = reference.split("/").toMutableList()
        if (referenceParts.isNotEmpty() && referenceParts.last() == USER_LIST_STRING) {
            referenceParts[referenceParts.size - 1] = USER_VARIABLE_STRING
            userList.removeAttribute(REFERENCE)
            userList.setAttribute(REFERENCE, referenceParts.joinToString("/"))
        }
    }

    private fun isNewUserList(userList: Node): Boolean {
        if (listOf(NAME, DEVICE_VALUE_KEY, INITIAL_INDEX, IS_LIST).all {
                NodeOperatorExtension.getNodeByName(userList, it) != null
            }) {
            if (NodeOperatorExtension.getNodeByName(userList, VALUE) == null) {
                val value = userList.firstChild.cloneNode(true)
                value.firstChild.textContent = ""
                userList.appendChild(value)
                outcomeDocument.renameNode(value, null, VALUE)
            }
            return true
        }

        if (NodeOperatorExtension.getNodeByName(userList, USER_VARIABLE_STRING) != null ||
            NodeOperatorExtension.getNodeByName(userList, USER_LIST_STRING) != null ||
            NodeOperatorExtension.getNodeByName(userList, DEFAULT) != null
        ) {
            return isNewUserList(userList.firstChild)
        }

        return false
    }

    private fun createNewUserLists(userList: Element) {
        userList.setAttribute("type", "UserVariable")
        userList.setAttribute("serialization", "custom")

        clearUserListNode(userList)
        val name: Node = NodeOperatorExtension.getNodeByName(userList, NAME)
            ?: return
        val deviceValueKey: Node = NodeOperatorExtension.getNodeByName(
            userList, DEVICE_LIST_KEY
        ) ?: return
        outcomeDocument.renameNode(deviceValueKey, null, DEVICE_VALUE_KEY)

        buildNewUserList(
            userList, name, createInitialIndexNode(deviceValueKey.cloneNode(true)),
            deviceValueKey
        )
    }

    private fun clearUserListNode(userList: Element) {
        var nameFound = false
        var deviceValueKeyFound = false
        val children = userList.childNodes

        for (i in 0 until children.length) {
            val temp = children.item(i)
            if (temp.nodeName == NAME && !nameFound) {
                nameFound = true
                if (temp.textContent.isEmpty()) {
                    temp.textContent = USER_LIST_STRING
                }
            } else if (temp.nodeName == DEVICE_LIST_KEY && !deviceValueKeyFound) {
                deviceValueKeyFound = true
            } else {
                userList.removeChild(temp)
            }
        }

        if (!nameFound) {
            // this case only appeared when deviceValueKey was the only item
            // TODO: add this comment to the online documentation
            addNameToNewUserList(userList)
        }

        if (!deviceValueKeyFound) {
            // this case only appeared when name was the only item
            addDeviceValueKeyToNewUserList(userList)
        }
    }

    private fun addNameToNewUserList(userList: Node) {
        val deviceValueKeyClone = userList.firstChild.cloneNode(true)
        deviceValueKeyClone.textContent = USER_LIST_STRING
        userList.appendChild(deviceValueKeyClone)
        outcomeDocument.renameNode(deviceValueKeyClone, null, NAME)
    }

    private fun addDeviceValueKeyToNewUserList(userList: Node) {
        val nameClone = userList.firstChild.cloneNode(true)
        val deviceValueKey = userList.firstChild.cloneNode(true)
        userList.removeChild(userList.firstChild)

        deviceValueKey.textContent = UUID.randomUUID().toString()
        userList.appendChild(deviceValueKey)
        outcomeDocument.renameNode(deviceValueKey, null, DEVICE_LIST_KEY)

        userList.appendChild(nameClone)
    }

    private fun createInitialIndexNode(initialIndex: Node): Node {
        initialIndex.firstChild.textContent = "-1"
        outcomeDocument.renameNode(initialIndex, null, INITIAL_INDEX)
        return initialIndex
    }

    private fun buildNewUserList(
        userList: Element,
        name: Node,
        initialIndex: Node?,
        deviceValueKey: Node
    ) {
        val tempName = name.cloneNode(true)
        val tempDeviceValueKey = deviceValueKey.cloneNode(true)
        userList.removeChild(name)
        userList.removeChild(deviceValueKey)

        val firstChild = name.cloneNode(true)
        firstChild.removeChild(firstChild.firstChild)

        val secondChild = firstChild.cloneNode(true)
        firstChild.appendChild(secondChild)

        if (NodeOperatorExtension.getNodeByName(userList, INITIAL_INDEX) == null) {
            secondChild.appendChild(initialIndex)
        }
        if (NodeOperatorExtension.getNodeByName(userList, IS_LIST) == null) {
            val isListNode = name.cloneNode(true)
            isListNode.firstChild.textContent = "true"
            secondChild.appendChild(isListNode)
            outcomeDocument.renameNode(isListNode, null, IS_LIST)
        }
        secondChild.appendChild(tempDeviceValueKey)
        secondChild.appendChild(tempName)

        userList.appendChild(firstChild)
        outcomeDocument.renameNode(firstChild, null, USER_LIST_STRING)
        outcomeDocument.renameNode(secondChild, null, DEFAULT)
    }

/**
 * These 3 functions are necessary because with CATROID-851, there are no userListLists in
 * neither the Project nor the Sprite.
 * To still be able to interpret old programs, these functions move userVariables that are
 * lists into the userVariableLists of the program and of each Sprite.
 **/
    private fun moveUserListReferencesToVariableReferences() {
        try {
            moveProjectsLists()
            moveSpritesLists()
        } catch (nullPointerException: NullPointerException) {
            Log.d(
                javaClass.simpleName,
                "UserList interpretation for this project failed when moving local lists.",
                nullPointerException
            )
        }
    }

    private fun moveProjectsLists() {
        val program = outcomeDocument.firstChild
        val programUserLists = NodeOperatorExtension
            .getNodeByName(program, "programListOfLists") ?: return
        var userVariableList = NodeOperatorExtension
            .getNodeByName(program, "programVariableList")

        if (userVariableList == null) {
            userVariableList = programUserLists.cloneNode(true)
            program.appendChild(userVariableList)
            outcomeDocument.renameNode(userVariableList, null, "programVariableList")
        } else {
            for (i in 0 until programUserLists.childNodes.length) {
                userVariableList.appendChild(programUserLists.firstChild)
            }
        }
        program.removeChild(programUserLists)
    }

    /**
     * Is a bit different because we are only allowed to move those userLists into the
     * userVariableList that belong to this Sprite */
    private fun moveSpritesLists() {
        val scenes = NodeOperatorExtension
            .getNodeByName(outcomeDocument.firstChild, "scenes") ?: return

        for (i in 0 until scenes.childNodes.length) {
            val objectList = scenes.childNodes.item(i).lastChild
            for (j in 0 until objectList.childNodes.length) {
                moveSpritesList(objectList.childNodes.item(j))
            }
        }
    }

    private fun moveSpritesList(objectNode: Node) {
        correctCloneBricks(objectNode)

        val objectsUserListList = NodeOperatorExtension
            .getNodeByName(objectNode, USER_LISTS_STRING) ?: return
        val objectsUserVariableList = NodeOperatorExtension
            .getNodeByName(objectNode, USER_VARIABLES_STRING)
        if (objectsUserVariableList != null) {
            for (k in 0 until objectsUserListList.childNodes.length) {
                objectsUserVariableList.appendChild(objectsUserListList.firstChild)
            }
        }
        objectNode.removeChild(objectsUserListList)
    }

    private fun correctCloneBricks(objectNode: Node) {
        val scriptList = NodeOperatorExtension
            .getNodeByName(objectNode, "scriptList") ?: return
        for (k in 0 until scriptList.childNodes.length) {
            val script = scriptList.childNodes.item(k)
            val brickList = NodeOperatorExtension
                .getNodeByName(script, "brickList") ?: continue

            for (l in 0 until brickList.childNodes.length) {
                correctCloneBrick(brickList.childNodes.item(l))
            }
        }
    }

    private fun correctCloneBrick(brick: Node) {
        val brickType = brick.attributes.getNamedItem("type")
        if (brickType.textContent == "CloneBrick") {
            val objectToClone = NodeOperatorExtension
                .getNodeByName(brick, "objectToClone") ?: return
            val objectToClonesUserListList = NodeOperatorExtension
                .getNodeByName(objectToClone, USER_LISTS_STRING) ?: return
            val objectToClonesUserVariableList = NodeOperatorExtension
                .getNodeByName(objectToClone, USER_VARIABLES_STRING)

            if (objectToClonesUserVariableList != null) {
                for (m in 0 until objectToClonesUserListList.childNodes.length) {
                    objectToClonesUserVariableList.appendChild(objectToClonesUserListList.firstChild)
                }
            }
            objectToClone.removeChild(objectToClonesUserListList)
        }
    }
}
