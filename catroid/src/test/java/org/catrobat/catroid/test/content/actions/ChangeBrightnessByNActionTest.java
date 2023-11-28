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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.catrobat.catroid.koin.CatroidKoinHelperKt;

import android.content.Context;

import org.koin.core.module.Module;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(JUnit4.class)
public class ChangeBrightnessByNActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float INITIALIZED_VALUE = 100f;
	private static final String NOT_NUMERICAL_STRING = "brightness";
	private static final float BRIGHTER_VALUE = 50.5f;
	private static final float DIMMER_VALUE = -20.8f;
	private Sprite sprite;

	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "Project");

		sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);
		inject(ProjectManager.class).getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testNormalBehavior() {
		assertEquals(INITIALIZED_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeBrightnessByNAction(sprite,
				new SequenceAction(), new Formula(BRIGHTER_VALUE)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + BRIGHTER_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeBrightnessByNAction(sprite,
			new SequenceAction(), new Formula(DIMMER_VALUE)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + BRIGHTER_VALUE + DIMMER_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	@Test(expected = NullPointerException.class)
	public void testNullSprite() {
		Action action = sprite.getActionFactory().createChangeBrightnessByNAction(null,
				new SequenceAction(), new Formula(BRIGHTER_VALUE));
		action.act(1.0f);
	}

	@Test
	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createChangeBrightnessByNAction(sprite,
				new SequenceAction(), new Formula(String.valueOf(BRIGHTER_VALUE))).act(1.0f);
		assertEquals(INITIALIZED_VALUE + BRIGHTER_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeBrightnessByNAction(sprite,
				new SequenceAction(), new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + BRIGHTER_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		sprite.getActionFactory().createChangeBrightnessByNAction(sprite, new SequenceAction(),
				null).act(1.0f);
		assertEquals(INITIALIZED_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNotANumberFormula() {
		sprite.getActionFactory().createChangeBrightnessByNAction(sprite,
			new SequenceAction(), new Formula(Double.NaN)).act(1.0f);
		assertEquals(INITIALIZED_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}
}
