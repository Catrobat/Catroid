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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class IfOnEdgeBounceBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private static final String TEST_PROJECT_NAME = TestUtils.TEST_PROJECT_NAME1;

	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;
	private static final int BOUNCE_LEFT_POS = -(SCREEN_WIDTH + 50);
	private static final int BOUNCE_RIGHT_POS = SCREEN_WIDTH + 50;
	private static final int BOUNCE_DOWN_POS = -(SCREEN_HEIGHT + 50);
	private static final int BOUNCE_UP_POS = SCREEN_HEIGHT + 50;
	private static final int SCREEN_HALF_HEIGHT = SCREEN_HEIGHT / 2;
	private static final int SCREEN_HALF_WIDTH = SCREEN_WIDTH / 2;

	private File testImage;
	private CostumeData costumeData;
	private int width;
	private int height;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		Values.SCREEN_HEIGHT = SCREEN_HEIGHT;
		Values.SCREEN_WIDTH = SCREEN_WIDTH;

		Project project = new Project(getInstrumentation().getTargetContext(), TEST_PROJECT_NAME);
		project.virtualScreenHeight = Values.SCREEN_HEIGHT;
		project.virtualScreenWidth = Values.SCREEN_WIDTH;
		assertTrue("cannot save project", StorageHandler.getInstance().saveProjectSynchronously(project));
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(TEST_PROJECT_NAME, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		costumeData = new CostumeData();
		costumeData.setCostumeFilename(testImage.getName());
		costumeData.setCostumeName("CostumeName");

		Bitmap bitmap = BitmapFactory.decodeFile(testImage.getAbsolutePath());
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	@Override
	protected void tearDown() throws Exception {
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testNoBounce() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.width = width;
		sprite.costume.height = height;
		sprite.costume.setXPosition(0);
		sprite.costume.setYPosition(0);

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		ifOnEdgeBounceBrick.execute();
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 0f, sprite.costume.rotation, 1e-3);

	}

	public void testBounceNorth() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.width = width;
		sprite.costume.height = height;

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.rotation = 90f;
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);

		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 180f, -sprite.costume.rotation + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.width, 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.height, 1e-3);

		sprite.costume.rotation = -30f + 90f;
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 150f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = -150f + 90;
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 150f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = -42.42f + 90f;
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 137.58, -sprite.costume.rotation + 90f, 1e-3);
	}

	public void testBounceSouth() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.width = width;
		sprite.costume.height = height;

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.rotation = -180f + 90f;
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);

		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 0f, -sprite.costume.rotation + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.width, 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.height, 1e-3);

		sprite.costume.rotation = -120f + 90f;
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 60f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = -30f + 90f;
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 30f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = -132.42f + 90f;
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 47.58f, -sprite.costume.rotation + 90f, 1e-3);

	}

	public void testBounceEast() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.width = width;
		sprite.costume.height = height;

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.rotation = 0f;
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);

		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -90f, -sprite.costume.rotation + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.width, 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.height, 1e-3);

		sprite.costume.rotation = -30f + 90f;
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -30f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = 30f + 90f;
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -30f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = -42.42f + 90f;
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -42.42f, -sprite.costume.rotation + 90f, 1e-3);

	}

	public void testBounceWest() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.width = width;
		sprite.costume.height = height;

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.rotation = 90f + 90f;
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);

		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 90f, -sprite.costume.rotation + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.width, 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.height, 1e-3);

		sprite.costume.rotation = 30f + 90f;
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 30f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = -30f + 90f;
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 30f, -sprite.costume.rotation + 90f, 1e-3);

		sprite.costume.rotation = 42.42f + 90f;
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 42.42f, -sprite.costume.rotation + 90f, 1e-3);
	}
}
