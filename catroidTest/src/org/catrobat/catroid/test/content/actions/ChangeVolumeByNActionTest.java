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
package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.physics.content.ActionFactory;

public class ChangeVolumeByNActionTest extends InstrumentationTestCase {
	private final float louderValue = 10.6f;
	private final float softerValue = -20.3f;

	public void testVolume() {
		Sprite sprite = new Sprite("testSprite");
		float expectedVolume = SoundManager.getInstance().getVolume();

		expectedVolume += louderValue;
		Formula louder = new Formula(louderValue);

		ActionFactory factory = sprite.getActionFactory();
		Action changeVolumeByAction = factory.createChangeVolumeByNAction(sprite, louder);
		changeVolumeByAction.act(1.0f);
		assertEquals("Incorrect sprite volume after ChangeVolumeByNBrick executed", expectedVolume, SoundManager
				.getInstance().getVolume());

		expectedVolume += softerValue;
		Formula softer = new Formula(softerValue);

		changeVolumeByAction = factory.createChangeVolumeByNAction(sprite, softer);
		changeVolumeByAction.act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeVolumeByNBrick executed", expectedVolume, SoundManager
				.getInstance().getVolume());
	}
}
