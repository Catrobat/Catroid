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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.xml.FullParser;
import at.tugraz.ist.catroid.xml.ParseException;

public class FullParserTest extends InstrumentationTestCase {

	Context androidContext;

	@Override
	protected void setUp() throws Exception {
		androidContext = getInstrumentation().getContext();
	}

	@Override
	protected void tearDown() throws Exception {
		androidContext = null;
	}

	public void testSpriteListParsing() {
		FullParser parser = new FullParser();

		InputStream xmlFileStream = null;

		try {
			xmlFileStream = androidContext.getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}

		List<Sprite> values = null;
		try {
			values = parser.parseSprites(xmlFileStream);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
		}
		for (int i = 0; i < values.size(); i++) {

		}
		assertNotNull("Values are null", values);
		assertEquals("All the sprites are not captures or incorrect", 3, values.size());
		assertEquals("Sprite name not correct", "second", values.get(2).getName());
		assertEquals("Scripts not parsed", 1, values.get(2).getNumberOfScripts());
		StartScript testScript = (StartScript) values.get(1).getScript(0);
		assertNotNull("Script is null", testScript);
		assertEquals("Script number of brick incorrect", 5, testScript.getBrickList().size());
		SetSizeToBrick testBrick = (SetSizeToBrick) testScript.getBrick(2);
		double sizeFormBrick = (Double) TestUtils.getPrivateField("size", testBrick, false);
		assertEquals("SETSizetoBrick size incorrect", 0.8, sizeFormBrick);

		WhenScript testWhnScript = (WhenScript) values.get(1).getScript(1);
		assertEquals("When Script action incorrect", "Tapped", testWhnScript.getAction());

		StartScript testScript2 = (StartScript) values.get(2).getScript(0);
		GlideToBrick testBrick2 = (GlideToBrick) testScript2.getBrick(5);
		int xpos = (Integer) TestUtils.getPrivateField("xDestination", testBrick2, false);
		int ypos = (Integer) TestUtils.getPrivateField("xDestination", testBrick2, false);
		int dur = (Integer) TestUtils.getPrivateField("durationInMilliSeconds", testBrick2, false);
		assertEquals("place at position x wrong", 500, xpos);
		assertEquals("place at position y wrong", 500, ypos);
		assertEquals("place at position y wrong", 3000, dur);

	}

	public void testParsingFullProject() {
		FullParser parser = new FullParser();
		InputStream xmlFileStream = null;

		try {
			xmlFileStream = androidContext.getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}
		Project testProject = parser.fullParser(xmlFileStream);

		assertNotNull("Project is null", testProject);
		assertEquals("Project name not correct", "testProject", testProject.getName());
		assertEquals("Project sprite List size incorrect", 3, testProject.getSpriteList().size());
		List<Sprite> sprites = testProject.getSpriteList();
		ShowBrick testBrick = (ShowBrick) sprites.get(1).getScript(0).getBrick(1);
		assertNotNull("Brick is null", testBrick);
		assertNotNull("sprite of brick is null", testBrick.getSprite());
		assertEquals("Script number of 3rd sprite wrong", 1, testProject.getSpriteList().get(2).getNumberOfScripts());
		assertEquals("Number of bricks for the script of 3rd sprite is wrong", 7, testProject.getSpriteList().get(2)
				.getNumberOfBricks());
	}

	public void testCostumeListParsing() {
		FullParser parser = new FullParser();
		InputStream xmlFileStream = null;

		try {
			xmlFileStream = androidContext.getAssets().open("test_standard_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}
		Project testProject = parser.fullParser(xmlFileStream);
		assertNotNull("project not created", testProject);
		List<Sprite> sprites = testProject.getSpriteList();
		assertEquals("all sprites not given", 2, sprites.size());
		Sprite testSprite = sprites.get(1);
		List<CostumeData> givenCostumes = (List<CostumeData>) TestUtils.getPrivateField("costumeDataList", testSprite,
				false);
		assertEquals("costumes number wrong", 3, givenCostumes.size());
		CostumeData testData = givenCostumes.get(1);
		String testfileName = (String) TestUtils.getPrivateField("fileName", testData, false);
		assertEquals("Costume file name wrong", "FE5DF421A5746EC7FC916AC1B94ECC17_banzaiCat", testfileName);
	}

}
