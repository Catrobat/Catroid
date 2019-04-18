/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.actions.WaitAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class WaitActionTest {

	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final float VALUE = 2f;
	private static final float DELTA = 0.1f;

	@Test
	public void testWait() {
		float waitOneSecond = 1.0f;
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(null, new Formula(waitOneSecond));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));

		assertThat(action.getTime() - waitOneSecond, is(greaterThan(0.5f)));
	}

	@Test
	public void testBrickWithStringFormula() {
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(null, new Formula(String.valueOf(VALUE)));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));

		assertThat(action.getTime() - VALUE, is(greaterThan(0.5f)));

		action = (WaitAction) factory.createDelayAction(null, new Formula(NOT_NUMERICAL_STRING));
		currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertEquals(0f, action.getTime(), DELTA);
	}

	@Test
	public void testNullFormula() {
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(null, null);
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertEquals(0f, action.getTime(), DELTA);
	}

	@Test
	public void testNotANumberFormula() {
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(null, new Formula(Double.NaN));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertEquals(0f, action.getTime(), DELTA);
	}
}
