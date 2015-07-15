/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.utils.Reflection;

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
		sprite = new Sprite("test");
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
		assertEquals("Wrong initialization!", 100f, look.getSizeInUserInterfaceDimensionUnit());
		assertEquals("Wrong initialization!", 0, look.getZIndex());
		assertEquals("Wrong initialization!", true, look.visible);
		assertEquals("Wrong initialization!", Touchable.enabled, look.getTouchable());
		assertEquals("Wrong initialization!", "", look.getImagePath());
	}

	public void testImagePath() {
		String projectName = "myProject";
		String fileName = "blubb";
		project = new Project(null, projectName);
		ProjectManager.getInstance().setProject(project);

		LookData lookData = new LookData();
		lookData.setLookFilename(fileName);
		look.setLookData(lookData);
		assertEquals("Wrong image path!", Constants.DEFAULT_ROOT + "/" + projectName + "/" + Constants.IMAGE_DIRECTORY
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

	public void testDirection() {
		float[] degreesInUserInterfaceDimensionUnit = { 90f, 60f, 30f, 0f, -30f, -60f, -90f, -120f, -150f, 180f, 150f,
				120f };
		float[] degrees = { 0f, 30f, 60f, 90f, 120f, 150f, 180f, 210f, 240f, -90f, -60f, -30f };

		assertEquals("Wrong Array length", degrees.length, degreesInUserInterfaceDimensionUnit.length);
		for (int index = 0; index < degrees.length; index++) {
			look.setDirectionInUserInterfaceDimensionUnit(degreesInUserInterfaceDimensionUnit[index]);
			assertEquals("Wrong degrees value!", degreesInUserInterfaceDimensionUnit[index],
					look.getDirectionInUserInterfaceDimensionUnit());
			assertEquals("Wrong degrees value!", degrees[index], look.getRotation());
		}

		look.setDirectionInUserInterfaceDimensionUnit(90f);
		look.changeDirectionInUserInterfaceDimensionUnit(10f);
		assertEquals("Wrong alpha value!", 100f, look.getDirectionInUserInterfaceDimensionUnit());
		assertEquals("Wrong alpha value!", -10f, look.getRotation());
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
}
