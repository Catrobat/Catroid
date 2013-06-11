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
package org.catrobat.catroid.pocketcode.test.content.sprite;

import org.catrobat.catroid.pocketcode.ProjectManager;
import org.catrobat.catroid.pocketcode.common.Constants;
import org.catrobat.catroid.pocketcode.common.LookData;
import org.catrobat.catroid.pocketcode.content.Look;
import org.catrobat.catroid.pocketcode.content.Project;
import org.catrobat.catroid.pocketcode.content.Sprite;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class LookTest extends InstrumentationTestCase {
	private Look look;
	private Sprite sprite;
	private Group parentGroup;
	private Project project;

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
		assertEquals("Wrong initialization!", 1f, look.getAlphaValue());
		assertEquals("Wrong initialization!", 1f, look.getBrightness());
		assertEquals("Wrong initialization!", 1f, look.getSize());
		assertEquals("Wrong initialization!", 0, look.getZIndex());
		assertEquals("Wrong initialization!", true, look.show);
		assertEquals("Wrong initialization!", Touchable.enabled, look.getTouchable());
		assertEquals("Wrong initialization!", "", look.getImagePath());

	}

	public void testXYPositions() {
		look.setXInUserInterfaceDimensionUnit(50f);
		assertEquals("Wrong x position!", 50f, look.getXInUserInterfaceDimensionUnit());
		look.setYInUserInterfaceDimensionUnit(120f);
		assertEquals("Wrong y position!", 120f, look.getYInUserInterfaceDimensionUnit());
		look.setWidth(120f);
		look.setHeight(200f);
		look.setXInUserInterfaceDimensionUnit(66f);
		assertEquals("Wrong x position!", 66f, look.getXInUserInterfaceDimensionUnit());
		look.setYInUserInterfaceDimensionUnit(42f);
		assertEquals("Wrong y position!", 42f, look.getYInUserInterfaceDimensionUnit());
		look.setXYInUserInterfaceDimensionUnit(123f, 456f);
		assertEquals("Wrong x position!", 123f, look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong x position!", 456f, look.getYInUserInterfaceDimensionUnit());
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

	public void testSize() {
		look.setSize(2f);
		assertEquals("Wrong size!", 2f, look.getSize());
	}

	public void testAlphaValue() {
		look.setAlphaValue(0.5f);
		assertEquals("Wrong alpha value!", 0.5f, look.getAlphaValue());
		look.changeAlphaValueBy(0.2f);
		assertEquals("Wrong alpha value!", 0.7f, look.getAlphaValue());
	}

	public void testBrightnessValue() {
		look.setBrightness(0.42f);
		assertEquals("Wrong brightness value!", 0.42f, look.getBrightness());
		look.changeBrightnessValueBy(0.2f);
		assertEquals("Wrong brightness value!", 0.62f, look.getBrightness());
	}

}
