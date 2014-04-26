package com.parrot.freeflight.receivers;

public interface DroneAvailabilityDelegate {
	public void onDroneAvailabilityChanged(boolean isDroneOnNetwork);
}
