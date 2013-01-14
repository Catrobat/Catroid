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
package org.catrobat.catroid.uitest.stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.CostumeFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SwitchToCostumeCrashTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public SwitchToCostumeCrashTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testSwitchToCostumeCrashPNG() throws IOException {
		prepareTest();

		String nyanCatPath = "";
		String nyanCat = "nyancat";
		String nyanCatPng = "nyancat_crash.png";
		try {
			// need to load image from assets, not from resources folder.
			// this way, the image is loaded similar to catroid, when importing an costume.
			// if we use the image from res-folder instead of assets, test would
			// pass even if the needed code in copyImageIntoCatroid() was deleted
			InputStream inputStream = getInstrumentation().getContext().getResources().getAssets().open(nyanCatPng);
			nyanCatPath = Utils.buildPath(Utils.buildProjectPath(UiTestUtils.DEFAULT_TEST_PROJECT_NAME),
					Constants.IMAGE_DIRECTORY, nyanCatPng);
			writeBufferToFile(inputStream, nyanCatPath);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image not loaded from Assets");
		}

		File nyanCatPngFile = new File(nyanCatPath);
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", nyanCatPngFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		assertTrue("Testfile not added from mockActivity", solo.searchText(nyanCat));

		String checksumNyanCatImageFile = Utils.md5Checksum(nyanCatPngFile);
		assertTrue("Checksum not in checksumcontainer", ProjectManager.INSTANCE.getFileChecksumContainer()
				.containsChecksum(checksumNyanCatImageFile));

		boolean isInCostumeDataList = false;
		for (CostumeData costumeData : ProjectManager.INSTANCE.getCurrentSprite().getCostumeDataList()) {
			if (costumeData.getChecksum().equalsIgnoreCase(checksumNyanCatImageFile)) {
				isInCostumeDataList = true;
			}
		}
		if (!isInCostumeDataList) {
			fail("File not added in CostumeDataList");
		}

		String scriptsSpinnerText = solo.getString(R.string.scripts);
		String looksSpinnerText = solo.getString(R.string.category_looks);
		clickOnSpinnerItem(looksSpinnerText, scriptsSpinnerText);
		solo.clickOnText(solo.getString(R.string.broadcast_nothing_selected));
		solo.clickOnText(nyanCat);

		UiTestUtils.clickOnActionBar(solo, R.id.btn_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(5000);
	}

	public void testSwitchToCostumeCrashJPG() throws IOException {
		prepareTest();

		String manImagePath = "";
		String manImage = "man_crash";
		String manImageJpg = "man_crash.jpg";
		try {
			// need to load image from assets, not from resources folder.
			// this way, the image is loaded similar to catroid, when importing an costume.
			// if we use the image from res-folder instead of assets, test would
			// pass even if the needed code in copyImageIntoCatroid() was deleted
			InputStream inputStream = getInstrumentation().getContext().getResources().getAssets().open(manImageJpg);
			manImagePath = Utils.buildPath(Utils.buildProjectPath(UiTestUtils.DEFAULT_TEST_PROJECT_NAME),
					Constants.IMAGE_DIRECTORY, manImageJpg);
			writeBufferToFile(inputStream, manImagePath);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image not loaded from Assets");
		}

		File nyanCatPngFile = new File(manImagePath);
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", nyanCatPngFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		assertTrue("Testfile not added from mockActivity", solo.searchText(manImage));

		String checksumNyanCatImageFile = Utils.md5Checksum(nyanCatPngFile);
		assertTrue("Checksum not in checksumcontainer", ProjectManager.INSTANCE.getFileChecksumContainer()
				.containsChecksum(checksumNyanCatImageFile));

		boolean isInCostumeDataList = false;
		for (CostumeData costumeData : ProjectManager.INSTANCE.getCurrentSprite().getCostumeDataList()) {
			if (costumeData.getChecksum().equalsIgnoreCase(checksumNyanCatImageFile)) {
				isInCostumeDataList = true;
			}
		}
		if (!isInCostumeDataList) {
			fail("File not added in CostumeDataList");
		}

		String scriptsSpinnerText = solo.getString(R.string.scripts);
		String looksSpinnerText = solo.getString(R.string.category_looks);
		clickOnSpinnerItem(looksSpinnerText, scriptsSpinnerText);
		solo.clickOnText(solo.getString(R.string.broadcast_nothing_selected));
		solo.clickOnText(manImage);

		UiTestUtils.clickOnActionBar(solo, R.id.btn_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(5000);
	}

	private void writeBufferToFile(InputStream inputStream, String imageFilePath) throws IOException {
		FileOutputStream nyanCatFileOutputStream;
		nyanCatFileOutputStream = new FileOutputStream(imageFilePath);
		byte[] buffer = new byte[inputStream.available()];
		inputStream.read(buffer);
		inputStream.close();
		nyanCatFileOutputStream.write(buffer);
		nyanCatFileOutputStream.close();
	}

	private void prepareTest() {
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		String scriptsSpinnerText = solo.getString(R.string.scripts);
		String looksSpinnerText = solo.getString(R.string.category_looks);
		clickOnSpinnerItem(scriptsSpinnerText, looksSpinnerText);
		UiTestUtils.waitForFragment(solo, R.id.fragment_costume_relative_layout);
	}

	private void clickOnSpinnerItem(String selectedSpinnerItem, String itemName) {
		solo.clickOnText(selectedSpinnerItem);
		solo.clickOnText(itemName);
	}

	private void createProject() {
		StorageHandler storageHandler = StorageHandler.getInstance();
		Project project = new Project(getActivity(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		Sprite backgroundSprite = project.getSpriteList().get(0);
		Script startScript = new StartScript(backgroundSprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(backgroundSprite);

		startScript.addBrick(setCostumeBrick);
		backgroundSprite.addScript(startScript);
		project.addSprite(backgroundSprite);

		ProjectManager.INSTANCE.setFileChecksumContainer(new FileChecksumContainer());
		ProjectManager.INSTANCE.setProject(project);
		ProjectManager.INSTANCE.setCurrentSprite(backgroundSprite);
		ProjectManager.INSTANCE.setCurrentScript(startScript);
		storageHandler.saveProject(project);
	}

	private CostumeFragment getCostumeFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (CostumeFragment) activity.getFragment(ScriptActivity.FRAGMENT_COSTUMES);
	}
}
