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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.arduino.Arduino;
import org.catrobat.catroid.content.Sprite;

public class ArduinoSendAction extends TemporalAction {

	private int pinNumberHigherByte, pinNumberLowerByte;
	private int pinValue;
	private Sprite sprite;

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public int getPinNumberHigherByte() {
		return pinNumberHigherByte;
	}

	public int getPinNumberLowerByte() {
		return pinNumberLowerByte;
	}

	public int getPinValue() {
		return pinValue;
	}

	public void setPinNumberHigherByte(int newpinNumberHigherByte) {
		pinNumberHigherByte = newpinNumberHigherByte;
	}

	public void setPinNumberLowerByte(int newpinNumberLowerByte) {
		pinNumberLowerByte = newpinNumberLowerByte;
	}

	public void setPinValue(int newpinValue) {
		pinValue = newpinValue;
	}

	@Override
	protected void update(float percent) {

		Log.d("Arduino", "BT Message " + pinNumberLowerByte + "" + pinNumberHigherByte + "" + pinValue);
		Arduino.sendArduinoDigitalPinMessage(pinNumberLowerByte, pinNumberHigherByte, pinValue);
	}
}