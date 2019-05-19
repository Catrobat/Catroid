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

package org.catrobat.catroid.test.io;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Test;

public class RemixTest extends AndroidTestCase {

	private Project firstProject;
	private Project secondProject;

	@Test
	public void testVerifyRemixUrlsOfMergedProgramConsistingOfTwoDownloadedPrograms() throws Exception {
		Project mergedProject = new Project();

		createProjectWithDifferentScripts();
		String expectedUrlOfFirstProgram = "/pocketcode/program/12345";
		String expectedUrlOfSecondProgram = "https://scratch.mit.edu/projects/10205819";

		firstProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfFirstProgram);
		Reflection.setPrivateField(XmlHeader.class, firstProject.getXmlHeader(),
				"remixGrandparentsUrlString", "/pocketcode/program/82341");
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		ProjectManager.getInstance().createHeader(mergedProject, firstProject, secondProject);

		String expectedUrlFieldValue = String.format("%s [%s], %s [%s]", firstProject.getName(),
				expectedUrlOfFirstProgram, secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergedProject.getXmlHeader(), "remixGrandparentsUrlString");

		assertEquals("Expecting remixOf header-field to be empty!", "", mergedRemixOfString);
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergedProject.getXmlHeader().getRemixParentsUrlString());
	}

	@Test
	public void testVerifyRemixUrlsOfMergedProgramConsistingOfTwoLocallyCreatedPrograms() throws Exception {
		Project mergedProject = new Project();
		createProjectWithDifferentScripts();

		ProjectManager.getInstance().createHeader(mergedProject, firstProject, secondProject);

		String expectedUrlFieldValue = String.format("%s, %s", firstProject.getName(), secondProject.getName());

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergedProject.getXmlHeader(), "remixGrandparentsUrlString");

		assertEquals("Expecting remixOf header-field to be empty!", "", mergedRemixOfString);
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergedProject.getXmlHeader().getRemixParentsUrlString());
	}

	@Test
	public void testVerifyRemixUrlsOfMergedProgramWhereFirstProgramHasBeenDownloadedAndSecondProgramHasBeenLocallyCreated() throws Exception {
		Project mergedProject = new Project();
		createProjectWithDifferentScripts();
		String expectedUrlOfFirstProgram = "http://pocketcode.org/details/3218";
		firstProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfFirstProgram);

		ProjectManager.getInstance().createHeader(mergedProject, firstProject, secondProject);

		String expectedUrlFieldValue = String.format("%s [%s], %s",
				firstProject.getName(), expectedUrlOfFirstProgram, secondProject.getName());

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergedProject.getXmlHeader(), "remixGrandparentsUrlString");

		assertEquals("Expecting remixOf header-field to be empty!", "", mergedRemixOfString);
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergedProject.getXmlHeader().getRemixParentsUrlString());
	}

	@Test
	public void testVerifyRemixUrlsOfMergedProgramWhereFirstProgramHasBeenLocallyCreatedAndSecondProgramHasBeenDownloaded() throws Exception {
		Project mergedProject = new Project();

		createProjectWithDifferentScripts();
		String expectedUrlOfSecondProgram = "http://pocketcode.org/details/3218";
		secondProject.getXmlHeader().setRemixParentsUrlString(expectedUrlOfSecondProgram);

		ProjectManager.getInstance().createHeader(mergedProject, firstProject, secondProject);

		String expectedUrlFieldValue = String.format("%s, %s [%s]", firstProject.getName(),
				secondProject.getName(), expectedUrlOfSecondProgram);

		String mergedRemixOfString = (String) Reflection.getPrivateField(XmlHeader.class,
				mergedProject.getXmlHeader(), "remixGrandparentsUrlString");

		assertEquals("Expecting remixOf header-field to be empty!", "", mergedRemixOfString);
		assertEquals("Unexpected remix-url header-field!",
				expectedUrlFieldValue, mergedProject.getXmlHeader().getRemixParentsUrlString());
	}

	private void createProjectWithDifferentScripts() {
		XstreamSerializer storageHandler = XstreamSerializer.getInstance();

		String firstSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_1);
		String secondSpriteName = getContext().getResources().getString(R.string.default_project_cloud_sprite_name_2);

		firstProject = TestUtils.createProjectWithGlobalValues("First Project", firstSpriteName, "test1", getContext());
		secondProject = TestUtils.createProjectWithSpriteValues("Second Project", firstSpriteName, "test2", getContext());
		secondProject.getDefaultScene().addSprite(new SingleSprite(secondSpriteName));
		storageHandler.getInstance().saveProject(firstProject);
		storageHandler.getInstance().saveProject(secondProject);
	}
}
