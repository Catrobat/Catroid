/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.content.bricks.brickspinner

import android.content.Context
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import java.io.Serializable

enum class PickableMusicalInstrument(private val nameStringId: Int, var value: Int) : Nameable,
    Serializable {
    PIANO(R.string.piano, 0), ELECTRIC_PIANO(R.string.electric_piano, 2), CELLO(
        R.string.cello,
        42
    ),
    FLUTE(R.string.flute, 73), VIBRAPHONE(R.string.vibraphone, 11), ORGAN(
        R.string.organ,
        16
    ),
    GUITAR(R.string.guitar, 24), ELECTRIC_GUITAR(R.string.electric_guitar, 26), BASS(
        R.string.bass,
        32
    ),
    PIZZICATO(R.string.pizzicato, 45), SYNTH_PAD(R.string.synth_pad, 90), CHOIR(
        R.string.choir,
        52
    ),
    SYNTH_LEAD(R.string.synth_lead, 80), WOODEN_FLUTE(
        R.string.wooden_flute,
        75
    ),
    TROMBONE(R.string.trombone, 57), SAXOPHONE(R.string.saxophone, 64), BASSOON(
        R.string.bassoon,
        70
    ),
    CLARINET(R.string.clarinet, 71), MUSIC_BOX(
        R.string.music_box,
        10
    ),
    STEEL_DRUM(R.string.steel_drum, 114), MARIMBA(R.string.marimba, 12);

    private var nameOfPickAbleMusicalInstrument: String? = null
    override fun getName(): String? {
        return nameOfPickAbleMusicalInstrument
    }

    override fun setName(name: String) {
        this.nameOfPickAbleMusicalInstrument = name
    }

    fun getString(context: Context): String {
        return context.getString(nameStringId)
    }

    companion object {
        @JvmStatic
		fun getIndexByValue(value: Int): Int {
            var index = 0
            for (instrument in values()) {
                if (instrument.value == value) {
                    return index
                }
                index++
            }
            return -1
        }
    }
}