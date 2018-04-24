/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.TouchUtil;

public class LookTest extends InstrumentationTestCase {

	private Look look;
	private Sprite sprite;

	private float width = 32;
	private float height = 16;

	@Override
	protected void setUp() {
		Group parentGroup = new Group();
		sprite = new SingleSprite("test");
		parentGroup.addActor(sprite.look);
		look = sprite.look;
	}

	public void testConstructor() {
		assertEquals(0f, look.getX());
		assertEquals(0f, look.getY());
		assertEquals(0f, look.getHeight());
		assertEquals(0f, look.getWidth());
		assertEquals(0f, look.getOriginX());
		assertEquals(0f, look.getOriginY());
		assertEquals(0f, look.getRotation());
		assertEquals(1f, look.getScaleX());
		assertEquals(1f, look.getScaleY());
		assertEquals(0f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals(100f, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0f, look.getColorInUserInterfaceDimensionUnit());
		assertEquals(100f, look.getSizeInUserInterfaceDimensionUnit());
		assertEquals(0, look.getZIndex());
		assertTrue(look.isVisible());
		assertEquals(Touchable.enabled, look.getTouchable());
		assertEquals("", look.getImagePath());
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
		assertEquals(x, look.getXInUserInterfaceDimensionUnit());
		assertEquals(x - width / 2, look.getX());
	}

	private void checkY(float y) {
		assertEquals(y, look.getYInUserInterfaceDimensionUnit());
		assertEquals(y - height / 2, look.getY());
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

		float[] expectedPositiveAngles = {0.0f, 45.0f, 90.0f, 135.0f, 180.0f, -135.0f, -90.0f, -45.0f, 0.0f};

		for (int index = 0; index < posigiveInputAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals(expectedPositiveAngles[index], convertNegativeZeroToPosigiveZero(catroidAngle));
		}
		for (int index = 0; index < posigiveHighInputAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveHighInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals(expectedPositiveAngles[index], convertNegativeZeroToPosigiveZero(catroidAngle));
		}
		for (int index = 0; index < posigiveHigherInputAngles.length; index++) {
			ParameterList params = new ParameterList(posigiveHigherInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals(expectedPositiveAngles[index], convertNegativeZeroToPosigiveZero(catroidAngle));
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
			assertEquals(expectedNegativeCatroidAngles[index], convertNegativeZeroToPosigiveZero(catroidAngle), 0.1);
		}

		for (int index = 0; index < negativeHighInputAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHighInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals(expectedNegativeCatroidAngles[index], convertNegativeZeroToPosigiveZero(catroidAngle));
		}

		for (int index = 0; index < negativeHigherInputAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHigherInputAngles[index]);
			float catroidAngle = (Float) Reflection.invokeMethod(look, "breakDownCatroidAngle", params);
			assertEquals(expectedNegativeCatroidAngles[index], convertNegativeZeroToPosigiveZero(catroidAngle));
		}
	}

	public void testCatroidAngleToStageAngle() {
		float[] positiveAngles = {0.0f, 45.0f, 90.0f, 135.0f, 180.0f, 225.0f, 270.0f, 315.0f, 360.0f};
		float[] positiveHighAngles = {360.0f, 405.0f, 450.0f, 495.0f, 540.0f, 585.0f, 630.0f, 675.0f, 720.0f};

		float[] expectedPositiveStageAngles = {90.0f, 45.0f, 0.0f, -45.0f, -90.0f, 225.0f, 180.0f, 135.0f, 90.0f};

		for (int index = 0; index < positiveAngles.length; index++) {
			ParameterList params = new ParameterList(positiveAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals(expectedPositiveStageAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < positiveHighAngles.length; index++) {
			ParameterList params = new ParameterList(positiveHighAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals(expectedPositiveStageAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}

		float[] negativeCatroidAngles = {-0.0f, -45.0f, -90.0f, -135.0f, -180.0f, -225.0f, -270.0f, -315.0f, -360.0f};
		float[] negativeHighCatroidAngles = {-360.0f, -405.0f, -450.0f, -495.0f, -540.0f, -585.0f, -630.0f, -675.0f,
				-720.0f};

		float[] expectedNegativeStageAngles = {90.0f, 135.0f, 180.0f, 225.0f, -90.0f, -45.0f, 0.0f, 45.0f, 90.0f};

		for (int index = 0; index < negativeCatroidAngles.length; index++) {
			ParameterList params = new ParameterList(negativeCatroidAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals(expectedNegativeStageAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < negativeHighCatroidAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHighCatroidAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertCatroidAngleToStageAngle", params);
			assertEquals(expectedNegativeStageAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}
	}

	public void testStageAngleToCatroidAngle() {
		float[] positiveStageAngles = {0.0f, 45.0f, 90.0f, 135.0f, 180.0f, 225.0f, 270.0f, 315.0f, 360.0f};
		float[] positiveHighStagedAngles = {360.0f, 405.0f, 450.0f, 495.0f, 540.0f, 585.0f, 630.0f, 675.0f,
				720.0f};
		float[] expectedPositiveAngles = {90.0f, 45.0f, 0.0f, -45.0f, -90.0f, -135.0f, 180.0f, 135.0f, 90.0f};

		for (int index = 0; index < positiveStageAngles.length; index++) {
			ParameterList params = new ParameterList(positiveStageAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals(expectedPositiveAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < positiveHighStagedAngles.length; index++) {
			ParameterList params = new ParameterList(positiveHighStagedAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals(expectedPositiveAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}

		float[] negativeStageAngles = {-0.0f, -45.0f, -90.0f, -135.0f, -180.0f, -225.0f, -270.0f, -315.0f, -360.0f};
		float[] negativeHighStageAngles = {-360.0f, -405.0f, -450.0f, -495.0f, -540.0f, -585.0f, -630.0f, -675.0f,
				-720.0f};
		float[] expectedNegativeCatroidAngles = {90.0f, 135.0f, 180.0f, -135.0f, -90.0f, -45.0f, 0.0f, 45.0f, 90.0f};

		for (int index = 0; index < negativeStageAngles.length; index++) {
			ParameterList params = new ParameterList(negativeStageAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals(expectedNegativeCatroidAngles[index],
					convertNegativeZeroToPosigiveZero(stageAngle));
		}

		for (int index = 0; index < negativeHighStageAngles.length; index++) {
			ParameterList params = new ParameterList(negativeHighStageAngles[index]);
			float stageAngle = (Float) Reflection.invokeMethod(look, "convertStageAngleToCatroidAngle", params);
			assertEquals(expectedNegativeCatroidAngles[index], convertNegativeZeroToPosigiveZero(stageAngle));
		}
	}

	public void testDirection() {
		float[] degreesInUserInterfaceDimensionUnit = {90f, 60f, 30f, 0f, -30f, -60f, -90f, -120f, -150f, 180f, 150f,
				120f};
		float[] degrees = {0f, 30f, 60f, 90f, 120f, 150f, 180f, 210f, 240f, -90f, -60f, -30f};

		assertEquals(degrees.length, degreesInUserInterfaceDimensionUnit.length);
		for (int index = 0; index < degrees.length; index++) {
			look.setDirectionInUserInterfaceDimensionUnit(degreesInUserInterfaceDimensionUnit[index]);
			assertEquals(degreesInUserInterfaceDimensionUnit[index], look.getDirectionInUserInterfaceDimensionUnit());
			assertEquals(degrees[index], look.getRotation());
		}

		look.setDirectionInUserInterfaceDimensionUnit(90f);
		look.changeDirectionInUserInterfaceDimensionUnit(10f);

		assertEquals(100f, look.getDirectionInUserInterfaceDimensionUnit());
		assertEquals(-10f, look.getRotation());

		look.setDirectionInUserInterfaceDimensionUnit(90f);
		look.changeDirectionInUserInterfaceDimensionUnit(360f);

		assertEquals(90f, look.getDirectionInUserInterfaceDimensionUnit());
		assertEquals(0f, look.getRotation());
	}

	public void testSize() {
		float size = 30f;
		look.setSizeInUserInterfaceDimensionUnit(size);
		assertEquals(size, look.getSizeInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals(size / 100f, look.getScaleX());
		assertEquals(size / 100f, look.getScaleY());

		look.changeSizeInUserInterfaceDimensionUnit(size);
		assertEquals(2 * size, look.getSizeInUserInterfaceDimensionUnit(), 1e-5);
		assertEquals(2 * size / 100f, look.getScaleX());
		assertEquals(2 * size / 100f, look.getScaleY());

		look.setSizeInUserInterfaceDimensionUnit(-10f);
		assertEquals(0f, look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testTransparency() {
		float transparency = 20f;
		look.setTransparencyInUserInterfaceDimensionUnit(transparency);
		assertEquals(transparency, look.getTransparencyInUserInterfaceDimensionUnit(),
				1e-5);
		assertEquals(0.8f, Reflection.getPrivateField(look, "alpha"));

		look.changeTransparencyInUserInterfaceDimensionUnit(transparency);
		assertEquals(2 * transparency, look.getTransparencyInUserInterfaceDimensionUnit(),
				1e-5);
		assertEquals(0.6f, Reflection.getPrivateField(look, "alpha"));

		look.setTransparencyInUserInterfaceDimensionUnit(-10f);
		assertEquals(0f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals(1f, Reflection.getPrivateField(look, "alpha"));

		look.setTransparencyInUserInterfaceDimensionUnit(200f);
		assertEquals(100f, look.getTransparencyInUserInterfaceDimensionUnit());
		assertEquals(0f, Reflection.getPrivateField(look, "alpha"));
	}

	public void testBrightness() {
		float brightness = 42f;
		look.setBrightnessInUserInterfaceDimensionUnit(brightness);
		assertEquals(brightness, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0.42f, Reflection.getPrivateField(look, "brightness"));

		look.changeBrightnessInUserInterfaceDimensionUnit(brightness);
		assertEquals(2 * brightness, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0.84f, Reflection.getPrivateField(look, "brightness"));

		look.setBrightnessInUserInterfaceDimensionUnit(-10);
		assertEquals(0f, look.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(0f, Reflection.getPrivateField(look, "brightness"));
	}

	public void testColor() {
		int red = -40;
		int green = 80;

		look.setColorInUserInterfaceDimensionUnit(red);
		assertEquals(160.0f, look.getColorInUserInterfaceDimensionUnit());

		look.changeColorInUserInterfaceDimensionUnit(green);
		assertEquals(40.0f, look.getColorInUserInterfaceDimensionUnit());
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

		assertEquals(touchPosition, squareRootOfScalar);
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

		assertTrue(look.doTouchDown(0, 0, 0));
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

		assertTrue(look.doTouchDown(0, 0, 0));
		assertFalse(look.doTouchDown(1, 0, 0));
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

		assertEquals(origin.getSizeInUserInterfaceDimensionUnit(), clone.getSizeInUserInterfaceDimensionUnit());

		assertEquals(origin.getXInUserInterfaceDimensionUnit(), clone.getXInUserInterfaceDimensionUnit());
		assertEquals(origin.getYInUserInterfaceDimensionUnit(), clone.getYInUserInterfaceDimensionUnit());

		assertEquals(origin.getColorInUserInterfaceDimensionUnit(), clone.getColorInUserInterfaceDimensionUnit());
		assertEquals(origin.getTransparencyInUserInterfaceDimensionUnit(),
				clone.getTransparencyInUserInterfaceDimensionUnit());

		assertEquals(origin.getRotationMode(), clone.getRotationMode());

		assertEquals(origin.getBrightnessInUserInterfaceDimensionUnit(),
				clone.getBrightnessInUserInterfaceDimensionUnit());

		assertEquals(origin.getDirectionInUserInterfaceDimensionUnit(),
				clone.getDirectionInUserInterfaceDimensionUnit());

		assertEquals(origin.isLookVisible(), clone.isLookVisible());
	}
}
