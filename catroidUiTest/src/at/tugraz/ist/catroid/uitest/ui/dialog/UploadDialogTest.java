/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.File;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class UploadDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = UiTestUtils.PROJECTNAME1;
	private String newTestProject = UiTestUtils.PROJECTNAME2;

	private String saveToken;

	public UploadDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Consts.TOKEN, "0");
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, saveToken).commit();
		UiTestUtils.clearAllUtilTestProjects();
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testUploadDialog() {
		// Okay looks like we are testing too much in one testcase?
		createTestProject();
		UiTestUtils.createValidUser(getActivity());
		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.waitForDialogToClose(5000);

		View renameView = solo.getText(getActivity().getString(R.string.project_rename));
		assertNotNull("View for rename project could not be found", renameView);
		assertEquals("rename View is visible.", renameView.getVisibility(), View.GONE);

		// clear the title
		solo.clearEditText(0);
		assertEquals("rename View is hidden.", renameView.getVisibility(), View.VISIBLE);

		// enter the same title
		solo.clickOnEditText(0);
		solo.enterText(0, testProject);
		assertEquals("rename View is visible.", renameView.getVisibility(), View.GONE);

		// enter a new title
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, newTestProject);
		assertEquals("rename View is hidden.", renameView.getVisibility(), View.VISIBLE);

		solo.clickOnButton(getActivity().getString(R.string.cancel_button));
		//		solo.sleep(500);
		//		solo.clickOnText(getActivity().getString(R.string.upload_project));
		//		solo.waitForDialogToClose(5000);

		//		renameView = solo.getText(getActivity().getString(R.string.project_rename));
		//		assertNotNull("View for rename project could not be found", renameView);
		//		assertEquals("rename View is visible.", View.GONE, renameView.getVisibility());
		//assertNotNull("Project Name is not saved.", solo.getEditText(testProject));

	}

	public void testOrientationChange() {
		createTestProject();
		String testText1 = "testText1";
		String testText2 = "testText2";
		UiTestUtils.createValidUser(getActivity());
		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(500);
		solo.clearEditText(0);
		solo.enterText(0, testText1);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testText1));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.enterText(1, testText2);

		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testText2));
	}

	private void createTestProject() {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + testProject);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		assertFalse("testProject was not deleted!", directory.exists());

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.enterText(0, testProject);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		solo.sleep(2000);

		File file = new File(Consts.DEFAULT_ROOT + "/" + testProject + "/" + testProject + Consts.PROJECT_EXTENTION);
		assertTrue(testProject + " was not created!", file.exists());
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_home);
	}
}
