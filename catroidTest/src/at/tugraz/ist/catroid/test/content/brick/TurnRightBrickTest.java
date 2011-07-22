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

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
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

public class TurnRightBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private final String projectName = "testProject";
	private File testImage;

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

	public void testTurnRightTwice() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		TurnRightBrick brick = new TurnRightBrick(sprite, 10);

		brick.execute();
		assertEquals("Wrong direction", 100, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong direction", 110, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}

	public void testTurnRightAndScale() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		TurnRightBrick brick = new TurnRightBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		brick.execute();
		brickScale.execute();

		assertEquals("Wrong direction", 100, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}

	public void testScaleandTurnRight() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		TurnRightBrick brick = new TurnRightBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		brickScale.execute();
		brick.execute();

		assertEquals("Wrong direction", 100, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}

	public void testTurnRightNegative() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		TurnRightBrick brick = new TurnRightBrick(sprite, -10);

		brick.execute();
		assertEquals("Wrong direction", 80, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());

	}

	public void testTurnRight() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		TurnRightBrick brick = new TurnRightBrick(sprite, 370);

		brick.execute();
		assertEquals("Wrong direction", 100, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());

	}

	public void testTurnRightAndTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		TurnRightBrick brickTurnRight = new TurnRightBrick(sprite, 50);
		TurnLeftBrick brickTurnLeft = new TurnLeftBrick(sprite, 20);

		brickTurnRight.execute();
		brickTurnLeft.execute();

		assertEquals("Wrong direction!", 120, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}
}
