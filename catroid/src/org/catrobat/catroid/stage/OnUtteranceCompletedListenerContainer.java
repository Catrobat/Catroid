/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.stage;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class OnUtteranceCompletedListenerContainer implements OnUtteranceCompletedListener {
	private final Map<String, List<OnUtteranceCompletedListener>> listeners = new HashMap<String, List<OnUtteranceCompletedListener>>();

	public synchronized boolean addOnUtteranceCompletedListener(File speechFile,
			OnUtteranceCompletedListener onUtteranceCompletedListener, String utteranceId) {
		List<OnUtteranceCompletedListener> utteranceIdListeners = listeners.get(utteranceId);

		if (utteranceIdListeners == null) {
			if (speechFile.exists()) {
				onUtteranceCompletedListener.onUtteranceCompleted(utteranceId);
				return false;
			} else {
				utteranceIdListeners = new ArrayList<TextToSpeech.OnUtteranceCompletedListener>();
				utteranceIdListeners.add(onUtteranceCompletedListener);
				listeners.put(utteranceId, utteranceIdListeners);
				return true;
			}
		} else {
			utteranceIdListeners.add(onUtteranceCompletedListener);
			return false;
		}
	}

	@Override
	public synchronized void onUtteranceCompleted(String utteranceId) {
		for (OnUtteranceCompletedListener listener : listeners.get(utteranceId)) {
			listener.onUtteranceCompleted(utteranceId);
		}
		listeners.put(utteranceId, null);
	}
}
