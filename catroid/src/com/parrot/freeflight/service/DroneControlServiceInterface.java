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
package com.parrot.freeflight.service;

import com.parrot.freeflight.drone.DroneConfig;

/**
 * @author Gerald
 * 
 */
public interface DroneControlServiceInterface {

	public void playLedAnimation(float frequency, int duration, int animationMode);

	public void triggerTakeOff();

	public void moveForward(final float power);

	public void moveBackward(final float power);

	public void calibrateMagneto();

	public void doLeftFlip();

	public void triggerEmergency();

	public void triggerConfigUpdate();

	public void turnLeft(final float power);

	public void turnRight(final float power);

	public void moveUp(final float power);

	public void moveDown(final float power);

	public void moveLeft(final float power);

	public void moveRight(final float power);

	public DroneConfig getDroneConfig();

	public void resetConfigToDefaults();

	public void requestConfigUpdate();

	public void takePhoto();

	public void record();

	public void flatTrim();

	public void setGaz(final float value);

	public void setRoll(final float value);

	public void setPitch(final float value);

	public void setYaw(final float value);

	public void setProgressiveCommandEnabled(boolean b);

	public void setProgressiveCommandCombinedYawEnabled(boolean b);

}
