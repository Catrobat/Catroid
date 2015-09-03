/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.test.utils.Reflection;

public class ProjectTest extends AndroidTestCase {

	private static final float OLD_LANGUAGE_VERSION = 0.8f;
	private static final String OLD_APPLICATION_NAME = "catty";
	private static final String OLD_PLATFORM = "iOS";

	public void testVersionName() throws NameNotFoundException {
		Project project = new Project(getContext(), "testProject");
		PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
		XmlHeader projectXmlHeader = project.getXmlHeader();
		assertEquals("Incorrect version name", packageInfo.versionName,
				(String) Reflection.getPrivateField(projectXmlHeader, "applicationVersion"));
	}

	public void testAddRemoveSprite() {
		Project project = new Project(getContext(), "testProject");
		Sprite bottomSprite = new Sprite("bottom");
		Sprite topSprite = new Sprite("top");

		project.addSprite(bottomSprite);
		project.addSprite(topSprite);

		assertTrue("spriteList did not contain bottomSprite", project.getSpriteList().contains(bottomSprite));
		assertTrue("spriteList did not contain topSprite", project.getSpriteList().contains(topSprite));

		assertTrue("bottomSprite was not removed from data structure", project.removeSprite(bottomSprite));
		assertFalse("bottomSprite was not removed from data structure", project.getSpriteList().contains(bottomSprite));
		assertFalse("bottomSprite could be removed from data structure twice", project.removeSprite(bottomSprite));

		assertTrue("topSprite was not removed from data structure", project.removeSprite(topSprite));
		assertFalse("topSprite was not removed from data structure", project.getSpriteList().contains(topSprite));
	}

	public void testSetDeviceData() {
		Project project = new Project();
		XmlHeader header = project.getXmlHeader();
		Reflection.setPrivateField(header, "catrobatLanguageVersion", OLD_LANGUAGE_VERSION);
		Reflection.setPrivateField(header, "applicationName", OLD_APPLICATION_NAME);
		Reflection.setPrivateField(header, "platform", OLD_PLATFORM);

		float languageVersion = (Float) Reflection.getPrivateField(header, "catrobatLanguageVersion");
		assertEquals("Version should be old", OLD_LANGUAGE_VERSION, languageVersion);

		String applicationName = (String) Reflection.getPrivateField(header, "applicationName");
		assertEquals("Application name should be the old one", OLD_APPLICATION_NAME, applicationName);

		String platform = (String) Reflection.getPrivateField(header, "platform");
		assertEquals("Platform should be the old one", OLD_PLATFORM, platform);

		// update the device data
		project.setDeviceData(getContext());

		languageVersion = (Float) Reflection.getPrivateField(header, "catrobatLanguageVersion");
		assertEquals("Version should be the current one", Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, languageVersion);

		applicationName = (String) Reflection.getPrivateField(header, "applicationName");
		assertEquals("Application name should be the current one", getContext().getString(R.string.app_name),
				applicationName);

		platform = (String) Reflection.getPrivateField(header, "platform");
		assertEquals("Platform should be the current one", Constants.PLATFORM_NAME, platform);
	}
}
