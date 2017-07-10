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

package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetLookByIndexActionTest extends SetLookActionTest {

	public void testSetLookByIndex() {
		Formula formula = new Formula(1);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action didn't set the first LookData", firstLookData, sprite.look.getLookData());

		formula = new Formula(2);
		action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action didn't set the second LookData", secondLookData, sprite.look.getLookData());
	}

	public void testSetLookByWrongIndex() {
		sprite.look.setLookData(firstLookData);

		Formula formula = new Formula(-1);
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action did set Lookdata wrongly with negative Formula value.", firstLookData, sprite.look
				.getLookData());

		formula = new Formula(42);
		action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action did set Lookdata wrongly with wrong Formula value.", firstLookData,
				sprite.look.getLookData());
	}
}
