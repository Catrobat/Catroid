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
import kotlin.collections.ArrayList

class OldUserListInterpreter(private val outcomeDocument: Document) {
    fun interpret(): Document {
        val userListNodes = getProgramsUserLists() ?: return outcomeDocument
        userListNodes.filterNot { isNewUserList(it) }
            .forEach { oldUserList ->
                if (oldUserList.childNodes.length !in 1..4) {
                    throw OldUserListInterpretationException(
                        "unlikely userList in " + outcomeDocument.documentURI + " found"
                    )
                }
                createNewUserLists(oldUserList as Element)
            }
        return outcomeDocument
    }

    private fun getProgramsUserLists(): ArrayList<Node>? {
        val userListWithNewFunction = outcomeDocument.getElementsByTagName("userList")
        val userListList = ArrayList<Node>()
        for (i in 0 until userListWithNewFunction.length) {
            if (userListWithNewFunction.item(i).childNodes.length != 0) {
                userListList.add(userListWithNewFunction.item(i))
            }
        }

        return if (userListList.isEmpty()) null else userListList
    }

    private fun isNewUserList(userList: Node): Boolean {
        if (listOf("name", "deviceValueKey", "initialIndex", "userVariableEntry").all {
                NodeOperatorExtension.getNodeByName(userList, it) != null
        } || NodeOperatorExtension.getNodeByName(userList, "userVariable") != null) {
            return true
        }
        return false
    }

    private fun createNewUserLists(userList: Element) {
        userList.setAttribute("class", "userList")
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

            deviceValueKey.textContent = "11111111-1111-1111-1111-111111111111"
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

    private fun buildNewUserList(userList: Element, name: Node, initialIndex: Node?, deviceValueKey: Node) {
        val tempName = name.cloneNode(true)
        userList.removeChild(name)
        if (NodeOperatorExtension.getNodeByName(userList, "initialIndex") == null) {
            userList.appendChild(initialIndex)
        }
        userList.appendChild(tempName)
        userList.appendChild(createUserVariableEntry(
            deviceValueKey.cloneNode(true) as Element, deviceValueKey, name
        ))
    }

    private fun createUserVariableEntry(
        userVariableEntry: Element, deviceValueKey: Node, name: Node
    ): Node {
        val attributes = userVariableEntry.attributes
        while (attributes.length != 0) {
            attributes.removeNamedItem(
                attributes.getNamedItem(attributes.item(0).nodeName).toString()
            )
        }
        userVariableEntry.removeChild(userVariableEntry.firstChild)
        outcomeDocument.renameNode(userVariableEntry, null, "userVariableEntry")

        userVariableEntry.appendChild(createIsListNode(deviceValueKey.cloneNode(true)))
        userVariableEntry.appendChild(createUserVariableEntriesNode(name.cloneNode(true)))
        return userVariableEntry
    }

    private fun createIsListNode(isListChild: Node): Node {
        isListChild.firstChild.textContent = "true"
        outcomeDocument.renameNode(isListChild, null, "isList")
        return isListChild
    }

    private fun createUserVariableEntriesNode(userVariableEntriesChild: Node): Node {
        userVariableEntriesChild.removeChild(userVariableEntriesChild.firstChild)
        outcomeDocument.renameNode(userVariableEntriesChild, null, "userVariableEntries")
        return userVariableEntriesChild
    }
}