/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.common;

import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.Serializable;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectData implements Nameable, Serializable, Comparable<ProjectData> {

	private static final long serialVersionUID = 1L;

	private String name;
	private File directory;
	private double languageVersion;
	private boolean hasScenes;

	public ProjectData(String name, File directory, double languageVersion, boolean hasScenes) {
		this.name = name;
		this.directory = directory;
		this.languageVersion = languageVersion;
		this.hasScenes = hasScenes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		throw new RuntimeException("Do not set the project name through this. TODO: refactor nameable interface.");
	}

	public double getLanguageVersion() {
		return languageVersion;
	}

	public boolean hasScenes() {
		return hasScenes;
	}

	public long getLastUsed() {
		return new File(directory, CODE_XML_FILE_NAME).lastModified();
	}

	public File getDirectory() {
		return new File(DEFAULT_ROOT_DIRECTORY, FileMetaDataExtractor.encodeSpecialCharsForFileSystem(getName()));
	}

	@Override
	public int compareTo(ProjectData projectData) {
		return name.compareTo(projectData.getName());
	}
}
