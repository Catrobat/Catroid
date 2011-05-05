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

package at.tugraz.ist.catroid.uitest.web;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.transfers.ProjectUploadTask;
import at.tugraz.ist.catroid.ui.DownloadActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class ProjectUpAndDownloadTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = "testProject";
	private String newTestProject = "newProjectToTest";

	public ProjectUpAndDownloadTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		deleteCreatedProjects();
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		deleteCreatedProjects();

		super.tearDown();
	}

	public void testUploadProject() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ProjectUploadTask.useTestUrl = true;
			}
		});

		createTestProject();
		addABrickToProject();
		uploadProject();

		deleteCreatedProjects();

		downloadProject();
	}

	private void createTestProject() {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + testProject);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		assertFalse("testProject was not deleted!", directory.exists());

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, testProject);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		solo.sleep(2000);

		File file = new File(Consts.DEFAULT_ROOT + "/" + testProject + "/" + testProject + Consts.PROJECT_EXTENTION);
		assertTrue(testProject + " was not created!", file.exists());
	}

	private void addABrickToProject() {
		solo.clickInList(0);
		solo.clickOnText(getActivity().getString(R.string.add_new_brick));

		solo.sleep(500);
		solo.clickOnText(getActivity().getString(R.string.brick_wait));

		solo.clickOnButton(getActivity().getString(R.string.main_menu));
	}

	private void uploadProject() {
		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(500);

		// enter a new title
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, newTestProject);

		// enter a description
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, "the project description");

		solo.clickOnButton(getActivity().getString(R.string.upload_button));

		solo.waitForDialogToClose(10000);
		assertTrue("Upload failed. Internet connection?",
				solo.searchText(getActivity().getString(R.string.success_project_upload)));
		solo.clickOnButton(0);
	}

	private void downloadProject() {
		int serverProjectId = ProjectManager.getInstance().getServerProjectId();
		String downloadUrl = Consts.TEST_FILE_DOWNLOAD_URL + serverProjectId + Consts.CATROID_EXTENTION;
		downloadUrl += "?fname=" + newTestProject;
		Intent intent = new Intent(getActivity(), DownloadActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(downloadUrl));
		launchActivityWithIntent("at.tugraz.ist.catroid", DownloadActivity.class, intent);

		assertTrue("Download takes to long.", solo.waitForActivity("MainMenuActivity", 10000));
		assertNotNull("Download not successful.",
				solo.searchText(getActivity().getString(R.string.success_project_download)));

		String projectPath = Consts.DEFAULT_ROOT + "/" + newTestProject;
		File downloadedDirectory = new File(projectPath);
		File downloadedSPFFile = new File(projectPath + "/" + newTestProject + Consts.PROJECT_EXTENTION);
		assertTrue("Downloaded Directory does not exist.", downloadedDirectory.exists());
		assertTrue("Project File does not exist.", downloadedSPFFile.exists());

	}

	private void deleteCreatedProjects() {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + testProject);
		UtilFile.deleteDirectory(directory);
		File newDirectory = new File(Consts.DEFAULT_ROOT + "/" + newTestProject);
		UtilFile.deleteDirectory(newDirectory);
	}
}
