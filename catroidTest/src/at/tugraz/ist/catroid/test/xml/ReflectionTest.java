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
package at.tugraz.ist.catroid.test.xml;

import java.io.IOException;
import java.io.InputStream;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.xml.parser.ObjectCreator;
import at.tugraz.ist.catroid.xml.parser.ParseException;

public class ReflectionTest extends InstrumentationTestCase {

	public void testFillingClassfromReflection() {
		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOexceptiona which can be FileNotFoundException");
		}

		ObjectCreator populator = new ObjectCreator();

		Project createdProject = null;

		try {
			createdProject = populator.getProjectWithHeaderValues(xmlFileStream);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Excption when parsing");
		}

		int androidVersionResult = (Integer) TestUtils.getPrivateField("platformVersion", createdProject, false);
		float catrobatLanguageVersionResult = (Float) TestUtils.getPrivateField("catrobatLanguageVersion",
				createdProject, false);
		String catroidVersionNameResult = (String) TestUtils.getPrivateField("applicationVersion", createdProject,
				false);
		String deviceNameresult = (String) TestUtils.getPrivateField("deviceName", createdProject, false);

		assertEquals("the Android version is wrong", 10, androidVersionResult);
		assertEquals("catrobatlanguageversion wrong", 0.3f, catrobatLanguageVersionResult);
		assertEquals("applicationVersion wrong", "0.6.0beta", catroidVersionNameResult);
		assertEquals("DeviceName wrong", "HTC Desire", deviceNameresult);
		assertNotNull("createdProject is null", createdProject);
		assertEquals("ProjectName tag not set", createdProject.getName(), "testProject");
		assertEquals("screenHeight tag not set", createdProject.virtualScreenHeight, 800);
		assertEquals("screenWidth tag not set", createdProject.virtualScreenWidth, 480);

	}

}
