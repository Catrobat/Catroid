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
import android.util.Log;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.xml.FullParser;
import at.tugraz.ist.catroid.xml.ParseException;
import at.tugraz.ist.catroid.xml.serializer.BrickSerializer;
import at.tugraz.ist.catroid.xml.serializer.CostumeSerializer;
import at.tugraz.ist.catroid.xml.serializer.SoundSerializer;

public class FullParserTest extends InstrumentationTestCase {

	Context androidContext;

	@Override
	protected void setUp() throws Exception {
		androidContext = getInstrumentation().getContext();
		NativeAppActivity.setContext(androidContext);
	}

	@Override
	protected void tearDown() throws Exception {
		androidContext = null;
		NativeAppActivity.setContext(androidContext);
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
			Project testProject = parser.parseSpritesWithProject(xmlFileStream);
			values = testProject.getSpriteList();
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
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
		assertEquals("WhenScript action incorrect", "TappedChanged", testWhnScript.getAction());

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
		Project testProject = null;
		try {
			xmlFileStream = androidContext.getAssets().open("test_project.xml");
			testProject = parser.fullParser("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unexpected parse Exception");
		}
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
		Project testProject = null;
		try {
			xmlFileStream = androidContext.getAssets().open("test_standard_project.xml");
			testProject = parser.fullParser("test_standard_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unexpected parser Exception");
		}

		assertNotNull("project not created", testProject);
		List<Sprite> sprites = testProject.getSpriteList();
		assertEquals("all sprites not given", 2, sprites.size());
		Sprite testSprite = sprites.get(1);
		List<CostumeData> givenCostumes = (List<CostumeData>) TestUtils.getPrivateField("costumeDataList", testSprite,
				false);
		CostumeSerializer cs = new CostumeSerializer();
		try {

			List<String> script2 = cs.serializeCostumeList(givenCostumes);
			for (String element : script2) {
				Log.i("script element", element);
			}
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		assertEquals("costumes number wrong", 3, givenCostumes.size());
		CostumeData testData = givenCostumes.get(1);
		String testfileName = (String) TestUtils.getPrivateField("fileName", testData, false);
		assertEquals("Costume file name wrong", "FE5DF421A5746EC7FC916AC1B94ECC17_banzaiCat", testfileName);
		WhenScript script = (WhenScript) testSprite.getScript(1);
		//		ScriptSerializer scriptSerializer = new ScriptSerializer(testSprite, testProject);
		//		try {
		//			List<String> script2 = scriptSerializer.serialize(script);
		//			for (String element : script2) {
		//				Log.i("script element", element);
		//			}
		//		} catch (IllegalArgumentException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		} catch (IllegalAccessException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}
		SetCostumeBrick costumeBrick = (SetCostumeBrick) script.getBrick(0);
		testData = (CostumeData) TestUtils.getPrivateField("costumeData", costumeBrick, false);
		testfileName = (String) TestUtils.getPrivateField("fileName", testData, false);
		assertEquals("costume data wrong", "FE5DF421A5746EC7FC916AC1B94ECC17_banzaiCat", testfileName);

		//RepeatBrick repeatBrick = (RepeatBrick) script.getBrick(1);
		BrickSerializer bs = new BrickSerializer(testSprite, script, testProject);
		try {
			bs.serialize(costumeBrick);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		assertNotNull("repeat brick is null", repeatBrick);
		//		int timestoRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", repeatBrick, false);
		//		assertEquals("repeat brick times to repeat incorrect", 3, timestoRepeat);
		//		LoopEndBrick leb = repeatBrick.getLoopEndBrick();
		//		//CostumeData testCostData = (CostumeData) TestUtils.getPrivateField("costumeData", costBrick, false);
		//		assertNotNull("Costume data null", leb);
		//		LoopEndBrick lebFromXML = (LoopEndBrick) script.getBrick(3);
		//		assertNotNull("The LoopEndBrick is null", lebFromXML);
		//		LoopBeginBrick rb = lebFromXML.getLoopBeginBrick();
		//		assertNotNull("The Loop BeginBrick is null", rb);

	}

	public void testSoundListParsing() {
		FullParser parser = new FullParser();
		InputStream xmlFileStream = null;
		Project testProject = null;

		try {
			xmlFileStream = androidContext.getAssets().open("test_sound_project.xml");
			testProject = parser.fullParser("test_sound_project.xml");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception caught at getting filestream");
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unexpected parse exception");
		}

		assertNotNull("project not created", testProject);
		List<Sprite> sprites = testProject.getSpriteList();
		assertEquals("all sprites not given", 6, sprites.size());
		Sprite testSprite = sprites.get(1);
		List<SoundInfo> soundList = (List<SoundInfo>) TestUtils.getPrivateField("soundList", testSprite, false);
		assertNotNull("Sound List is null", soundList);
		assertEquals("All soundInfo items not created", 2, soundList.size());
		SoundInfo si = soundList.get(0);
		assertEquals("SoundInfo file name not correct",
				"B318332ADA3D79C0012978166F38E9F9_Geige_Super Mario on violin.mp3", si.getSoundFileName());
		WhenScript testScript = (WhenScript) testSprite.getScript(1);

		PlaySoundBrick playSoundBrick = (PlaySoundBrick) testScript.getBrick(4);
		//		BrickSerializer bs = new BrickSerializer(testSprite, testScript, testProject);
		//		try {
		//			bs.serialize(playSoundBrick);
		//		} catch (IllegalArgumentException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (IllegalAccessException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		assertNotNull("The PlaySoundBrick is null", playSoundBrick);
		SoundInfo si2 = (SoundInfo) TestUtils.getPrivateField("soundInfo", playSoundBrick, false);
		assertEquals("SoundInfo name is not correct", "Geige", si2.getTitle());
	}

	public void testPerformanceTest() {
		FullParser parser = new FullParser();

		Project testProject = null;
		//		StorageHandler sh = StorageHandler.getInstance();
		//		testProject = sh.loadProject("test_aquarium_project.xml");
		try {

			testProject = parser.fullParser("test_pointto_project.xml");

		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unexpected parser exception");
		}

		assertNotNull("project not created", testProject);
		List<Sprite> sprites = null;
		sprites = testProject.getSpriteList();
		assertEquals("all sprites not given", 9, sprites.size());
		SoundSerializer ss = new SoundSerializer();

		try {
			List<String> script2 = ss.serializeSoundList(sprites.get(3).getSoundList());
			for (String element : script2) {
				Log.i("script element", element);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		StartScript testScript = (StartScript) sprites.get(7).getScript(0);
		//		PointToBrick pointtoBrick = (PointToBrick) testScript.getBrick(6);
		//		assertNotNull("Point to brick is null", pointtoBrick);
		//		Sprite pointedSprite = (Sprite) TestUtils.getPrivateField("pointedSprite", pointtoBrick, false);
		//		assertNotNull(pointedSprite);
		//		assertEquals(pointedSprite.getName(), sprites.get(1).getName());
	}

	public void testParseMalformedProject() {
		FullParser parser = new FullParser();
		try {
			Project testProject = parser.fullParser("test_malformed_project.xml");
			fail("Parseexception expected");
		} catch (ParseException e) {
			e.printStackTrace();
			//fail("Parseexception expected");
		}
	}

}
