package org.catrobat.catroid.test.utils;

public class SimulatedSoundRecorder {
	private boolean recording = false;

	public SimulatedSoundRecorder(String path) {
	}

	public void start() {
		recording = true;
	}

	public boolean isRecording() {
		return recording;
	}

	public void stop() {
		if (!recording) {
			throw new IllegalStateException();
		}
		recording = false;
	}

}
