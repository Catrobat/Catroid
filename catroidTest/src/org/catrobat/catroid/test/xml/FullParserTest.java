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
package org.catrobat.catroid.test.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.xml.parser.FullParser;
import org.catrobat.catroid.xml.parser.ParseException;

import android.test.InstrumentationTestCase;

public class FullParserTest extends InstrumentationTestCase {

	public void testSpriteListParsing() {
		InputStream xmlFileStream = null;

		try {
			xmlFileStream = getInstrumentation().getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}

		List<Sprite> values = null;
		try {
			Project testProject = FullParser.parseSpritesWithProject(xmlFileStream);
			values = testProject.getSpriteList();
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
		}
		assertNotNull("Values are null", values);
		assertEquals("All the sprites are not captures or incorrect", 3, values.size());
		assertEquals("Sprite name not correct", "second", values.get(2).getName());
		assertEquals("Scripts not parsed", 1, values.get(2).getNumberOfScripts());
		StartScript testScript = (StartScript) values.get(1).getScript(0);
		assertNotNull("Script is null", testScript);
		assertEquals("Script number of brick incorrect", 6, testScript.getBrickList().size());
		SetSizeToBrick testBrick = (SetSizeToBrick) testScript.getBrick(2);
		double sizeFormBrick = (Double) Reflection.getPrivateField(testBrick, "size");
		assertEquals("SetSizetoBrick size incorrect", 0.8, sizeFormBrick);

		WhenScript testWhenScript = (WhenScript) values.get(1).getScript(1);
		assertEquals("WhenScript action incorrect", "Tapped", testWhenScript.getAction());

		StartScript testScript2 = (StartScript) values.get(2).getScript(0);
		GlideToBrick testBrick2 = (GlideToBrick) testScript2.getBrick(5);
		int xPosition = (Integer) Reflection.getPrivateField(testBrick2, "xDestination");
		int yPosition = (Integer) Reflection.getPrivateField(testBrick2, "yDestination");
		int duration = (Integer) Reflection.getPrivateField(testBrick2, "durationInMilliSeconds");
		assertEquals("Wrong GlideToBrick x position", 500, xPosition);
		assertEquals("Wrong GlideToBrick y position", 500, yPosition);
		assertEquals("Wrong GlideToBrick duration", 3000, duration);
	}

