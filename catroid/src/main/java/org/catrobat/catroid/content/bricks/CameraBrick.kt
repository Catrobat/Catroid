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
import com.google.common.collect.HashBiMap
import org.catrobat.catroid.R
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils

@CatrobatLanguageBrick(command = "Turn")
class CameraBrick(private var spinnerSelectionON: Boolean = true) : BrickBaseType() {
    companion object {
        private const val CAMERA_CATLANG_PARAMETER_NAME = "camera"
        private val SPINNER_VALUE_MAP = HashBiMap.create(
            mapOf(
                false to "off",
                true to "on"
            )
        )
    }

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

    override fun getArgumentByCatlangName(name: String): Map.Entry<String?, String?>? {
        return if (name == CAMERA_CATLANG_PARAMETER_NAME) {
            CatrobatLanguageUtils.getCatlangArgumentTuple(CAMERA_CATLANG_PARAMETER_NAME, SPINNER_VALUE_MAP[spinnerSelectionON])
        } else super.getArgumentByCatlangName(name)
    }

    override fun getRequiredCatlangArgumentNames(): Collection<String>? {
        val requiredArguments = ArrayList(super.getRequiredCatlangArgumentNames())
        requiredArguments.add(CAMERA_CATLANG_PARAMETER_NAME)
        return requiredArguments
    }

    override fun setParameters(context: Context, project: Project, scene: Scene, sprite: Sprite, arguments: Map<String, String>) {
        super.setParameters(context, project, scene, sprite, arguments)
        val cameraStatus = arguments[CAMERA_CATLANG_PARAMETER_NAME]
        val selectedCameraStatus = SPINNER_VALUE_MAP.inverse()[cameraStatus]
        if (selectedCameraStatus != null) {
            spinnerSelectionON = selectedCameraStatus
        } else {
            throw CatrobatLanguageParsingException("Invalid camera status: $cameraStatus")
        }
    }
}
