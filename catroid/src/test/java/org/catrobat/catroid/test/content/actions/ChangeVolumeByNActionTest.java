/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;

@RunWith(JUnit4.class)
public class ChangeVolumeByNActionTest {

	private static final float INITIALIZED_VALUE = 70f;
	private static final float CHANGE_VALUE = 12.3f;
	private static final String NOT_NUMERICAL_STRING = "volume";
	private final float louderValue = 10.6f;
	private final float softerValue = -20.3f;
	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		initializeStaticSingletonMethods();
		sprite = new Sprite("testSprite");
		SoundManager.getInstance().setVolume(INITIALIZED_VALUE);
	}

	@Test
	public void testVolume() {
		float expectedVolume = SoundManager.getInstance().getVolume();

		expectedVolume += louderValue;
		Formula louder = new Formula(louderValue);

		sprite.getActionFactory().createChangeVolumeByNAction(sprite, new SequenceAction(), louder).act(1.0f);
		assertEquals(expectedVolume, SoundManager.getInstance().getVolume());

		expectedVolume += softerValue;
		Formula softer = new Formula(softerValue);

		sprite.getActionFactory().createChangeVolumeByNAction(sprite, new SequenceAction(), softer).act(1.0f);
		assertEquals(expectedVolume, SoundManager.getInstance().getVolume());
	}

	@Test
	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createChangeVolumeByNAction(sprite, new SequenceAction(), new Formula(String.valueOf(CHANGE_VALUE)))
				.act(1.0f);
		assertEquals(INITIALIZED_VALUE + CHANGE_VALUE, SoundManager.getInstance().getVolume());

		sprite.getActionFactory().createChangeVolumeByNAction(sprite, new SequenceAction(), new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + CHANGE_VALUE, SoundManager.getInstance().getVolume());
	}

	@Test
	public void testNullFormula() {
		sprite.getActionFactory().createChangeVolumeByNAction(sprite, new SequenceAction(), null).act(1.0f);
		assertEquals(INITIALIZED_VALUE, SoundManager.getInstance().getVolume());
	}

	@Test
	public void testNotANumberFormula() {
		sprite.getActionFactory().createChangeVolumeByNAction(sprite, new SequenceAction(), new Formula(Double.NaN)).act(1.0f);
		assertEquals(INITIALIZED_VALUE, SoundManager.getInstance().getVolume());
	}
}
