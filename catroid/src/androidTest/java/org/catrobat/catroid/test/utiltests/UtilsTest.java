/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.test.AndroidTestCase;
import android.util.Log;

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
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UtilsTest extends AndroidTestCase {
	private static final String TAG = UtilsTest.class.getSimpleName();

	private final String testFileContent = "Hello, this is a Test-String";
	private static final String MD5_EMPTY = "D41D8CD98F00B204E9800998ECF8427E";
	private static final String MD5_CATROID = "4F982D927F4784F69AD6D6AF38FD96AD";
	private static final String MD5_HELLO_WORLD = "ED076287532E86365E841E92BFC50D8C";
	private static final String NEW_PROGRAM_NAME = "new name";
	private File testFile;
	private File copiedFile;

	private Project defaultProject;

	@Override
	protected void setUp() throws Exception {
		OutputStream outputStream = null;
		TestUtils.deleteTestProjects(NEW_PROGRAM_NAME);
		try {
			testFile = File.createTempFile("testCopyFiles", ".txt");
			if (testFile.canWrite()) {
				outputStream = new FileOutputStream(testFile);
				outputStream.write(testFileContent.getBytes());
				outputStream.flush();
			}
		} catch (IOException e) {
			Log.e(TAG, "File handling error", e);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (testFile != null && testFile.exists()) {
			testFile.delete();
		}
		if (copiedFile != null && copiedFile.exists()) {
			copiedFile.delete();
		}

		TestUtils.deleteTestProjects(NEW_PROGRAM_NAME);
		super.tearDown();
	}

	public void testMD5CheckSumOfFile() {

		PrintWriter printWriter = null;

		File tempDir = new File(Constants.TMP_PATH);
		tempDir.mkdirs();

		File md5TestFile = new File(Utils.buildPath(Constants.TMP_PATH, "catroid.txt"));

		if (md5TestFile.exists()) {
			md5TestFile.delete();
		}

		try {
			md5TestFile.createNewFile();
			assertEquals("MD5 sums are not the same for empty file", MD5_EMPTY.toLowerCase(Locale.US),
					Utils.md5Checksum(md5TestFile));

			printWriter = new PrintWriter(md5TestFile);
			printWriter.print("catroid");
		} catch (IOException e) {
			Log.e(TAG, "File handling error", e);
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}

		assertEquals("MD5 sums are not the same for catroid file", MD5_CATROID.toLowerCase(Locale.US),
				Utils.md5Checksum(md5TestFile));

		UtilFile.deleteDirectory(tempDir);
	}

	public void testMD5CheckSumOfString() {
		assertEquals("MD5 sums do not match!", MD5_CATROID.toLowerCase(Locale.US), Utils.md5Checksum("catroid"));
		assertEquals("MD5 sums do not match!", MD5_EMPTY.toLowerCase(Locale.US), Utils.md5Checksum(""));
		assertEquals("MD5 sums do not match!", MD5_HELLO_WORLD.toLowerCase(Locale.US),
				Utils.md5Checksum("Hello World!"));
	}

	public void testBuildPath() {
		String first = "/abc/abc";
		String second = "/def/def/";
		String result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);

		first = "/abc/abc";
		second = "def/def/";
		result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);

		first = "/abc/abc/";
		second = "/def/def/";
		result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);

		first = "/abc/abc/";
		second = "def/def/";
		result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);
	}

	public void testDeleteSpecialCharactersFromString() {
		String testString = "This:IsA-\" */ :<Very>?|Very\\\\Long_Test_String";
		String newString = Utils.deleteSpecialCharactersInString(testString);
		assertEquals("Strings are not equal!", "ThisIsA-  VeryVeryLong_Test_String", newString);
	}

	public void testBuildProjectPath() {
		if (!Utils.externalStorageAvailable()) {
			fail("No SD card present");
		}
		String projectName = "test?Projekt\"1";
		String expectedPath = Constants.DEFAULT_ROOT + "/test%3FProjekt%221";
		assertEquals("Paths are different!", expectedPath, Utils.buildProjectPath(projectName));
	}

	public void testProjectSameAsStandardProject() {
		ScreenValues.SCREEN_WIDTH = 480;
		ScreenValues.SCREEN_HEIGHT = 800;

		try {
			defaultProject = DefaultProjectHandler.createAndSaveDefaultProject(NEW_PROGRAM_NAME, getContext());
		} catch (IOException | IllegalArgumentException e) {
			Log.e(TAG, "error creating standard project", e);
			fail("error creating standard project");
		}
		assertTrue("Failed to recognize the standard project", Utils.isStandardProject(defaultProject, getContext()));

		addSpriteAndCompareToStandardProject();
		addScriptAndCompareToStandardProject();
		addBrickAndCompareToStandardProject();
		changeParametersOfBricksAndCompareToStandardProject();
		removeBrickAndCompareToStandardProject();
		removeScriptAndCompareToStandardProject();
		removeSpriteAndCompareToStandardProject();

		SystemClock.sleep(1000);
	}

	public void testLoadProjectIfNeeded() {
		Utils.saveToPreferences(getContext(), Constants.PREF_PROJECTNAME_KEY, "projectNameWhichDoesNotExist");
		try {
			Utils.loadProjectIfNeeded(getContext());
		} catch (Exception e) {
			fail("Tried to load project which should not be loadable.");
		}
		TestUtils.removeFromPreferences(getContext(), Constants.PREF_PROJECTNAME_KEY);
	}

	public void testExtractRemixUrlsOfProgramHeaderUrlFieldContainingSingleAbsoluteUrl() {
		final String expectedFirstProgramRemixUrl = "https://share.catrob.at/pocketcode/program/16267";
		final String remixUrlsString = expectedFirstProgramRemixUrl;

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals("Invalid number of remix URLs extracted", 1, result.size());
		assertEquals("Failed to extract remix URL", expectedFirstProgramRemixUrl, result.get(0));
	}

	public void testExtractRemixUrlsOfProgramHeaderUrlFieldContainingSingleRelativeUrl() {
		final String expectedFirstProgramRemixUrl = "/pocketcode/program/3570";
		final String remixUrlsString = expectedFirstProgramRemixUrl;

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals("Invalid number of remix URLs extracted", 1, result.size());
		assertEquals("Failed to extract remix URL", expectedFirstProgramRemixUrl, result.get(0));
	}

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
		assertEquals("Invalid number of remix URLs extracted", 2, result.size());
		assertEquals("Failed to extract URL of first program", expectedFirstProgramRemixUrl, result.get(0));
		assertEquals("Failed to extract URL of second program", expectedSecondProgramRemixUrl, result.get(1));
	}

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
		assertEquals("Invalid number of remix URLs extracted", 2, result.size());
		assertEquals("Failed to extract URL of first program", expectedFirstProgramRemixUrl, result.get(0));
		assertEquals("Failed to extract URL of second program", expectedSecondProgramRemixUrl, result.get(1));
	}

	public void testExtractRemixUrlsOfMergedProgramHeaderUrlFieldContainingNoUrls() {
		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProgramName("Program A");

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProgramName("Program B");

		final String remixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		List<String> result = Utils.extractRemixUrlsFromString(remixUrlsString);
		assertEquals("Invalid number of remix URLs extracted", 0, result.size());
	}

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
		assertEquals("Invalid number of remix URLs extracted", 2, result.size());
		assertEquals("Failed to extract URL of first program", expectedFirstProgramRemixUrl, result.get(0));
		assertEquals("Failed to extract URL of second program", expectedSecondProgramRemixUrl, result.get(1));
	}

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
		assertEquals("Invalid number of remix URLs extracted", 4, result.size());
		assertEquals("Failed to extract URL of first program", expectedFirstProgramRemixUrl, result.get(0));
		assertEquals("Failed to extract URL of second program", expectedSecondProgramRemixUrl, result.get(1));
		assertEquals("Failed to extract URL of third program", expectedThirdProgramRemixUrl, result.get(2));
		assertEquals("Failed to extract URL of fourth program", expectedFourthProgramRemixUrl, result.get(3));
	}

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
		assertEquals("Invalid number of remix URLs extracted", 2, result.size());
		assertEquals("Failed to extract remix URL of second program", expectedSecondProgramRemixUrl, result.get(0));
		assertEquals("Failed to extract remix URL of fourth program", expectedFourthProgramRemixUrl, result.get(1));
	}

	private void addSpriteAndCompareToStandardProject() {
		Sprite sprite = new SingleSprite("TestSprite");
		defaultProject.getDefaultScene().addSprite(sprite);
		assertFalse("Failed to recognize that the project is not standard after adding a new SingleSprite",
				Utils.isStandardProject(defaultProject, getContext()));
		defaultProject.getDefaultScene().removeSprite(sprite);
		assertTrue("Failed to recognize the standard project", Utils.isStandardProject(defaultProject, getContext()));
	}

	private void addScriptAndCompareToStandardProject() {
		Sprite catroidSprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		WhenScript whenScript = new WhenScript();
		catroidSprite.addScript(whenScript);
		assertFalse("Failed to recognize that the project is not standard after adding a new script",
				Utils.isStandardProject(defaultProject, getContext()));
		catroidSprite.removeScript(whenScript);
		assertTrue("Failed to recognize the standard project", Utils.isStandardProject(defaultProject, getContext()));
	}

	private void addBrickAndCompareToStandardProject() {
		Sprite catroidSprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		Brick brick = new HideBrick();
		Script catroidScript = catroidSprite.getScript(0);
		catroidScript.addBrick(brick);
		assertFalse("Failed to recognize that the project is not standard after adding a new brick",
				Utils.isStandardProject(defaultProject, getContext()));
		catroidScript.removeBrick(brick);
		assertTrue("Failed to recognize the standard project", Utils.isStandardProject(defaultProject, getContext()));
	}

	private void changeParametersOfBricksAndCompareToStandardProject() {
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
			assertFalse("Failed to recognize that the project is not standard after changing the set look brick",
					Utils.isStandardProject(defaultProject, getContext()));

			setLookBrick.setLook(oldLookData);
			assertTrue("Failed to recognize the standard project",
					Utils.isStandardProject(defaultProject, getContext()));
		}

		if (waitBrick != null) {
			Formula oldTime = waitBrick.getTimeToWait();
			Formula newTimeToWait = new Formula(2345);

			waitBrick.setTimeToWait(newTimeToWait);
			assertFalse("Failed to recognize that the project is not standard after changing the wait brick",
					Utils.isStandardProject(defaultProject, getContext()));

			waitBrick.setTimeToWait(oldTime);
			assertTrue("Failed to recognize the standard project",
					Utils.isStandardProject(defaultProject, getContext()));
		}
	}

	private void removeBrickAndCompareToStandardProject() {
		Script catroidScript = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		ArrayList<Brick> brickList = catroidScript.getBrickList();
		Brick brick = brickList.get(brickList.size() - 1);
		brickList.remove(brickList.size() - 1);
		assertFalse("Failed to recognize that the project is not standard after removing a brick",
				Utils.isStandardProject(defaultProject, getContext()));

		brickList.add(brick);
		assertTrue("Failed to recognize the standard project", Utils.isStandardProject(defaultProject, getContext()));
	}

	private void removeScriptAndCompareToStandardProject() {
		Script catroidScript = defaultProject.getDefaultScene().getSpriteList().get(1).getScript(0);
		Sprite sprite = defaultProject.getDefaultScene().getSpriteList().get(1);
		sprite.removeScript(catroidScript);
		assertFalse("Failed to recognize that the project is not standard after removing a script",
				Utils.isStandardProject(defaultProject, getContext()));

		sprite.addScript(catroidScript);
		assertTrue("Failed to recognize the standard project", Utils.isStandardProject(defaultProject, getContext()));
	}

	private void removeSpriteAndCompareToStandardProject() {
		Sprite catroidSprite = defaultProject.getDefaultScene().getSpriteList().get(3);
		int lastIndex = defaultProject.getDefaultScene().getSpriteList().size() - 1;
		List<Sprite> spriteList = defaultProject.getDefaultScene().getSpriteList();
		spriteList.remove(lastIndex);
		assertFalse("Failed to recognize that the project is not standard after removing a sprite",
				Utils.isStandardProject(defaultProject, getContext()));

		spriteList.add(catroidSprite);
		assertTrue("Failed to recognize the standard project", Utils.isStandardProject(defaultProject, getContext()));
	}

	public void testSetBitAllOnesSetIndex0To1() {
		assertEquals("Setting an already set bit should not change numberToModify",
				0b11111111, Utils.setBit(0b11111111, 0, 1));
	}

	public void testSetBitAllButOneZerosSetIndex3To1() {
		assertEquals("Setting an already set bit should not change numberToModify",
				0b00001000, Utils.setBit(0b00001000, 3, 1));
	}

	public void testSetBitAllZerosSetIndex7To0() {
		assertEquals("Clearing an already cleared bit should not change numberToModify",
				0b00000000, Utils.setBit(0b00000000, 7, 0));
	}

	public void testSetBitAllButOneOnesSetIndex4To0() {
		assertEquals("Clearing an already cleared bit should not change numberToModify",
				0b11011111, Utils.setBit(0b11011111, 5, 0));
	}

	public void testSetBitAllZerosSetIndex0To1() {
		assertEquals("Didn't set bit as expected",
				0b00000001, Utils.setBit(0b00000000, 0, 1));
	}

	public void testSetBitAllOnesSetIndex0To0() {
		assertEquals("Didn't clear bit as expected",
				0b11111110, Utils.setBit(0b11111111, 0, 0));
	}

	public void testSetBitAllZerosSetIndex7To1() {
		assertEquals("Didn't set bit as expected",
				0b10000000, Utils.setBit(0b00000000, 7, 1));
	}

	public void testSetBitAllOnesSetIndex7To0() {
		assertEquals("Didn't clear bit as expected",
				0b01111111, Utils.setBit(0b11111111, 7, 0));
	}

	public void testSetBitNegativeIndex() {
		assertEquals("Negative index should not modify numberToModify",
				0, Utils.setBit(0, -3, 1));
	}

	public void testSetBitMaxIndex() {
		assertEquals("Didn't set bit as expected",
				0x80000000, Utils.setBit(0x00000000, 31, 1));
	}

	public void testSetBitTooLargeIndex() {
		assertEquals("Too large index (>=32) should not modify numberToModify",
				0, Utils.setBit(0, 32, 1));
	}

	public void testSetBitNonbinaryValue() {
		assertEquals("Any value other than 0 should set the bit specified by index",
				0b00000001, Utils.setBit(0b00000000, 0, 4));
	}

	public void testGetBitGet0FromIndex0() {
		assertEquals("Didn't get expected bit value",
				0, Utils.getBit(0b11111110, 0));
	}

	public void testGetBitGet1FromIndex0() {
		assertEquals("Didn't get expected bit value",
				1, Utils.getBit(0b00000001, 0));
	}

	public void testGetBitGet0FromIndex7() {
		assertEquals("Didn't get expected bit value",
				0, Utils.getBit(0b01111111, 7));
	}

	public void testGetBitGet1FromIndex7() {
		assertEquals("Didn't get expected bit value",
				1, Utils.getBit(0b10000000, 7));
	}

	public void testGetBitGet0FromMaxIndex() {
		assertEquals("Didn't get expected bit value",
				0, Utils.getBit(0x7FFFFFFF, 31));
	}

	public void testGetBitGet1FromMaxIndex() {
		assertEquals("Didn't get expected bit value",
				1, Utils.getBit(0x80000000, 31));
	}

	public void testGetBitNegativeIndex() {
		assertEquals("Negative index should return 0",
				0, Utils.getBit(0xFFFFFFFF, -3));
	}

	public void testGetBitTooLargeIndex() {
		assertEquals("Too large index (>=32) should return 0",
				0, Utils.getBit(0xFFFFFFFF, 32));
	}
}
