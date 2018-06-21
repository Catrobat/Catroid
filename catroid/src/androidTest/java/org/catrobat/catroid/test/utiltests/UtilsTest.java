/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.test.utiltests;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.stage.ShowBubbleActor;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {
	private final String testFileContent = "Hello, this is a Test-String";
	private static final String MD5_EMPTY = "D41D8CD98F00B204E9800998ECF8427E";
	private static final String MD5_CATROID = "4F982D927F4784F69AD6D6AF38FD96AD";
	private static final String MD5_HELLO_WORLD = "ED076287532E86365E841E92BFC50D8C";
	private static final String NEW_PROGRAM_NAME = "new name";
	private File testFile;

	private Project defaultProject;

	@Before
	public void setUp() throws Exception {
		testFile = File.createTempFile("testCopyFiles", ".txt");
		OutputStream outputStream = new FileOutputStream(testFile);
		outputStream.write(testFileContent.getBytes());
		outputStream.flush();
		outputStream.close();
	}

	@After
	public void tearDown() throws Exception {
		if (testFile != null && testFile.exists()) {
			testFile.delete();
		}

		TestUtils.deleteProjects(NEW_PROGRAM_NAME);
	}

	@Test
	public void testMD5CheckSumOfFile() throws IOException {

		PrintWriter printWriter = null;

		File tempDir = new File(Constants.TMP_PATH);
		tempDir.mkdirs();

		File md5TestFile = new File(PathBuilder.buildPath(Constants.TMP_PATH, "catroid.txt"));

		if (md5TestFile.exists()) {
			md5TestFile.delete();
		}

		md5TestFile.createNewFile();
		assertEquals(MD5_EMPTY.toLowerCase(Locale.US), Utils.md5Checksum(md5TestFile));

		printWriter = new PrintWriter(md5TestFile);
		printWriter.print("catroid");
		printWriter.close();

		assertEquals(MD5_CATROID.toLowerCase(Locale.US), Utils.md5Checksum(md5TestFile));

		StorageOperations.deleteDir(tempDir);
	}

	@Test
	public void testMD5CheckSumOfString() {
		assertEquals(MD5_CATROID.toLowerCase(Locale.US), Utils.md5Checksum("catroid"));
		assertEquals(MD5_EMPTY.toLowerCase(Locale.US), Utils.md5Checksum(""));
		assertEquals(MD5_HELLO_WORLD.toLowerCase(Locale.US), Utils.md5Checksum("Hello World!"));
	}

	@Test
	public void testBuildPath() {
		String first = "/abc/abc";
		String second = "/def/def/";
		String result = "/abc/abc/def/def";
		assertEquals(PathBuilder.buildPath(first, second), result);

		first = "/abc/abc";
		second = "def/def/";
		result = "/abc/abc/def/def";
		assertEquals(PathBuilder.buildPath(first, second), result);

		first = "/abc/abc/";
		second = "/def/def/";
		result = "/abc/abc/def/def";
		assertEquals(PathBuilder.buildPath(first, second), result);

		first = "/abc/abc/";
		second = "def/def/";
		result = "/abc/abc/def/def";
		assertEquals(PathBuilder.buildPath(first, second), result);
	}

	@Test
	public void testBuildProjectPath() {
		assertTrue(Utils.isExternalStorageAvailable());
		String projectName = "test?Projekt\"1";
		String expectedPath = Constants.DEFAULT_ROOT_DIRECTORY.getAbsolutePath() + "/test%3FProjekt%221";
		assertEquals(expectedPath, PathBuilder.buildProjectPath(projectName));
	}

	@Test
	public void testCompareProjectToDefaultProject() throws IOException, IllegalArgumentException {
		ScreenValues.SCREEN_WIDTH = 480;
		ScreenValues.SCREEN_HEIGHT = 800;

		defaultProject = DefaultProjectHandler.createAndSaveDefaultProject(NEW_PROGRAM_NAME,
				InstrumentationRegistry.getTargetContext());

		assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));

		addSpriteAndCompareToDefaultProject();
		addScriptAndCompareToDefalutProject();
		addBrickAndCompareToDefaultProject();
		changeParametersOfBricksAndCompareToDefaultProject();
		removeBrickAndCompareToDefaultProject();
		removeScriptAndCompareToDefaultProject();
		removeSpriteAndCompareToDefaultProject();

		SystemClock.sleep(1000);
	}

	@Test
	public void testExtractRemixUrlsOfProgramHeaderUrlFieldContainingSingleAbsoluteUrl() {
		final String expectedFirstProgramRemixUrl = "https://share.catrob.at/pocketcode/program/16267";
		final String remixUrlsString = expectedFirstProgramRemixUrl;

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals(1, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
	}

	@Test
	public void testExtractRemixUrlsOfProgramHeaderUrlFieldContainingSingleRelativeUrl() {
		final String expectedFirstProgramRemixUrl = "/pocketcode/program/3570";
		final String remixUrlsString = expectedFirstProgramRemixUrl;

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals(1, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
	}

	@Test
	public void testExtractRemixUrlsOfMergedProgramHeaderUrlFieldContainingTwoAbsoluteUrls() {
		final String expectedFirstProgramRemixUrl = "https://share.catrob.at/pocketcode/program/16267";
		final String expectedSecondProgramRemixUrl = "https://scratch.mit.edu/projects/110380057/";

		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProgramName("Catrobat program");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProgramName("Scratch program");
		headerOfSecondProgram.setRemixParentsUrlString(expectedSecondProgramRemixUrl);

		final String remixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);
		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals(2, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
		assertEquals(expectedSecondProgramRemixUrl, result.get(1));
	}

	@Test
	public void testExtractRemixUrlsOfMergedProgramHeaderUrlFieldContainingTwoRelativeUrls() {
		final String expectedFirstProgramRemixUrl = "/pocketcode/program/16267";
		final String expectedSecondProgramRemixUrl = "/pocketcode/program/3570";

		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProgramName("Program A");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProgramName("Program B");
		headerOfSecondProgram.setRemixParentsUrlString(expectedSecondProgramRemixUrl);

		final String remixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals(2, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
		assertEquals(expectedSecondProgramRemixUrl, result.get(1));
	}

	@Test
	public void testExtractRemixUrlsOfMergedProgramHeaderUrlFieldContainingNoUrls() {
		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProgramName("Program A");

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProgramName("Program B");

		final String remixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals(0, result.size());
	}

	@Test
	public void testExtractRemixUrlsOfMergedProgramHeaderUrlFieldContainingMultipleMixedUrls() {
		final String expectedFirstProgramRemixUrl = "https://scratch.mit.edu/projects/117697631/";
		final String expectedSecondProgramRemixUrl = "/pocketcode/program/3570";

		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProgramName("My Scratch program");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProgramName("The Periodic Table");
		headerOfSecondProgram.setRemixParentsUrlString(expectedSecondProgramRemixUrl);

		final String remixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals(2, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
		assertEquals(expectedSecondProgramRemixUrl, result.get(1));
	}

	@Test
	public void testExtractRemixUrlsOfRemergedProgramHeaderUrlFieldContainingMixedUrls() {
		final String expectedFirstProgramRemixUrl = "https://scratch.mit.edu/projects/117697631/";
		final String expectedSecondProgramRemixUrl = "/pocketcode/program/3570";
		final String expectedThirdProgramRemixUrl = "https://scratch.mit.edu/projects/121648946/";
		final String expectedFourthProgramRemixUrl = "https://share.catrob.at/pocketcode/program/16267";

		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProgramName("My first Scratch program");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProgramName("The Periodic Table");
		headerOfSecondProgram.setRemixParentsUrlString(expectedSecondProgramRemixUrl);

		final String firstMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		final XmlHeader headerOfFirstMergedProgram = new XmlHeader();
		headerOfFirstMergedProgram.setProgramName("First merged Catrobat program");
		headerOfFirstMergedProgram.setRemixParentsUrlString(firstMergedRemixUrlsString);

		final XmlHeader headerOfThirdProgram = new XmlHeader();
		headerOfThirdProgram.setProgramName("My second Scratch program");
		headerOfThirdProgram.setRemixParentsUrlString(expectedThirdProgramRemixUrl);

		final String secondMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstMergedProgram,
				headerOfThirdProgram);

		final XmlHeader headerOfSecondMergedProgram = new XmlHeader();
		headerOfSecondMergedProgram.setProgramName("Second merged Catrobat program");
		headerOfSecondMergedProgram.setRemixParentsUrlString(secondMergedRemixUrlsString);

		final XmlHeader headerOfFourthProgram = new XmlHeader();
		headerOfFourthProgram.setProgramName("My third Catrobat program");
		headerOfFourthProgram.setRemixParentsUrlString(expectedFourthProgramRemixUrl);

		final String finalMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfSecondMergedProgram,
				headerOfFourthProgram);

		List<String> result = Utils.extractRemixUrlsFromString(finalMergedRemixUrlsString);
		assertEquals(4, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
		assertEquals(expectedSecondProgramRemixUrl, result.get(1));
		assertEquals(expectedThirdProgramRemixUrl, result.get(2));
		assertEquals(expectedFourthProgramRemixUrl, result.get(3));
	}

	@Test
	public void testExtractRemixUrlsOfRemergedProgramHeaderUrlFieldContainingMissingUrls() {
		final String expectedSecondProgramRemixUrl = "/pocketcode/program/3570";
		final String expectedFourthProgramRemixUrl = "https://share.catrob.at/pocketcode/program/16267";

		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProgramName("Program A");

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProgramName("Program B");
		headerOfSecondProgram.setRemixParentsUrlString(expectedSecondProgramRemixUrl);

		final String firstMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		final XmlHeader headerOfFirstMergedProgram = new XmlHeader();
		headerOfFirstMergedProgram.setProgramName("First merged program");
		headerOfFirstMergedProgram.setRemixParentsUrlString(firstMergedRemixUrlsString);

		final XmlHeader headerOfThirdProgram = new XmlHeader();
		headerOfThirdProgram.setProgramName("Program C");

		final String secondMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstMergedProgram,
				headerOfThirdProgram);

		final XmlHeader headerOfSecondMergedProgram = new XmlHeader();
		headerOfSecondMergedProgram.setProgramName("Second merged program");
		headerOfSecondMergedProgram.setRemixParentsUrlString(secondMergedRemixUrlsString);

		final XmlHeader headerOfFourthProgram = new XmlHeader();
		headerOfFourthProgram.setProgramName("Program D");
		headerOfFourthProgram.setRemixParentsUrlString(expectedFourthProgramRemixUrl);

		final String finalMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfSecondMergedProgram,
				headerOfFourthProgram);

		List<String> result = Utils.extractRemixUrlsFromString(finalMergedRemixUrlsString);
		assertEquals(2, result.size());
		assertEquals(expectedSecondProgramRemixUrl, result.get(0));
		assertEquals(expectedFourthProgramRemixUrl, result.get(1));
	}

	private void addSpriteAndCompareToDefaultProject() {
		Sprite sprite = new SingleSprite("TestSprite");
		defaultProject.getDefaultScene().addSprite(sprite);
		assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
		defaultProject.getDefaultScene().removeSprite(sprite);
		assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
	}

	private void addScriptAndCompareToDefalutProject() {
		Sprite catroidSprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		WhenScript whenScript = new WhenScript();
		catroidSprite.addScript(whenScript);
		assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
		catroidSprite.removeScript(whenScript);
		assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
	}

	private void addBrickAndCompareToDefaultProject() {
		Sprite catroidSprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		Brick brick = new HideBrick();
		Script catroidScript = catroidSprite.getScript(0);
		catroidScript.addBrick(brick);
		assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
		catroidScript.removeBrick(brick);
		assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
	}

	private void changeParametersOfBricksAndCompareToDefaultProject() {
		Script catroidScript = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		ArrayList<Brick> brickList = catroidScript.getBrickList();
		SetLookBrick setLookBrick = null;
		WaitBrick waitBrick = null;
		for (int i = 0; i < brickList.size(); i++) {
			if (brickList.get(i) instanceof SetLookBrick) {
				setLookBrick = (SetLookBrick) brickList.get(i);
				break;
			}
			if (brickList.get(i) instanceof WaitBrick) {
				waitBrick = (WaitBrick) brickList.get(i);
				break;
			}
		}

		if (setLookBrick != null) {
			LookData oldLookData = setLookBrick.getLook();
			LookData newLookData = new LookData();
			setLookBrick.setLook(newLookData);
			assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));

			setLookBrick.setLook(oldLookData);
			assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
		}

		if (waitBrick != null) {
			Formula oldTime = waitBrick.getTimeToWait();
			Formula newTimeToWait = new Formula(2345);

			waitBrick.setTimeToWait(newTimeToWait);
			assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));

			waitBrick.setTimeToWait(oldTime);
			assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
		}
	}

	private void removeBrickAndCompareToDefaultProject() {
		Script catroidScript = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		ArrayList<Brick> brickList = catroidScript.getBrickList();
		Brick brick = brickList.get(brickList.size() - 1);
		brickList.remove(brickList.size() - 1);
		assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));

		brickList.add(brick);
		assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
	}

	private void removeScriptAndCompareToDefaultProject() {
		Script catroidScript = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		sprite.removeScript(catroidScript);
		assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));

		sprite.addScript(catroidScript);
		assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
	}

	private void removeSpriteAndCompareToDefaultProject() {
		Sprite catroidSprite = defaultProject.getDefaultScene().getSpriteList().get(3);
		int lastIndex = defaultProject.getDefaultScene().getSpriteList().size() - 1;
		List<Sprite> spriteList = defaultProject.getDefaultScene().getSpriteList();
		spriteList.remove(lastIndex);
		assertFalse(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));

		spriteList.add(catroidSprite);
		assertTrue(Utils.isDefaultProject(defaultProject, InstrumentationRegistry.getTargetContext()));
	}

	@Test
	public void testSetBitAllOnesSetIndex0To1() {
		assertEquals(0b11111111, Utils.setBit(0b11111111, 0, 1));
	}

	@Test
	public void testSetBitAllButOneZerosSetIndex3To1() {
		assertEquals(0b00001000, Utils.setBit(0b00001000, 3, 1));
	}

	@Test
	public void testSetBitAllZerosSetIndex7To0() {
		assertEquals(0b00000000, Utils.setBit(0b00000000, 7, 0));
	}

	@Test
	public void testSetBitAllButOneOnesSetIndex4To0() {
		assertEquals(0b11011111, Utils.setBit(0b11011111, 5, 0));
	}

	@Test
	public void testSetBitAllZerosSetIndex0To1() {
		assertEquals(0b00000001, Utils.setBit(0b00000000, 0, 1));
	}

	@Test
	public void testSetBitAllOnesSetIndex0To0() {
		assertEquals(0b11111110, Utils.setBit(0b11111111, 0, 0));
	}

	@Test
	public void testSetBitAllZerosSetIndex7To1() {
		assertEquals(0b10000000, Utils.setBit(0b00000000, 7, 1));
	}

	@Test
	public void testSetBitAllOnesSetIndex7To0() {
		assertEquals(0b01111111, Utils.setBit(0b11111111, 7, 0));
	}

	@Test
	public void testSetBitNegativeIndex() {
		assertEquals(0, Utils.setBit(0, -3, 1));
	}

	@Test
	public void testSetBitMaxIndex() {
		assertEquals(0x80000000, Utils.setBit(0x00000000, 31, 1));
	}

	@Test
	public void testSetBitTooLargeIndex() {
		assertEquals(0, Utils.setBit(0, 32, 1));
	}

	@Test
	public void testSetBitNonbinaryValue() {
		assertEquals(0b00000001, Utils.setBit(0b00000000, 0, 4));
	}

	@Test
	public void testGetBitGet0FromIndex0() {
		assertEquals(0, Utils.getBit(0b11111110, 0));
	}

	@Test
	public void testGetBitGet1FromIndex0() {
		assertEquals(1, Utils.getBit(0b00000001, 0));
	}

	@Test
	public void testGetBitGet0FromIndex7() {
		assertEquals(0, Utils.getBit(0b01111111, 7));
	}

	@Test
	public void testGetBitGet1FromIndex7() {
		assertEquals(1, Utils.getBit(0b10000000, 7));
	}

	@Test
	public void testGetBitGet0FromMaxIndex() {
		assertEquals(0, Utils.getBit(0x7FFFFFFF, 31));
	}

	@Test
	public void testGetBitGet1FromMaxIndex() {
		assertEquals(1, Utils.getBit(0x80000000, 31));
	}

	@Test
	public void testGetBitNegativeIndex() {
		assertEquals(0, Utils.getBit(0xFFFFFFFF, -3));
	}

	@Test
	public void testGetBitTooLargeIndex() {
		assertEquals(0, Utils.getBit(0xFFFFFFFF, 32));
	}

	@Test
	public void testFormatStringForBubbleBricks() {
		String testFirstCharWhitespace = " ThisIsAReallyLongishWord toTest TheWordWrapperFunc";
		String[] expectedResult = {"ThisIsAReallyLon", "gishWord toTest", "TheWordWrapperFu", "nc"};
		List<String> expectedResultList = Arrays.asList(expectedResult);

		List<String> resultList = ShowBubbleActor.formatStringForBubbleBricks(testFirstCharWhitespace);

		assertNotNull(resultList);
		assertEquals(expectedResultList, resultList);
	}
}
