package com.parrot.freeflight.receivers;

public interface DroneVideoRecordStateReceiverDelegate {
	public void onDroneRecordVideoStateChanged(boolean recording, boolean usbActive, int remainingTime);
}
