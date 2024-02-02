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
import org.w3c.dom.Node

object NodeOperatorExtension {
    @JvmStatic
    fun getChildNodeByName(node: Node?, nodeName: String): Node? {
        if (node == null) return null
        val nodeList = node.childNodes
        for (i in 0 until nodeList.length) {
            if (nodeList.item(i).nodeName == nodeName) {
                return nodeList.item(i)
            }
        }
        return null
    }

    @JvmStatic
    fun createNode(outcomeDocument: Document, clonedNode: Node, nodeName: String): Node {
        val newNode = clonedNode.cloneNode(true)
        repeat(newNode.childNodes.length) {
            newNode.removeChild(newNode.firstChild)
        }
        outcomeDocument.renameNode(newNode, null, nodeName)
        return newNode
    }
}
