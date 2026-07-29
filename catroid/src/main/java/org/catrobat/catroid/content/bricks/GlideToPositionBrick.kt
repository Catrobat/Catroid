/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.StringOption
import org.catrobat.catroid.formulaeditor.Formula
import org.koin.java.KoinJavaComponent

class GlideToPositionBrick(var destinationSprite: Sprite? = null) : FormulaBrick(), BrickSpinner
.OnItemSelectedListener<Sprite?> {
    private var spinnerSelection: Int = 0

    init {
        addAllowedBrickField(
            BrickField.DURATION_IN_SECONDS,
            R.id.brick_glide_to_position_edit_text_duration

        )
    }

    constructor(durationInMilliSecondsValue: Int) : this() {
        val durationInSeconds = durationInMilliSecondsValue / 1000.0
        setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, Formula(durationInSeconds))
    }

    override fun getViewResource(): Int {
        return R.layout.brick_glide_to_position
    }

    override fun getDefaultBrickField(): BrickField {
        return BrickField.DURATION_IN_SECONDS
    }

    override fun getView(context: Context?): View? {
        super.getView(context)
        val projectManager = KoinJavaComponent.get(ProjectManager::class.java)
        setSecondsLabel(view, BrickField.DURATION_IN_SECONDS)
        val items: MutableList<Nameable?> = ArrayList()
        items.add(StringOption(context!!.getString(R.string.brick_glide_to_random_position)))
        items.add(StringOption(context.getString(R.string.brick_glide_to_touch_position)))
        items.addAll(projectManager.currentlyEditedScene.spriteList)
        items.remove(projectManager.currentlyEditedScene.backgroundSprite)
        items.remove(projectManager.currentSprite)
        val spinner: BrickSpinner<Sprite?> = BrickSpinner<Sprite?>(
            R.id
                .brick_glide_to_position_spinner,
            view, items
        )
        spinner.setOnItemSelectedListener(this)

        if (spinnerSelection == BrickValues.GLIDE_TO_RANDOM_POSITION) {
            spinner.setSelection(0)
        }
        if (spinnerSelection == BrickValues.GLIDE_TO_TOUCH_POSITION) {
            spinner.setSelection(1)
        }
        if (spinnerSelection == BrickValues.GLIDE_TO_OTHER_SPRITE_POSITION) {
            spinner.setSelection(destinationSprite)
        }
        return view
    }

    override fun onNewOptionSelected(spinnerId: Int?) {
        //is needed
    }

    override fun onEditOptionSelected(spinnerId: Int?) {
        //is needed
    }

    override fun onStringOptionSelected(spinnerId: Int?, string: String) {
        val context = view.context

        if (string == context.getString(R.string.brick_glide_to_touch_position)) {
            spinnerSelection = BrickValues.GLIDE_TO_TOUCH_POSITION
            destinationSprite = null
        }

        if (string == context.getString(R.string.brick_glide_to_random_position)) {
            spinnerSelection = BrickValues.GLIDE_TO_RANDOM_POSITION
            destinationSprite = null
        }
    }

    override fun onItemSelected(spinnerId: Int?, item: Sprite?) {
        spinnerSelection = BrickValues.GLIDE_TO_OTHER_SPRITE_POSITION
        destinationSprite = item
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createGlideToPositionAction(
                sprite, destinationSprite, sequence,
                getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS), spinnerSelection
            )
        )
    }

    companion object {
        @Suppress("unused")
        private const val serialVersionUID = 1L
    }
}
