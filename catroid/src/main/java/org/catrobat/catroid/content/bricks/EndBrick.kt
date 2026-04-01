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

package org.catrobat.catroid.content.bricks

import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import java.util.UUID

class EndBrick @JvmOverloads constructor(
    endBrickParent: BrickBaseType,
    private val viewResource: Int = R.layout.brick_loop_end
) : BrickBaseType() {

    init {
        parent = endBrickParent
    }

    override fun getViewResource(): Int = viewResource

    override fun addActionToSequence(sprite: Sprite?, sequence: ScriptSequenceAction?) {
        // not needed for EndBricks, in case you need it for one, please be aware that changes
        // here will affect multiple composite bricks
    }

    override fun isCommentedOut(): Boolean = parent.isCommentedOut

    override fun consistsOfMultipleParts(): Boolean = true

    override fun getAllParts(): List<Brick?>? = parent.allParts

    override fun addToFlatList(bricks: List<Brick?>?) {
        parent.addToFlatList(bricks)
    }

    override fun getDragAndDropTargetList(): List<Brick?>? =
        parent.parent.dragAndDropTargetList

    override fun getPositionInDragAndDropTargetList(): Int =
        parent.parent.dragAndDropTargetList.indexOf(parent)

    override fun getBrickID(): UUID? = parent.brickID
}
