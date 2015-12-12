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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.raspberrypi.RPiSocketConnection;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class RaspiIfLogicAction extends Action {

	private Sprite sprite;
	private Action ifAction;
	private Action elseAction;
	private boolean isInitialized = false;
	private Formula pinNumber;
    private int pin;


    public void setPinNumber(Formula pinNumber) {
        this.pinNumber = pinNumber;
    }


    protected void begin() {
        Integer pinNumberInterpretation;

        try {
            pinNumberInterpretation = pinNumber == null ? Integer.valueOf(0) : pinNumber.interpretInteger(sprite);
        } catch (InterpretationException interpretationException) {
            pinNumberInterpretation = 0;
            Log.e(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.",
                    interpretationException);
        }
        this.pin = pinNumberInterpretation;
	}

	@Override
	public boolean act(float delta) {
		if (!isInitialized) {
			begin();
			isInitialized = true;
		}

		if (readIfConditionValue()) {
			return ifAction.act(delta);
		} else {
			return elseAction.act(delta);
		}
	}

    protected boolean readIfConditionValue() {
        RPiSocketConnection connection = RaspberryPiService.connection;
        try {
            Log.d(getClass().getSimpleName(), "RPi get " + pin);
            return connection.getPin(pin);
        } catch (Exception e){
            Log.e(getClass().getSimpleName(), "RPi: exception during getPin: " + e);
        }
        return false;
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


	@Override
	public void setActor(Actor actor) {
		super.setActor(actor);
		ifAction.setActor(actor);
		elseAction.setActor(actor);
	}
}
