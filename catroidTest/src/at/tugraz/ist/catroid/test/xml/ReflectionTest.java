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

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.xml.ObjectCreator;
import at.tugraz.ist.catroid.xml.ProjectProxy;

public class ReflectionTest extends InstrumentationTestCase {

	public void testFillingClassfromSetter() {
		ObjectCreator populator = new ObjectCreator();
		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
		}
		ProjectProxy createdProject = null;
		//for (int i = 0; i < 100; i++) {
		createdProject = populator.setterSet(xmlFileStream);
		//}
		assertEquals("androidVersion tag not set", createdProject.getAndroidVersion(), 10);
		assertEquals("catroidVersionCode tag not set", createdProject.getCatroidVersionCode(), 8);
		assertEquals("catroidVersionName tag not set", createdProject.getCatroidVersionName(), "0.5.6a");
		assertEquals("deviceName tag not set", createdProject.getDeviceName(), "HTC Desire");
		assertEquals("ProjectName tag not set", createdProject.getProjectName(), "testProject");
		assertEquals("screenHeight tag not set", createdProject.getVirtualScreenHeight(), 800);
		assertEquals("screenWidth tag not set", createdProject.getVirtualScreenWidth(), 480);
	}

	public void testFillingClassfromReflection() {
		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
		}
		ObjectCreator populator = new ObjectCreator();

		Context testContext = this.getInstrumentation().getContext();
		ProjectProxy createdProject = null;
		//for (int i = 0; i < 100; i++) {
		createdProject = populator.reflectionSet(xmlFileStream);
		//}
		assertEquals("androidVersion tag not set", createdProject.getAndroidVersion(), 10);
		assertEquals("catroidVersionCode tag not set", createdProject.getCatroidVersionCode(), 8);
		assertEquals("catroidVersionName tag not set", createdProject.getCatroidVersionName(), "0.5.6a");
		assertEquals("deviceName tag not set", createdProject.getDeviceName(), "HTC Desire");
		assertEquals("ProjectName tag not set", createdProject.getProjectName(), "testProject");
		assertEquals("screenHeight tag not set", createdProject.getVirtualScreenHeight(), 800);
		assertEquals("screenWidth tag not set", createdProject.getVirtualScreenWidth(), 480);

	}

}
