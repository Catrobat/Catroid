/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import com.parrot.arsdk.arcommands.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM;
import com.parrot.arsdk.arcontroller.ARDeviceController;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDeviceController;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class JumpingSumoSoundAction extends TemporalAction {

	private Sprite sprite;
	private Formula duration;
	private Formula volumeInPercent;
	private byte normalizedPower;
	private JumpingSumoSoundBrick.Sounds soundEnum;
	private byte normalizedVolume;

	private static final int MIN_VOLUME = 0;
	private static final int MAX_VOLUME = 100;

	private ARDeviceController deviceController;
	private JumpingSumoDeviceController controller;
	private static final String TAG = JumpingSumoMoveBackwardAction.class.getSimpleName();

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setVolume(Formula volume) {
		this.volumeInPercent = volume;
	}

	public void setSoundEnum(JumpingSumoSoundBrick.Sounds soundEnum) {
		this.soundEnum = soundEnum;
	}

	@Override
	protected void begin() {
		super.begin();
		controller = JumpingSumoDeviceController.getInstance();
		deviceController = controller.getDeviceController();

		int volumeValue;
		try {
			volumeValue = volumeInPercent.interpretInteger(sprite);
			normalizedVolume = (byte) -volumeValue;
		} catch (InterpretationException interpretationException) {
			volumeValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		if (volumeValue < MIN_VOLUME) {
			volumeValue = MIN_VOLUME;
			normalizedVolume = (byte) -volumeValue;
		} else if (volumeValue > MAX_VOLUME) {
			volumeValue = MAX_VOLUME;
			normalizedVolume = (byte) -volumeValue;
		}

		start();
	}

	protected void start() {

		if (deviceController != null) {


			switch (soundEnum) {
				case ROBOT:
					deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume(normalizedVolume);
					deviceController.getFeatureJumpingSumo().sendAudioSettingsTheme(ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ROBOT);
					break;
				case INSECT:
					deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume(normalizedVolume);
					deviceController.getFeatureJumpingSumo().sendAudioSettingsTheme(ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_INSECT);
					break;
				case MONSTER:
					deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume(normalizedVolume);
					deviceController.getFeatureJumpingSumo().sendAudioSettingsTheme(ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_MONSTER);
					break;
			}
		}else {
			Log.d(TAG, "error: send -stop command JS");
		}
	}
/*
	@Override
	protected void begin() {
		super.begin();
		controller = JumpingSumoDeviceController.getInstance();
		deviceController = controller.getDeviceController();

		try {
			if (duration == null) {
				normalizedPower = Byte.valueOf(JUMPING_SUMO_MOVE_SPEED_STOP);
			} else {
				float normPower = powerInPercent.interpretFloat(sprite);
				normalizedPower = (byte) -normPower;
			}
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			normalizedPower = Byte.valueOf(JUMPING_SUMO_MOVE_SPEED_STOP);
		}
		move();
	}

	protected void move() {
		if (deviceController != null) {
			deviceController.getFeatureJumpingSumo().setPilotingPCMDSpeed(normalizedPower);
			deviceController.getFeatureJumpingSumo().setPilotingPCMDFlag((byte) 1);
			Log.d(TAG, "send -move command JS");
		} else {
			Log.d(TAG, "error: send -move command JS");
		}
	}

	protected void moveEnd() {
		if (deviceController != null) {
			deviceController.getFeatureJumpingSumo().setPilotingPCMDSpeed(Byte.valueOf(JUMPING_SUMO_MOVE_SPEED_STOP));
			deviceController.getFeatureJumpingSumo().setPilotingPCMDFlag((byte) 0);
			Log.d(TAG, "send -stop command JS");
		} else {
			Log.d(TAG, "error: send -stop command JS");
		}
	}
*/
	@Override
	protected void update(float percent) {
		//Nothing to do
	}
}
