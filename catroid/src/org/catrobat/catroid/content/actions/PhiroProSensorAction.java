/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.arduino.phiropro.PhiroPro;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.formulaeditor.Sensors;

public class PhiroProSensorAction extends Action {

	private int sensorNumber;
	private Sprite sprite;
	private Action ifAction;
	private Action elseAction;
	private Formula ifCondition;
	private Boolean ifConditionValue;
	private boolean isInitialized = false;
	private boolean isInterpretedCorrectly;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	protected void begin() {
		try {
			if (ifCondition == null) {
				isInterpretedCorrectly = false;
				return;
			}
			Double interpretation = ifCondition.interpretDouble(sprite);
			ifConditionValue = interpretation.intValue() != 0 ? true : false;
			isInterpretedCorrectly = true;
		} catch (InterpretationException interpretationException) {
			isInterpretedCorrectly = false;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}
	}

	@Override
	public boolean act(float delta) {
		if (!isInitialized) {
			begin();
			isInitialized = true;
		}

		if (!isInterpretedCorrectly) {
			return true;
		}

		if (ifConditionValue) {
			return ifAction.act(delta);
		} else {
			return elseAction.act(delta);
		}
	}

	@Override
	public void restart() {
		ifAction.restart();
		elseAction.restart();
		isInitialized = false;
		super.restart();
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setIfAction(Action ifAction) {
		this.ifAction = ifAction;
	}

	public void setElseAction(Action elseAction) {
		this.elseAction = elseAction;
	}

	public void setIfCondition(Formula ifCondition) {
		this.ifCondition = ifCondition;
	}

	public void setSensor(int sensorNumber)
	{
		this.sensorNumber =sensorNumber;
		this.setIfCondition(new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR, getPhiroProSensorByNumber().name(), null )));
	}

	private Sensors getPhiroProSensorByNumber() {
		switch (this.sensorNumber) {
			case 0:
				return Sensors.PHIRO_PRO_SIDE_RIGHT;
			case 1:
				return Sensors.PHIRO_PRO_FRONT_RIGHT;
			case 2:
				return Sensors.PHIRO_PRO_BOTTOM_RIGHT;
			case 3:
				return Sensors.PHIRO_PRO_BOTTOM_LEFT;
			case 4:
				return Sensors.PHIRO_PRO_FRONT_LEFT;
			case 5:
				return Sensors.PHIRO_PRO_SIDE_LEFT;
		}

		return Sensors.PHIRO_PRO_SIDE_RIGHT;
	}

//	@Override
//	protected void update(float percent) {
//		PhiroPro phiroPro = btService.getDevice(BluetoothDevice.PHIRO_PRO);
//		if(phiroPro != null) {
//			phiroPro.getSensorValue(Sensors.PHIRO_PRO_BOTTOM_LEFT);
//		}
//	}


	@Override
	public void setActor(Actor actor) {
		super.setActor(actor);
		ifAction.setActor(actor);
		elseAction.setActor(actor);
	}
}