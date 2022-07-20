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
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction

class FadeParticleEffectBrick(fadeType: Int = FADE_IN) : BrickBaseType() {

    companion object {
        const val FADE_IN = 0
        const val FADE_OUT = 1
    }

    private var fadeSpinnerSelectionId: Int = fadeType

    override fun getViewResource(): Int = R.layout.brick_fade_particle_effect

    override fun getView(context: Context): View {
        super.getView(context)
        val fadeSpinner = view.findViewById<Spinner>(R.id.brick_fade_particle_effect_spinner)
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
        spinnerValues[FADE_OUT] = context.getString(R.string.particle_effects_fade_out)
        spinnerValues[FADE_IN] = context.getString(R.string.particle_effects_fade_in)
        val spinnerAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerValues)
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
            sprite.actionFactory.createFadeParticleEffectsAction(
                sprite,
                fadeSpinnerSelectionId == FADE_IN
            )
        )
    }
}
