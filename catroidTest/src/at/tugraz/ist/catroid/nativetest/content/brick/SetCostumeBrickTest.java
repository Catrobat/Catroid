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
package at.tugraz.ist.catroid.nativetest.content.brick;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.stage.NativeAppActivity;

public class SetCostumeBrickTest extends InstrumentationTestCase {
	private String testName = "testName";

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
	}

	public void testSetCostume() throws Exception {
		NativeAppActivity.setContext(getInstrumentation().getContext());

		String projectName = "myProject";
		Project project = new Project(null, projectName);
		ProjectManager.getInstance().setProject(project);

		Sprite sprite = new Sprite("new sprite");
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(testName);
		sprite.getCostumeDataList().add(costumeData);
		setCostumeBrick.setCostume(costumeData);

		assertEquals("Image path not empty", "", sprite.costume.getImagePath());

		setCostumeBrick.execute();

		assertEquals("Image path not correct", "images/" + testName, sprite.costume.getImagePath());
	}
}
