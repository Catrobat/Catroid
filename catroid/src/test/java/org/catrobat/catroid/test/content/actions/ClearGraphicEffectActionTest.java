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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ClearGraphicEffectActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testClearGraphicEffect() {
		float value = 80f;
		Sprite sprite = new Sprite("new Sprite");
		sprite.look.setTransparencyInUserInterfaceDimensionUnit(value);
		assertEquals(value, sprite.look.getTransparencyInUserInterfaceDimensionUnit());
		sprite.look.setBrightnessInUserInterfaceDimensionUnit(value);
		assertEquals(value, sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createClearGraphicEffectAction(sprite);
		action.act(1.0f);
		assertEquals(0f, sprite.look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals(100f, sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createClearGraphicEffectAction(null);
		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}
}
