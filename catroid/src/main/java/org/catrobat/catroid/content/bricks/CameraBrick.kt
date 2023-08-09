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
import org.catrobat.catroid.io.catlang.CatrobatLanguageBrick

@CatrobatLanguageBrick(command = "Turn")
class CameraBrick(private var spinnerSelectionON: Boolean = true) : BrickBaseType(), UpdateableSpinnerBrick {

    override fun getView(context: Context): View {
        super.getView(context)
        assignVideoSpinnerProperties()
        return view
    }

    private fun assignVideoSpinnerProperties() {
        view.findViewById<Spinner>(R.id.brick_video_spinner).apply {
            adapter = createArrayAdapter(context)
            onItemSelectedListener = AdapterViewOnItemSelectedListenerImpl { position ->
                spinnerSelectionON = position == 1
            }
            setSelection(if (spinnerSelectionON) 1 else 0)
        }
    }

    private fun createArrayAdapter(context: Context): ArrayAdapter<String?> {
        val spinnerValues = arrayOf(
            context.getString(R.string.video_brick_camera_off),
            context.getString(R.string.video_brick_camera_on)
        )
        val spinnerAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerValues)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return spinnerAdapter
    }

    override fun getViewResource(): Int = R.layout.brick_video

    override fun addRequiredResources(requiredResourcesSet: ResourcesSet) {
        requiredResourcesSet.add(VIDEO)
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(sprite.actionFactory.createUpdateCameraPreviewAction(spinnerSelectionON))
    }

    override fun updateSelectedItem(
        context: Context,
        spinnerId: Int,
        itemName: String?,
        itemIndex: Int
    ) {
        spinnerSelectionON = itemIndex == 1
    }

    override fun serializeToCatrobatLanguage(indentionLevel: Int): String {
        val state = if (spinnerSelectionON) "on" else "off"
        return getCatrobatLanguageParamerCall(indentionLevel, "camera", state)
    }
}
