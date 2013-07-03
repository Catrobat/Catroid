package org.catrobat.catroid.uitest.util;

import org.catrobat.catroid.soundrecorder.SoundRecorder;

public class SimulatedSoundRecorder extends SoundRecorder {
	private boolean recording = false;

	public SimulatedSoundRecorder(String path) {
		super(path);
	}

	@Override
	public void start() {
		recording = true;
	}

	@Override
	public boolean isRecording() {
		return recording;
	}

	@Override
	public void stop() {
		if (!recording) {
			throw new IllegalStateException();
		}
		recording = false;
	}

	@Override
	public int getMaxAmplitude() {
		return (int) (Math.random() * 32000 + 767);
	}

}
