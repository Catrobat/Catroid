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

class PlotArcBrick() : FormulaBrick() {
    private var direction: Directions

    enum class Directions {
        LEFT, RIGHT
    }

    init {
        direction = Directions.LEFT
        addAllowedBrickField(
            BrickField.SIZE,
            R.id.brick_plot_arc_edit_text1
        )
        addAllowedBrickField(
            BrickField.DEGREES,
            R.id.brick_plot_arc_edit_text2
        )
    }

    constructor(directionEnum: Directions, radius: Float, degrees: Float) : this(directionEnum,
                                                                               Formula
        (radius), Formula(degrees))

    constructor(directionEnum: Directions, formula1: Formula?, formula2: Formula?) : this() {
        direction = directionEnum
        setFormulaWithBrickField(BrickField.SIZE, formula1)
        setFormulaWithBrickField(BrickField.DEGREES, formula2)
    }

    override fun getViewResource(): Int {
        return R.layout.brick_plot_arc
    }

    override fun getView(context: Context): View {
        super.getView(context)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            context,
            R.array.brick_plot_arc_direction_spinner, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = view.findViewById<Spinner>(R.id.brick_plot_arc_spinner)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = AdapterViewOnItemSelectedListenerImpl { position: Int? ->
            direction = Directions.values()[position!!]
            Unit
        }
        spinner.setSelection(direction.ordinal)
        return view
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory?.createPlotArcAction(sprite, sequence, direction,
                                                      getFormulaWithBrickField(BrickField.SIZE),
                                                      getFormulaWithBrickField(BrickField.DEGREES)));
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}