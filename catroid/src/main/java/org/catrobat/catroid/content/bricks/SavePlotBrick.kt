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

package org.catrobat.catroid.content.bricks

import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.formulaeditor.Formula

class SavePlotBrick() : FormulaBrick() {
    constructor(value: String) : this(Formula(value))

    constructor(formula: Formula) : this() {
        setFormulaWithBrickField(BrickField.WRITE_FILENAME, formula)
    }

    init {
        addAllowedBrickField(
            BrickField.WRITE_FILENAME, R.id.brick_save_plot_edit_text
        )
    }

    override fun getViewResource(): Int = R.layout.brick_save_plot

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createSavePlotAction(
                sprite, sequence, getFormulaWithBrickField(BrickField.WRITE_FILENAME)
            )
        )
    }
}
