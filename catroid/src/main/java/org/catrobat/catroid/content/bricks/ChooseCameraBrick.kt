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
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet

class ChooseCameraBrick(private var spinnerSelectionFRONT: Boolean = true) : BrickBaseType() {

    override fun getViewResource() = R.layout.brick_choose_camera

    override fun getView(context: Context): View {
        super.getView(context)
        view.findViewById<Spinner>(R.id.brick_choose_camera_spinner).apply {
            adapter = createArrayAdapter(context)
            onItemSelectedListener = AdapterViewOnItemSelectedListenerImpl { position ->
                spinnerSelectionFRONT = position == 1
            }
            setSelection(if (spinnerSelectionFRONT) 1 else 0)
        }
        return view
    }

    private fun createArrayAdapter(context: Context): ArrayAdapter<String?> {
        val spinnerValues = arrayOf(
            context.getString(R.string.choose_camera_back),
            context.getString(R.string.choose_camera_front)
        )
        val spinnerAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerValues)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return spinnerAdapter
    }

    override fun addRequiredResources(requiredResourcesSet: ResourcesSet) {
        if (spinnerSelectionFRONT) {
            requiredResourcesSet.add(CAMERA_FRONT)
        } else {
            requiredResourcesSet.add(CAMERA_BACK)
        }
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        if (spinnerSelectionFRONT) {
            sequence.addAction(sprite.actionFactory.createSetFrontCameraAction())
        } else {
            sequence.addAction(sprite.actionFactory.createSetBackCameraAction())
        }
    }
}
