/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.nativetest.content.brick;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.stage.NativeAppActivity;

import android.test.InstrumentationTestCase;

public class SetCostumeBrickTest extends InstrumentationTestCase {
	private String testName = "testName";

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
		super.tearDown();
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
