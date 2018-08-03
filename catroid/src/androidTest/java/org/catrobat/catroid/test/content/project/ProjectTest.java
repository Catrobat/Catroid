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
package org.catrobat.catroid.test.content.project;

import android.content.pm.PackageInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProjectTest {

	private static final float OLD_LANGUAGE_VERSION = 0.8f;
	private static final String OLD_APPLICATION_NAME = "catty";
	private static final String OLD_PLATFORM = "iOS";

	@Test
	public void testVersionName() throws Exception {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		PackageInfo packageInfo = InstrumentationRegistry.getTargetContext().getPackageManager()
				.getPackageInfo(InstrumentationRegistry.getTargetContext().getPackageName(), 0);

		XmlHeader projectXmlHeader = project.getXmlHeader();

		assertEquals(packageInfo.versionName, (String) Reflection.getPrivateField(projectXmlHeader, "applicationVersion"));
	}

	@Test
	public void testAddRemoveSprite() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		Scene scene = project.getDefaultScene();
		Sprite bottomSprite = new SingleSprite("bottom");
		Sprite topSprite = new SingleSprite("top");

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
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
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
	public void testSetDeviceData() throws Exception {
		Project project = new Project();
		XmlHeader header = project.getXmlHeader();
		Reflection.setPrivateField(header, "catrobatLanguageVersion", OLD_LANGUAGE_VERSION);
		Reflection.setPrivateField(header, "applicationName", OLD_APPLICATION_NAME);
		Reflection.setPrivateField(header, "platform", OLD_PLATFORM);

		float languageVersion = (Float) Reflection.getPrivateField(header, "catrobatLanguageVersion");
		assertEquals(OLD_LANGUAGE_VERSION, languageVersion);

		String applicationName = (String) Reflection.getPrivateField(header, "applicationName");
		assertEquals(OLD_APPLICATION_NAME, applicationName);

		String platform = (String) Reflection.getPrivateField(header, "platform");
		assertEquals(OLD_PLATFORM, platform);

		project.setDeviceData(InstrumentationRegistry.getTargetContext());

		languageVersion = (Float) Reflection.getPrivateField(header, "catrobatLanguageVersion");
		assertEquals(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, languageVersion);

		applicationName = (String) Reflection.getPrivateField(header, "applicationName");
		assertEquals(InstrumentationRegistry.getTargetContext().getString(R.string.app_name), applicationName);

		platform = (String) Reflection.getPrivateField(header, "platform");
		assertEquals(Constants.PLATFORM_NAME, platform);
	}
}
