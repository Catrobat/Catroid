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

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

public class XMLTest extends TestCase {

	public XMLTest() throws IOException {

	}

	public void testXml() throws IOException, SAXException {
		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// 2. Compile the schema. 
		// Here the schema is loaded from a java.io.File, but you could use 
		// a java.net.URL or a javax.xml.transform.Source instead.
		File schemaLocation = new File("res/schema1.xsd");
		//Source schemaSource = new StreamSource(getContext().getAssets().open("catroid_xml_schema.xsd"));
		Schema schema = factory.newSchema(schemaLocation);

		// 3. Get a validator from the schema.
		Validator validator = schema.newValidator();

		// 4. Parse the document you want to check.
		String testProject = "test_project.xml";
		Source source = new StreamSource(new File("res/test_project.xml"));

		//		try {
		//			String line;
		//			//			Process p = Runtime.getRuntime().exec(
		//			//					new String[] { "/Users/knut0025/development/android-sdk-mac_86/platform-tools/adb", "shell", "am",
		//			//							"instrument", "-w",
		//			//							"at.tugraz.ist.catroid.test.content.project/android.test.InstrumentationTestRunner" });
		//			//
		//			//							"-e", "class",
		//			//							"at.tugraz.ist.catroid.test.content.project.ProjectManagerTest#createTestProject",
		//			//							"at.tugraz.ist.catroid.test.content.project/android.test.InstrumentationTestRunner" });
		//			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
		//			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		//			while ((line = bri.readLine()) != null) {
		//				System.out.println(line);
		//			}
		//			bri.close();
		//			while ((line = bre.readLine()) != null) {
		//				System.out.println(line);
		//			}
		//			bre.close();
		//			p.waitFor();
		//			System.out.println("Done.");
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		// 5. Check the document
		try {
			validator.validate(source);
			System.out.println(testProject + " is valid.");
		} catch (SAXException ex) {
			System.out.println(testProject + " is not valid because ");
			System.out.println(ex.getMessage());
			assertFalse(testProject + " is not valid because: " + ex.getMessage(), true);
		}
	}
}
