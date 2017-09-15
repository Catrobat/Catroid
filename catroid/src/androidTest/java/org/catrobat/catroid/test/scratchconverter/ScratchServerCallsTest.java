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
package org.catrobat.catroid.test.scratchconverter;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebScratchProgramException;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;
import java.util.List;
import java.util.Locale;

/*
 * These tests need an internet connection
 */

public class ScratchServerCallsTest extends InstrumentationTestCase {
	public ScratchServerCallsTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServerCalls.useTestUrl = true;
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.deleteTestProjects("uploadtestProject");
		ServerCalls.useTestUrl = false;
		super.tearDown();
	}

	public void testScratchSearchWithEmptyQueryParam() {
		try {
			ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("", 20, 0);
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", programDataList);
			assertTrue("Empty query should to no results!", programDataList.size() == 0);
			assertNotNull("No search result returned", searchResult);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	private void checkScratchProgramData(ScratchProgramData programData) {
		assertTrue("Invalid program ID", programData.getId() > 0);

		assertNotNull("Invalid program title", programData.getTitle());
		assertTrue("Invalid program title", programData.getTitle().length() > 0);

		assertNotNull("Program has invalid owner", programData.getOwner());
		assertTrue("Program has invalid owner", programData.getOwner().length() > 0);

		assertNotNull("Program has invalid notes & credits description", programData.getNotesAndCredits());
		assertNotNull("Program has invalid instructions-description", programData.getInstructions());

		assertNotNull("Program contains no screenshot-image URL", programData.getImage());
		assertNotNull("Program contains no screenshot-image URL", programData.getImage().getUrl());
		final String urlString = programData.getImage().getUrl().toString();
		assertTrue("Screenshot-image URL '" + urlString + "' does not start with base URL '"
						+ Constants.SCRATCH_IMAGE_BASE_URL + "' any more: " + Constants.SCRATCH_IMAGE_BASE_URL,
				urlString.startsWith(Constants.SCRATCH_IMAGE_BASE_URL));

		final int[] imageSize = new int[] { Constants.SCRATCH_IMAGE_DEFAULT_WIDTH, Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT };
		assertTrue("Invalid width extracated of image URL", programData.getImage().getWidth() == imageSize[0]);
		assertTrue("Invalid height extracted from image URL", programData.getImage().getHeight() == imageSize[1]);
		final String imageURLWithoutQuery = programData.getImage().getUrl().toString().split("\\?")[0];
		final String expectedImageURLWithoutQuery = String.format(Locale.getDefault(), "%s%d.png",
				Constants.SCRATCH_IMAGE_BASE_URL, programData.getId());
		assertEquals("Image URL is corrupt!", expectedImageURLWithoutQuery, imageURLWithoutQuery);

		assertNotNull("Program has no modified date", programData.getModifiedDate());
		assertNotNull("Program has no shared date", programData.getSharedDate());

		assertTrue("View-counter-value of program is invalid", programData.getViews() >= 0);
		assertTrue("Love-counter-value of program is invalid", programData.getLoves() >= 0);
		assertTrue("Favorites-counter-value of program is invalid", programData.getFavorites() >= 0);
	}

	public void testScratchSearchWithQueryParam() {
		try {
			ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", 20, 0);
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", searchResult);
			assertNotNull("Invalid search result", programDataList);
			assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
			assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);
			assertTrue("Search result is too big...", searchResult.getProgramDataList().size() <= 20);

			for (ScratchProgramData programData : programDataList) {
				checkScratchProgramData(programData);
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testScratchSearchMaxNumberOfItemsParam() {
		try {
			final int maxNumberOfItems = 10;

			ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", maxNumberOfItems, 0);
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", searchResult);
			assertNotNull("Invalid search result", programDataList);
			assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
			assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);
			assertTrue("Search result is too big...", searchResult.getProgramDataList().size() <= maxNumberOfItems);

			for (ScratchProgramData programData : programDataList) {
				checkScratchProgramData(programData);
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testScratchSearchPagination() {
		try {
			for (int pageIndex = 1; pageIndex < 3; pageIndex++) {
				ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", 20, pageIndex);
				List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

				assertNotNull("Invalid search result", searchResult);
				assertNotNull("Invalid search result", programDataList);
				assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
				assertTrue("Wrong page number", searchResult.getPageNumber() == pageIndex);
				assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);
				assertTrue("Search result is too big...", searchResult.getProgramDataList().size() <= 20);

				for (ScratchProgramData programData : programDataList) {
					checkScratchProgramData(programData);
				}
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testFetchDefaultScratchPrograms() {
		try {
			ScratchSearchResult searchResult = ServerCalls.getInstance().fetchDefaultScratchPrograms();
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", searchResult);
			assertNotNull("Invalid search result", programDataList);
			assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
			assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);

			for (ScratchProgramData programData : programDataList) {
				checkScratchProgramData(programData);
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testFetchScratchProgramDetails() {
		try {
			final long expectedProgramID = 10205819;
			final String expectedProgramTitle = "Dancin' in the Castle";
			final String expectedProgramOwner = "jschombs";
			ScratchProgramData programData = ServerCalls.getInstance().fetchScratchProgramDetails(expectedProgramID);

			checkScratchProgramData(programData);
			assertEquals("Invalid program ID", programData.getId(), expectedProgramID);
			assertEquals("Wrong program title?! Maybe the program owner changed the program title...",
					programData.getTitle(), expectedProgramTitle);
			assertEquals("Program has invalid owner", programData.getOwner(), expectedProgramOwner);
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		} catch (WebScratchProgramException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}
}
