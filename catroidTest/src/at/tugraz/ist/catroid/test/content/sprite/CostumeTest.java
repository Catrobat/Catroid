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
package at.tugraz.ist.catroid.test.content.sprite;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;

public class CostumeTest extends InstrumentationTestCase {
	private Costume costume;
	private Sprite sprite;
	private Project project;

	@Override
	protected void setUp() {
		sprite = new Sprite("test");
		costume = sprite.costume;
	}

	public void testConstructor() {
		assertEquals("Wrong initialization!", 0f, costume.x);
		assertEquals("Wrong initialization!", 0f, costume.y);
		assertEquals("Wrong initialization!", 0f, costume.height);
		assertEquals("Wrong initialization!", 0f, costume.width);
		assertEquals("Wrong initialization!", 0f, costume.originX);
		assertEquals("Wrong initialization!", 0f, costume.originY);
		assertEquals("Wrong initialization!", 0f, costume.rotation);
		assertEquals("Wrong initialization!", 1f, costume.scaleX);
		assertEquals("Wrong initialization!", 1f, costume.scaleY);
		assertEquals("Wrong initialization!", 1f, costume.getAlphaValue());
		assertEquals("Wrong initialization!", 1f, costume.getBrightnessValue());
		assertEquals("Wrong initialization!", 1f, costume.getSize());
		assertEquals("Wrong initialization!", 0, costume.zPosition);
		assertEquals("Wrong initialization!", true, costume.show);
		assertEquals("Wrong initialization!", true, costume.touchable);
		assertEquals("Wrong initialization!", "", costume.getImagePath());

	}

	public void testXYPositions() {
		costume.setXPosition(50f);
		assertEquals("Wrong x position!", 50f, costume.getXPosition());
		costume.setYPosition(120f);
		assertEquals("Wrong y position!", 120f, costume.getYPosition());
		costume.width = 120f;
		costume.height = 200f;
		costume.setXPosition(66f);
		assertEquals("Wrong x position!", 66f, costume.getXPosition());
		costume.setYPosition(42f);
		assertEquals("Wrong y position!", 42f, costume.getYPosition());
		costume.setXYPosition(123f, 456f);
		assertEquals("Wrong x position!", 123f, costume.getXPosition());
		assertEquals("Wrong x position!", 456f, costume.getYPosition());
	}

	public void testImagePath() {
		String projectName = "myProject";
		String fileName = "blubb";
		project = new Project(null, projectName);
		ProjectManager.getInstance().setProject(project);

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(fileName);
		costume.setCostumeData(costumeData);
		assertEquals("Wrong image path!", Consts.DEFAULT_ROOT + "/" + projectName + "/" + Consts.IMAGE_DIRECTORY + "/"
				+ fileName, costume.getImagePath());
	}

	public void testSize() {
		costume.setSize(2f);
		assertEquals("Wrong size!", 2f, costume.getSize());
	}

	public void testAlphaValue() {
		costume.setAlphaValue(0.5f);
		assertEquals("Wrong alpha value!", 0.5f, costume.getAlphaValue());
		costume.changeAlphaValueBy(0.2f);
		assertEquals("Wrong alpha value!", 0.7f, costume.getAlphaValue());
	}

	public void testBrightnessValue() {
		costume.setBrightnessValue(0.42f);
		assertEquals("Wrong brightness value!", 0.42f, costume.getBrightnessValue());
		costume.changeBrightnessValueBy(0.2f);
		assertEquals("Wrong brightness value!", 0.62f, costume.getBrightnessValue());
	}

}
