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
package org.catrobat.catroid.test.utiltests;

import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.stage.ShowBubbleActor;
import org.catrobat.catroid.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class UtilsTest {

	@Test
	public void testExtractRemixUrlsOfProgramHeaderUrlFieldContainingSingleAbsoluteUrl() {
		final String expectedFirstProgramRemixUrl = "https://share.catrob.at/pocketcode/program/16267";

		List<String> result = Utils.extractRemixUrlsFromString(expectedFirstProgramRemixUrl);
		assertEquals(1, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
	}

	@Test
	public void testExtractRemixUrlsOfProgramHeaderUrlFieldContainingSingleRelativeUrl() {
		final String expectedFirstProgramRemixUrl = "/pocketcode/program/3570";

		List<String> result = Utils.extractRemixUrlsFromString(expectedFirstProgramRemixUrl);
		assertEquals(1, result.size());
		assertEquals(expectedFirstProgramRemixUrl, result.get(0));
	}

	@Test
	public void testExtractRemixUrlsOfMergedProgramHeaderUrlFieldContainingTwoAbsoluteUrls() {
		final String expectedFirstProgramRemixUrl = "https://share.catrob.at/pocketcode/program/16267";
		final String expectedSecondProgramRemixUrl = "https://scratch.mit.edu/projects/110380057/";

		final XmlHeader headerOfFirstProgram = new XmlHeader();
		headerOfFirstProgram.setProjectName("Catrobat program");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProjectName("Scratch program");
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
		headerOfFirstProgram.setProjectName("Program A");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProjectName("Program B");
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
		headerOfFirstProgram.setProjectName("Program A");

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProjectName("Program B");

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
		headerOfFirstProgram.setProjectName("My Scratch program");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProjectName("The Periodic Table");
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
		headerOfFirstProgram.setProjectName("My first Scratch program");
		headerOfFirstProgram.setRemixParentsUrlString(expectedFirstProgramRemixUrl);

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProjectName("The Periodic Table");
		headerOfSecondProgram.setRemixParentsUrlString(expectedSecondProgramRemixUrl);

		final String firstMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		final XmlHeader headerOfFirstMergedProgram = new XmlHeader();
		headerOfFirstMergedProgram.setProjectName("First merged Catrobat program");
		headerOfFirstMergedProgram.setRemixParentsUrlString(firstMergedRemixUrlsString);

		final XmlHeader headerOfThirdProgram = new XmlHeader();
		headerOfThirdProgram.setProjectName("My second Scratch program");
		headerOfThirdProgram.setRemixParentsUrlString(expectedThirdProgramRemixUrl);

		final String secondMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstMergedProgram,
				headerOfThirdProgram);

		final XmlHeader headerOfSecondMergedProgram = new XmlHeader();
		headerOfSecondMergedProgram.setProjectName("Second merged Catrobat program");
		headerOfSecondMergedProgram.setRemixParentsUrlString(secondMergedRemixUrlsString);

		final XmlHeader headerOfFourthProgram = new XmlHeader();
		headerOfFourthProgram.setProjectName("My third Catrobat program");
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
		headerOfFirstProgram.setProjectName("Program A");

		final XmlHeader headerOfSecondProgram = new XmlHeader();
		headerOfSecondProgram.setProjectName("Program B");
		headerOfSecondProgram.setRemixParentsUrlString(expectedSecondProgramRemixUrl);

		final String firstMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstProgram,
				headerOfSecondProgram);

		final XmlHeader headerOfFirstMergedProgram = new XmlHeader();
		headerOfFirstMergedProgram.setProjectName("First merged program");
		headerOfFirstMergedProgram.setRemixParentsUrlString(firstMergedRemixUrlsString);

		final XmlHeader headerOfThirdProgram = new XmlHeader();
		headerOfThirdProgram.setProjectName("Program C");

		final String secondMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfFirstMergedProgram,
				headerOfThirdProgram);

		final XmlHeader headerOfSecondMergedProgram = new XmlHeader();
		headerOfSecondMergedProgram.setProjectName("Second merged program");
		headerOfSecondMergedProgram.setRemixParentsUrlString(secondMergedRemixUrlsString);

		final XmlHeader headerOfFourthProgram = new XmlHeader();
		headerOfFourthProgram.setProjectName("Program D");
		headerOfFourthProgram.setRemixParentsUrlString(expectedFourthProgramRemixUrl);

		final String finalMergedRemixUrlsString = Utils.generateRemixUrlsStringForMergedProgram(headerOfSecondMergedProgram,
				headerOfFourthProgram);

		List<String> result = Utils.extractRemixUrlsFromString(finalMergedRemixUrlsString);
		assertEquals(2, result.size());
		assertEquals(expectedSecondProgramRemixUrl, result.get(0));
		assertEquals(expectedFourthProgramRemixUrl, result.get(1));
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

	@Test
	public void testLineBreakStringForBubbleBricks() {
		String testLineBreaks = "This\n   is a Test\n\n to Test   \n the line break\n";
		String[] expectedResult = {"This", "   is a Test", "", " to Test   ", " the line break"};
		List<String> expectedResultList = Arrays.asList(expectedResult);

		List<String> resultList = ShowBubbleActor.formatStringForBubbleBricks(testLineBreaks);

		assertNotNull(resultList);
		assertEquals(expectedResultList, resultList);

		testLineBreaks = "This\n tests\n many\n linebraks\n\n\n\n\n in a\n\n row";
		String[] expectedResult2 = {"This", " tests", " many", " linebraks", "", "", "", "",
				" in a", "", " row"};
		expectedResultList = Arrays.asList(expectedResult2);

		resultList = ShowBubbleActor.formatStringForBubbleBricks(testLineBreaks);

		assertNotNull(resultList);
		assertEquals(expectedResultList, resultList);
	}
}
