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

package org.catrobat.catroid.test.content.script;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class WhenConditionScriptTest {
	private static final int POSITION_DELTA = 15;
	private Formula formula;
	private Sprite sprite;
	private WhenConditionScript conditionScript;
	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() {
		sprite = new Sprite("testSprite");
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
		createProjectWithSprite(sprite);

		formula = Mockito.mock(Formula.class);
		conditionScript = new WhenConditionScript(formula);
		sprite.addScript(conditionScript);
	}

	private void createProjectWithSprite(Sprite sprite) {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "TestProject");
		projectManager.getValue().setCurrentProject(project);
		project.getDefaultScene().addSprite(sprite);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void executeWhenConditionScriptOnce() throws InterpretationException {
		when(formula.interpretBoolean(any(Scope.class))).thenReturn(true);
		conditionScript.addBrick(new ChangeXByNBrick(POSITION_DELTA));
		sprite.initializeEventThreads(EventId.START);
		sprite.initConditionScriptTriggers();

		sprite.look.act(1.0f);
		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals(POSITION_DELTA, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void executeWhenConditionScriptMultipleTimes() throws InterpretationException {
		when(formula.interpretBoolean(any(Scope.class))).thenReturn(true, true, false, true);
		conditionScript.addBrick(new ChangeXByNBrick(POSITION_DELTA));
		sprite.initializeEventThreads(EventId.START);
		sprite.initConditionScriptTriggers();

		for (int i = 0; i < 10; i++) {
			sprite.look.act(1.0f);
		}

		assertEquals(POSITION_DELTA * 2, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void executeWhenConditionScriptBeforeAndAfterBeingStopped() throws InterpretationException {
		when(formula.interpretBoolean(any(Scope.class))).thenReturn(true, true, false, true);
		conditionScript.addBrick(new ChangeXByNBrick(POSITION_DELTA));
		conditionScript.addBrick(new StopScriptBrick(0));
		sprite.initializeEventThreads(EventId.START);
		sprite.initConditionScriptTriggers();

		for (int i = 0; i < 10; i++) {
			sprite.look.act(1.0f);
		}

		assertEquals(POSITION_DELTA * 2, (int) sprite.look.getXInUserInterfaceDimensionUnit());
	}
}
