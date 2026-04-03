/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.formulaeditor;

import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.soundrecorder.SoundRecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.VisibleForTesting;

public class SensorLoudness {
	private static final int UPDATE_INTERVAL = 50;
	private static final double SCALE_RANGE = 100d;
	private static final double MAX_AMP_VALUE = 32767d;
	private static final String TAG = SensorLoudness.class.getSimpleName();
	private static final String LOUDNESS_SENSOR_RECORDING_FILE_NAME = "loudness_sensor.m4a";
	private List<SensorCustomEventListener> listenerList = new ArrayList<>();

	@VisibleForTesting
	interface SoundRecorderFactory {
		SoundRecorder create(String path);
	}

	private SoundRecorder recorder;
	private final Handler handler;
	private final SoundRecorderFactory soundRecorderFactory;
	private final String recorderPath;
	private Double lastValue = 0.0;

	public SensorLoudness() {
		this(SensorLoudness::defaultSoundRecorderFactory, defaultRecorderPath());
	}

	@VisibleForTesting
	SensorLoudness(SoundRecorderFactory soundRecorderFactory) {
		this(soundRecorderFactory, defaultRecorderPath());
	}

	@VisibleForTesting
	SensorLoudness(SoundRecorderFactory soundRecorderFactory, String recorderPath) {
		handler = new Handler();
		this.soundRecorderFactory = soundRecorderFactory;
		this.recorderPath = recorderPath;
		recorder = createSoundRecorder();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			Double loudness = ((SCALE_RANGE / MAX_AMP_VALUE) * recorder.getMaxAmplitude());
			if (!loudness.equals(lastValue) && !loudness.equals(0.0)) {
				lastValue = loudness;
				SensorCustomEvent event = new SensorCustomEvent(Sensors.LOUDNESS, loudness);

				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		if (!recorder.isRecording()) {
			try {
				recorder.start();
				statusChecker.run();
			} catch (IOException ioException) {
				Log.d(TAG, "Could not start recorder", ioException);
				listenerList.remove(listener);
				cleanupRecorderFile();
				recorder = createSoundRecorder();
				return false;
			} catch (RuntimeException runtimeException) {
				Log.d(TAG, "Could not start recorder", runtimeException);
				listenerList.remove(listener);
				cleanupRecorderFile();
				recorder = createSoundRecorder();
				return false;
			}
		}
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				if (recorder.isRecording()) {
					try {
						recorder.stop();
					} catch (IOException ioException) {
						// ignored, nothing we can do
						Log.d(TAG, "Could not stop recorder", ioException);
					}
					cleanupRecorderFile();
					recorder = createSoundRecorder();
				}
				lastValue = 0.0;
			}
		}
	}

	private void cleanupRecorderFile() {
		File recorderFile = new File(recorderPath);
		if (recorderFile.exists() && !recorderFile.delete()) {
			Log.d(TAG, "Could not delete recorder file " + recorderPath);
		}
	}

	private SoundRecorder createSoundRecorder() {
		return soundRecorderFactory.create(recorderPath);
	}

	private static SoundRecorder defaultSoundRecorderFactory(String path) {
		return new SoundRecorder(path);
	}

	@VisibleForTesting
	static String defaultRecorderPath() {
		return new File(Constants.SOUND_RECORDER_CACHE_DIRECTORY, LOUDNESS_SENSOR_RECORDING_FILE_NAME)
				.getAbsolutePath();
	}

	@VisibleForTesting
	public void setSoundRecorder(SoundRecorder soundRecorder) {
		recorder = soundRecorder;
	}

	@VisibleForTesting
	public SoundRecorder getSoundRecorder() {
		return recorder;
	}
}
