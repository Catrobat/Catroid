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

package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenCollideWithScript
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.StringOption
import org.catrobat.catroid.formulaeditor.Formula

class WhenCollideWithBrick(private var script: WhenCollideWithScript) : BrickBaseType(),
    BrickSpinner
    .OnItemSelectedListener<Sprite>,
    ScriptBrick {

    private var spinner: BrickSpinner<Sprite>? = null

    constructor() : this(WhenCollideWithScript())

    init {
        script.scriptBrick = this
        commentedOut = script.isCommentedOut
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Brick {
        val clone = super.clone() as WhenCollideWithBrick
        clone.script = script.clone() as WhenCollideWithScript
        clone.script.scriptBrick = clone
        clone.spinner = null
        return clone
    }

    override fun getViewResource() = R.layout.brick_when_you_collide_with

    override fun getView(context: Context): View? {
        super.getView(context)
        val items: MutableList<Nameable> = ArrayList()
        items.add(StringOption(context.getString(R.string.touches_finger)))
        items.add(StringOption(context.getString(R.string.touches_edge)))
        items.addAll(ProjectManager.getInstance().currentlyEditedScene.spriteList)
        spinner = BrickSpinner(R.id.brick_when_you_collide_with_spinner, view, items)
        spinner!!.setOnItemSelectedListener(this)
        spinner!!.setSelection(script.getSpriteToCollideWithName())
        return view
    }

    override fun getPositionInScript() = -1

    override fun addToFlatList(bricks: List<Brick?>?) {
        super.addToFlatList(bricks)
        for (brick in getScript().brickList) {
            brick.addToFlatList(bricks)
        }
    }

    override fun onNewOptionSelected(spinnerId: Int?) = Unit

    override fun onEditOptionSelected(spinnerId: Int?) = Unit

    override fun onStringOptionSelected(spinnerId: Int?, string: String?) {
        script.setSpriteToCollideWithName(string)
    }

    override fun onItemSelected(spinnerId: Int?, item: Sprite?) {
        script.setSpriteToCollideWith(item)
    }

    override fun getDragAndDropTargetList(): List<Brick?>? = getScript().brickList

    override fun getPositionInDragAndDropTargetList() = -1

    override fun getScript(): Script = script

    override fun setCommentedOut(commentedOut: Boolean) {
        super.setCommentedOut(commentedOut)
        getScript().isCommentedOut = commentedOut
    }

    override fun addActionToSequence(sprite: Sprite?, sequence: ScriptSequenceAction?) = Unit

    @CallSuper
    override fun addRequiredResources(requiredResourcesSet: ResourcesSet?) {
        script.getFormula()?.addRequiredResources(requiredResourcesSet)
    }

    fun getFormula(): Formula? = script.getFormula()
}
