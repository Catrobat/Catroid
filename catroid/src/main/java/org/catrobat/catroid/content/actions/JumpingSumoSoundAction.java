/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
	private Formula volumeInPercent;
	private JumpingSumoSoundBrick.Sounds soundEnum;

	private ARDeviceController deviceController;
	private JumpingSumoDeviceController controller;
	private static final String TAG = JumpingSumoNoSoundAction.class.getSimpleName();

	@Override
	protected void update(float percent) {
		int normVolume;
		controller = JumpingSumoDeviceController.getInstance();
		deviceController = controller.getDeviceController();

		try {
			normVolume = volumeInPercent.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			normVolume = 0;
		}

		if (deviceController != null) {

			switch (soundEnum) {
				case DEFAULT:
					deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume((byte) normVolume);
					deviceController.getFeatureJumpingSumo().sendAudioSettingsTheme(ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_DEFAULT);
					break;
				case ROBOT:
					deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume((byte) normVolume);
					deviceController.getFeatureJumpingSumo().sendAudioSettingsTheme(ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ROBOT);
					break;
				case INSECT:
					deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume((byte) normVolume);
					deviceController.getFeatureJumpingSumo().sendAudioSettingsTheme(ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_INSECT);
					break;
				case MONSTER:
					deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume((byte) normVolume);
					deviceController.getFeatureJumpingSumo().sendAudioSettingsTheme(ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_ENUM.ARCOMMANDS_JUMPINGSUMO_AUDIOSETTINGS_THEME_THEME_MONSTER);
					break;
			}
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setVolume(Formula volume) {
		this.volumeInPercent = volume;
	}

	public void setSoundEnum(JumpingSumoSoundBrick.Sounds soundEnum) {
		this.soundEnum = soundEnum;
	}
}
