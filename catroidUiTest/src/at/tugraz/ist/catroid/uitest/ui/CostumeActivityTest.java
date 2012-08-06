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
package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.EditText;
import android.widget.ListAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class CostumeActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private final int RESOURCE_IMAGE = at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;

	private ProjectManager projectManager = ProjectManager.getInstance();
	private Solo solo;
	private String costumeName = "costumeNametest";
	private File imageFile;
	private File imageFile2;
	private File paintroidImageFile;
	private ArrayList<CostumeData> costumeDataList;

	public CostumeActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);

		paintroidImageFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/testFile.png",
				R.drawable.catroid_banzai, getActivity());

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		projectManager.getFileChecksumContainer().addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile2.getName());
		costumeData.setCostumeName("costumeNameTest2");
		costumeDataList.add(costumeData);
		projectManager.getFileChecksumContainer().addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		paintroidImageFile.delete();
		super.tearDown();
	}

	public void testCopyCostume() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		solo.clickOnText(getActivity().getString(R.string.copy_costume), 1);
		if (solo.searchText(costumeName + "_" + getActivity().getString(R.string.copy_costume_addition), 1, true)) {
			assertEquals("the copy of the costume wasn't added to the costumeDataList in the sprite", 3,
					costumeDataList.size());
		} else {
			fail("copy costume didn't work");
		}
	}

	public void testDeleteCostume() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		ListAdapter adapter = ((CostumeActivity) solo.getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(300);
		int newCount = adapter.getCount();
		assertEquals("the old count was not right", 2, oldCount);
		assertEquals("the new count is not right - all costumes should be deleted", 1, newCount);
		assertEquals("the count of the costumeDataList is not right", 1, costumeDataList.size());
	}

	public void testRenameCostume() {
		String newName = "newName";
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.costume_name));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(200);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(100);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		assertEquals("costume is not renamed in CostumeList", newName, costumeDataList.get(0).getCostumeName());
		if (!solo.searchText(newName)) {
			fail("costume not renamed in actual view");
		}
	}

	public void testRenameCostumeMixedCase() {
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(500);
		solo.clickOnView(solo.getView(R.id.costume_name));
		solo.sleep(300);
		solo.clearEditText(0);
		String newNameMixedCase = "coSTuMeNamEtESt";
		solo.enterText(0, newNameMixedCase);
		solo.sendKey(Solo.ENTER);
		solo.sleep(100);
		assertEquals("Costume is not renamed to Mixed Case", newNameMixedCase, costumeDataList.get(0).getCostumeName());
		if (!solo.searchText(newNameMixedCase)) {
			fail("costume not renamed in actual view");
		}
	}

	public void testMainMenuButton() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
		// needed to fix NullPointerException in next Testcase
		solo.finishInactiveActivities();
	}

	public void testDialogsOnChangeOrientation() {
		String newName = "newTestName";
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.costume_name));
		assertTrue("Dialog is not visible", solo.searchText(getActivity().getString(R.string.ok)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(100);
		assertTrue("Dialog is not visible", solo.searchText(getActivity().getString(R.string.ok)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(200);
		assertTrue("Costume wasnt renamed", solo.searchText(newName));
	}

	public void testGetImageFromPaintroid() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumPaintroidImageFile));

		boolean isInCostumeDataList = false;
		for (CostumeData costumeData : projectManager.getCurrentSprite().getCostumeDataList()) {
			if (costumeData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInCostumeDataList = true;
			}
		}
		if (!isInCostumeDataList) {
			fail("File not added in CostumeDataList");
		}
	}

	public void testEditImageWithPaintroid() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5PaintroidImage = Utils.md5Checksum(paintroidImageFile);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertNotSame("Picture was not changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())),
				md5PaintroidImage);

		boolean isInCostumeDataListPaintroidImage = false;
		boolean isInCostumeDataListSunnglasses = false;
		for (CostumeData costumeDatas : projectManager.getCurrentSprite().getCostumeDataList()) {
			if (costumeDatas.getChecksum().equalsIgnoreCase(md5PaintroidImage)) {
				isInCostumeDataListPaintroidImage = true;
			}
			if (costumeDatas.getChecksum().equalsIgnoreCase(md5ImageFile)) {
				isInCostumeDataListSunnglasses = true;
			}
		}
		assertTrue("File not added in CostumeDataList", isInCostumeDataListPaintroidImage);
		assertFalse("File not deleted from CostumeDataList", isInCostumeDataListSunnglasses);
	}

	public void testEditImageWithPaintroidNoChanges() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);

		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testEditImageWithPaintroidNoPath() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);
		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testGetImageFromPaintroidNoPath() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		CostumeData costumeData = costumeDataList.get(0);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int numberOfCostumeDatas = costumeDataList.size();
		assertEquals("wrong size of costumedatalist", 2, numberOfCostumeDatas);
		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
	}

	public void testGetImageFromGallery() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));

		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumPaintroidImageFile));

		boolean isInCostumeDataList = false;
		for (CostumeData costumeData : projectManager.getCurrentSprite().getCostumeDataList()) {
			if (costumeData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInCostumeDataList = true;
			}
		}
		if (!isInCostumeDataList) {
			fail("File not added in CostumeDataList");
		}
	}

	public void testEditImagePaintroidToSomethingWhichIsAlreadyUsed() throws IOException {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5ImageFile = Utils.md5Checksum(imageFile);
		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(costumeData.getAbsolutePath())),
				md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);
		assertEquals("too many references for checksum", 0,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
		assertEquals("not the right number of checksum references", 2, projectManager.getFileChecksumContainer()
				.getUsage(md5PaintroidImageFile));
	}

	public void testEditImageWhichIsAlreadyUsed() {
		File tempImageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				"catroid_sunglasses2.png", RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		CostumeData costumeDataToAdd = new CostumeData();
		costumeDataToAdd.setCostumeFilename(tempImageFile.getName());
		costumeDataToAdd.setCostumeName("justforthistest");
		costumeDataList.add(costumeDataToAdd);
		projectManager.getFileChecksumContainer().addChecksum(costumeDataToAdd.getChecksum(),
				costumeDataToAdd.getAbsolutePath());

		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5ImageFile = Utils.md5Checksum(imageFile);
		//		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("wrong number of costumedatas", 3, costumeDataList.size());
		assertTrue("new added image has been deleted", tempImageFile.exists());
		assertEquals("wrong number of checksum references of sunnglasses picture", 1, projectManager
				.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testAddButton() {
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		solo.clickOnView(getActivity().findViewById(R.id.btn_action_add_button));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		String dialogTitle = solo.getString(R.string.add_costume_dialog_title);
		assertTrue("Add image dialog should be displayed", solo.searchText(dialogTitle));
	}

	public void testAddDialogRotate() {
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		solo.clickOnView(getActivity().findViewById(R.id.btn_action_add_button));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(500);
		solo.setActivityOrientation(Solo.PORTRAIT);
		String dialogTitle = solo.getString(R.string.add_costume_dialog_title);
		assertTrue("Dialog should be displayed after device rotation", solo.searchText(dialogTitle));
	}

	public void testDialogCanceledOnTouchOutside() {
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		solo.clickOnView(getActivity().findViewById(R.id.btn_action_add_button));
		float screenHeight = solo.getCurrentActivity().getResources().getDisplayMetrics().heightPixels;
		float screenWidth = solo.getCurrentActivity().getResources().getDisplayMetrics().widthPixels;
		solo.clickOnScreen(screenWidth, screenHeight);
		String dialogTitle = solo.getString(R.string.add_costume_dialog_title);
		assertFalse("Dialog should disappear", solo.searchText(dialogTitle, 0, false));

	}

	public void testCostumeNames() {
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.waitForActivity(CostumeActivity.class.getSimpleName());

		String buttonCopyCostumeText = solo.getString(R.string.copy_costume);
		solo.clickOnText(buttonCopyCostumeText);
		while (solo.scrollUp()) {
			;
		}

		String defaultCostumeName = solo.getString(R.string.default_costume_name);
		String expectedCostumeName = "";

		renameCostume(costumeName, defaultCostumeName);
		renameCostume("costumeNameTest2", defaultCostumeName);
		expectedCostumeName = defaultCostumeName + "1";
		assertEquals("costume not renamed correctly", expectedCostumeName, costumeDataList.get(1).getCostumeName());
		renameCostume("costumeNametest_", defaultCostumeName);
		expectedCostumeName = defaultCostumeName + "2";
		assertEquals("costume not renamed correctly", expectedCostumeName, costumeDataList.get(2).getCostumeName());

		renameCostume(defaultCostumeName + "1", "a");
		while (solo.scrollUp()) {
			;
		}
		solo.clickOnText(solo.getString(R.string.copy_costume));
		renameCostume(defaultCostumeName + "_", defaultCostumeName);
		expectedCostumeName = defaultCostumeName + "1";
		assertEquals("costume not renamed correctly", expectedCostumeName, costumeDataList.get(3).getCostumeName());

		// test that Image from paintroid is correctly renamed
		String fileName = defaultCostumeName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE2, getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image was not created");
		}
		String checksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		expectedCostumeName = defaultCostumeName + "3";
		assertEquals("costume not renamed correctly", expectedCostumeName, costumeDataList.get(4).getCostumeName());
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImageFile));

		// test that Image from gallery is correctly renamed
		fileName = defaultCostumeName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE, getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image was not created");
		}
		checksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", imageFile.getAbsolutePath());
		intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		expectedCostumeName = defaultCostumeName + "4";
		assertEquals("costume not renamed correctly", expectedCostumeName, costumeDataList.get(5).getCostumeName());
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImageFile));

	}

	private void renameCostume(String currentCostumeName, String newCostumeName) {
		solo.clickOnText(currentCostumeName);
		EditText editTextCostumeName = (EditText) solo.getView(R.id.dialog_rename_costume_editText);
		solo.clearEditText(editTextCostumeName);
		solo.enterText(editTextCostumeName, newCostumeName);
		String buttonOKText = solo.getCurrentActivity().getString(R.string.ok);
		solo.clickOnButton(buttonOKText);
	}
}
