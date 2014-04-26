package com.parrot.freeflight.receivers;

public interface DroneConnectionChangeReceiverDelegate {
	public void onDroneConnected();

	public void onDroneDisconnected();
}
