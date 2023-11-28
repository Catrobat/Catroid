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
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.junit.After;
import org.koin.core.module.Module;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ChangeSizeByNActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float INITIALIZED_VALUE = 100f;
	private static final float CHANGE_VALUE = 44.4f;
	private static final String NOT_NUMERICAL_STRING = "size";
	private static final float CHANGE_SIZE = 20f;
	private static final float DELTA = 0.0001f;
	private Sprite sprite;

	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		MockUtil.mockContextForProject(dependencyModules);
		sprite = new Sprite("testSprite");
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testSize() {
		assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeSizeByNAction(sprite,
				new SequenceAction(), new Formula(CHANGE_SIZE)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + CHANGE_SIZE, sprite.look.getSizeInUserInterfaceDimensionUnit(), DELTA);

		sprite.getActionFactory().createChangeSizeByNAction(sprite, new SequenceAction(),
				new Formula(-CHANGE_SIZE)).act(1.0f);
		assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit(), DELTA);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createChangeSizeByNAction(null, new SequenceAction(),
				new Formula(CHANGE_SIZE));
		action.act(1.0f);
	}

	@Test
	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createChangeSizeByNAction(sprite, new SequenceAction(), new Formula(String.valueOf(CHANGE_VALUE)))
				.act(1.0f);
		assertEquals(INITIALIZED_VALUE + CHANGE_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeSizeByNAction(sprite, new SequenceAction(), new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + CHANGE_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		sprite.getActionFactory().createChangeSizeByNAction(sprite, new SequenceAction(), null).act(1.0f);
		assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNotANumberFormula() {
		sprite.getActionFactory().createChangeSizeByNAction(sprite, new SequenceAction(), new Formula(Double.NaN)).act(1.0f);
		assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit());
	}
}
