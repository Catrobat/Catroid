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
package org.catrobat.catroid.test.content.project;

import android.content.Context;
import android.os.Build;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ProjectTest {

	private static final double OLD_LANGUAGE_VERSION = 0.8;
	private static final String OLD_APPLICATION_NAME = "catty";
	private static final String OLD_PLATFORM = "iOS";
	private static final String OLD_PLATFORM_VERSION = "1.0.0 beta";

	@Test
	public void testVersionName() {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		XmlHeader projectXmlHeader = project.getXmlHeader();

		assertEquals("testStub", projectXmlHeader.getApplicationVersion());
	}

	@Test
	public void testAddRemoveSprite() {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		Scene scene = project.getDefaultScene();
		Sprite bottomSprite = new Sprite("bottom");
		Sprite topSprite = new Sprite("top");

		scene.addSprite(bottomSprite);
		scene.addSprite(topSprite);

		assertTrue(scene.getSpriteList().contains(bottomSprite));
		assertTrue(scene.getSpriteList().contains(topSprite));

		assertTrue(scene.removeSprite(bottomSprite));
		assertFalse(scene.getSpriteList().contains(bottomSprite));
		assertFalse(scene.removeSprite(bottomSprite));

		assertTrue(scene.removeSprite(topSprite));
		assertFalse(scene.getSpriteList().contains(topSprite));
	}

	@Test
	public void testAddRemoveScene() {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		Scene sceneOne = new Scene("test1", project);
		Scene sceneTwo = new Scene("test2", project);

		project.addScene(sceneOne);
		project.addScene(sceneTwo);

		assertTrue(project.getSceneList().contains(sceneOne));
		assertTrue(project.getSceneList().contains(sceneTwo));

		project.removeScene(sceneOne);
		project.removeScene(sceneTwo);

		assertFalse(project.getSceneList().contains(sceneOne));
		assertFalse(project.getSceneNames().contains(sceneOne.getName()));

		assertFalse(project.getSceneList().contains(sceneTwo));
		assertFalse(project.getSceneNames().contains(sceneTwo.getName()));
	}

	@Test
	public void testSetDeviceData() {
		Project project = new Project();
		XmlHeader header = project.getXmlHeader();

		header.setCatrobatLanguageVersion(OLD_LANGUAGE_VERSION);
		header.setApplicationName(OLD_APPLICATION_NAME);
		header.setPlatform(OLD_PLATFORM);
		header.setPlatformVersion(OLD_PLATFORM_VERSION);

		project.setDeviceData(MockUtil.mockContextForProject());

		assertEquals(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, header.getCatrobatLanguageVersion());
		assertEquals(Mockito.mock(Context.class).getString(R.string.app_name), header.getApplicationName());
		assertEquals(Constants.PLATFORM_NAME, header.getPlatform());
		assertEquals(String.valueOf(Build.VERSION.SDK_INT), header.getPlatformVersion());
	}
}
