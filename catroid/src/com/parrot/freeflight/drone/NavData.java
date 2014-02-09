/*
 * NavData
 *
 *  Created on: May 5, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.drone;


public class NavData 
{
	public int batteryStatus;
	public boolean recording;
	public boolean cameraReady;
	public boolean recordReady;
	public boolean usbActive;
	public int usbRemainingTime;
	public int emergencyState;
	public boolean flying;
	public int numFrames;
	public boolean initialized;

	
	// This constants should match the enums defined in jni/common.h file
	public static final int ERROR_STATE_NONE                          = 0;
	public static final int ERROR_STATE_NAVDATA_CONNECTION            = 1;
	public static final int ERROR_STATE_START_NOT_RECEIVED            = 2;
	public static final int ERROR_STATE_EMERGENCY_CUTOUT              = 3;
	public static final int ERROR_STATE_EMERGENCY_MOTORS              = 4;
	public static final int ERROR_STATE_EMERGENCY_CAMERA              = 5;
	public static final int ERROR_STATE_EMERGENCY_PIC_WATCHDOG        = 6;
	public static final int ERROR_STATE_EMERGENCY_PIC_VERSION         = 7;
	public static final int ERROR_STATE_EMERGENCY_ANGLE_OUT_OF_RANGE  = 8;
	public static final int ERROR_STATE_EMERGENCY_VBAT_LOW            = 9;
	public static final int ERROR_STATE_EMERGENCY_USER_EL             = 10;
	public static final int ERROR_STATE_EMERGENCY_ULTRASOUND          = 11;
	public static final int ERROR_STATE_EMERGENCY_UNKNOWN             = 12;
	public static final int ERROR_STATE_ALERT_CAMERA                  = 13;
	public static final int ERROR_STATE_ALERT_VBAT_LOW                = 14;
	public static final int ERROR_STATE_ALERT_ULTRASOUND              = 15;
	public static final int ERROR_STATE_ALERT_VISION                  = 16;
	
	
	public NavData()
	{
		batteryStatus = 0;
		emergencyState = ERROR_STATE_NONE;
		flying = false;
		initialized = false;
		usbActive = false;
		usbRemainingTime = -1;
		cameraReady = false;
		recordReady = false;
	}


	public void copyFrom(NavData navData) 
	{
		this.batteryStatus = navData.batteryStatus;
		this.emergencyState = navData.emergencyState;
		this.flying = navData.flying;
		this.initialized = navData.initialized;
		this.recording = navData.recording;
		this.numFrames = navData.numFrames;
		this.usbActive = navData.usbActive;
		this.usbRemainingTime = navData.usbRemainingTime;
		this.cameraReady = navData.cameraReady;
		this.recordReady = navData.recordReady;
	}
	
	
	public static final boolean isEmergency(int code)
	{
	    if (code > NavData.ERROR_STATE_NONE && code < ERROR_STATE_ALERT_CAMERA) {
	        return true;
	    }
	    
	    return false;
	}
}
