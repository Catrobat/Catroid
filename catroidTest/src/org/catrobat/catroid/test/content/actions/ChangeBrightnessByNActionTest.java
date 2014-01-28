/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physic.content.ActionFactory;

public class ChangeBrightnessByNActionTest extends AndroidTestCase {

	private final Formula brighter = new Formula(50.5f);
	private final Formula dimmer = new Formula(-20.8f);

	Sprite sprite;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite brightness value", 100f,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		float brightness = sprite.look.getBrightnessInUserInterfaceDimensionUnit();
		brightness += brighter.interpretDouble(sprite);

		//ChangeBrightnessByNAction action1 = ExtendedActions.changeBrightnessByN(sprite, brighter);
		Action action1 = sprite.getActionFactory().createChangeBrightnessByNAction(sprite, brighter); // TODO[physic] 
		sprite.look.addAction(action1);
		action1.act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		brightness = sprite.look.getBrightnessInUserInterfaceDimensionUnit();
		brightness += dimmer.interpretDouble(sprite);

		//ChangeBrightnessByNAction action2 = ExtendedActions.changeBrightnessByN(sprite, dimmer);
		Action action2 = sprite.getActionFactory().createChangeBrightnessByNAction(sprite, dimmer); // TODO[physic] 
		sprite.look.addAction(action2);
		action2.act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createChangeBrightnessByNAction(null, dimmer); // TODO[physic] ExtendedActions
		try {
			action.act(1.0f);
			fail("Execution of ChangeBrightnessByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown correctly", true);
		}
	}
}
