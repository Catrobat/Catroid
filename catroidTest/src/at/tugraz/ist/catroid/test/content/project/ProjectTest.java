/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.project;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;

public class ProjectTest extends AndroidTestCase {
	public void testVersionNameAndNumber() throws NameNotFoundException {
		Project project = new Project(getContext(), "testProject");
		PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo("at.tugraz.ist.catroid", 0);
		assertEquals("Incorrect version name", packageInfo.versionName, project.getVersionName());
		assertEquals("Incorrect version code", packageInfo.versionCode, project.getVersionCode());
	}

	public void testAddRemoveSprite() throws NameNotFoundException {
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

	public void testGetMaxZValue() throws NameNotFoundException {
		Project project = new Project(getContext(), "testProject");
		Sprite bottomSprite = new Sprite("bottom");
		Sprite topSprite = new Sprite("top");

		project.addSprite(bottomSprite);
		project.addSprite(topSprite);

		final int maxZ = 17;

		bottomSprite.setZPosition(maxZ - 5);
		topSprite.setZPosition(maxZ);

		assertEquals("Maximum Z value was incorrect", project.getMaxZValue(), maxZ);
	}
}
