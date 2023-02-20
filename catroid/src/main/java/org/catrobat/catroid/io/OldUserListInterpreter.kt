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
package org.catrobat.catroid.io

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import kotlin.collections.ArrayList

class OldUserListInterpreter(private val outcomeDocument: Document) {
    fun interpret(): Document {
        val userListNodes = getProgramsUserLists() ?: return outcomeDocument
        for (i in 0 until userListNodes.size) {
            val userList = userListNodes[i] as Element
            val isOldUserList1 = userList.childNodes.length == 2 &&
                userList.childNodes.item(0).nodeName.equals("deviceListKey") &&
                userList.childNodes.item(1).nodeName.equals("name")
            val isOldUserList2 = userList.childNodes.length == 3 &&
                userList.childNodes.item(0).nodeName.equals("deviceListKey") &&
                userList.childNodes.item(1).nodeName.equals("initialIndex") &&
                userList.childNodes.item(2).nodeName.equals("name")

            if (isOldUserList1 || isOldUserList2) {
                createNewUserLists(userList)
            }
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