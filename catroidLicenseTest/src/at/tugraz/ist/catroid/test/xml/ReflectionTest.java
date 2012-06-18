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
package at.tugraz.ist.catroid.test.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.xml.ObjectCreator;
import at.tugraz.ist.catroid.xml.ProjectProxy;

public class ReflectionTest extends TestCase {

	public void testFillingClassfromSetter() {
		ObjectCreator populator = new ObjectCreator();
		File testStandardXML = new File("res/catroidXMLsToValidate/standardProjekt.xml");
		InputStream standardFileStream = null;
		try {
			standardFileStream = new FileInputStream(testStandardXML);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		ProjectProxy createdProject = null;
		//for (int i = 0; i < 100; i++) {
		createdProject = populator.setterSet(standardFileStream);
		//}
		assertEquals("androidVersion tag not set", createdProject.getAndroidVersion(), 10);
		assertEquals("catroidVersionCode tag not set", createdProject.getCatroidVersionCode(), 10);
		assertEquals("catroidVersionName tag not set", createdProject.getCatroidVersionName(), "0.5.6a");
		assertEquals("deviceName tag not set", createdProject.getDeviceName(), "GT-I9100");
		assertEquals("ProjectName tag not set", createdProject.getProjectName(), "standardProjekt");
		assertEquals("screenHeight tag not set", createdProject.getVirtualScreenHeight(), 800);
		assertEquals("screenWidth tag not set", createdProject.getVirtualScreenWidth(), 480);
	}

	public void testFillingClassfromReflection() {
		ObjectCreator populator = new ObjectCreator();
		File testStandardXML = new File("res/catroidXMLsToValidate/standardProjekt.xml");
		InputStream standardFileStream = null;

		try {
			standardFileStream = new FileInputStream(testStandardXML);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		Project createdProject = null;
		//for (int i = 0; i < 100; i++) {
		createdProject = populator.reflectionSet(standardFileStream);
		assertNotNull(createdProject);

		//}
		//		assertEquals("androidVersion tag not set", createdProject.getAndroidVersion(), 10);
		//		assertEquals("catroidVersionCode tag not set", createdProject.getCatroidVersionCode(), 10);
		//		assertEquals("catroidVersionName tag not set", createdProject.getCatroidVersionName(), "0.5.6a");
		//		assertEquals("deviceName tag not set", createdProject.getDeviceName(), "GT-I9100");
		assertEquals("ProjectName tag not set", createdProject.getName(), "standardProjekt");
		assertEquals("screenHeight tag not set", createdProject.virtualScreenHeight, 480);
		assertEquals("screenWidth tag not set", createdProject.virtualScreenWidth, 800);

	}

}
