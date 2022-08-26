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

package org.catrobat.catroid.stage

import android.content.Context
import org.catrobat.catroid.utils.MobileServiceAvailability

class SpeechRecognitionHolderFactory(
    private val gmsSpeechRecognitionHolder: SpeechRecognitionHolderInterface,
    private val hmsSpeechRecognitionHolder: SpeechRecognitionHolderInterface,
    private val mobileServiceAvailability: MobileServiceAvailability
) {
    companion object {
        private val dummy = object : SpeechRecognitionHolderInterface {
            override fun forceSetLanguage() = Unit
            override fun initSpeechRecognition(
                stageActivity: StageActivity,
                stageResourceHolder: StageResourceHolder
            ) = Unit
            override fun startListening() = Unit
            override fun destroy() = Unit

            override var callback: OnSpeechRecognitionResultCallback? = object :
                OnSpeechRecognitionResultCallback {
                override fun onResult(spokenWords: String) = Unit
            }
        }
    }

    var instance: SpeechRecognitionHolderInterface = dummy
        private set

    fun isRecognitionAvailable(context: Context): Boolean {
        return when {
            mobileServiceAvailability.isGmsAvailable(context) -> {
                instance = gmsSpeechRecognitionHolder
                true
            }
            mobileServiceAvailability.isHmsAvailable(context) -> {
                instance = hmsSpeechRecognitionHolder
                true
            }
            else -> false
        }
    }
}
