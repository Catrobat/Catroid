/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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
package org.catrobat.catroid.test.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

public class XMLValidatingTest extends TestCase {

	private static final String XMLSCHEMA_URL = "http://catroid.org/CatrobatLanguage/xmlSchema/version-0.3/catrobatXmlSchema.xsd";

	public void testXmlWithSchemaValidator() throws IOException, SAXException {

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		URL schemaUrl = new URL(XMLSCHEMA_URL);
		Schema schema = factory.newSchema(schemaUrl);

		Validator schemaValidator = schema.newValidator();

		File xmlDirectory = new File("res/catrobat_v0.3_XMLsToValidate/");
		File[] xmlFilesToValidate = xmlDirectory.listFiles();

		File currentXMLFile = null;
		try {
			for (File xmlFile : xmlFilesToValidate) {
				currentXMLFile = xmlFile;
				Source source = new StreamSource(currentXMLFile);
				schemaValidator.validate(source);
			}
		} catch (SAXException ex) {
			ex.printStackTrace();
			assertFalse(currentXMLFile + " is not valid because: " + ex.getMessage(), true);
		}
	}

}
