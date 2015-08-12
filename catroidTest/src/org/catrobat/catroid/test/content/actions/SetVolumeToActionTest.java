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
package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;

public class SetVolumeToActionTest extends InstrumentationTestCase {

	private static final float VOLUME = 91f;
	private final Formula volume = new Formula(VOLUME);
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testVolume() {
		ExtendedActions.setVolumeTo(sprite, volume).act(1.0f);
		assertEquals("Incorrect sprite volume value after SetVolumeToBrick executed", VOLUME,
				SoundManager.getInstance().getVolume());
	}

	public void testBrickWithStringFormula() {
		ExtendedActions.setVolumeTo(sprite, new Formula(String.valueOf(VOLUME))).act(1.0f);
		assertEquals("Incorrect sprite volume value after SetVolumeToBrick executed", VOLUME, SoundManager.getInstance()
				.getVolume());

		ExtendedActions.setVolumeTo(sprite, new Formula(String.valueOf(NOT_NUMERICAL_STRING))).act(1.0f);
		assertEquals("Incorrect sprite volume value after SetVolumeToBrick executed", VOLUME, SoundManager.getInstance()
				.getVolume());
	}

	public void testNullFormula() {
		ExtendedActions.setVolumeTo(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite volume value after SetVolumeToBrick executed", 0f, SoundManager.getInstance()
				.getVolume());
	}

	public void testNotANumberFormula() {
		ExtendedActions.setVolumeTo(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite volume value after SetVolumeToBrick executed", VOLUME, SoundManager.getInstance()
				.getVolume());
	}
}
