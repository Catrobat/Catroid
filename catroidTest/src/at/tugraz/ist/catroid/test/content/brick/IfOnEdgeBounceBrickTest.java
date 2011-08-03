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
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class IfOnEdgeBounceBrickTest extends InstrumentationTestCase {

	private static final int BOUNCE_LEFT_POS = -(Consts.MAX_REL_COORDINATES + 50);
	private static final int BOUNCE_RIGHT_POS = Consts.MAX_REL_COORDINATES + 50;
	private static final int BOUNCE_DOWN_POS = -(Consts.MAX_REL_COORDINATES + 50);
	private static final int BOUNCE_UP_POS = Consts.MAX_REL_COORDINATES + 50;

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

	public void testNoBounce() {

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);

		brick.execute();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 90., sprite.getDirection(), 1e-3);

	}

	public void testBounceNorth() {

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);
		double width;
		double height;

		sprite.setDirection(0);
		sprite.setXYPosition(0, BOUNCE_UP_POS);

		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", Consts.MAX_REL_COORDINATES - (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 180, sprite.getDirection(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.getCostume().getVirtuelWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.getCostume().getVirtuelHeight(), 1e-3);

		sprite.setDirection(30);
		sprite.setXYPosition(0, BOUNCE_UP_POS);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", Consts.MAX_REL_COORDINATES - (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 150, sprite.getDirection(), 1e-3);

		sprite.setDirection(150);
		sprite.setXYPosition(0, BOUNCE_UP_POS);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", Consts.MAX_REL_COORDINATES - (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 150, sprite.getDirection(), 1e-3);

		sprite.setDirection(42.42);
		sprite.setXYPosition(0, BOUNCE_UP_POS);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", Consts.MAX_REL_COORDINATES - (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 137.58, sprite.getDirection(), 1e-3);
	}

	public void testBounceSouth() {

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);
		double width;
		double height;

		sprite.setDirection(180);
		sprite.setXYPosition(0, BOUNCE_DOWN_POS);

		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", -Consts.MAX_REL_COORDINATES + (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 0, sprite.getDirection(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.getCostume().getVirtuelWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.getCostume().getVirtuelHeight(), 1e-3);

		sprite.setDirection(120);
		sprite.setXYPosition(0, BOUNCE_DOWN_POS);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", -Consts.MAX_REL_COORDINATES + (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 60, sprite.getDirection(), 1e-3);

		sprite.setDirection(30);
		sprite.setXYPosition(0, BOUNCE_DOWN_POS);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", -Consts.MAX_REL_COORDINATES + (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 30, sprite.getDirection(), 1e-3);

		sprite.setDirection(132.42);
		sprite.setXYPosition(0, BOUNCE_DOWN_POS);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", -Consts.MAX_REL_COORDINATES + (int) (height / 2), sprite.getYPosition());
		assertEquals("Wrong direction", 47.58, sprite.getDirection(), 1e-3);

	}

	public void testBounceEast() {

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);
		double width;
		double height;

		sprite.setDirection(90);
		sprite.setXYPosition(BOUNCE_RIGHT_POS, 0);

		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", Consts.MAX_REL_COORDINATES - (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", -90, sprite.getDirection(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.getCostume().getVirtuelWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.getCostume().getVirtuelHeight(), 1e-3);

		sprite.setDirection(30);
		sprite.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", Consts.MAX_REL_COORDINATES - (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", -30, sprite.getDirection(), 1e-3);

		sprite.setDirection(-30);
		sprite.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", Consts.MAX_REL_COORDINATES - (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", -30, sprite.getDirection(), 1e-3);

		sprite.setDirection(42.42);
		sprite.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", Consts.MAX_REL_COORDINATES - (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", -42.42, sprite.getDirection(), 1e-3);

	}

	public void testBounceWest() {

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);
		double width;
		double height;

		sprite.setDirection(-90);
		sprite.setXYPosition(BOUNCE_LEFT_POS, 0);

		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", -Consts.MAX_REL_COORDINATES + (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 90, sprite.getDirection(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.getCostume().getVirtuelWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.getCostume().getVirtuelHeight(), 1e-3);

		sprite.setDirection(-30);
		sprite.setXYPosition(BOUNCE_LEFT_POS, 0);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", -Consts.MAX_REL_COORDINATES + (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 30, sprite.getDirection(), 1e-3);

		sprite.setDirection(30);
		sprite.setXYPosition(BOUNCE_LEFT_POS, 0);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", -Consts.MAX_REL_COORDINATES + (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 30, sprite.getDirection(), 1e-3);

		sprite.setDirection(-42.42);
		sprite.setXYPosition(BOUNCE_LEFT_POS, 0);
		brick.execute();

		width = sprite.getCostume().getVirtuelWidth();
		height = sprite.getCostume().getVirtuelHeight();
		assertEquals("Wrong X-Position!", -Consts.MAX_REL_COORDINATES + (int) (width / 2), sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 42.42, sprite.getDirection(), 1e-3);
	}
}
