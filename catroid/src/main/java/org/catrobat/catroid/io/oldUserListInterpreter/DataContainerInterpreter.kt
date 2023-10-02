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

package org.catrobat.catroid.io.oldUserListInterpreter

import org.catrobat.catroid.io.NodeOperatorExtension
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

const val MULTIPLAYER_VARIABLE_LIST_STRING = "programMultiplayerVariableList"
const val OBJECT_REFERENCE_INDEX = 6

class DataContainerInterpreter(
    private val outcomeDocument: Document
) {
    fun interpret() {
        val program = outcomeDocument.firstChild
        val scenes = NodeOperatorExtension.getNodeByName(program, "scenes") ?: return

        for (i in 0 until scenes.childNodes.length) {
            val scene = scenes.childNodes.item(i)
            val dataList = NodeOperatorExtension.getNodeByName(scene, "data") ?: return
            val objectList =
                NodeOperatorExtension.getNodeByName(scene, "objectList") ?: return
            val userVariableReferences = getElementsFromDataList(dataList)

            userVariableReferences.forEach { userVariableReference ->
                moveVariablesFromDataContainerIntoLocalVariableList(userVariableReference, objectList)
            }
        }

        if (NodeOperatorExtension.getNodeByName(program, MULTIPLAYER_VARIABLE_LIST_STRING) == null) {
            addMultiplayerVariableList()
        }
    }

    private fun moveVariablesFromDataContainerIntoLocalVariableList(
        userVariableReference: Node,
        objectList: Node
    ) {
        val objectIndex = correctDataContainerVariableReference(userVariableReference as Element)
        val spriteObject = objectList.childNodes.item(objectIndex)
        if (spriteObject.attributes.getNamedItem("type").nodeValue != "Sprite") {
            throw OldUserListInterpretationException("correctOldListFor")
        }
        var localVariableList =
            NodeOperatorExtension.getNodeByName(spriteObject, USER_VARIABLES_STRING)
        if (localVariableList == null) {
            localVariableList = createLocalVariableList(
                spriteObject.firstChild.cloneNode(true), spriteObject
            )
        }
        localVariableList.appendChild(userVariableReference)
    }

    private fun correctDataContainerVariableReference(userVariableReference: Element): Int {
        val reference = userVariableReference.getAttribute(REFERENCE)
        val referenceParts = reference.split("/").toMutableList()
        val objectIndexString = referenceParts[OBJECT_REFERENCE_INDEX].filter { it.isDigit() }
        var objectIndex = 0
        if (objectIndexString != "") {
            objectIndex = objectIndexString.toInt() - 1
        }
        referenceParts.removeAt(0)
        referenceParts.removeAt(0)
        referenceParts.removeAt(0)
        referenceParts.removeAt(2)
        referenceParts.removeAt(2)
        userVariableReference.removeAttribute(REFERENCE)
        userVariableReference.setAttribute(REFERENCE, referenceParts.joinToString("/"))
        return objectIndex
    }

    private fun createLocalVariableList(localVariableList: Node, spriteObject: Node): Node {
        val childNodes = localVariableList.childNodes
        repeat(childNodes.length) {
            localVariableList.removeChild(childNodes.item(0))
        }
        outcomeDocument.renameNode(localVariableList, null, USER_VARIABLES_STRING)
        insertLocalVariableListInRightOrder(spriteObject, localVariableList)
        return localVariableList
    }

    private fun insertLocalVariableListInRightOrder(spriteObject: Node, localVariableList: Node) {
        val userDefinedBrickListNode =
            NodeOperatorExtension.getNodeByName(spriteObject, "userDefinedBrickList")
        if (userDefinedBrickListNode != null) {
            spriteObject.removeChild(userDefinedBrickListNode)
            spriteObject.appendChild(localVariableList)
            spriteObject.appendChild(userDefinedBrickListNode)
        } else {
            spriteObject.appendChild(localVariableList)
        }
    }

    private fun getElementsFromDataList(dataList: Node): ArrayList<Node> {
        val userVariableReferences = ArrayList<Node>()

        for (i in 0 until dataList.childNodes.length) {
            val dataListElement = dataList.childNodes.item(i)

            for (j in 0 until dataListElement.childNodes.length) {
                val entry = dataListElement.childNodes.item(j)
                val list = NodeOperatorExtension.getNodeByName(entry, "list") ?: continue

                for (k in 0 until list.childNodes.length) {
                    userVariableReferences.add(list.childNodes.item(k))
                }
            }
        }
        return userVariableReferences
    }

    private fun addMultiplayerVariableList() {
        val program = outcomeDocument.firstChild
        val multiplayerVariableList = program.childNodes
            .item(program.childNodes.length - 1).cloneNode(true)
        repeat(multiplayerVariableList.childNodes.length) {
            multiplayerVariableList.removeChild(multiplayerVariableList.firstChild)
        }
        outcomeDocument.renameNode(
            multiplayerVariableList, null, MULTIPLAYER_VARIABLE_LIST_STRING
        )
        program.appendChild(multiplayerVariableList)
    }
}
