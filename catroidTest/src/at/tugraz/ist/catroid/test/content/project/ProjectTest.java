/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.project;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class ProjectTest extends AndroidTestCase {

	public void testVersionNameAndNumber() throws NameNotFoundException {
		Project project = new Project(getContext(), "testProject");
		PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo("at.tugraz.ist.catroid", 0);
		assertEquals("Incorrect version name", packageInfo.versionName, (String) TestUtils.getPrivateField(
				"catroidVersionName", project, false));
		assertEquals("Incorrect version code", packageInfo.versionCode, TestUtils.getPrivateField("catroidVersionCode",
				project, false));
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
}
