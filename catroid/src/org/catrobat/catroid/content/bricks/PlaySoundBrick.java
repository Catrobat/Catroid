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
package org.catrobat.catroid.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class PlaySoundBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	private SoundInfo sound;
	//TODO: IllyaBoyko: oldSelectedSound should be only present in UI Logic.
	private transient SoundInfo oldSelectedSound;

	public PlaySoundBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		PlaySoundBrick copyBrick = (PlaySoundBrick) clone();
		for (SoundInfo soundInfo : sprite.getSoundList()) {
			if (soundInfo.getAbsolutePath().equals(sound.getAbsolutePath())) {
				copyBrick.sound = soundInfo;
			}
		}
		return copyBrick;
	}


	@Override
	public Brick clone() {
		return new PlaySoundBrick();
	}

	//for testing purposes:
	public void setSoundInfo(SoundInfo soundInfo) {
		this.sound = soundInfo;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.playSound(sprite, sound));
		return null;
	}


	public SoundInfo getSoundInfo() {
		return sound;
	}

	public SoundInfo getOldSoundInfo() {
		return oldSelectedSound;
	}

	public void setOldSoundInfo(SoundInfo currentSound) {
		this.oldSelectedSound = currentSound;
	}
}
