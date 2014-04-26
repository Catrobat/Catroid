package com.parrot.freeflight.receivers;

public interface DroneFirmwareCheckReceiverDelegate {
	public void onFirmwareChecked(boolean updateRequired);
}