	public void testParsingFullProject() {
		Project testProject = null;
		try {
			testProject = XmlTestUtils.loadProjectFromAssets("test_project.xml", getInstrumentation().getContext());
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
		PointInDirectionBrick pointBrick = (PointInDirectionBrick) testProject.getSpriteList().get(1).getScript(0)
				.getBrick(5);
		assertNotNull("pointTowards null", pointBrick);
		double directionDegrees = (Double) Reflection.getPrivateField(pointBrick, "degrees");
		assertEquals("direction wrong", -90.0, directionDegrees);
		Direction direction = (Direction) Reflection.getPrivateField(pointBrick, "direction");
		assertNotNull("direction is null, read resolve not run", direction);
	}

	public void testCostumeListParsing() {
		Project testProject = null;
		try {
			testProject = XmlTestUtils.loadProjectFromAssets("standardProject.xml", getInstrumentation().getContext());
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unexpected parser Exception");
		}

		assertNotNull("project not created", testProject);
		List<Sprite> sprites = testProject.getSpriteList();
		assertEquals("all sprites not given", 2, sprites.size());
		Sprite testSprite = sprites.get(1);
		@SuppressWarnings("unchecked")
		List<CostumeData> givenCostumes = (List<CostumeData>) Reflection.getPrivateField(testSprite, "costumeList");

		assertEquals("costumes number wrong", 3, givenCostumes.size());
		CostumeData testData = givenCostumes.get(1);
		String testFileName = (String) Reflection.getPrivateField(testData, "fileName");
		assertEquals("Costume file name wrong", "FE5DF421A5746EC7FC916AC1B94ECC17_banzaiCat", testFileName);
		WhenScript script = (WhenScript) testSprite.getScript(1);
		SetCostumeBrick costumeBrick = (SetCostumeBrick) script.getBrick(0);
		assertNotNull("brick sprite is null", costumeBrick.getSprite());
		testData = (CostumeData) Reflection.getPrivateField(costumeBrick, "costume");
		testFileName = (String) Reflection.getPrivateField(testData, "fileName");
		assertEquals("costume data wrong", "FE5DF421A5746EC7FC916AC1B94ECC17_banzaiCat", testFileName);
		StartScript startScript = (StartScript) testSprite.getScript(0);
		RepeatBrick repeatBrick = (RepeatBrick) startScript.getBrick(1);

		assertNotNull("repeat brick is null", repeatBrick);
		int timesToRepeat = (Integer) Reflection.getPrivateField(repeatBrick, "timesToRepeat");
		assertEquals("repeat brick times to repeat incorrect", 3, timesToRepeat);
		LoopEndBrick loopEndBrick = repeatBrick.getLoopEndBrick();
		assertNotNull("Costume data null", loopEndBrick);
		LoopEndBrick lebFromXML = (LoopEndBrick) startScript.getBrick(3);
		assertNotNull("The LoopEndBrick is null", lebFromXML);
		LoopBeginBrick repeatBrickFromLoopEnd = lebFromXML.getLoopBeginBrick();
		assertNotNull("The LoopBeginBrick is null", repeatBrickFromLoopEnd);
	}

	public void testSoundListParsing() {
		Project testProject = null;
		try {
			testProject = XmlTestUtils.loadProjectFromAssets("test_sound_project.xml", getInstrumentation()
					.getContext());
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unexpected parse exception");
		}

		assertNotNull("project not created", testProject);
		List<Sprite> sprites = testProject.getSpriteList();
		assertEquals("all sprites not given", 6, sprites.size());
		Sprite testSprite = sprites.get(1);
		@SuppressWarnings("unchecked")
		List<SoundInfo> soundList = (List<SoundInfo>) Reflection.getPrivateField(testSprite, "soundList");
		assertNotNull("Sound List is null", soundList);
		assertEquals("All soundInfo items not created", 2, soundList.size());
		SoundInfo soundListSoundinfo = soundList.get(0);
		assertEquals("SoundInfo file name not correct",
				"B318332ADA3D79C0012978166F38E9F9_Geige_Super Mario on violin.mp3",
				soundListSoundinfo.getSoundFileName());
		WhenScript testScript = (WhenScript) testSprite.getScript(1);

		PlaySoundBrick playSoundBrick = (PlaySoundBrick) testScript.getBrick(4);

		assertNotNull("The PlaySoundBrick is null", playSoundBrick);
		SoundInfo brickSoundInfo = (SoundInfo) Reflection.getPrivateField(playSoundBrick, "sound");
		assertEquals("SoundInfo name is not correct", "Geige", brickSoundInfo.getTitle());
		assertEquals("Sound infos don't match", soundListSoundinfo, brickSoundInfo);
	}

	public void testPerformanceTest() {
		Project testProject = null;
		try {
			testProject = XmlTestUtils.loadProjectFromAssets("test_pointto_project.xml", getInstrumentation()
					.getContext());
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unexpected parser exception");
		}

		assertNotNull("project not created", testProject);
		List<Sprite> sprites = null;
		sprites = testProject.getSpriteList();
		assertEquals("all sprites not given", 9, sprites.size());

		StartScript testScript = (StartScript) sprites.get(7).getScript(0);
		PointToBrick pointtoBrick = (PointToBrick) testScript.getBrick(6);
		assertNotNull("Point to brick is null", pointtoBrick);
		Sprite pointedSprite = (Sprite) Reflection.getPrivateField(pointtoBrick, "pointedSprite");
		assertNotNull("Pointed Sprite is null", pointedSprite);
		assertEquals("Poined sprite wrong", pointedSprite.getName(), sprites.get(1).getName());
	}

	public void testParseMalformedProject() {
		try {
			XmlTestUtils.loadProjectFromAssets("test_malformed_project.xml", getInstrumentation().getContext());
			fail("parse exception expected");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void testLoadProjectTwoTimes() {
		Project loadedProject = null;
		Project loadedProject2 = null;
		try {
			InputStream firstFileStream = getInstrumentation().getContext().getAssets().open("standardProject.xml");
			loadedProject = FullParser.parseSpritesWithProject(firstFileStream);
			assertNotNull("loadedProject null", loadedProject);
			assertEquals("sprites not right", 2, loadedProject.getSpriteList().size());

			InputStream secondFileStream = getInstrumentation().getContext().getAssets().open("standardProject.xml");
			loadedProject2 = FullParser.parseSpritesWithProject(secondFileStream);
			assertNotNull("loadedProject null", loadedProject2);
			assertEquals("sprites not right", 2, loadedProject2.getSpriteList().size());
		} catch (IOException e) {
			fail("Unexpected parse exception");
			e.printStackTrace();
		} catch (ParseException e) {
			fail("Unexpected parse exception");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
		}
	}
}
