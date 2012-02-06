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
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.content.bricks.TurnRightBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class TurnLeftBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private final String projectName = "testProject";
	private File testImage;
	private CostumeData costumeData;

	@Override
	public void setUp() throws Exception {

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		costumeData = new CostumeData();
		costumeData.setCostumeFilename(testImage.getName());
		costumeData.setCostumeName("CostumeName");

		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;
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

	public void testTurnLeftTwice() {
		Sprite sprite = new Sprite("test");
		sprite.costume.setCostumeData(costumeData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 10);

		turnLeftBrick.execute();
		assertEquals("Wrong direction!", 10f, sprite.costume.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());

		turnLeftBrick.execute();
		assertEquals("Wrong direction!", 20f, sprite.costume.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
	}

	public void testTurnLeftAndScale() {
		Sprite sprite = new Sprite("test");
		sprite.costume.setCostumeData(costumeData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		turnLeftBrick.execute();
		brickScale.execute();

		assertEquals("Wrong direction!", 10f, sprite.costume.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
	}

	public void testScaleAndTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.costume.setCostumeData(costumeData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		brickScale.execute();
		turnLeftBrick.execute();

		assertEquals("Wrong direction!", 10f, sprite.costume.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());

	}

	public void testTurnLeftNegative() {
		Sprite sprite = new Sprite("test");
		sprite.costume.setCostumeData(costumeData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, -10);

		turnLeftBrick.execute();

		assertEquals("Wrong direction!", -10f, sprite.costume.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
	}

	public void testTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.costume.setCostumeData(costumeData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 370);

		turnLeftBrick.execute();

		assertEquals("Wrong direction!", 370f, sprite.costume.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
	}

	public void testTurnLeftAndTurnRight() {
		Sprite sprite = new Sprite("test");
		sprite.costume.setCostumeData(costumeData);

		TurnLeftBrick brickTurnLeft = new TurnLeftBrick(sprite, 50);
		TurnRightBrick brickTurnRight = new TurnRightBrick(sprite, 30);

		brickTurnLeft.execute();
		brickTurnRight.execute();

		assertEquals("Wrong direction!", 20f, sprite.costume.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
	}
}
