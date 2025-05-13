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
import android.widget.ArrayAdapter
import android.widget.Spinner
import org.catrobat.catroid.R
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.formulaeditor.Formula

class PlotThroughBrick() : FormulaBrick() {

    init {
        addAllowedBrickField(
            BrickField.X_POSITION,
            R.id.brick_plot_through_x1_edit_text
        )
        addAllowedBrickField(
            BrickField.Y_POSITION,
            R.id.brick_plot_through_y1_edit_text
        )
        addAllowedBrickField(
            BrickField.X_DESTINATION,
            R.id.brick_plot_through_x2_edit_text
        )
        addAllowedBrickField(
            BrickField.Y_DESTINATION,
            R.id.brick_plot_through_y2_edit_text
        )
    }

    constructor(x1: Int, y1: Int, x2: Int, y2: Int) : this(Formula(x1), Formula(y1), Formula(x2), Formula(y2))

    constructor(formula1: Formula, formula2: Formula, formula3: Formula, formula4: Formula) : this
        () {
        setFormulaWithBrickField(BrickField.X_POSITION, formula1)
        setFormulaWithBrickField(BrickField.Y_POSITION, formula2)
        setFormulaWithBrickField(BrickField.X_DESTINATION, formula3)
        setFormulaWithBrickField(BrickField.Y_DESTINATION, formula4)
    }

    override fun getViewResource(): Int {
        return R.layout.brick_plot_through
    }

    override fun getView(context: Context): View {
        super.getView(context)
        return view
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory?.createPlotThroughAction(sprite, sequence, getFormulaWithBrickField(BrickField.X_POSITION),
                                                          getFormulaWithBrickField(BrickField.Y_POSITION),
                                                          getFormulaWithBrickField(BrickField.X_DESTINATION),
                                                          getFormulaWithBrickField(BrickField.Y_DESTINATION)));
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}