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

package org.catrobat.catroid.projecthandler;

import com.thoughtworks.xstream.XStream;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.data.LookInfo;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.data.SoundInfo;
import org.catrobat.catroid.data.SpriteInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public final class ProjectHolder {

	public static final String TAG = ProjectHolder.class.getSimpleName();

	private static final ProjectHolder INSTANCE = new ProjectHolder();

	private ProjectInfo currentProject;

	public static ProjectHolder getInstance() {
		return INSTANCE;
	}

	public ProjectInfo deserialize(String name) throws FileNotFoundException {
		File file = new File(Constants.DEFAULT_ROOT
				+ "/" + Constants.PROJECTS_DIRECTORY
				+ "/" + name
				+ "/" + name + ".xml");

		if (!file.exists()) {
			throw new FileNotFoundException("Project xml for " + name + "does not exits.");
		}

		XStream xStream = new XStream();
		setXStreamAliases(xStream);

		return (ProjectInfo) xStream.fromXML(file);
	}

	public void serialize(ProjectInfo project) throws IOException {
		XStream xStream = new XStream();
		setXStreamAliases(xStream);

		String xml = xStream.toXML(project);
		String filename = Constants.DEFAULT_ROOT
				+ "/" + Constants.PROJECTS_DIRECTORY
				+ "/" + project.getName()
				+ "/" + project.getName() + ".xml";

		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

		writer.write(xml);
		writer.close();
	}

	private void setXStreamAliases(XStream xStream) {
		xStream.alias("project", ProjectInfo.class);
		xStream.alias("scene", SceneInfo.class);
		xStream.alias("sprite", SpriteInfo.class);
		xStream.alias("look", LookInfo.class);
		xStream.alias("sound", SoundInfo.class);
	}

	public ProjectInfo getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(ProjectInfo project) {
		currentProject = project;
	}
}
