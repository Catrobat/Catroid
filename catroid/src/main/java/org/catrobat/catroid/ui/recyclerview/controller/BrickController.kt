/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick

class BrickController {
    companion object {
        @JvmField
        val TAG = BrickController::class.java.simpleName
    }

    fun copy(bricksToCopy: List<Brick>, parent: Sprite) {
        for (brick in bricksToCopy) {
            val script = brick.script
            if (brick is ScriptBrick) {
                try {
                    parent.addScript(script.clone())
                } catch (e: CloneNotSupportedException) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
                continue
            }
            if (!bricksToCopy.contains(brick.parent)) {
                val copyBrick = try {
                    brick.clone()
                } catch (e: CloneNotSupportedException) {
                    Log.e(TAG, Log.getStackTraceString(e))
                    continue
                }
                if (copyBrick is CompositeBrick) {
                    removeUnselectedBricksInCompositeBricks(copyBrick, brick as CompositeBrick)
                }
                script.addBrick(copyBrick)
            }
        }
    }

    private fun removeUnselectedBricksInCompositeBricks(
        copyBrick: CompositeBrick,
        referenceBrick: CompositeBrick
    ) {
        var copyCounter = 0
        for (i in referenceBrick.nestedBricks.indices) {
            if (referenceBrick.nestedBricks[i].checkBox?.isChecked == false) {
                copyBrick.nestedBricks.removeAt(copyCounter)
                continue
            }
            if (referenceBrick.nestedBricks[i] is CompositeBrick) {
                removeUnselectedBricksInCompositeBricks(
                    copyBrick.nestedBricks[copyCounter] as CompositeBrick,
                    referenceBrick.nestedBricks[i] as CompositeBrick
                )
            }
            copyCounter++
        }
        copyCounter = 0
        if (referenceBrick.hasSecondaryList()) {
            for (i in referenceBrick.secondaryNestedBricks.indices) {
                if (referenceBrick.secondaryNestedBricks[i].checkBox?.isChecked == false) {
                    copyBrick.secondaryNestedBricks.removeAt(copyCounter)
                    continue
                }
                if (referenceBrick.secondaryNestedBricks[i] is CompositeBrick) {
                    removeUnselectedBricksInCompositeBricks(
                        copyBrick.secondaryNestedBricks[copyCounter] as CompositeBrick,
                        referenceBrick.secondaryNestedBricks[i] as CompositeBrick
                    )
                }
                copyCounter++
            }
        }
    }

    fun delete(bricksToDelete: List<Brick>, parent: Sprite) {
        for (brick in bricksToDelete) {
            val script = brick.script
            if (brick is UserDefinedReceiverBrick) {
                val userDefinedBrick = brick.userDefinedBrick
                parent.removeUserDefinedBrick(userDefinedBrick)
            }
            if (brick is ScriptBrick) {
                parent.removeScript(script)
                continue
            }
            if (!bricksToDelete.contains(brick.parent)) {
                if (brick is CompositeBrick) {
                    val unselectedBricks = getAllUnSelectedChildBricksOfCompositeBrick(brick)
                    addUnselectedBricksToNextUnselectedParentBrick(unselectedBricks, brick)
                }
                script.removeBrick(brick)
            }
        }
    }

    private fun getAllUnSelectedChildBricksOfCompositeBrick(compositeBrick: CompositeBrick): List<Brick> {
        val unselectedBricks: MutableList<Brick> = ArrayList()
        for (childBrick in compositeBrick.nestedBricks) {
            if (!childBrick.checkBox.isChecked) {
                unselectedBricks.add(childBrick)
            }
            if (childBrick is CompositeBrick) {
                unselectedBricks.addAll(getAllUnSelectedChildBricksOfCompositeBrick(childBrick))
            }
        }
        if (compositeBrick.hasSecondaryList()) {
            for (childBrick in compositeBrick.secondaryNestedBricks) {
                if (!childBrick.checkBox.isChecked) {
                    unselectedBricks.add(childBrick)
                }
                if (childBrick is CompositeBrick) {
                    unselectedBricks.addAll(getAllUnSelectedChildBricksOfCompositeBrick(childBrick))
                }
            }
        }
        return unselectedBricks
    }

    private fun addUnselectedBricksToNextUnselectedParentBrick(
        unselectedBricks: List<Brick>,
        lastSelectedBrick: Brick
    ) {
        when (val parentBrick = lastSelectedBrick.parent) {
            is ScriptBrick -> {
                val pos = parentBrick.getScript().brickList.indexOf(lastSelectedBrick)
                parentBrick.getScript().brickList.addAll(
                    pos, unselectedBricks
                )
            }
            is CompositeBrick -> {
                val pos = parentBrick.nestedBricks.indexOf(lastSelectedBrick)
                parentBrick.nestedBricks.addAll(
                    pos, unselectedBricks
                )
            }
            is IfLogicBeginBrick.ElseBrick -> {
                val pos =
                    (parentBrick.getParent() as IfLogicBeginBrick).secondaryNestedBricks.indexOf(
                        lastSelectedBrick
                    )
                (parentBrick.getParent() as IfLogicBeginBrick).secondaryNestedBricks.addAll(
                    pos, unselectedBricks
                )
            }
            is PhiroIfLogicBeginBrick.ElseBrick -> {
                val pos =
                    (parentBrick.getParent() as PhiroIfLogicBeginBrick).secondaryNestedBricks.indexOf(
                        lastSelectedBrick
                    )
                (parentBrick.getParent() as PhiroIfLogicBeginBrick).secondaryNestedBricks.addAll(
                    pos, unselectedBricks
                )
            }
        }
    }
}
