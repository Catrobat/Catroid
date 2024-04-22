/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.After;
import org.koin.core.module.Module;

import java.util.Collections;
import java.util.List;

import org.catrobat.catroid.koin.CatroidKoinHelperKt;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;

@RunWith(JUnit4.class)
public class SetYActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float Y_POSITION = 73.3f;
	private final Formula yPosition = new Formula(Y_POSITION);
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite sprite;

	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		initializeStaticSingletonMethods(dependencyModules);
		sprite = new Sprite("testSprite");
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testNormalBehavior() {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetYAction(sprite, new SequenceAction(), yPosition).act(1.0f);
		assertEquals(Y_POSITION, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test(expected = NullPointerException.class)
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createSetYAction(null, new SequenceAction(), yPosition);
		action.act(1.0f);
	}

	@Test
	public void testBoundaryPositions() {
		sprite.getActionFactory().createSetYAction(sprite, new SequenceAction(), new Formula(Integer.MAX_VALUE)).act(1.0f);
		assertEquals(Integer.MAX_VALUE, (int) sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetYAction(sprite, new SequenceAction(), new Formula(Integer.MIN_VALUE)).act(1.0f);
		assertEquals(Integer.MIN_VALUE, (int) sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createSetYAction(sprite, new SequenceAction(), new Formula(String.valueOf(Y_POSITION))).act(1.0f);
		assertEquals(Y_POSITION, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetYAction(sprite, new SequenceAction(), new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals(Y_POSITION, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		sprite.getActionFactory().createSetYAction(sprite, new SequenceAction(), null).act(1.0f);
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNotANumberFormula() {
		sprite.getActionFactory().createSetYAction(sprite, new SequenceAction(), new Formula(Double.NaN)).act(1.0f);
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
