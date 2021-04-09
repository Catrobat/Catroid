/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.widget.TextView
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.ParameterizedData
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.utils.LoopUtil.checkLoopBrickForLoopDelay

class ParameterizedBrick : ListSelectorBrick(), CompositeBrick {
    private var loopBricks = mutableListOf<Brick>()
    private var endBrick = ParameterizedEndBrick(this)

    @Transient
    @XStreamOmitField
    var parameterizedData: ParameterizedData = ParameterizedData()

    override val selectorId: Int
        get() = R.id.brick_param_list_of_list_text

    override fun hasSecondaryList(): Boolean = false

    override fun getNestedBricks(): List<Brick> = loopBricks

    override fun getSecondaryNestedBricks(): List<Brick>? = null

    fun addBrick(brick: Brick): Boolean = loopBricks.add(brick)

    override fun updateSelectorText() {
        super.updateSelectorText()
        val listSize = userLists.size
        val firstLabel = view.findViewById<TextView>(R.id.brick_param_first_label)
        firstLabel.text = view.resources.getQuantityString(
            R.plurals.brick_parameterized_foreach_plural,
            listSize, listSize
        )
        val secondLabel = view.findViewById<TextView>(R.id.brick_param_second_label)
        secondLabel.text = view.resources.getQuantityString(
            R.plurals.brick_parameterized_stored_plural,
            listSize, listSize
        )
    }

    override fun setCommentedOut(commentedOut: Boolean) {
        super.setCommentedOut(commentedOut)
        for (brick in loopBricks) {
            brick.isCommentedOut = commentedOut
        }
        endBrick.isCommentedOut = commentedOut
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Brick {
        val clone = super.clone() as ParameterizedBrick
        clone.endBrick = ParameterizedEndBrick(clone)
        clone.loopBricks = mutableListOf()
        clone.parameterizedData = ParameterizedData()
        for (brick in loopBricks) {
            clone.addBrick(brick.clone())
        }
        return clone
    }

    override fun consistsOfMultipleParts(): Boolean = true

    override fun getAllParts(): List<Brick> {
        val bricks = mutableListOf<Brick>()
        bricks.add(this)
        bricks.add(endBrick)
        return bricks
    }

    override fun addToFlatList(bricks: MutableList<Brick>) {
        super.addToFlatList(bricks)
        for (brick in loopBricks) {
            brick.addToFlatList(bricks)
        }
        bricks.add(endBrick)
    }

    override fun setParent(parent: Brick) {
        super.setParent(parent)
        for (brick in loopBricks) {
            brick.parent = this
        }
        endBrick.parent = this
    }

    override fun getDragAndDropTargetList(): List<Brick> = loopBricks

    override fun removeChild(brick: Brick): Boolean {
        if (loopBricks.remove(brick)) {
            return true
        }

        for (childBrick in loopBricks) {
            if (childBrick.removeChild(brick)) {
                return true
            }
        }
        return false
    }

    override fun getViewResource(): Int = R.layout.brick_parameterized_input

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        val repeatSequence =
            ActionFactory.createScriptSequenceAction(sequence.script) as ScriptSequenceAction
        val isLoopDelay = checkLoopBrickForLoopDelay(this, sequence.script)
        loopBricks.filterNot { brick -> brick.isCommentedOut }.forEach {
            it.addActionToSequence(sprite, repeatSequence)
        }
        endBrick.addActionToSequence(sprite, repeatSequence)
        parameterizedData?.reset()

        sequence.addAction(
            sprite.actionFactory.createRepeatParameterizedAction(
                sprite, parameterizedData,
                createLinkedPair(), positionInformation, repeatSequence, isLoopDelay
            )
        )
    }

    override fun addRequiredResources(requiredResourcesSet: ResourcesSet) {
        super.addRequiredResources(requiredResourcesSet)
        for (brick in loopBricks) {
            brick.addRequiredResources(requiredResourcesSet)
        }
    }

    override fun onUserListSelected(userLists: List<UserList>) {
        super.onUserListSelected(userLists)
        createLinkedVariables()
    }

    private fun createLinkedVariables() {
        val projectManager = ProjectManager.getInstance()
        val currentProject = projectManager.currentProject
        val currentSprite = projectManager.currentSprite
        val globalLists = currentProject.userLists ?: emptyList()
        val globalVariables = currentProject.userVariables ?: emptyList()
        val localVariables = currentSprite?.userVariables ?: emptyList()

        userLists.forEach { currentList ->
            if (globalVariables.none { it.name == currentList.name } &&
                localVariables.none { it.name == currentList.name }) {
                if (globalLists.contains(currentList)) {
                    currentProject.addUserVariable(UserVariable(currentList.name))
                } else {
                    currentSprite.addUserVariable(UserVariable(currentList.name))
                }
            }
        }
    }

    private fun createLinkedPair(): List<Pair<UserList, UserVariable>> {
        val result = mutableListOf<Pair<UserList, UserVariable>>()
        val projectManager = ProjectManager.getInstance()
        val currentProject = projectManager.currentProject
        val currentSprite = projectManager.currentSprite

        userLists.forEach { currentList ->
            val variable = currentProject?.getUserVariable(currentList.name)
                ?: currentSprite?.getUserVariable(currentList.name) ?: return@forEach

            result.add(Pair(currentList, variable))
        }

        return result
    }
}
