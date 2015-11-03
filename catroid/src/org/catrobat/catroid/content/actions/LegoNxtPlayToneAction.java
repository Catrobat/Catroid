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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class LegoNxtPlayToneAction extends TemporalAction {

	private Formula hertz;
	private Formula durationInSeconds;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {
		int hertzInterpretation;
		float durationInterpretation;

		try {
			hertzInterpretation = hertz.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			hertzInterpretation = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		try {
			durationInterpretation = durationInSeconds.interpretFloat(sprite);
		} catch (InterpretationException interpretationException) {
			durationInterpretation = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		LegoNXT nxt = btService.getDevice(BluetoothDevice.LEGO_NXT);
		if (nxt == null) {
			return;
		}

		int durationInMs = (int) (durationInterpretation * 1000);

		nxt.playTone(hertzInterpretation * 100, durationInMs);
	}

	public void setHertz(Formula hertz) {
		this.hertz = hertz;
	}

	public void setDurationInSeconds(Formula durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
