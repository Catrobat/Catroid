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
package at.tugraz.ist.catroid.test.content.sprite;

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class CostumeTest extends InstrumentationTestCase {

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

	public void testUpdatePosition() {
		Sprite sprite = new Sprite("testSprite");
		Costume costume = sprite.getCostume();
		costume.changeImagePath(testImage.getAbsolutePath());

		int width = costume.getImageWidth();
		int height = costume.getImageHeight();

		int virtualPositionX = 100;
		int virtualPositionY = 100;

		sprite.setXYPosition(virtualPositionX, virtualPositionY);

		int expectedPositionX = Math.round(toDeviceXCoordinates(virtualPositionX) - width / 2f);
		int expectedPositionY = Math.round(toDeviceYCoordinates(virtualPositionY) - height / 2f);

		assertEquals("Incorrect x position", expectedPositionX, costume.getDrawPositionX());
		assertEquals("Incorrect y position", expectedPositionY, costume.getDrawPositionY());
	}

	public void testUpdateSize() {
		Sprite sprite = new Sprite("testSprite");
		Costume costume = sprite.getCostume();
		costume.changeImagePath(testImage.getAbsolutePath());

		double size = 50;

		int width = costume.getImageWidth();
		int height = costume.getImageHeight();

		sprite.setSize(size);

		int expectedWidth = (int) (width * 0.5f);
		int expectedHeight = (int) (height * 0.5f);
		int expectedPositionX = Math.round((Values.SCREEN_WIDTH / 2f) - (expectedWidth / 2));
		int expectedPositionY = Math.round((Values.SCREEN_HEIGHT / 2f) - (expectedHeight / 2));

		assertEquals("Incorrect x position", expectedPositionX, costume.getDrawPositionX());
		assertEquals("Incorrect y position", expectedPositionY, costume.getDrawPositionY());
		assertEquals("Incorrect width", expectedWidth, costume.getImageWidth());
		assertEquals("Incorrect height", expectedHeight, costume.getImageHeight());
	}

	public void testUpdateDirection() {
		Sprite sprite = new Sprite("testSprite");
		Costume costume = sprite.getCostume();
		costume.changeImagePath(testImage.getAbsolutePath());

		double direction = 30;
		double radians = direction / 180 * Math.PI;

		int width = costume.getImageWidth();
		int height = costume.getImageHeight();

		sprite.setDirection(direction);

		int expectedWidth = (int) Math
				.round(height * Math.abs(Math.cos(radians)) + width * Math.abs(Math.sin(radians)));
		int expectedHeight = (int) Math.round(width * Math.abs(Math.cos(radians)) + height
				* Math.abs(Math.sin(radians)));

		int expectedPositionX = Math.round((Values.SCREEN_WIDTH / 2f) - (expectedWidth / 2));
		int expectedPositionY = Math.round((Values.SCREEN_HEIGHT / 2f) - (expectedHeight / 2));

		assertEquals("Wrong height", expectedHeight, costume.getImageHeight());
		assertEquals("Wrong width", expectedWidth, costume.getImageWidth());
		assertEquals("Wrong x position", expectedPositionX, costume.getDrawPositionX());
		assertEquals("Wrong y position", expectedPositionY, costume.getDrawPositionY());
	}

	private float toDeviceXCoordinates(int virtualXCoordinate) {
		return Values.SCREEN_WIDTH / 2f + (virtualXCoordinate * Values.SCREEN_WIDTH)
				/ (2f * Consts.MAX_REL_COORDINATES);
	}

	private float toDeviceYCoordinates(int virtualYCoordinate) {
		return Values.SCREEN_HEIGHT / 2f - (virtualYCoordinate * Values.SCREEN_HEIGHT)
				/ (2f * Consts.MAX_REL_COORDINATES);
	}
}
