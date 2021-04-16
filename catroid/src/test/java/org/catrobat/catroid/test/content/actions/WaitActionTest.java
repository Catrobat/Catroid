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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.WaitAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class WaitActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final float VALUE = 2f;
	private static final float DELTA = 0.1f;
	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		Project project = new Project(MockUtil.mockContextForProject(), "Project");
		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testWait() {
		float waitOneSecond = 1.0f;
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(sprite, new SequenceAction(),
				new Formula(waitOneSecond));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));

		assertThat(action.getTime() - waitOneSecond, is(greaterThan(0.5f)));
	}

	@Test
	public void testBrickWithStringFormula() {
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(sprite, new SequenceAction(),
				new Formula(String.valueOf(VALUE)));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));

		assertThat(action.getTime() - VALUE, is(greaterThan(0.5f)));

		action = (WaitAction) factory.createDelayAction(sprite, new SequenceAction(),
				new Formula(NOT_NUMERICAL_STRING));
		currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertEquals(0f, action.getTime(), DELTA);
	}

	@Test(expected = NullPointerException.class)
	public void testNullFormula() {
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(null, new SequenceAction(),
				null);
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertEquals(0f, action.getTime(), DELTA);
	}

	@Test
	public void testNotANumberFormula() {
		ActionFactory factory = new ActionFactory();
		WaitAction action = (WaitAction) factory.createDelayAction(sprite, new SequenceAction(),
				new Formula(Double.NaN));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertEquals(0f, action.getTime(), DELTA);
	}
}
