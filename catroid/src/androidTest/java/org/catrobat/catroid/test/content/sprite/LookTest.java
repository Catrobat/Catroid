/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.sprite;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.TouchUtil;

public class LookTest extends InstrumentationTestCase {
	private Look look;
	private Sprite sprite;
	private Group parentGroup;
	private Project project;

	private float width = 32;
	private float height = 16;

	@Override
	protected void setUp() {
		parentGroup = new Group();
		sprite = new SingleSprite("test");
		parentGroup.addActor(sprite.look);
		look = sprite.look;
	}

	public void testConstructor() {
		assertEquals("Wrong initialization!", 0f, look.getX());
		assertEquals("Wrong initialization!", 0f, look.getY());
		assertEquals("Wrong initialization!", 0f, look.getHeight());
		assertEquals("Wrong initialization!", 0f, look.getWidth());
		assertEquals("Wrong initialization!", 0f, look.getOriginX());
		assertEquals("Wrong initialization!", 0f, look.getOriginY());
		assertEquals("Wrong initialization!", 0f, look.getRotation());
		assertEquals("Wrong initialization!", 1f, look.getScaleX());
		assertEquals("Wrong initialization!", 1f, look.getScaleY());
		assertEquals("Wrong initialization!", 0f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals("Wrong initialization!", 100f, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals("Wrong initialization!", 0f, look.getColorInUserInterfaceDimensionUnit());
		assertEquals("Wrong initialization!", 100f, look.getSizeInUserInterfaceDimensionUnit());
		assertEquals("Wrong initialization!", 0, look.getZIndex());
		assertEquals("Wrong initialization!", true, look.isVisible());
		assertEquals("Wrong initialization!", Touchable.enabled, look.getTouchable());
		assertEquals("Wrong initialization!", "", look.getImagePath());
	}

	public void testImagePath() {
		String projectName = "myProject";
		String fileName = "blubb";
		project = new Project(null, projectName);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);

		LookData lookData = new LookData();
		lookData.setLookFilename(fileName);
		lookData.setLookName(fileName);
		look.setLookData(lookData);
		assertEquals("Wrong image path!", Constants.DEFAULT_ROOT + "/" + projectName + "/" + project.getDefaultScene().getName() + "/" + Constants.IMAGE_DIRECTORY
				+ "/" + fileName, look.getImagePath());
	}

	public void testPositions() {
		float x = 3f;
		float y = 10f;
		look.setWidth(width);
		look.setHeight(height);

		look.setXInUserInterfaceDimensionUnit(x);
		checkX(x);

		look.setYInUserInterfaceDimensionUnit(y);
		checkY(y);

		look.changeXInUserInterfaceDimensionUnit(x);
		checkX(2 * x);

		look.changeYInUserInterfaceDimensionUnit(y);
		checkY(2 * y);

		x = 5f;
		y = -3f;
		look.setPositionInUserInterfaceDimensionUnit(x, y);
		checkX(x);
		checkY(y);
	}

	private void checkX(float x) {
		assertEquals("Wrong alpha value!", x, look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong alpha value!", x - width / 2, look.getX());
	}

	private void checkY(float y) {
		assertEquals("Wrong alpha value!", y, look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong alpha value!", y - height / 2, look.getY());
	}

	private float convertNegativeZeroToPosigiveZero(float value) {
		if (value == 0.0f) {
			return 0.0f;
		}
		return value;
	}

	public void testBreakDownCatroidAngle() {
		Look look = new Look(new SingleSprite("testsprite"));

		float[] posigiveInputAngles = {0.0f, 45.0f, 90.0f, 135.0f, 180.0f, 225.0f, 270.0f, 315.0f, 360.0f};
		float[] posigiveHighInputAngles = {360.0f, 405.0f, 450.0f, 495.0f, 540.0f, 585.0f, 630.0f, 675.0f, 720.0f};
		float[] posigiveHigherInputAngles = {720.0f, 765.0f, 810.0f, 855.0f, 900.0f, 945.0f, 990.0f, 1035.0f, 1080.0f};

		float[] expectedPosigiveCatroidAngles = {0.0f, 45.0f, 90.0f, 135.0f, 180.0f, -135.0f, -90.0f, -45.0f, 0.0f};

		for (int index = 0; index < posigiveInputAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals("positive angle break down to wrong angle", expectedPosigiveCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(catroidAngle));
		}
		for (int index = 0; index < posigiveHighInputAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveHighInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals("high positive angle break down to wrong angle", expectedPosigiveCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(catroidAngle));
		}
		for (int index = 0; index < posigiveHigherInputAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveHigherInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals("higher positive angle break down to wrong angle", expectedPosigiveCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(catroidAngle));
		}

		float[] negativeInputAngles = {-0.0f, -45.0f, -90.0f, -135.0f, -180.0f, -225.0f, -270.0f, -315.0f, -360.0f};
		float[] negativeHighInputAngles = {-360.0f, -405.0f, -450.0f, -495.0f, -540.0f, -585.0f, -630.0f, -675.0f,
				-720.0f};
		float[] negativeHigherInputAngles = {-720.0f, -765.0f, -810.0f, -855.0f, -900.0f, -945.0f, -990.0f, -1035.0f,
				-1080.0f};

		float[] expectedNegativeCatroidAngles = {0.0f, -45.0f, -90.0f, -135.0f, 180.0f, 135.0f, 90.0f, 45.0f, 0.0f};

		for (int index = 0; index < negativeInputAngles.length; index++) {
			ParameterList params = new ParameterList(negativeInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals("negative angle break down to wrong angle", expectedNegativeCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(catroidAngle), 0.1);
		}

		for (int index = 0; index < negativeHighInputAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHighInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals("high negative angle break down to wrong angle", expectedNegativeCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(catroidAngle));
		}

		for (int index = 0; index < negativeHigherInputAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHigherInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals("higher negative angle break down to wrong angle", expectedNegativeCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(catroidAngle));
		}
	}

	public void testCatroidAngleToStageAngle() {
		float[] posigiveCatroidAngles = {0.0f, 45.0f, 90.0f, 135.0f, 180.0f, 225.0f, 270.0f, 315.0f, 360.0f};
		float[] posigiveHighCatroidAngles = {360.0f, 405.0f, 450.0f, 495.0f, 540.0f, 585.0f, 630.0f, 675.0f, 720.0f};
		//float[] expectedPosigiveStageAngles = { 90.0f, 45.0f, 0.0f, 315.0f, 270.0f, 225.0f, 180.0f, 135.0f, 90.0f };
		float[] expectedPosigiveStageAngles = {90.0f, 45.0f, 0.0f, -45.0f, -90.0f, 225.0f, 180.0f, 135.0f, 90.0f};

		for (int index = 0; index < posigiveCatroidAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveCatroidAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals("positive catroid angle converted to wrong stage angle", expectedPosigiveStageAngles[index],
					convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < posigiveHighCatroidAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveHighCatroidAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals("high positive catroid angle converted to wrong stage angle",
					expectedPosigiveStageAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}

		float[] negativeCatroidAngles = {-0.0f, -45.0f, -90.0f, -135.0f, -180.0f, -225.0f, -270.0f, -315.0f, -360.0f};
		float[] negativeHighCatroidAngles = {-360.0f, -405.0f, -450.0f, -495.0f, -540.0f, -585.0f, -630.0f, -675.0f,
				-720.0f};
		//float[] expectedNegativeStageAngles = { 90.0f, 135.0f, 180.0f, 225.0f, 270.0f, 315.0f, 0.0f, 45.0f, 90.0f };
		float[] expectedNegativeStageAngles = {90.0f, 135.0f, 180.0f, 225.0f, -90.0f, -45.0f, 0.0f, 45.0f, 90.0f};

		for (int index = 0; index < negativeCatroidAngles.length; index++) {
			ParameterList params = new ParameterList(negativeCatroidAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals("negative catroid angle converted to wrong stage angle", expectedNegativeStageAngles[index],
					convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < negativeHighCatroidAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHighCatroidAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals("high negative catroid angle converted to wrong stage angle",
					expectedNegativeStageAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}
	}

	public void testStageAngleToCatroidAngle() {
		float[] posigiveStageAngles = {0.0f, 45.0f, 90.0f, 135.0f, 180.0f, 225.0f, 270.0f, 315.0f, 360.0f};
		float[] posigiveHighCatroiStagedAngles = {360.0f, 405.0f, 450.0f, 495.0f, 540.0f, 585.0f, 630.0f, 675.0f,
				720.0f};
		float[] expectedPosigiveCatroidAngles = {90.0f, 45.0f, 0.0f, -45.0f, -90.0f, -135.0f, 180.0f, 135.0f, 90.0f};

		for (int index = 0; index < posigiveStageAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveStageAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals("positive stage angle converted to wrong catroid angle", expectedPosigiveCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < posigiveHighCatroiStagedAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveHighCatroiStagedAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals("high positive stage angle converted to wrong catroid angle",
					expectedPosigiveCatroidAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}

		float[] negativeStageAngles = {-0.0f, -45.0f, -90.0f, -135.0f, -180.0f, -225.0f, -270.0f, -315.0f, -360.0f};
		float[] negativeHighStageAngles = {-360.0f, -405.0f, -450.0f, -495.0f, -540.0f, -585.0f, -630.0f, -675.0f,
				-720.0f};
		float[] expectedNegativeCatroidAngles = {90.0f, 135.0f, 180.0f, -135.0f, -90.0f, -45.0f, 0.0f, 45.0f, 90.0f};

		for (int index = 0; index < negativeStageAngles.length; index++) {
			ParameterList params = new ParameterList(negativeStageAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals("negative stage angle converted to wrong catroid angle", expectedNegativeCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < negativeHighStageAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHighStageAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals("high negative stage angle converted to wrong catroid angle",
					expectedNegativeCatroidAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}
	}

	public void testDirection() {
		float[] degreesInUserInterfaceDimensionUnit = {90f, 60f, 30f, 0f, -30f, -60f, -90f, -120f, -150f, 180f, 150f,
				120f};
		float[] degrees = {0f, 30f, 60f, 90f, 120f, 150f, 180f, 210f, 240f, -90f, -60f, -30f};

		assertEquals("Wrong Array length", degrees.length, degreesInUserInterfaceDimensionUnit.length);
		for (int index = 0; index < degrees.length; index++) {
			look.setDirectionInUserInterfaceDimensionUnit(degreesInUserInterfaceDimensionUnit[index]);
			assertEquals("Wrong degrees value!", degreesInUserInterfaceDimensionUnit[index],
					look.getDirectionInUserInterfaceDimensionUnit());
			assertEquals("Wrong degrees value!", degrees[index], look.getRotation());
		}

		look.setDirectionInUserInterfaceDimensionUnit(90f);
		look.changeDirectionInUserInterfaceDimensionUnit(10f);
		assertEquals("Wrong degrees value!", 100f, look.getDirectionInUserInterfaceDimensionUnit());
		assertEquals("Wrong degrees value!", -10f, look.getRotation());

		look.setDirectionInUserInterfaceDimensionUnit(90f);
		look.changeDirectionInUserInterfaceDimensionUnit(360f);
		assertEquals("Wrong degrees value!", 90f, look.getDirectionInUserInterfaceDimensionUnit());
		assertEquals("Wrong degrees value!", 0f, look.getRotation());
	}

	public void testWidthAndHeight() {
		// TODO
	}

	public void testSize() {
		float size = 30f;
		look.setSizeInUserInterfaceDimensionUnit(size);
		assertEquals("Wrong size!", size, look.getSizeInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals("Wrong size value!", size / 100f, look.getScaleX());
		assertEquals("Wrong size value!", size / 100f, look.getScaleY());

		look.changeSizeInUserInterfaceDimensionUnit(size);
		assertEquals("Wrong size!", 2 * size, look.getSizeInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals("Wrong size value!", 2 * size / 100f, look.getScaleX());
		assertEquals("Wrong size value!", 2 * size / 100f, look.getScaleY());

		look.setSizeInUserInterfaceDimensionUnit(-10f);
		assertEquals("Wrong size value!", 0f, look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testTransparency() {
		float transparency = 20f;
		look.setTransparencyInUserInterfaceDimensionUnit(transparency);
		assertEquals("Wrong transparency value!", transparency, look.getTransparencyInUserInterfaceDimensionUnit(),
				1e-5);
		assertEquals("Wrong alpha value!", 0.8f, Reflection.getPrivateField(look, "alpha"));

		look.changeTransparencyInUserInterfaceDimensionUnit(transparency);
		assertEquals("Wrong transparency value!", 2 * transparency, look.getTransparencyInUserInterfaceDimensionUnit(),
				1e-5);
		assertEquals("Wrong alpha value!", 0.6f, Reflection.getPrivateField(look, "alpha"));

		look.setTransparencyInUserInterfaceDimensionUnit(-10f);
		assertEquals("Wrong transparency value!", 0f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals("Wrong alpha value!", 1f, Reflection.getPrivateField(look, "alpha"));

		look.setTransparencyInUserInterfaceDimensionUnit(200f);
		assertEquals("Wrong transparency value!", 100f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals("Wrong alpha value!", 0f, Reflection.getPrivateField(look, "alpha"));

		// setVisible
	}

	public void testBrightness() {
		float brightness = 42f;
		look.setBrightnessInUserInterfaceDimensionUnit(brightness);
		assertEquals("Wrong brightness value!", brightness, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals("Wrong brightness value!", 0.42f, Reflection.getPrivateField(look, "brightness"));

		look.changeBrightnessInUserInterfaceDimensionUnit(brightness);
		assertEquals("Wrong brightness value!", 2 * brightness, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals("Wrong brightness value!", 0.84f, Reflection.getPrivateField(look, "brightness"));

		look.setBrightnessInUserInterfaceDimensionUnit(-10);
		assertEquals("Wrong brightness value!", 0f, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals("Wrong brightness value!", 0f, Reflection.getPrivateField(look, "brightness"));
	}

	public void testColor() {
		int red = -40;
		int green = 80;

		look.setColorInUserInterfaceDimensionUnit(red);
		assertEquals("Wrong color value!", 160.0f, look.getColorInUserInterfaceDimensionUnit());

		look.changeColorInUserInterfaceDimensionUnit(green);
		assertEquals("Wrong color value!", 40.0f, look.getColorInUserInterfaceDimensionUnit());
	}

	public void testDistanceTo() {
		look.setXInUserInterfaceDimensionUnit(25);
		look.setYInUserInterfaceDimensionUnit(55);
		float touchPosition = look.getDistanceToTouchPositionInUserInterfaceDimensions();

		float pointAx = look.getXInUserInterfaceDimensionUnit();
		float pointAy = look.getYInUserInterfaceDimensionUnit();
		int touchIndex = TouchUtil.getLastTouchIndex();
		float pointBx = TouchUtil.getX(touchIndex);
		float pointBy = TouchUtil.getY(touchIndex);

		float vectorX = pointBx - pointAx;
		float vectorY = pointBy - pointAy;

		double squareX = (float) Math.pow(vectorX, 2);
		double squareY = (float) Math.pow(vectorY, 2);

		float squareRootOfScalar = (float) Math.sqrt(squareX + squareY);

		assertEquals("Wrong distance to value!", touchPosition, squareRootOfScalar);
	}

	public void testTouchDownFlipped() {
		final int width = 1;
		final int height = 1;

		Look look = new Look(sprite) {
			{
				pixmap = TestUtils.createRectanglePixmap(width, height, Color.RED);
			}
		};
		look.setSize(width, height);

		assertTrue("Flipped look not touched", look.doTouchDown(0, 0, 0));
	}

	public void testTouchDownFlippedWithAlpha() {
		final int width = 2;
		final int height = 1;

		Look look = new Look(sprite) {
			{
				pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
				pixmap.drawPixel(0, 0, Color.RED.toIntBits());
			}
		};
		look.setSize(width, height);

		assertTrue("Look not touched", look.doTouchDown(0, 0, 0));
		assertFalse("Look touched on alpha shouldn't trigger touch down", look.doTouchDown(1, 0, 0));
	}

	public void testCloneValues() {
		Look origin = new Look(null);
		origin.setSizeInUserInterfaceDimensionUnit(12);
		origin.setPositionInUserInterfaceDimensionUnit(4, 12);
		origin.setColorInUserInterfaceDimensionUnit(42);
		origin.setTransparencyInUserInterfaceDimensionUnit(7);
		origin.setRotationMode(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY);
		origin.setBrightnessInUserInterfaceDimensionUnit(3);
		origin.setDirectionInUserInterfaceDimensionUnit(8);
		origin.setLookVisible(false);

		Look clone = new Look(null);
		origin.copyTo(clone);

		assertEquals("Size differs", origin.getSizeInUserInterfaceDimensionUnit(),
				clone.getSizeInUserInterfaceDimensionUnit());
		assertEquals("X position differs", origin.getXInUserInterfaceDimensionUnit(),
				clone.getXInUserInterfaceDimensionUnit());
		assertEquals("Y position differs", origin.getYInUserInterfaceDimensionUnit(),
				clone.getYInUserInterfaceDimensionUnit());
		assertEquals("Color differs", origin.getColorInUserInterfaceDimensionUnit(),
				clone.getColorInUserInterfaceDimensionUnit());
		assertEquals("Transparency differs", origin.getTransparencyInUserInterfaceDimensionUnit(), clone
				.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals("Rotation mode differs", origin.getRotationMode(), clone.getRotationMode());
		assertEquals("Brightness differs", origin.getBrightnessInUserInterfaceDimensionUnit(),
				clone.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals("Direction differs", origin.getDirectionInUserInterfaceDimensionUnit(),
				clone.getDirectionInUserInterfaceDimensionUnit());
		assertEquals("Visibility differs", origin.isLookVisible(), clone.isLookVisible());
	}
}
