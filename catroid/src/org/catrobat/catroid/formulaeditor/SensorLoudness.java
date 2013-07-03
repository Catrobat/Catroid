/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.io.IOException;
import java.util.ArrayList;

import org.catrobat.catroid.soundrecorder.SoundRecorder;

import android.os.Handler;

public class SensorLoudness {

	private static SensorLoudness instance = null;
	private int m_interval = 50; // 50 milliseconds by default, can be changed later
	private double scale_range = 100d; //scale the give amplitude to scale_range

	private final double MAX_AMP_VALUE = 32767d;
	private ArrayList<SensorCustomEventListener> listener_ = new ArrayList<SensorCustomEventListener>();

	private SoundRecorder mRecorder = null;
	private Handler m_handler;
	private float current_value = 0f;

	//Periodic update the loudness_value
	Runnable m_statusChecker = new Runnable() {
		@Override
		public void run() {
			float[] loudness = new float[1];
			loudness[0] = (float) (scale_range / MAX_AMP_VALUE) * mRecorder.getMaxAmplitude();
			if (current_value != loudness[0] && loudness[0] != 0f) {
				current_value = loudness[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.LOUDNESS, loudness);
				for (SensorCustomEventListener el : listener_) {
					el.onCustomSensorChanged(event);
				}
			}
			m_handler.postDelayed(m_statusChecker, m_interval);
		}
	};

	private SensorLoudness() {
		m_handler = new Handler();
		mRecorder = new SoundRecorder("dev/null");
	};

	public static SensorLoudness getSensorLoudness() {
		if (instance == null) {
			instance = new SensorLoudness();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listener_.contains(listener)) {
			return true;
		}
		listener_.add(listener);
		if (!mRecorder.isRecording()) {
			try {
				mRecorder.start();
				m_statusChecker.run();
			} catch (Exception e) {
				listener_.remove(listener);
				mRecorder = new SoundRecorder("dev/null");
				return false;
			}
		}
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listener_.contains(listener)) {
			listener_.remove(listener);
			if (listener_.size() == 0) {
				m_handler.removeCallbacks(m_statusChecker);
				if (mRecorder.isRecording()) {
					try {
						mRecorder.stop();
					} catch (IOException e) {
					}
					mRecorder = new SoundRecorder("dev/null");
				}
				current_value = 0f;
			}
		}
	}
}
