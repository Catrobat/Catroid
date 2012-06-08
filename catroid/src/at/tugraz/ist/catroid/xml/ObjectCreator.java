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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Sam
 * 
 */
public class ObjectCreator {

	/**
	 * @param XMLFile
	 * @return
	 */
	public ProjectProxy setterSet(InputStream XMLFile) {
		SimpleParser parser = new SimpleParser();
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

	/**
	 * @param XMLFile
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public ProjectProxy reflectionSet(InputStream XMLFile) {
		SimpleParser parser = new SimpleParser();
		List<String> headerVlaues = parser.parse(XMLFile);
		Class projectClass;
		ProjectProxy project = null;

		SetterNames[] setterIndexes = SetterNames.values();
		try {
			projectClass = Class.forName("at.tugraz.ist.catroid.xml.ProjectProxy");
			project = new ProjectProxy();
			Method methodList[] = projectClass.getDeclaredMethods();
			for (int i = 0; i < methodList.length; i++) {
				String methodName = methodList[i].getName();
				if (methodName.startsWith("set")) {
					for (int j = 0; j < setterIndexes.length; j++) {
						if (methodName.equalsIgnoreCase(setterIndexes[j].getsetterName())) {
							Class params[] = methodList[i].getParameterTypes();
							Method setterMehod = projectClass.getMethod(methodName, params);
							Object arg = null;

							if (params[0].getCanonicalName().equals("int")) {
								arg = new Integer(Integer.valueOf(headerVlaues.get(j)));
							} else if (params[0].getCanonicalName().equals("java.lang.String")) {
								arg = new String(headerVlaues.get(j));
							}
							setterMehod.invoke(project, arg);
							j = setterIndexes.length;
						}
					}
				}
			}

		} catch (Throwable e) {
			System.err.println(e);

		}

		return project;
	}

	/**
	 * @param xmlTagString
	 * @return
	 */
	private String getSetterName(String xmlTagString) {

		char c = (char) (xmlTagString.charAt(0) - 32);
		Character d = c;

		return "set" + (d.toString().concat(xmlTagString.substring(1)));
	}

}
