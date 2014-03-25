/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.parrot.freeflight.drone;

import android.content.Context;

import com.parrot.freeflight.drone.DroneProxy.EVideoRecorderCapability;

public class DroneProxyWrapper implements DroneProxyInterface {

	private DroneProxy droneProxy = null;

	public DroneProxyWrapper(DroneProxy droneProxy) {
		this.droneProxy = droneProxy;
	}

	@Override
	public void doConnect(Context context, EVideoRecorderCapability recordVideoResolution) {
		droneProxy.doConnect(context, recordVideoResolution);
	}

	@Override
	public void doDisconnect() {
		droneProxy.doDisconnect();
	}

	@Override
	public NavData getNavdata() {
		return droneProxy.getNavdata();
	}

	@Override
	public void doPause() {
		droneProxy.doPause();
	}

	@Override
	public void doResume() {
		droneProxy.doResume();
	}

	@Override
	public void updateNavdata() {
		droneProxy.updateNavdata();
	}

	@Override
	public DroneConfig getConfig() {
		return droneProxy.getConfig();
	}

	@Override
	public void setAcademyMediaListener(DroneAcademyMediaListener listener) {
		droneProxy.setAcademyMediaListener(listener);
	}

	@Override
	public void onAcademyNewMediaReady(String path, boolean addToQueue) {
		droneProxy.onAcademyNewMediaReady(path, addToQueue);
	}

	@Override
	public void initNavdata() {
		droneProxy.initNavdata();
	}

	@Override
	public void triggerTakeOff() {
		droneProxy.triggerTakeOff();
	}

	@Override
	public void triggerEmergency() {
		droneProxy.triggerEmergency();
	}

	@Override
	public void setControlValue(int control, float value) {
		droneProxy.setControlValue(control, value);
	}

	@Override
	public void setMagnetoEnabled(boolean absoluteControlEnabled) {
		droneProxy.setMagnetoEnabled(absoluteControlEnabled);
	}

	@Override
	public void setCommandFlag(int flag, boolean enable) {
		droneProxy.setCommandFlag(flag, enable);
	}

	@Override
	public void setDeviceOrientation(int heading, int headingAccuracy) {
		droneProxy.setDeviceOrientation(heading, headingAccuracy);
	}

	@Override
	public void switchCamera() {
		droneProxy.switchCamera();
	}

	@Override
	public void triggerConfigUpdateNative() {
		droneProxy.triggerConfigUpdateNative();
	}

	@Override
	public void flatTrimNative() {
		flatTrimNative();
	}

	@Override
	public void setDefaultConfigurationNative() {
		droneProxy.setDefaultConfigurationNative();
	}

	@Override
	public void resetConfigToDefaults() {
		droneProxy.resetConfigToDefaults();
	}

	@Override
	public void takePhoto() {
		droneProxy.takePhoto();
	}

	@Override
	public void record() {
		droneProxy.record();
	}

	@Override
	public void playLedAnimation(float frequency, int duration, int animationMode) {
		droneProxy.playLedAnimation(frequency, duration, animationMode);
	}

	@Override
	public void calibrateMagneto() {
		droneProxy.calibrateMagneto();
	}

	@Override
	public void doFlip() {
		droneProxy.doFlip();
	}

	@Override
	public void setLocation(double latitude, double longitude, double altitude) {
		droneProxy.setLocation(latitude, longitude, altitude);
	}

}
