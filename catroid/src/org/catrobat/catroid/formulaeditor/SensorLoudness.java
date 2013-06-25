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
import android.util.Log;

public class SensorLoudness {

	private int m_interval = 50; // 50 milliseconds by default, can be changed later
	private double scale_range = 100d; //scale the give amplitude to scale_range

	private final double MAX_AMP_VALUE = 32767d;
	private static SensorLoudness instance = null;
	private ArrayList<SensorLoudnessEventListener> listener_ = new ArrayList<SensorLoudnessEventListener>();

	private SoundRecorder mRecorder = null;
	private Handler m_handler;

	//Periodic update the loudness_value
	Runnable m_statusChecker = new Runnable() {
		@Override
		public void run() {
			double loudness = mRecorder.getMaxAmplitude();
			for (SensorLoudnessEventListener el : listener_) {
				el.onLoudnessChanged(scale_range / MAX_AMP_VALUE * loudness);
			}
			m_handler.postDelayed(m_statusChecker, m_interval);
		}
	};

	private SensorLoudness() {
		m_handler = new Handler();
	};

	public static SensorLoudness getSensorLoudness() {
		if (instance == null) {
			instance = new SensorLoudness();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorLoudnessEventListener listener) {
		if (listener_.contains(listener)) {
			return true;
		}
		listener_.add(listener);
		if (mRecorder == null) {
			try {
				mRecorder = new SoundRecorder("dev/null");
				mRecorder.start();
				m_statusChecker.run();
			} catch (Exception e) {
				listener_.remove(listener);
				mRecorder = null;
				Log.e("CATROID", "LoudnessSensor failed to start recording.", e);
				return false;
			}
		}
		return true;
	}

	public synchronized void unregisterListener(SensorLoudnessEventListener listener) {
		if (listener_.contains(listener)) {
			listener_.remove(listener);
			if (listener_.size() == 0 && mRecorder != null) {
				m_handler.removeCallbacks(m_statusChecker);
				if (mRecorder.isRecording()) {
					try {
						mRecorder.stop();
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("CATROID", "LoudnessSensor Stopping failed.",e);
					}
				}
				mRecorder = null;
			}
		}
	}

	public interface SensorLoudnessEventListener {
		public abstract void onLoudnessChanged(double loudness);
	}
}
