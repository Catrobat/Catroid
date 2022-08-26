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

package org.catrobat.catroid.test.utiltests;

import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@RunWith(AndroidJUnit4.class)
public class DefaultProjectComparatorTest {

	private static final String PROJECT_NAME = DefaultProjectComparatorTest.class.getSimpleName();
	private Project defaultProject;
	private File projectDir = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME);

	private int screenWidthBuffer;
	private int screenHeightBuffer;

	@Before
	public void setUp() throws IOException {
		screenWidthBuffer = ScreenValues.SCREEN_WIDTH;
		screenHeightBuffer = ScreenValues.SCREEN_HEIGHT;

		ScreenValues.SCREEN_WIDTH = 480;
		ScreenValues.SCREEN_HEIGHT = 800;

		if (projectDir.isDirectory()) {
			StorageOperations.deleteDir(projectDir);
		}

		defaultProject = DefaultProjectHandler
				.createAndSaveDefaultProject(PROJECT_NAME, ApplicationProvider.getApplicationContext(), false);
	}

	@After
	public void tearDown() throws Exception {
		ScreenValues.SCREEN_WIDTH = screenWidthBuffer;
		ScreenValues.SCREEN_HEIGHT = screenHeightBuffer;

		if (projectDir.isDirectory()) {
			StorageOperations.deleteDir(projectDir);
		}
	}

	@Test
	public void testCompareProjectToDefaultProject() {
		assertTrue(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));

		addSpriteAndCompareToDefaultProject();
		addScriptAndCompareToDefalutProject();
		addBrickAndCompareToDefaultProject();
		removeBrickAndCompareToDefaultProject();
		removeScriptAndCompareToDefaultProject();
		removeSpriteAndCompareToDefaultProject();
	}

	private void addSpriteAndCompareToDefaultProject() {
		Sprite sprite = new Sprite("TestSprite");
		defaultProject.getDefaultScene().addSprite(sprite);
		assertFalse(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
		defaultProject.getDefaultScene().removeSprite(sprite);
		assertTrue(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
	}

	private void addScriptAndCompareToDefalutProject() {
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		WhenScript whenScript = new WhenScript();
		sprite.addScript(whenScript);
		assertFalse(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
		sprite.removeScript(whenScript);
		assertTrue(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
	}

	private void addBrickAndCompareToDefaultProject() {
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		Brick brick = new HideBrick();
		Script script = sprite.getScript(0);
		script.addBrick(brick);
		assertFalse(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
		script.removeBrick(brick);
		assertTrue(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
	}

	private void removeBrickAndCompareToDefaultProject() {
		Script script = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		List<Brick> brickList = script.getBrickList();
		Brick brick = brickList.get(brickList.size() - 1);
		brickList.remove(brickList.size() - 1);
		assertFalse(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));

		brickList.add(brick);
		assertTrue(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
	}

	private void removeScriptAndCompareToDefaultProject() {
		Script script = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		sprite.removeScript(script);
		assertFalse(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));

		sprite.addScript(script);
		assertTrue(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
	}

	private void removeSpriteAndCompareToDefaultProject() {
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(3);
		int lastIndex = defaultProject.getDefaultScene().getSpriteList().size() - 1;
		List<Sprite> spriteList = defaultProject.getDefaultScene().getSpriteList();
		spriteList.remove(lastIndex);
		assertFalse(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));

		spriteList.add(sprite);
		assertTrue(Utils.isDefaultProject(defaultProject, ApplicationProvider.getApplicationContext()));
	}
}
