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
package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.IOException;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.MyProjectsActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class RenameSpriteDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private String testProject = UiTestUtils.PROJECTNAME1;
	private String cat = "cat";
	private String kat = "kat";
	private String catMixedCase = "CaT";

	public RenameSpriteDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		ProjectManager.getInstance().deleteCurrentProject();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testRenameSpriteDialog() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.sleep(1000);
		assertTrue("Cannot click on project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.clickLongOnText(cat);

		solo.clickOnText(solo.getString(R.string.rename));
		solo.sleep(100);
		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, kat);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		String first = ((Sprite) spritesList.getItemAtPosition(1)).getName();

		assertEquals("The first sprite is NOT rename!", first, kat);
	}

	public void testRenameSpriteDialogMixedCase() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.sleep(500);
		assertTrue("Cannot click on project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.clickLongOnText(cat);

		solo.sleep(1000);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, catMixedCase);
		solo.sendKey(Solo.ENTER);

		ListView spriteList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		String first = ((Sprite) spriteList.getItemAtPosition(1)).getName();
		assertEquals("The first sprite name was not renamed to Mixed Case", first, catMixedCase);
	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();
		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		storageHandler.saveProject(project);
	}
}
