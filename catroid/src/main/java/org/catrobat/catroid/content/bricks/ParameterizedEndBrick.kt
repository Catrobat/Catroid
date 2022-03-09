/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

class ParameterizedEndBrick() : UserListBrick() {
    init {
        addAllowedBrickField(Brick.BrickField.ASSERT_LOOP_ACTUAL, R.id.brick_param_assert_text)
    }

    constructor(parent: ParameterizedBrick) : this() {
        this.parent = parent
    }

    override fun consistsOfMultipleParts(): Boolean = true

    override fun getAllParts(): List<Brick> = parent.allParts

    override fun getDragAndDropTargetList(): List<Brick> = parent.parent.dragAndDropTargetList

    override fun getPositionInDragAndDropTargetList(): Int = parent.parent.dragAndDropTargetList.indexOf(parent)

    override fun getViewResource(): Int = R.layout.brick_parameterized_assert

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createParameterizedAssertAction(
                sprite, sequence, getFormulaWithBrickField(Brick.BrickField.ASSERT_LOOP_ACTUAL),
                userList,
                (parent as ParameterizedBrick).parameterizedData, positionInformation
            )
        )
    }

    override fun getBrickID(): UUID = parent.brickID

    override fun getSpinnerId(): Int = R.id.brick_param_expected_list
}
