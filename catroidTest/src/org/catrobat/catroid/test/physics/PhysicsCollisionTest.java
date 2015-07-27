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

package org.catrobat.catroid.test.physics;

import android.test.InstrumentationTestCase;
import android.util.Log;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.test.utils.Reflection;

public class PhysicsCollisionTest extends InstrumentationTestCase {

	public void testGenerateKeyForCollisionBetween() {
		PhysicsCollision physicsCollision = new PhysicsCollision(null);
		Sprite sprite1 = new Sprite("testsprite_1234()77//Njasd%&klf");
		Sprite sprite2 = new Sprite("spritetest_9087124356iguaöwdzf() //OGZLUSDüKJGFLHKsd");

		Object[] values1 = {sprite1, sprite2};
		Reflection.ParameterList paramList = new Reflection.ParameterList(values1);
		String key1 = (String) Reflection.invokeMethod(PhysicsCollision.class, physicsCollision, "generateKey", paramList);

		Object[] values2 = {sprite2, sprite1};
		paramList = new Reflection.ParameterList(values2);
		String key2 = (String) Reflection.invokeMethod(PhysicsCollision.class, physicsCollision, "generateKey", paramList);

		String key1SubstringSprite1 = key1.substring(0, sprite1.getName().length());
		String key1SubstringSprite2 = key1.substring(key1.length() - sprite2.getName().length(), key1.length());
		String key2SubstringSprite1 = key2.substring(key1.length() - sprite1.getName().length(), key2.length());
		String key2SubstringSprite2 = key2.substring(0, sprite2.getName().length());
		Log.d("phill_test", key1SubstringSprite1);
		Log.d("phill_test", key1SubstringSprite2);
		Log.d("phill_test", key2SubstringSprite1);
		Log.d("phill_test", key2SubstringSprite2);
		assertEquals("sprite1 name not equal to key1 partition", sprite1.getName(), key1SubstringSprite1);
		assertEquals("sprite2 name not equal to key1 partition", sprite2.getName(), key1SubstringSprite2);
		assertEquals("sprite1 name not equal to key2 partition", sprite1.getName(), key2SubstringSprite1);
		assertEquals("sprite2 name not equal to key2 partition", sprite2.getName(), key2SubstringSprite2);
	}
}
