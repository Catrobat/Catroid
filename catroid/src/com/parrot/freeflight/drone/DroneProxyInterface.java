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

public interface DroneProxyInterface {

	//public static DroneProxy getInstance(Context appContext);

	//public DroneProxy(Context appContext) ;
	public void doConnect(final Context context, EVideoRecorderCapability recordVideoResolution);

	public void doDisconnect();

	public NavData getNavdata();

	public void doPause();

	public void doResume();

	public void updateNavdata();

	//public void onConnected();

	//public void onDisconnected();

	//public void onConnectionFailed(final int reason);

	//public void onConfigChanged();

	public DroneConfig getConfig();

	public void setAcademyMediaListener(DroneAcademyMediaListener listener);

	/*
	 * Rename with caution! This method is called from native code. Rename will require rebuild of the native libraries!
	 */
	public void onAcademyNewMediaReady(String path, boolean addToQueue);

	// Native methods

	public void initNavdata();

	public void triggerTakeOff();

	public void triggerEmergency();

	public void setControlValue(int control, float value);

	public void setMagnetoEnabled(boolean absoluteControlEnabled);

	public void setCommandFlag(int flag, boolean enable);

	public void setDeviceOrientation(int heading, int headingAccuracy);

	public void switchCamera();

	public void triggerConfigUpdateNative();

	public void flatTrimNative();

	public void setDefaultConfigurationNative();

	public void resetConfigToDefaults();

	public void takePhoto();

	public void record();

	public void playLedAnimation(float frequency, int duration, int animationMode);

	public void calibrateMagneto();

	public void doFlip();

	public void setLocation(double latitude, double longitude, double altitude);

}
