/*
 * DroneConfig
 *
 *  Created on: May 5, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.drone;

public class DroneConfig 
{
	public enum EDroneVersion {
		UNKNOWN,
		DRONE_1,
		DRONE_2
	}
	
	public static final int YAW_MIN        = 40;
	public static final int YAW_MAX        = 350;
	public static final int VERT_SPEED_MIN = 200;
	public static final int VERT_SPEED_MAX = 2000;
	public static final int TILT_MIN       = 5;
	public static final int TILT_MAX       = 30;
	public static final int DEVICE_TILTMAX_MIN = 5;
	public static final int DEVICE_TILTMAX_MAX = 50;
	public static final int DEFAULT_TILT_MAX = 30;
	public static final int ALTITUDE_MIN   = 3;
	public static final int ALTITUDE_MAX   = 100;
	
	public static final int UVLC_CODEC = 0x20;
	public static final int P264_CODEC = 0x40;
	public static final int MP4_360P_CODEC = 0x80;
	public static final int H264_360P_CODEC = 0x81;
	public static final int MP4_360P_H264_720P_CODEC = 0x82;
	public static final int H264_720P_CODEC = 0x83;
	public static final int MP4_360P_SLRS_CODEC = 0x84;
	public static final int H264_360P_SLRS_CODEC = 0x85;
	public static final int H264_720P_SLRS_CODEC = 0x86;
	public static final int H264_AUTO_RESIZE_CODEC = 0x87;
	
	public static final int TELNET_PORT = 23;
	public static final int REPAIR_FTP_PORT = 21;
	
	private EDroneVersion droneVersion = EDroneVersion.UNKNOWN;
	private String softwareVersion;
	private String hardwareVersion;
	private String inertialHardwareVersion;
	private String inertialSoftwareVersion;
	
	private String motor1SoftVersion;
	private String motor2SoftVersion;
	private String motor3SoftVersion;
	private String motor4SoftVersion;
	private String motor1HardVersion;
	private String motor2HardVersion;
	private String motor3HardVersion;
	private String motor4HardVersion;
	private String motor1Vendor;
	private String motor2Vendor;
	private String motor3Vendor;
	private String motor4Vendor;
	
	private String networkName;
	
	private String ownerMac;
	private boolean adaptiveVideo;
	private boolean outdoorHull;
	private boolean outdoorFlight;
	private boolean recordOnUsb;
	
	private int yawSpeedMax;
	private int vertSpeedMax;
	private int tilt;
	private int deviceTiltMax; 
	private int videoCodec;
	private int altitudeLimit;

	
	public DroneConfig()
	{
		yawSpeedMax = YAW_MIN;
		vertSpeedMax = VERT_SPEED_MIN;
		tilt = TILT_MIN;
	}
	
	public DroneConfig(DroneConfig config) 
	{
		this.softwareVersion = config.softwareVersion;
		this.hardwareVersion = config.hardwareVersion;
		this.inertialHardwareVersion = config.inertialHardwareVersion;
		this.inertialSoftwareVersion = config.inertialSoftwareVersion;
		this.motor1Vendor = config.motor1Vendor;
		this.motor2Vendor = config.motor2Vendor;
		this.motor3Vendor = config.motor3Vendor;
		this.motor4Vendor = config.motor4Vendor;
		this.motor1HardVersion = config.motor1HardVersion;
		this.motor2HardVersion = config.motor2HardVersion;
		this.motor3HardVersion = config.motor3HardVersion;
		this.motor4HardVersion = config.motor4HardVersion;
		this.motor1SoftVersion = config.motor1SoftVersion;
		this.motor2SoftVersion = config.motor2SoftVersion;
		this.motor3SoftVersion = config.motor3SoftVersion;
		this.motor4SoftVersion = config.motor4SoftVersion;
		
		this.networkName = config.networkName;
		this.ownerMac = config.ownerMac;	
		this.altitudeLimit = config.altitudeLimit;
		this.adaptiveVideo = config.adaptiveVideo;
		this.outdoorHull = config.outdoorHull;
		this.outdoorFlight = config.outdoorFlight;
		this.yawSpeedMax = config.yawSpeedMax;
		this.vertSpeedMax = config.vertSpeedMax;
		this.tilt = config.tilt;
		this.deviceTiltMax = config.deviceTiltMax;
		this.videoCodec = config.videoCodec;
		this.recordOnUsb = config.recordOnUsb;
	}
	
	
	public static String getHost() 
	{
		return getDroneHostNative();
	}
	
	
	public static int getFtpPort() 
	{
		return getFtpPortNative();
	}
	
	
	public void setOutdoorHull(boolean enabled)
	{
		this.outdoorHull = enabled;
		updateOutdoorHullNative();
	}
	
	
	public void setAdaptiveVideo(boolean enabled)
	{
		this.adaptiveVideo = enabled;
		updateAdaptiveVideoNative();
	}
	
	
	public void setOwnerMac(String ownerMac)
	{
		this.ownerMac = ownerMac;
		updateOwnerMacNative();
	}
	

	public void setAltitudeLimit(int altitudeLimit) 
	{
		this.altitudeLimit = altitudeLimit;
		updateAltitudeLimit(altitudeLimit);
	}


	public int getAltitudeLimit() 
	{
		return this.altitudeLimit;
	}
	
	
	public void setOutdoorFlight(boolean enabled)
	{
		this.outdoorFlight = enabled;
		updateOutdoorFlightNative();
	}
	
	
	public void setYawSpeedMax(int yawSpeedMax)
	{
		this.yawSpeedMax = yawSpeedMax;
		updateYawSpeedMaxNative();
	}
	
	
	public void setVertSpeedMax(int speed)
	{
		this.vertSpeedMax = speed;
		updateVertSpeedMaxNative();
	}
	
	
	public void setTilt(int tilt)
	{
		this.tilt = tilt;
		updateTiltNative();
	}


	public void setDeviceTiltMax(int tiltMax) {
		this.deviceTiltMax = tiltMax;
		
		updateDeviceTiltMax(tiltMax);
	}


	public void setNetworkName(String name) 
	{
		this.networkName = name;
		updateNetworkNameNative();
	}
	
	
	public void setVideoCodec(int codec)
	{
		this.videoCodec = codec;
		updateVideoCodecNative();
	}

	public void setRecordOnUsb(boolean enable)
	{
		this.recordOnUsb = enable;
		updateRecordOnUsb();
	}
	
	public String getHardwareVersion()
	{
		return hardwareVersion;
	}	 
	
	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public String getInertialHardwareVersion() {
		return inertialHardwareVersion;
	}

	public String getInertialSoftwareVersion() {
		return inertialSoftwareVersion;
	}

	public String getMotor1SoftVersion() {
		return motor1SoftVersion;
	}

	public String getMotor2SoftVersion() {
		return motor2SoftVersion;
	}

	public String getMotor3SoftVersion() {
		return motor3SoftVersion;
	}

	public String getMotor4SoftVersion() {
		return motor4SoftVersion;
	}

	public String getMotor1HardVersion() {
		return motor1HardVersion;
	}

	public String getMotor2HardVersion() {
		return motor2HardVersion;
	}

	public String getMotor3HardVersion() {
		return motor3HardVersion;
	}

	public String getMotor4HardVersion() {
		return motor4HardVersion;
	}

	public String getMotor1Vendor() {
		return motor1Vendor;
	}

	public String getMotor2Vendor() {
		return motor2Vendor;
	}

	public String getMotor3Vendor() {
		return motor3Vendor;
	}

	public String getMotor4Vendor() {
		return motor4Vendor;
	}

	public String getNetworkName() {
		return networkName;
	}

	public String getOwnerMac() {
		return ownerMac;
	}

	public boolean isOutdoorHull() {
		return outdoorHull;
	}

	public boolean isOutdoorFlight() {
		return outdoorFlight;
	}
	
	public boolean isAdaptiveVideo()
	{
		return adaptiveVideo;
	}
	
	public boolean isRecordOnUsb()
	{
		return recordOnUsb;
	}

	public int getYawSpeedMax() {
		return yawSpeedMax;
	}

	public int getVertSpeedMax() {
		return vertSpeedMax;
	}

	public int getTilt() {
		return tilt;
	}
	
	public int getDeviceTiltMax() {
		return deviceTiltMax;
	}

	public int getVideoCodec() {
		return videoCodec;
	}
	
	public EDroneVersion getDroneVersion()
	{
		if (droneVersion == EDroneVersion.UNKNOWN) {
			droneVersion = EDroneVersion.values()[getDroneFamily()];
		}
		
		return droneVersion;
	}


	private native void updateOutdoorHullNative();
	private native void updateNetworkNameNative();
	private native void updateAdaptiveVideoNative();
	private native void updateOwnerMacNative();
	private native void updateAltitudeLimit(int limit);
	private native void updateOutdoorFlightNative();
	private native void updateYawSpeedMaxNative();
	private native void updateVertSpeedMaxNative();
	private native void updateTiltNative();
	private native void updateVideoCodecNative();
	private native void updateRecordOnUsb();
	private native void updateDeviceTiltMax(int tilt);
	
	private native int getDroneFamily();
	
	private static native int getFtpPortNative();
	private static native String getDroneHostNative();


}
