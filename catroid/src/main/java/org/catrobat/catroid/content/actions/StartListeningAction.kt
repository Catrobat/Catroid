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

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.OnSpeechRecognitionResultCallback
import org.catrobat.catroid.stage.SpeechRecognitionHolderFactory
import org.koin.java.KoinJavaComponent.get

class StartListeningAction : Action(), OnSpeechRecognitionResultCallback {

    companion object {
        private val TAG = StartListeningAction::class.java.simpleName
    }

    private var listeningRequested = false
    private var spokenWordsReceived = false
    private val speechRecognitionHolder = get(SpeechRecognitionHolderFactory::class.java).instance
    var userVariable: UserVariable? = null

    override fun act(delta: Float): Boolean {
        if (!listeningRequested) {
            requestStartListening()
        }

        return spokenWordsReceived
    }

    override fun restart() {
        listeningRequested = false
        spokenWordsReceived = false
        super.restart()
    }

    private fun requestStartListening() {
        speechRecognitionHolder.apply {
            callback = this@StartListeningAction
            startListening()
        }
        listeningRequested = true
    }

    override fun onResult(spokenWords: String) {
        Log.d(TAG, "received: $spokenWords")
        userVariable?.value = spokenWords
        spokenWordsReceived = true
    }
}
