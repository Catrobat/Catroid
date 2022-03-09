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

import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.koin.core.module.Module;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class StartScriptTest {
	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() {
		PowerMockito.mockStatic(GdxNativesLoader.class);
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "Project");
		projectManager.getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testStartScript() {
		double size = 300;
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(size);

		testScript.addBrick(hideBrick);
		testScript.addBrick(setSizeToBrick);
		testSprite.addScript(testScript);

		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}

		assertFalse(testSprite.look.isLookVisible());
		assertEquals((float) size / 100, testSprite.look.getScaleX());
		assertEquals((float) size / 100, testSprite.look.getScaleY());
	}
}
