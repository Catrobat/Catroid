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
package org.catrobat.catroid.xml.serializer;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.catrobat.catroid.content.Project;

public class XmlSerializer {
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>";

	public void toXml(Project projectToSerialize, String pathToXML) throws SerializeException {
		ProjectSerializer projectSerializer = new ProjectSerializer();

		PrintWriter xmlWriter;

		try {
			List<String> projectXmlLines = projectSerializer.serialize(projectToSerialize);
			xmlWriter = new PrintWriter(new File(pathToXML));
			xmlWriter.println(XML_HEADER);
			for (String xmlLine : projectXmlLines) {
				xmlWriter.print(xmlLine);
			}
			xmlWriter.flush();
			xmlWriter.close();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new SerializeException("Exception when Serializing to xml", e);
		}
	}
}
