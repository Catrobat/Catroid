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
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.formulaeditor.Formula

class ReadVariableFromFileBrick constructor() : UserVariableBrickWithFormula() {
    constructor(value: String) : this(Formula(value))

    constructor(formula: Formula) : this() {
        setFormulaWithBrickField(BrickField.READ_FILENAME, formula)
    }

    init {
        addAllowedBrickField(BrickField.READ_FILENAME, R.id.brick_read_variable_from_file_edit_text)
    }

    private var spinnerSelectionID: Int = KEEP

    companion object Mode {
        private const val KEEP = 0
        private const val DELETE = 1
    }

    override fun getViewResource(): Int = R.layout.brick_read_variable_from_file

    override fun getSpinnerId(): Int = R.id.brick_read_variable_from_file_spinner_variable

    override fun getView(context: Context): View {
        super.getView(context)
        view.findViewById<Spinner>(R.id.brick_read_variable_from_file_spinner_mode).apply {
            adapter = createArrayAdapter(context)
            onItemSelectedListener = AdapterViewOnItemSelectedListenerImpl { position ->
                spinnerSelectionID = position
            }
            setSelection(spinnerSelectionID)
        }
        return view
    }

    private fun createArrayAdapter(context: Context): ArrayAdapter<String?> {
        val spinnerValues = arrayOfNulls<String>(2)
        spinnerValues[KEEP] = context.getString(R.string.brick_read_variable_from_file_keep)
        spinnerValues[DELETE] = context.getString(R.string.brick_read_variable_from_file_delete)

        ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerValues).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return this
        }
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        userVariable?.name?.let {
            sequence.addAction(
                sprite.actionFactory.createReadVariableFromFileAction(
                    sprite, sequence,
                    getFormulaWithBrickField(BrickField.READ_FILENAME),
                    userVariable,
                    spinnerSelectionID == DELETE
                )
            )
        }
    }

    override fun addRequiredResources(requiredResourcesSet: ResourcesSet) {
        requiredResourcesSet.add(STORAGE_READ)
        super.addRequiredResources(requiredResourcesSet)
    }
}
