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

import android.speech.tts.UtteranceProgressListener
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class UtteranceProgressListenerContainer : UtteranceProgressListener() {
    private val listeners: MutableMap<String?, MutableList<UtteranceProgressListener>?> = HashMap()

    @Synchronized
    fun addUtteranceProgressListener(speechFile: File, utteranceProgressListener: UtteranceProgressListener, utteranceId: String?): Boolean {
        val utteranceIdListeners = listeners[utteranceId]
        return if (utteranceIdListeners == null) {
            if (speechFile.exists()) {
                utteranceProgressListener.onDone(utteranceId)
                false
            } else {
                val a = ArrayList<UtteranceProgressListener>()
                a.add(utteranceProgressListener)
                listeners[utteranceId] = a
                true
            }
        } else {
            utteranceIdListeners.add(utteranceProgressListener)
            false
        }
    }

    @Synchronized
    override fun onDone(utteranceId: String) {
        for (listener in listeners[utteranceId].orEmpty()) {
            listener.onDone(utteranceId)
        }
        listeners[utteranceId] = null
    }

    @SuppressWarnings("EmptyFunctionBlock")
    override fun onStart(utteranceId: String) {}

    @SuppressWarnings("EmptyFunctionBlock")
    override fun onError(utteranceId: String) {}
}
