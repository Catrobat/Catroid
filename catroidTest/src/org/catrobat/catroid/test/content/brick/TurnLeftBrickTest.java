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
package org.catrobat.catroid.test.content.brick;

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import android.test.InstrumentationTestCase;
import org.catrobat.catroid.test.R;

public class TurnLeftBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private final String projectName = "testProject";
	private File testImage;
	private LookData lookData;

	@Override
	public void setUp() throws Exception {

		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName("LookName");

		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;
	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
		super.tearDown();
	}

	public void testTurnLeftTwice() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 10);

		turnLeftBrick.execute();
		assertEquals("Wrong direction!", 10f, sprite.look.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYPosition());

		turnLeftBrick.execute();
		assertEquals("Wrong direction!", 20f, sprite.look.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYPosition());
	}

	public void testTurnLeftAndScale() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		turnLeftBrick.execute();
		brickScale.execute();

		assertEquals("Wrong direction!", 10f, sprite.look.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYPosition());
	}

	public void testScaleAndTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		brickScale.execute();
		turnLeftBrick.execute();

		assertEquals("Wrong direction!", 10f, sprite.look.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYPosition());

	}

	public void testTurnLeftNegative() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, -10);

		turnLeftBrick.execute();

		assertEquals("Wrong direction!", -10f, sprite.look.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYPosition());
	}

	public void testTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 370);

		turnLeftBrick.execute();

		assertEquals("Wrong direction!", 370f, sprite.look.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYPosition());
	}

	public void testTurnLeftAndTurnRight() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		TurnLeftBrick brickTurnLeft = new TurnLeftBrick(sprite, 50);
		TurnRightBrick brickTurnRight = new TurnRightBrick(sprite, 30);

		brickTurnLeft.execute();
		brickTurnRight.execute();

		assertEquals("Wrong direction!", 20f, sprite.look.rotation, 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYPosition());
	}
}
