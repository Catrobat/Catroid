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

package at.tugraz.ist.catroid.test.content.brick;

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class NextCostumeBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
	private String projectName;

	@Override
	protected void setUp() throws Exception {

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		Values.SCREEN_HEIGHT = 200;
		Values.SCREEN_WIDTH = 200;
	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
	}

	public void testNextCostume() {

		Sprite sprite = new Sprite("cat");

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		NextCostumeBrick nextCostumeBrick = new NextCostumeBrick(sprite);

		CostumeData costumeData1 = new CostumeData();
		costumeData1.setCostumeFilename(testImage.getName());
		costumeData1.setCostumeName("testImage1");
		sprite.getCostumeDataList().add(costumeData1);

		CostumeData costumeData2 = new CostumeData();
		costumeData2.setCostumeFilename(testImage.getName());
		costumeData2.setCostumeName("testImage2");
		sprite.getCostumeDataList().add(costumeData2);

		setCostumeBrick.setCostume(costumeData1);
		setCostumeBrick.execute();
		nextCostumeBrick.execute();

		assertEquals("Costume is not next costume", costumeData2, sprite.costume.getCostumeData());
	}

	public void testLastCostume() {
		Sprite sprite = new Sprite("cat");

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		NextCostumeBrick nextCostumeBrick = new NextCostumeBrick(sprite);

		CostumeData costumeData1 = new CostumeData();
		costumeData1.setCostumeFilename(testImage.getName());
		costumeData1.setCostumeName("testImage2");
		sprite.getCostumeDataList().add(costumeData1);

		CostumeData costumeData2 = new CostumeData();
		costumeData2.setCostumeFilename(testImage.getName());
		costumeData2.setCostumeName("testImage");
		sprite.getCostumeDataList().add(costumeData2);

		CostumeData costumeData3 = new CostumeData();
		costumeData3.setCostumeFilename(testImage.getName());
		costumeData3.setCostumeName("testImage");
		sprite.getCostumeDataList().add(costumeData3);

		setCostumeBrick.setCostume(costumeData3);
		setCostumeBrick.execute();
		nextCostumeBrick.execute();

		assertEquals("Costume is not next costume", costumeData1, sprite.costume.getCostumeData());
	}

	public void testCostumeGalleryNull() {

		Sprite sprite = new Sprite("cat");
		NextCostumeBrick nextCostumeBrick = new NextCostumeBrick(sprite);
		nextCostumeBrick.execute();

		assertEquals("Costume is not null", null, sprite.costume.getCostumeData());
	}

	public void testCostumeGalleryWithOneCostume() {
		Sprite sprite = new Sprite("cat");

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		NextCostumeBrick nextCostumeBrick = new NextCostumeBrick(sprite);

		CostumeData costumeData1 = new CostumeData();
		costumeData1.setCostumeFilename(testImage.getName());
		costumeData1.setCostumeName("testImage1");
		sprite.getCostumeDataList().add(costumeData1);

		setCostumeBrick.setCostume(costumeData1);
		setCostumeBrick.execute();
		nextCostumeBrick.execute();

		assertEquals("Wrong costume after executing NextCostumeBrick with just one costume", costumeData1,
				sprite.costume.getCostumeData());
	}

	public void testNextCostumeWithNoCostumeSet() {

		Sprite sprite = new Sprite("cat");

		NextCostumeBrick nextCostumeBrick = new NextCostumeBrick(sprite);

		CostumeData costumeData1 = new CostumeData();
		costumeData1.setCostumeFilename(testImage.getName());
		costumeData1.setCostumeName("testImage1");
		sprite.getCostumeDataList().add(costumeData1);

		nextCostumeBrick.execute();

		assertNull("No Custume should be set.", sprite.costume.getCostumeData());
	}
}
