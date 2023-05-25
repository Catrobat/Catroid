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

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.UUID
import kotlin.collections.ArrayList

class OldUserListInterpreter(private val outcomeDocument: Document) {
    fun interpret(): Document {
        moveUserListReferencesToVariableReferences()
        val userListNodes = getProgramsUserLists() ?: return outcomeDocument

        userListNodes.filterNot { isNewUserList(it) }
            .forEach { oldUserList ->
                if (oldUserList.childNodes.length !in 1..4) {
                    throw OldUserListInterpretationException(
                        "unlikely userList in " + outcomeDocument.documentURI + " found"
                    )
                }
                // TODO: make better error handling
                try {
                    createNewUserLists(oldUserList as Element)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        return outcomeDocument
    }

    private fun getProgramsUserLists(): ArrayList<Node>? {
        val userListWithNewFunction = outcomeDocument.getElementsByTagName("userList")
        val userListList = ArrayList<Node>()
        for (i in 0 until userListWithNewFunction.length) {
            val userList = userListWithNewFunction.item(i)
            if (userList.childNodes.length != 0) {
                userListList.add(userList)
            } else if ((userList as Element).hasAttribute("reference")) {
                updateUserListReference(userList)
            }
            outcomeDocument.renameNode(userList, null, "userVariable")
        }
        updateAllUserListReferences()
        return if (userListList.isEmpty()) null else userListList
    }

    private fun updateAllUserListReferences()
    {
        val userDataList = outcomeDocument.getElementsByTagName("userData")
        for (i in 0 until userDataList.length) {
            updateUserListReference(userDataList.item(i) as Element)
        }
    }

    private fun updateUserListReference(userList: Element) {
        val reference = userList.getAttribute("reference")
        val referenceParts = reference.split("/").toMutableList()
        if (referenceParts.isNotEmpty() && referenceParts.last() == "userList") {
            referenceParts[referenceParts.size - 1] = "userVariable"
            userList.removeAttribute("reference")
            userList.setAttribute("reference", referenceParts.joinToString("/"))
        }
    }

    private fun isNewUserList(userList: Node): Boolean {
        if (listOf("name", "deviceValueKey", "initialIndex", "isList").all {
                NodeOperatorExtension.getNodeByName(userList, it) != null
            }) {
            if (NodeOperatorExtension.getNodeByName(userList, "value") == null) {
                val value = userList.firstChild.cloneNode(true)
                value.firstChild.textContent = ""
                userList.appendChild(value)
                outcomeDocument.renameNode(value, null, "value")
            }
            return true
        }

        if (NodeOperatorExtension.getNodeByName(userList, "userVariable") != null ||
            NodeOperatorExtension.getNodeByName(userList, "userList") != null ||
            NodeOperatorExtension.getNodeByName(userList, "default") != null
        ) {
            return isNewUserList(userList.firstChild)
        }

        return false
    }

    private fun createNewUserLists(userList: Element) {
        userList.setAttribute("type", "UserVariable")
        userList.setAttribute("serialization", "custom")

        clearUserListNode(userList)
        val name: Node = NodeOperatorExtension.getNodeByName(userList, "name")
            ?: return
        val deviceValueKey: Node = NodeOperatorExtension.getNodeByName(
            userList, "deviceListKey"
        ) ?: return
        outcomeDocument.renameNode(deviceValueKey, null, "deviceValueKey")

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
            if (temp.nodeName == "name" && !nameFound) {
                nameFound = true
                if (temp.textContent.isEmpty()) {
                    temp.textContent = "userList"
                }
            } else if (temp.nodeName == "deviceListKey" && !deviceValueKeyFound) {
                deviceValueKeyFound = true
            } else {
                userList.removeChild(temp)
            }
        }

        if (!nameFound) {
            // this case only appeared when deviceValueKey was the only item
            val deviceValueKeyClone = userList.firstChild.cloneNode(true)
            deviceValueKeyClone.textContent = "userList"
            userList.appendChild(deviceValueKeyClone)
            outcomeDocument.renameNode(deviceValueKeyClone, null, "name")
        }

        if (!deviceValueKeyFound) {
            // this case only appeared when name was the only item
            val nameClone = userList.firstChild.cloneNode(true)
            val deviceValueKey = userList.firstChild.cloneNode(true)
            userList.removeChild(userList.firstChild)

            deviceValueKey.textContent = UUID.randomUUID().toString()
            userList.appendChild(deviceValueKey)
            outcomeDocument.renameNode(deviceValueKey, null, "deviceListKey")

            userList.appendChild(nameClone)
        }
    }

    private fun createInitialIndexNode(initialIndex: Node): Node {
        initialIndex.firstChild.textContent = "-1"
        outcomeDocument.renameNode(initialIndex, null, "initialIndex")
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

        if (NodeOperatorExtension.getNodeByName(userList, "initialIndex") == null) {
            secondChild.appendChild(initialIndex)
        }
        if (NodeOperatorExtension.getNodeByName(userList, "isList") == null) {
            val isListNode = name.cloneNode(true)
            isListNode.firstChild.textContent = "true"
            secondChild.appendChild(isListNode)
            outcomeDocument.renameNode(isListNode, null, "isList")
        }
        secondChild.appendChild(tempDeviceValueKey)
        secondChild.appendChild(tempName)

        userList.appendChild(firstChild)
        outcomeDocument.renameNode(firstChild, null, "userList")
        outcomeDocument.renameNode(secondChild, null, "default")
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
        } catch (e: Exception) {
            e.printStackTrace()
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
        val scenes = NodeOperatorExtension.getNodeByName(outcomeDocument.firstChild, "scenes")
            ?: return

        for (i in 0 until scenes.childNodes.length) {
            val objectList = scenes.childNodes.item(i).lastChild
            for (j in 0 until objectList.childNodes.length) {
                val objectNode = objectList.childNodes.item(j)
                correctCloneBrick(objectNode)

                val objectsUserListList = NodeOperatorExtension
                    .getNodeByName(objectNode, "userLists") ?: continue
                val objectsUserVariableList = NodeOperatorExtension
                    .getNodeByName(objectNode, "userVariables")
                if (objectsUserVariableList != null) {
                    for (k in 0 until objectsUserListList.childNodes.length) {
                        objectsUserVariableList.appendChild(objectsUserListList.firstChild)
                    }
                }
                objectNode.removeChild(objectsUserListList)
            }
        }
    }

    private fun correctCloneBrick(objectNode: Node) {
        val scriptList = NodeOperatorExtension
            .getNodeByName(objectNode, "scriptList") ?: return
        for (k in 0 until scriptList.childNodes.length) {
            val script = scriptList.childNodes.item(k)
            val brickList = NodeOperatorExtension
                .getNodeByName(script, "brickList") ?: continue
            for (l in 0 until brickList.childNodes.length) {
                val brick = brickList.childNodes.item(l)
                val brickType = brick.attributes.getNamedItem("type")
                if (brickType.textContent == "CloneBrick") {
                    val objectToClone = NodeOperatorExtension
                        .getNodeByName(brick, "objectToClone") ?: continue
                    val objectToClonesUserListList = NodeOperatorExtension
                        .getNodeByName(objectToClone, "userLists") ?: continue
                    val objectToClonesUserVariableList = NodeOperatorExtension
                        .getNodeByName(objectToClone, "userVariables")
                    if (objectToClonesUserVariableList != null) {
                        for (m in 0 until objectToClonesUserListList.childNodes.length) {
                            objectToClonesUserVariableList
                                .appendChild(objectToClonesUserListList.firstChild)
                        }
                    }
                    objectToClone.removeChild(objectToClonesUserListList)
                }
            }
        }
    }
}