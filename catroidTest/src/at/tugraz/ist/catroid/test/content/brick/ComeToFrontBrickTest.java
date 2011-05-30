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

package at.tugraz.ist.catroid.test.content.brick;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import android.view.View;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class ComeToFrontBrickTest extends AndroidTestCase {

	@Override
	public void tearDown() {
		TestUtils.clearProject("testProject");
	}

	public void testComeToFront() throws NameNotFoundException {
		Project project = new Project(getContext(), "testProject");

		Sprite bottomSprite = new Sprite("catroid");
		assertEquals("Unexpected initial z position of bottomSprite", 0, bottomSprite.getZPosition());

		Sprite topSprite = new Sprite("scratch");
		assertEquals("Unexpected initial z position of topSprite", 0, topSprite.getZPosition());

		topSprite.setZPosition(2);
		assertEquals("topSprite z position should now be 2", 2, topSprite.getZPosition());
		project.addSprite(bottomSprite);
		project.addSprite(topSprite);

		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(bottomSprite);
		ProjectManager.getInstance().setProject(project);
		comeToFrontBrick.execute();
		assertEquals("bottomSprite z position should now be 3", bottomSprite.getZPosition(), 3);
	}

	public void testNullSprite() throws NameNotFoundException {
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(null);

		try {
			comeToFrontBrick.execute();
			fail("Execution of ComeToFrontBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testBoundaries() throws NameNotFoundException {
		Project project = new Project(getContext(), "testProject");

		Sprite sprite = new Sprite("testSprite");
		sprite.setZPosition(Integer.MAX_VALUE);

		project.addSprite(sprite);

		ComeToFrontBrick brick = new ComeToFrontBrick(sprite);
		ProjectManager.getInstance().setProject(project);
		brick.execute();

		assertEquals("An Integer overflow occured during ComeToFrontBrick Execution", Integer.MAX_VALUE,
				sprite.getZPosition());
	}

	public void testGetView() throws NameNotFoundException {
		ProjectManager.getInstance().setProject(new Project(getContext(), "testProject"));
		ComeToFrontBrick brick = new ComeToFrontBrick(new Sprite("testSprite"));
		View view = brick.getView(getContext(), 1, null);
		assertNotNull("getView returned null", view);
	}
}
