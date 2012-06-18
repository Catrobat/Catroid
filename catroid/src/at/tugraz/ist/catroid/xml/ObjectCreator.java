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
package at.tugraz.ist.catroid.xml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import at.tugraz.ist.catroid.content.Project;

public class ObjectCreator {

	public ProjectProxy setterSet(InputStream XMLFile) {

		HeaderTagsParser parser = new HeaderTagsParser();
		List<String> headerVlaues = parser.parse(XMLFile);
		ProjectProxy newProject = new ProjectProxy();

		newProject.setAndroidVersion(Integer.valueOf(headerVlaues.get(HeaderTags.ANDROIDVERSION.ordinal())));
		newProject.setCatroidVersionCode(Integer.valueOf(headerVlaues.get(HeaderTags.CATROIDVERSIONCODE.ordinal())));
		newProject.setCatroidVersionName(headerVlaues.get(HeaderTags.CATROIDVERSIONNAME.ordinal()));
		newProject.setDeviceName(headerVlaues.get(HeaderTags.DEVICENAME.ordinal()));
		newProject.setProjectName(headerVlaues.get(HeaderTags.PROJECTNAME.ordinal()));
		newProject.setVirtualScreenHeight(Integer.valueOf(headerVlaues.get(HeaderTags.SCREENHEIGHT.ordinal())));
		newProject.setVirtualScreenWidth(Integer.valueOf(headerVlaues.get(HeaderTags.SCREENWIDTH.ordinal())));

		return newProject;

	}

	public Project reflectionSet(InputStream XMLFile) {
		HeaderTagsParser parser = new HeaderTagsParser();
		List<String> headerVlaues = parser.parse(XMLFile);
		Class projectClass;
		Project project = null;

		HeaderTags[] headerTag = HeaderTags.values();
		try {
			project = new Project();
			//projectClass = Class.forName("at.tugraz.ist.catroid.content.Project");
			//projectClass = Class.forName("at.tugraz.ist.catroid.xml.ProjectProxy");
			projectClass = project.getClass();
			for (int i = 0; i < 5; i++) {
				Field projectNameField = projectClass.getDeclaredField(headerTag[i].getXmlTagString());
				projectNameField.setAccessible(true);
				Object arg = null;
				String canName = projectNameField.getType().getCanonicalName();
				if (projectNameField.getType().getCanonicalName().equals("int")) {
					arg = new Integer(Integer.valueOf(headerVlaues.get(i)));
				} else if (projectNameField.getType().getCanonicalName().equals("java.lang.String")) {
					arg = new String(headerVlaues.get(i));
				}

				projectNameField.set(project, arg);
			}
			Field projectNameField = projectClass.getDeclaredField("virtualScreenWidth");
			projectNameField.setAccessible(true);
			projectNameField.set(project, Integer.valueOf(headerVlaues.get(5)));
			projectNameField = projectClass.getDeclaredField("virtualScreenHeight");
			projectNameField.setAccessible(true);
			projectNameField.set(project, Integer.valueOf(headerVlaues.get(6)));
			//			Method methodList[] = projectClass.getDeclaredMethods();
			//			for (int i = 0; i < methodList.length; i++) {
			//				String methodName = methodList[i].getName();
			//				if (methodName.startsWith("set")) {
			//					for (int j = 0; j < setterIndexes.length; j++) {
			//						if (methodName.equalsIgnoreCase(setterIndexes[j].getsetterName())) {
			//							Class params[] = methodList[i].getParameterTypes();
			//							Method setterMethod = projectClass.getMethod(methodName, params);
			//							Object arg = null;
			//
			//							if (params[0].getCanonicalName().equals("int")) {
			//								arg = new Integer(Integer.valueOf(headerVlaues.get(j)));
			//							} else if (params[0].getCanonicalName().equals("java.lang.String")) {
			//								arg = new String(headerVlaues.get(j));
			//							}
			//							setterMethod.invoke(project, arg);
			//							j = setterIndexes.length;
			//						}
			//					}
			//				}
			//			}

		} catch (Throwable e) {
			System.err.println(e);

		}

		return project;
	}
}
