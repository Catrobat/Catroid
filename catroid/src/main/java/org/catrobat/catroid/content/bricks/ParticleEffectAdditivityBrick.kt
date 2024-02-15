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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.common.collect.HashBiMap
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils
import java.util.AbstractMap

@CatrobatLanguageBrick(command = "Turn")
class ParticleEffectAdditivityBrick(fadeType: Int = ON) : BrickBaseType(), UpdateableSpinnerBrick {

    companion object {
        const val ON = 0
        const val OFF = 1
        private const val PARTICLE_EFFECT_CATLANG_PARAMETER_NAME = "particle effect additivity"
        private val SPINNER_VALUE_MAP = HashBiMap.create(
            mapOf(
                OFF to "off",
                ON to "on"
            )
        )
    }

    private var fadeSpinnerSelectionId: Int = fadeType

    override fun getViewResource(): Int = R.layout.brick_additive_particle_effect

    override fun getView(context: Context): View {
        super.getView(context)
        val fadeSpinner = view.findViewById<Spinner>(R.id.brick_additive_particle_effect_spinner)
        setupAdditiveSpinner(fadeSpinner, createArrayAdapter(context), createItemSelectedListener())
        fadeSpinner.setSelection(fadeSpinnerSelectionId)
        return view
    }

    private fun setupAdditiveSpinner(
        spinner: Spinner,
        adapter: ArrayAdapter<*>,
        onItemSelectedListener: AdapterView.OnItemSelectedListener
    ) {
        spinner.adapter = adapter
        spinner.onItemSelectedListener = onItemSelectedListener
    }

    private fun createArrayAdapter(context: Context): ArrayAdapter<String?> {
        val spinnerValues = arrayOfNulls<String>(2)
        spinnerValues[OFF] = context.getString(R.string.particle_effects_additive_off)
        spinnerValues[ON] = context.getString(R.string.particle_effects_additive_on)
        val spinnerAdapter = ArrayAdapter(
            context, android.R.layout.simple_spinner_item,
            spinnerValues
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return spinnerAdapter
    }

    private fun createItemSelectedListener(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                fadeSpinnerSelectionId = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createAdditiveParticleEffectsAction(
                sprite,
                fadeSpinnerSelectionId == ON
            )
        )
    }

    override fun updateSelectedItem(
        context: Context,
        spinnerId: Int,
        itemName: String?,
        itemIndex: Int
    ) {
        if (itemIndex == ON || itemIndex == OFF) {
            fadeSpinnerSelectionId = itemIndex
        }
    }

    override fun getArgumentByCatlangName(name: String?): MutableMap.MutableEntry<String, String> {
        return if (name == PARTICLE_EFFECT_CATLANG_PARAMETER_NAME) {
            CatrobatLanguageUtils.getCatlangArgumentTuple(name, SPINNER_VALUE_MAP[fadeSpinnerSelectionId])
        } else super.getArgumentByCatlangName(name)
    }

    override fun getRequiredCatlangArgumentNames(): Collection<String>? {
        val requiredArguments = ArrayList(super.getRequiredCatlangArgumentNames())
        requiredArguments.add(PARTICLE_EFFECT_CATLANG_PARAMETER_NAME)
        return requiredArguments
    }

    override fun setParameters(context: Context, project: Project, scene: Scene, sprite: Sprite, arguments: Map<String, String>) {
        super.setParameters(context, project, scene, sprite, arguments)
        val particleEffect = arguments[PARTICLE_EFFECT_CATLANG_PARAMETER_NAME]
        val selectedParticleEffect = SPINNER_VALUE_MAP.inverse()[particleEffect]
        if (selectedParticleEffect != null) {
            fadeSpinnerSelectionId = selectedParticleEffect
        } else {
            throw CatrobatLanguageParsingException("Invalid particle effect additivity value: $particleEffect")
        }
    }
}
