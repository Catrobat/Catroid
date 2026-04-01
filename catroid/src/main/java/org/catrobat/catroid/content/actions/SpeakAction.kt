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

package org.catrobat.catroid.content.actions

import android.content.Context
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.stage.SpeechSynthesizer
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider

class SpeakAction : AsynchronousAction() {
    private var isFinished = false
    lateinit var mobileServiceAvailability: MobileServiceAvailability
    lateinit var context: Context
    lateinit var speechSynthesizer: SpeechSynthesizer

    override fun initialize() {
        if (mobileServiceAvailability.isGmsAvailable(context)) {
            speechSynthesizer.setUtteranceProgressListener(this::onError, this::onDone)
        } else if (mobileServiceAvailability.isHmsAvailable(context)) {
            speechSynthesizer.setHuaweiTextToSpeechListener(this::onError, this::onDone)
        } else {
            return
        }
        speechSynthesizer.synthesize(AndroidStringProvider(context))
    }

    private fun onError() {
        isFinished = true
    }

    private fun onDone() {
        SoundManager.getInstance().playSoundFile(
            speechSynthesizer.speechFile?.absolutePath, speechSynthesizer.scope?.sprite
        )
        isFinished = true
    }

    override fun isFinished(): Boolean = isFinished
}
