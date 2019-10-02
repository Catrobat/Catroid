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

package org.catrobat.catroid.test.utiltests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	private DefaultProjectHandler defaultProjectHandler;

	@Before
	public void setUp() throws IOException {
		screenWidthBuffer = ScreenValues.SCREEN_WIDTH;
		screenHeightBuffer = ScreenValues.SCREEN_HEIGHT;

		ScreenValues.SCREEN_WIDTH = 480;
		ScreenValues.SCREEN_HEIGHT = 800;

		if (projectDir.isDirectory()) {
			StorageOperations.deleteDir(projectDir);
		}

		defaultProjectHandler = TestUtils.createDefaultProjectHandler(InstrumentationRegistry.getTargetContext());
		defaultProject = defaultProjectHandler
				.createAndSaveDefaultProject(PROJECT_NAME, false);
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
		assertTrue(defaultProjectHandler.isDefaultProject(defaultProject
		));

		addSpriteAndCompareToDefaultProject();
		addScriptAndCompareToDefalutProject();
		addBrickAndCompareToDefaultProject();
		removeBrickAndCompareToDefaultProject();
		removeScriptAndCompareToDefaultProject();
		removeSpriteAndCompareToDefaultProject();
	}

	private void addSpriteAndCompareToDefaultProject() {
		Sprite sprite = new SingleSprite("TestSprite");
		defaultProject.getDefaultScene().addSprite(sprite);
		assertFalse(defaultProjectHandler.isDefaultProject(defaultProject));
		defaultProject.getDefaultScene().removeSprite(sprite);
		assertTrue(defaultProjectHandler.isDefaultProject(defaultProject));
	}

	private void addScriptAndCompareToDefalutProject() {
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		WhenScript whenScript = new WhenScript();
		sprite.addScript(whenScript);
		assertFalse(defaultProjectHandler.isDefaultProject(defaultProject));
		sprite.removeScript(whenScript);
		assertTrue(defaultProjectHandler.isDefaultProject(defaultProject));
	}

	private void addBrickAndCompareToDefaultProject() {
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		Brick brick = new HideBrick();
		Script script = sprite.getScript(0);
		script.addBrick(brick);
		assertFalse(defaultProjectHandler.isDefaultProject(defaultProject));
		script.removeBrick(brick);
		assertTrue(defaultProjectHandler.isDefaultProject(defaultProject));
	}

	private void removeBrickAndCompareToDefaultProject() {
		Script script = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		List<Brick> brickList = script.getBrickList();
		Brick brick = brickList.get(brickList.size() - 1);
		brickList.remove(brickList.size() - 1);
		assertFalse(defaultProjectHandler.isDefaultProject(defaultProject));

		brickList.add(brick);
		assertTrue(defaultProjectHandler.isDefaultProject(defaultProject));
	}

	private void removeScriptAndCompareToDefaultProject() {
		Script script = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		sprite.removeScript(script);
		assertFalse(defaultProjectHandler.isDefaultProject(defaultProject));

		sprite.addScript(script);
		assertTrue(defaultProjectHandler.isDefaultProject(defaultProject));
	}

	private void removeSpriteAndCompareToDefaultProject() {
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(3);
		int lastIndex = defaultProject.getDefaultScene().getSpriteList().size() - 1;
		List<Sprite> spriteList = defaultProject.getDefaultScene().getSpriteList();
		spriteList.remove(lastIndex);
		assertFalse(defaultProjectHandler.isDefaultProject(defaultProject));

		spriteList.add(sprite);
		assertTrue(defaultProjectHandler.isDefaultProject(defaultProject));
	}
}
