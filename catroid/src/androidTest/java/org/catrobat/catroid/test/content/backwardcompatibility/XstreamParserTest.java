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

package org.catrobat.catroid.test.content.backwardcompatibility;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.ZipArchiver;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class XstreamParserTest {

	private File projectDir;

	@After
	public void tearDown() throws IOException {
		if (projectDir != null && projectDir.isDirectory()) {
			StorageOperations.deleteDir(projectDir);
		}
	}

	private void copyProjectFromAssets(String assetName, String projectName) throws IOException {
		InputStream inputStream = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(assetName);
		new ZipArchiver().unzip(inputStream, new File(DEFAULT_ROOT_DIRECTORY, projectName));
	}

	private void testLoadProjectWithoutScenes(String projectName, String assetName) throws IOException, LoadingProjectException {
		copyProjectFromAssets(assetName, projectName);
		projectDir = new File(DEFAULT_ROOT_DIRECTORY, projectName);

		Project project = XstreamSerializer.getInstance()
				.loadProject(projectDir, ApplicationProvider.getApplicationContext());

		assertNotNull(project);

		assertEquals(projectName, project.getName());

		assertEquals(1, project.getSceneList().size());

		assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.default_scene_name),
				project.getSceneList().get(0).getName());
	}

	@Test
	public void testLoadProjectWithLanguageVersion08() throws IOException, LoadingProjectException {
		String projectName = "Falling balls";
		String assetName = "Falling_balls.catrobat";

		testLoadProjectWithoutScenes(projectName, assetName);
	}

	@Test
	public void testLoadProjectLanguageVersion091() throws IOException, LoadingProjectException {
		String projectName = "Air fight 0.5";
		String assetName = "Air_fight_0.5.catrobat";

		testLoadProjectWithoutScenes(projectName, assetName);
	}

	@Test
	public void testLoadProjectLanguageVersion092() throws IOException, LoadingProjectException {
		String projectName = "NoteAndSpeakBrick";
		String assetName = "Note_And_Speak_Brick.catrobat";

		testLoadProjectWithoutScenes(projectName, assetName);
	}

	@Test
	public void testLoadProjectLanguageVersion095() throws IOException, LoadingProjectException {
		String projectName = "GhostEffectBricks";
		String assetName = "Ghost_Effect_Bricks.catrobat";

		testLoadProjectWithoutScenes(projectName, assetName);
	}

	@Test
	public void testLoadProjectLanguageVersion0999() throws IOException, LoadingProjectException {
		String projectName = "TestUserDataConversion0999To09991";
		String assetName = "TestUserDataConversion0999To09991.catrobat";

		copyProjectFromAssets(assetName, projectName);
		projectDir = new File(DEFAULT_ROOT_DIRECTORY, projectName);

		Project project = XstreamSerializer.getInstance()
				.loadProject(projectDir, ApplicationProvider.getApplicationContext());

		assertNotNull(project);

		assertEquals(projectName, project.getName());

		assertEquals(2, project.getSceneList().size());

		Scene scene1 = project.getSceneList().get(0);
		Scene scene2 = project.getSceneList().get(1);

		assertEquals("Scene 1",
				scene1.getName());

		assertEquals("Scene 2",
				scene2.getName());

		Scope scopeLocal = new Scope(project, scene1.getSprite("SpriteWithLocalVarAndList"),
				new SequenceAction());
		Scope scopeGlobal = new Scope(project, scene1.getSprite("SpriteWithGlobalVarAndList"),
				new SequenceAction());

		assertNotNull(UserDataWrapper.getUserVariable("localVar", scopeLocal));

		assertNotNull(UserDataWrapper.getUserList("localList", scopeLocal));

		assertNull(UserDataWrapper.getUserVariable("localVar", scopeGlobal));

		assertNull(UserDataWrapper.getUserList("localList", scopeGlobal));

		assertNotNull(UserDataWrapper.getUserVariable("globalVar", scopeLocal));

		assertNotNull(UserDataWrapper.getUserList("globalList", scopeLocal));

		assertNull(UserDataWrapper.getUserVariable("localVar", scopeGlobal));

		assertNull(UserDataWrapper.getUserList("localList", scopeGlobal));

		assertNotSame(UserDataWrapper.getUserVariable("localVar", scopeLocal),
				UserDataWrapper.getUserVariable("globalList", scopeLocal));

		assertNotSame(UserDataWrapper.getUserList("localList", scopeLocal),
				UserDataWrapper.getUserList("globalList", scopeLocal));
	}
}
