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
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.LanguageObject
import org.catrobat.catroid.utils.Utils.SPEECH_RECOGNITION_SUPPORTED_LANGUAGES
import java.util.Locale

class SetListeningLanguageBrick : BrickBaseType(),
    BrickSpinner.OnItemSelectedListener<LanguageObject> {

    private var languageObject: LanguageObject? = null

    @Transient
    private lateinit var spinner: BrickSpinner<LanguageObject>

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createSetListeningLanguageAction(
                languageObject?.languageTag
            )
        )
    }

    override fun getView(context: Context?): View {
        super.getView(context)

        val languageObjectList = mutableListOf<Nameable>()
            .apply {
                SPEECH_RECOGNITION_SUPPORTED_LANGUAGES
                    .forEach { languageTag ->
                        val locale = Locale.forLanguageTag(languageTag)
                        val languageName = locale.getDisplayName(locale)
                        add(LanguageObject(languageName, languageTag))
                    }
            }

        with(
            BrickSpinner<LanguageObject>(
                R.id.brick_set_listening_language_spinner,
                view,
                languageObjectList
            )
        ) {
            spinner = this
            setOnItemSelectedListener(this@SetListeningLanguageBrick)
            setSelection(languageObject)
        }

        return view
    }

    override fun getViewResource(): Int = R.layout.brick_set_listening_language

    override fun onNewOptionSelected(spinnerId: Int?) = Unit

    override fun onEditOptionSelected(spinnerId: Int?) = Unit

    override fun onStringOptionSelected(spinnerId: Int?, string: String?) = Unit

    override fun onItemSelected(spinnerId: Int?, item: LanguageObject?) {
        languageObject = item
    }

    override fun addRequiredResources(requiredResourcesSet: Brick.ResourcesSet) {
        requiredResourcesSet.add(Brick.SPEECH_RECOGNITION)
        super.addRequiredResources(requiredResourcesSet)
    }
}
