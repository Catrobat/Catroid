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
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.formulaeditor.Formula

class TapForBrick() : VisualPlacementBrick() {
    constructor(xPositionValue: Int, yPositionValue: Int, durationInSecondsValue: Double) : this(
        Formula(xPositionValue),
        Formula(yPositionValue),
        Formula(durationInSecondsValue)
    )

    constructor(xPosition: Formula?, yPosition: Formula?, durationInSeconds: Formula?) : this() {
        setFormulaWithBrickField(BrickField.X_POSITION, xPosition)
        setFormulaWithBrickField(BrickField.Y_POSITION, yPosition)
        setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, durationInSeconds)
    }

    override fun getDefaultBrickField(): BrickField = BrickField.X_POSITION

    override fun getViewResource(): Int = R.layout.brick_tap_for

    override fun getView(context: Context): View {
        super.getView(context)
        setSecondsLabel(view, BrickField.DURATION_IN_SECONDS)
        return view
    }

    override fun addActionToSequence(
        sprite: Sprite,
        sequence: ScriptSequenceAction
    ) {
        sequence.addAction(
            sprite.actionFactory.createTapForAction(
                sprite, sequence,
                getFormulaWithBrickField(BrickField.X_POSITION),
                getFormulaWithBrickField(BrickField.Y_POSITION),
                getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)
            )
        )
    }

    override fun getXBrickField(): BrickField? = BrickField.X_POSITION

    override fun getYBrickField(): BrickField? = BrickField.Y_POSITION

    override fun getXEditTextId(): Int = R.id.brick_tap_for_edit_x

    override fun getYEditTextId(): Int = R.id.brick_tap_for_edit_y

    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_tap_for_edit_x)
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_tap_for_edit_y)
        addAllowedBrickField(BrickField.DURATION_IN_SECONDS, R.id.brick_tap_for_edit_duration)
    }
}
