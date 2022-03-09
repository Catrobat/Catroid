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
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.koin.core.module.Module;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class WhenBackgroundChangesScriptTest {
	private Sprite sprite;
	private WhenBackgroundChangesScript whenBgChangesScript;
	private Script startScript;
	private LookData bg1;
	private LookData bg2;
	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() {
		PowerMockito.mockStatic(GdxNativesLoader.class);

		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "TestProject");
		projectManager.getValue().setCurrentProject(project);
		sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		initBackground(project.getDefaultScene());
		initScripts();

		sprite.look.setPositionInUserInterfaceDimensionUnit(0, 0);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	private void initBackground(Scene scene) {
		initLookData();
		Sprite background = scene.getBackgroundSprite();
		background.look = Mockito.spy(background.look);
		when(background.look.getZIndex()).thenReturn(Constants.Z_INDEX_BACKGROUND);
		background.getLookList().add(bg1);
		background.getLookList().add(bg2);
		scene.getSpriteList().set(0, background);
	}

	private void initLookData() {
		bg1 = new LookData("bg1", new File("bg1"));
		bg2 = new LookData("bg2", new File("bg2"));
	}

	private void initScripts() {
		whenBgChangesScript = new WhenBackgroundChangesScript();
		whenBgChangesScript.setLook(bg2);
		startScript = new StartScript();
		sprite.addScript(startScript);
		sprite.addScript(whenBgChangesScript);
	}

	@Test
	public void testScriptBasic() {
		final int position = 15;
		SetBackgroundBrick setBackgroundBrick = new SetBackgroundBrick();
		setBackgroundBrick.setLook(bg2);
		startScript.addBrick(setBackgroundBrick);
		whenBgChangesScript.addBrick(new SetXBrick(position));
		sprite.initializeEventThreads(EventId.START);

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals((float) position, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testScriptWithWrongBackground() {
		final int position = 15;
		SetBackgroundBrick setBackgroundBrick = new SetBackgroundBrick();
		setBackgroundBrick.setLook(bg1);
		startScript.addBrick(setBackgroundBrick);
		whenBgChangesScript.addBrick(new SetXBrick(position));
		sprite.initializeEventThreads(EventId.START);

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals((float) 0, sprite.look.getXInUserInterfaceDimensionUnit());
	}

	@Test
	public void testRestartScript() {
		final int position = 15;
		SetBackgroundBrick setBackgroundBrick1 = new SetBackgroundBrick();
		setBackgroundBrick1.setLook(bg1);
		SetBackgroundBrick setBackgroundBrick2 = new SetBackgroundBrick();
		setBackgroundBrick2.setLook(bg2);
		startScript.addBrick(setBackgroundBrick2);
		startScript.addBrick(setBackgroundBrick1);
		startScript.addBrick(setBackgroundBrick2);
		whenBgChangesScript.addBrick(new SetXBrick(position));
		sprite.initializeEventThreads(EventId.START);

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals((float) position, sprite.look.getXInUserInterfaceDimensionUnit());
	}
}
