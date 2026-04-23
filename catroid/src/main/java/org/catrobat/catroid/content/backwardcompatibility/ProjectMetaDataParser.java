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

package org.catrobat.catroid.content.backwardcompatibility;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.common.ProjectData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

public class ProjectMetaDataParser {

	private File xmlFile;

	public ProjectMetaDataParser(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public ProjectData getProjectMetaData() throws IOException {
		if (!xmlFile.exists()) {
			throw new FileNotFoundException(xmlFile.getAbsolutePath() + " does not exist.");
		}
		XStream xstream = new XStream();
		xstream.allowTypesByWildcard(new String[] {"org.catrobat.catroid.**"});
		xstream.processAnnotations(ProjectMetaData.class);
		xstream.ignoreUnknownElements();
		ProjectMetaData metaData;
		try {
			metaData = (ProjectMetaData) xstream.fromXML(xmlFile);
		} catch (Exception e) {
			throw new IOException("Project metadata invalid", e);
		}
		metaData.setFile(xmlFile);
		return new ProjectData(metaData.getName(),
				metaData.getDirectory(),
				metaData.getLanguageVersion(),
				metaData.hasScenes());
	}

	@XStreamAlias("program")
	private static class ProjectMetaData implements Serializable {

		private static final long serialVersionUID = 1L;

		private XmlHeaderMetaData header;
		private File xmlFile;

		public String getName() {
			return header.programName;
		}

		public void setFile(File xmlFile) {
			this.xmlFile = xmlFile;
		}

		public File getDirectory() {
			return xmlFile.getParentFile();
		}

		public double getLanguageVersion() {
			return header.catrobatLanguageVersion;
		}

		public boolean hasScenes() {
			return header.scenesEnabled;
		}

		private static final class XmlHeaderMetaData implements Serializable {

			private static final long serialVersionUID = 1L;

			private String programName;
			private double catrobatLanguageVersion;
			private boolean scenesEnabled = false;
		}
	}
}
