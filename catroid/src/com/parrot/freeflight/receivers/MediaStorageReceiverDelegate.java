package com.parrot.freeflight.receivers;

public interface MediaStorageReceiverDelegate {
	/**
	 * Called when removable external storage become mounted
	 */
	public void onMediaStorageMounted();

	/**
	 * Called when removable external storage is removed
	 */
	public void onMediaStorageUnmounted();

	/**
	 * You need to close all files that you have opened when this happens
	 */
	public void onMediaEject();
}
