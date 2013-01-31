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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.xml.parser.CatroidXMLConstants;
import org.catrobat.catroid.xml.parser.FullParser;
import org.catrobat.catroid.xml.parser.ParseException;
import org.catrobat.catroid.xml.serializer.SerializeException;
import org.catrobat.catroid.xml.serializer.XmlSerializer;

import android.test.InstrumentationTestCase;
import android.util.Log;

public class SerializerTest extends InstrumentationTestCase {

	public void testSerializingToXml() {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project();
		project.setName("testSerializeProject");
		Sprite firstSprite = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript = new StartScript(firstSprite);
		Script otherScript = new StartScript(secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(secondSprite, size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

		// adding Bricks: ----------------
		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(setSizeToBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick); // secondSprite
		otherScript.setPaused(true);
		// -------------------------------

		firstSprite.addScript(testScript);
		secondSprite.addScript(otherScript);

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		String projectDirectoryName = Utils.buildProjectPath("test__" + project.getName());
		File projectDirectory = new File(projectDirectoryName);
		try {

			if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
				projectDirectory.mkdir();

			}
			XmlSerializer.toXml(project, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (IllegalArgumentException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (SecurityException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (SerializeException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		}

		Project loadedProject = null;
		if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {

			try {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Constants.PROJECTCODE_NAME));
				loadedProject = FullParser.parseSpritesWithProject(projectFileStream);
			} catch (ParseException e) {
				fail("unexpected SerilizeException");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				fail("unexpected SerilizeException");
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				fail("unexpected SerilizeException");
				e.printStackTrace();
			}
		}

		ArrayList<Sprite> preSpriteList = (ArrayList<Sprite>) project.getSpriteList();
		ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getSpriteList();

		// Test sprite names:
		assertEquals("First sprite does not match after deserialization", preSpriteList.get(0).getName(),
				postSpriteList.get(0).getName());
		assertEquals("Second sprite does not match after deserialization", preSpriteList.get(1).getName(),
				postSpriteList.get(1).getName());
		assertEquals("Third sprite does not match after deserialization", preSpriteList.get(2).getName(),
				postSpriteList.get(2).getName());
		assertEquals("Fourth sprite does not match after deserialization", preSpriteList.get(3).getName(),
				postSpriteList.get(3).getName());

		// Test project name:
		assertEquals("Title missmatch after deserialization", project.getName(), loadedProject.getName());

		// Test random brick values
		int actualXPosition = (Integer) Reflection.getPrivateField(
				(postSpriteList.get(1).getScript(0).getBrickList().get(0)), "xPosition");
		int actualYPosition = (Integer) Reflection.getPrivateField(
				(postSpriteList.get(1).getScript(0).getBrickList().get(0)), "yPosition");

		double actualSize = (Double) Reflection.getPrivateField(
				(postSpriteList.get(0).getScript(0).getBrickList().get(2)), "size");

		assertEquals("Size was not deserialized right", size, actualSize);
		assertEquals("XPosition was not deserialized right", xPosition, actualXPosition);
		assertEquals("YPosition was not deserialized right", yPosition, actualYPosition);

		assertFalse("paused should not be set in script", preSpriteList.get(0).getScript(0).isPaused());

		UtilFile.deleteDirectory(projectDirectory);
	}

	public void testReferenceSerializing() {
		Sprite testSprite = new Sprite("test");
		Sprite pointedSprite = new Sprite("pointed");

		CostumeData referenceCostume = new CostumeData();
		referenceCostume.setCostumeFilename("testfileName");
		referenceCostume.setCostumeName("testName");

		SoundInfo referencedSound = new SoundInfo();
		referencedSound.setSoundFileName("soundFile");
		referencedSound.setTitle("SongTitle");

		RepeatBrick repeatBrick = new RepeatBrick(testSprite, 4);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		SetCostumeBrick costumeBrick = new SetCostumeBrick(testSprite);
		costumeBrick.setCostume(referenceCostume);

		PlaySoundBrick soundBrick = new PlaySoundBrick(testSprite);
		soundBrick.setSoundInfo(referencedSound);

		PointToBrick pointBrick = new PointToBrick(testSprite, pointedSprite);

		Script testScript = new StartScript(testSprite);
		Script otherScript = new StartScript(pointedSprite);
		Script testScriptReferenced = new StartScript(testSprite);

		WhenStartedBrick whenStartedBrick = new WhenStartedBrick(testSprite, testScript);

		PointInDirectionBrick pointIndirection = new PointInDirectionBrick(testSprite, Direction.DIRECTION_LEFT);

		HideBrick hideBrick = new HideBrick(pointedSprite);
		ShowBrick showBrick = new ShowBrick(pointedSprite);
		testScript.addBrick(repeatBrick);
		testScript.addBrick(costumeBrick);
		testScript.addBrick(soundBrick);
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(pointBrick);
		testScript.addBrick(pointIndirection);
		testScriptReferenced.addBrick(whenStartedBrick);
		testSprite.addScript(testScript);
		testSprite.addScript(testScriptReferenced);
		try {
			Field costumeField = Sprite.class.getDeclaredField(CatroidXMLConstants.COSTUME_LIST_FIELD_NAME);
			Field soundField = Sprite.class.getDeclaredField(CatroidXMLConstants.SOUND_LIST_FIELD_NAME);
			List<CostumeData> costumeList = new ArrayList<CostumeData>();
			List<SoundInfo> soundList = new ArrayList<SoundInfo>();
			costumeList.add(referenceCostume);
			costumeField.setAccessible(true);
			costumeField.set(testSprite, costumeList);
			soundList.add(referencedSound);
			soundField.setAccessible(true);
			soundField.set(testSprite, soundList);
		} catch (SecurityException e1) {
			fail("unexpected SerilizeException");
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			fail("unexpected SerilizeException");
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		}
		otherScript.addBrick(hideBrick);
		otherScript.addBrick(showBrick);
		pointedSprite.addScript(otherScript);

		Project testProject = new Project();
		testProject.setName("testReferenceSerializerProject");
		testProject.addSprite(testSprite);
		testProject.addSprite(pointedSprite);

		String projectDirectoryName = Utils.buildProjectPath("test__" + testProject.getName());
		File projectDirectory = new File(projectDirectoryName);

		if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
			projectDirectory.mkdir();

		}
		try {
			XmlSerializer.toXml(testProject, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (SerializeException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		}

		Project loadedProject = null;
		if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {

			try {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Constants.PROJECTCODE_NAME));
				loadedProject = FullParser.parseSpritesWithProject(projectFileStream);
			} catch (ParseException e) {
				fail("unexpected SerilizeException");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				fail("unexpected SerilizeException");
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				fail("Exception when parsing the headers");
			}
		}
		assertNotNull("loaded project is null", loadedProject);
		Sprite loadedFirstSprite = loadedProject.getSpriteList().get(0);
		RepeatBrick loadedRepeatBrick = (RepeatBrick) loadedFirstSprite.getScript(0).getBrick(0);
		LoopEndBrick referenceLoopEndBrick = loadedRepeatBrick.getLoopEndBrick();
		LoopEndBrick loadedLoopEndBrick = (LoopEndBrick) loadedFirstSprite.getScript(0).getBrick(3);
		assertEquals("LoopEndBrick not referenced right", loadedLoopEndBrick, referenceLoopEndBrick);

		CostumeData loadedCostume = loadedFirstSprite.getCostumeDataList().get(0);
		assertNotNull("Costume not in sprite costumeList", loadedCostume);
		SetCostumeBrick loadedCostumeBrick = (SetCostumeBrick) loadedFirstSprite.getScript(0).getBrick(1);
		CostumeData brickReferencedCostumeData = (CostumeData) Reflection.getPrivateField(loadedCostumeBrick,
				CatroidXMLConstants.COSTUME_DATA_FIELD_NAME);
		assertEquals("Costume data referencing wrong", loadedCostume, brickReferencedCostumeData);

		SoundInfo loadedSound = loadedFirstSprite.getSoundList().get(0);
		PlaySoundBrick loadedPlaySoundBrick = (PlaySoundBrick) loadedFirstSprite.getScript(0).getBrick(2);
		SoundInfo brickReferenceSoundInfo = (SoundInfo) Reflection.getPrivateField(loadedPlaySoundBrick,
				CatroidXMLConstants.SOUND_INFO_FIELD_NAME);
		assertEquals("Sound Info referencing wrong", loadedSound, brickReferenceSoundInfo);
		assertTrue("PlaySoundBrick sprite soundInfo doesnt have referenced SoundInfo", loadedPlaySoundBrick.getSprite()
				.getSoundList().contains(brickReferenceSoundInfo));
		assertEquals("Sprites are different", loadedPlaySoundBrick.getSprite(), loadedFirstSprite);
		PointToBrick loadedPointBrick = (PointToBrick) loadedFirstSprite.getScript(0).getBrick(4);
		Sprite referencedSprite = (Sprite) Reflection.getPrivateField(loadedPointBrick, "pointedSprite");
		assertEquals("SpriteReferencing wrong", loadedProject.getSpriteList().get(1), referencedSprite);

		WhenStartedBrick loadedScriptBrick = (WhenStartedBrick) loadedFirstSprite.getScript(1).getBrick(0);
		StartScript referencedScript = (StartScript) Reflection.getPrivateField(loadedScriptBrick, "script");
		assertEquals("Script referencing of bricks wrong", loadedFirstSprite.getScript(0), referencedScript);
		UtilFile.deleteDirectory(projectDirectory);
	}

	public void testSerializePerformanceTest() {
		Project bigProject = null;
		try {
			bigProject = XmlTestUtils.loadProjectFromAssets("standardProject.xml", getInstrumentation().getContext());
		} catch (ParseException e) {
			fail("Unexpected ParseException");
			e.printStackTrace();
		}

		String bigProjectDirectoryName = Utils.buildProjectPath("test_1_" + bigProject.getName());
		File bigProjectDirectory = new File(bigProjectDirectoryName);

		if (!(bigProjectDirectory.exists() && bigProjectDirectory.isDirectory() && bigProjectDirectory.canWrite())) {
			bigProjectDirectory.mkdir();

		}
		try {
			long starTime = System.currentTimeMillis();
			XmlSerializer.toXml(bigProject, Utils.buildPath(bigProjectDirectoryName, Constants.PROJECTCODE_NAME));
			long endTime = System.currentTimeMillis();
			long duration = endTime - starTime;
			Log.i("SerializerTest", "Big project duration is " + duration + " ms");
		} catch (SerializeException e) {
			fail("Unexpected exception");
			e.printStackTrace();
		}
		Project loadedBigProject = null;
		try {
			InputStream bigProjectFileStream = new FileInputStream(Utils.buildPath(
					bigProjectDirectory.getAbsolutePath(), Constants.PROJECTCODE_NAME));
			loadedBigProject = FullParser.parseSpritesWithProject(bigProjectFileStream);
		} catch (ParseException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
		}
		assertNotNull("big project null", loadedBigProject);
		//assertEquals("number of sprites wrong", 11, loadedBigProject.getSpriteList().size());
		UtilFile.deleteDirectory(bigProjectDirectory);
	}

	private static class ProjectWithCatrobatLanguageVersion extends Project {
		static final long serialVersionUID = 1L;
		private final float catrobatLanguageVersion;

		@SuppressWarnings("unused")
		public ProjectWithCatrobatLanguageVersion() {
			catrobatLanguageVersion = 0.1f;
		}

		public ProjectWithCatrobatLanguageVersion(String name, float catrobatLanguageVersion) {
			super(null, name);
			this.catrobatLanguageVersion = catrobatLanguageVersion;
		}

		@Override
		public float getCatrobatLanguageVersion() {
			return catrobatLanguageVersion;
		}
	}

	public void testSerializingChildClassProject() {
		Project project = new ProjectWithCatrobatLanguageVersion("versionProject", 0.1f);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		String projectDirectoryName = Utils.buildProjectPath("test_" + project.getName());
		File projectDirectory = new File(projectDirectoryName);

		if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
			projectDirectory.mkdir();

		}
		try {
			XmlSerializer.toXml(project, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (SerializeException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		}
		Project testProject = null;
		try {
			InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
					Constants.PROJECTCODE_NAME));
			testProject = FullParser.parseSpritesWithProject(projectFileStream);
		} catch (FileNotFoundException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (ParseException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
		}

		assertNotNull("testproject is null", testProject);
		UtilFile.deleteDirectory(projectDirectory);
	}

	public void testSavingWithNothingSelected() {
		Sprite testSprite = new Sprite("test");

		CostumeData referenceCostume = new CostumeData();
		referenceCostume.setCostumeFilename("testfileName");
		referenceCostume.setCostumeName("testName");

		SoundInfo referencedSound = new SoundInfo();
		referencedSound.setSoundFileName("soundFile");
		referencedSound.setTitle("SongTitle");

		SetCostumeBrick costumeBrick = new SetCostumeBrick(testSprite);
		costumeBrick.setCostume(null);

		PlaySoundBrick soundBrick = new PlaySoundBrick(testSprite);
		soundBrick.setSoundInfo(null);

		Script testScript = new StartScript(testSprite);

		testScript.addBrick(costumeBrick);
		testScript.addBrick(soundBrick);
		testSprite.addScript(testScript);

		try {
			Field costumeField = Sprite.class.getDeclaredField(CatroidXMLConstants.COSTUME_LIST_FIELD_NAME);
			Field soundField = Sprite.class.getDeclaredField(CatroidXMLConstants.SOUND_LIST_FIELD_NAME);
			List<CostumeData> costumeList = new ArrayList<CostumeData>();
			List<SoundInfo> soundList = new ArrayList<SoundInfo>();
			costumeList.add(referenceCostume);
			costumeField.setAccessible(true);
			costumeField.set(testSprite, costumeList);
			soundList.add(referencedSound);
			soundField.setAccessible(true);
			soundField.set(testSprite, soundList);
		} catch (SecurityException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		}

		Project testProject = new Project();
		testProject.setName("testReferenceSerializerProject");
		testProject.addSprite(testSprite);

		String projectDirectoryName = Utils.buildProjectPath("test__" + testProject.getName());
		File projectDirectory = new File(projectDirectoryName);

		if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
			projectDirectory.mkdir();

		}
		try {
			XmlSerializer.toXml(testProject, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (SerializeException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		}

		Project loadedProject = null;
		if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {

			try {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Constants.PROJECTCODE_NAME));
				loadedProject = FullParser.parseSpritesWithProject(projectFileStream);
			} catch (ParseException e) {
				fail("unexpected SerilizeException");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				fail("unexpected SerilizeException");
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				fail("Exception when parsing the headers");
			}
		}
		assertNotNull("loaded project is null", loadedProject);
		Sprite loadedFirstSprite = loadedProject.getSpriteList().get(0);

		CostumeData loadedCostume = loadedFirstSprite.getCostumeDataList().get(0);
		assertNotNull("Costume not in sprite costumeList", loadedCostume);
		SetCostumeBrick loadedCostumeBrick = (SetCostumeBrick) loadedFirstSprite.getScript(0).getBrick(0);
		CostumeData brickReferencedCostumeData = (CostumeData) Reflection.getPrivateField(loadedCostumeBrick,
				CatroidXMLConstants.COSTUME_DATA_FIELD_NAME);
		assertNull("Costume data referencing wrong", brickReferencedCostumeData);

		PlaySoundBrick loadedPlaySoundBrick = (PlaySoundBrick) loadedFirstSprite.getScript(0).getBrick(1);
		SoundInfo brickReferenceSoundInfo = (SoundInfo) Reflection.getPrivateField(loadedPlaySoundBrick,
				CatroidXMLConstants.SOUND_INFO_FIELD_NAME);
		assertNull("Sound Info referencing wrong", brickReferenceSoundInfo);

		UtilFile.deleteDirectory(projectDirectory);
		TestUtils.deleteTestProjects("test__testReferenceSerializerProject", "test__testSerializeProject");
	}
}
