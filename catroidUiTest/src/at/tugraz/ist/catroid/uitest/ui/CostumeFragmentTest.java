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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.fragment.CostumeFragment;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class CostumeFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private final int RESOURCE_IMAGE = at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;

	private ProjectManager projectManager = ProjectManager.getInstance();
	private Solo solo;
	private String costumeName = "costumeNametest";
	private File imageFile;
	private File imageFile2;
	private File paintroidImageFile;
	private ArrayList<CostumeData> costumeDataList;

	public CostumeFragmentTest() {
		super(MainMenuActivity.class);
	}

	@SuppressWarnings("deprecation")
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
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		paintroidImageFile.delete();
		super.tearDown();
	}

	public void testAddNewCostumeActionbarIcon() {
		goToCostumesTab();
		String addCostumeFromCameraText = solo.getString(R.string.add_costume_from_camera);
		String addCostumeFromGalleryText = solo.getString(R.string.add_costume_from_gallery);
		assertFalse("Menu to add costume from camera should not be visible", solo.searchText(addCostumeFromCameraText));
		assertFalse("Menu to add costume from gallery should not be visible",
				solo.searchText(addCostumeFromGalleryText));
		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		assertTrue("Menu to add costume from camera was not visible visible", solo.searchText(addCostumeFromCameraText));
		assertTrue("Menu to add costume from gallery was not visible", solo.searchText(addCostumeFromGalleryText));
		solo.goBack();
	}

	public void testAddNewCostumeDialog() {
		// currently disabled, until at least 2 camera apps are installed on testdevice
		// otherwise test would timeout, because chooser is not shown
		// String addCostumeFromCameraDialogTitle = solo.getString(R.string.select_costume_from_camera);
		String addCostumeFromGalleryDialogTitle = solo.getString(R.string.select_costume_from_gallery);

		goToCostumesTab();
		solo.clickOnText(solo.getString(R.string.sound_delete));
		solo.clickOnButton(0);
		solo.sleep(300);

		// see comment at the top of the method
		//		solo.clickOnView(solo.getView(R.id.view_camera_non_scrollable));
		//		solo.waitForText(addCostumeFromCameraDialogTitle, 0, 1000);
		//		assertTrue("Dialog to add new costume from camera did not appear",
		//				solo.searchText(addCostumeFromCameraDialogTitle));
		//		solo.goBack();
		//		solo.sleep(200);
		//		solo.clickOnView(solo.getView(R.id.costumelist_footerview_camera));
		//		solo.waitForText(addCostumeFromCameraDialogTitle, 0, 1000);
		//		assertTrue("Dialog to add new costume from camera did not appear",
		//				solo.searchText(addCostumeFromCameraDialogTitle));
		//		solo.goBack();
		//		solo.sleep(200);
		//		solo.clickOnView(solo.getView(R.id.costumelist_footerview_camera_add_image));
		//		solo.waitForText(addCostumeFromCameraDialogTitle, 0, 1000);
		//		assertTrue("Dialog to add new costume from camera did not appear",
		//				solo.searchText(addCostumeFromCameraDialogTitle));
		//		solo.goBack();
		//		solo.sleep(200);

		solo.clickOnView(solo.getView(R.id.view_gallery_non_scrollable));
		solo.waitForText(addCostumeFromGalleryDialogTitle, 0, 1000);
		assertTrue("Dialog to add new costume from gallery did not appear",
				solo.searchText(addCostumeFromGalleryDialogTitle));
		solo.goBack();
		solo.sleep(200);

		solo.clickOnView(solo.getView(R.id.costumelist_footerview_gallery));
		solo.waitForText(addCostumeFromGalleryDialogTitle, 0, 1000);
		assertTrue("Dialog to add new costume from gallery did not appear",
				solo.searchText(addCostumeFromGalleryDialogTitle));
		solo.goBack();
		solo.sleep(200);
		solo.clickOnView(solo.getView(R.id.costumelist_footerview_gallery_add_image));
		solo.waitForText(addCostumeFromGalleryDialogTitle, 0, 1000);
		assertTrue("Dialog to add new costume from gallery did not appear",
				solo.searchText(addCostumeFromGalleryDialogTitle));
		solo.goBack();
		solo.sleep(200);
	}

	public void testCopyCostume() {
		goToCostumesTab();
		solo.clickOnText(getActivity().getString(R.string.copy_costume), 1);
		if (solo.searchText(costumeName + "_" + getActivity().getString(R.string.copy_costume_addition), 1, true)) {
			assertEquals("the copy of the costume wasn't added to the costumeDataList in the sprite", 3,
					costumeDataList.size());
		} else {
			fail("copy costume didn't work");
		}
	}

	public void testDeleteCostume() {
		goToCostumesTab();
		ListAdapter adapter = getCostumeFragment().getListAdapter();

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
		goToCostumesTab();
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
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		assertEquals("costume is not renamed in CostumeList", newName, costumeDataList.get(0).getCostumeName());
		if (!solo.searchText(newName)) {
			fail("costume not renamed in actual view");
		}
	}

	public void testRenameCostumeMixedCase() {
		goToCostumesTab();
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
		goToCostumesTab();
		UiTestUtils.clickOnUpActionBarButton(solo.getCurrentActivity());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
	}

	public void testDialogsOnChangeOrientation() {
		String newName = "newTestName";
		goToCostumesTab();
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
		goToCostumesTab();
		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
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
		goToCostumesTab();
		CostumeData costumeData = costumeDataList.get(0);
		getCostumeFragment().setSelectedCostumeData(costumeData);

		String md5PaintroidImage = Utils.md5Checksum(paintroidImageFile);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_PAINTROID_EDIT_IMAGE);

		solo.sleep(5000);
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
		goToCostumesTab();
		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		getCostumeFragment().setSelectedCostumeData(costumeData);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);

		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testEditImageWithPaintroidNoPath() {
		goToCostumesTab();
		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		getCostumeFragment().setSelectedCostumeData(costumeData);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);
		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testGetImageFromPaintroidNoPath() {
		goToCostumesTab();
		CostumeData costumeData = costumeDataList.get(0);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int numberOfCostumeDatas = costumeDataList.size();
		assertEquals("wrong size of costumedatalist", 2, numberOfCostumeDatas);
		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
	}

	public void testGetImageFromGallery() {
		goToCostumesTab();
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
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
		goToCostumesTab();
		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		getCostumeFragment().setSelectedCostumeData(costumeData);
		String md5ImageFile = Utils.md5Checksum(imageFile);
		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
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
		goToCostumesTab();

		CostumeData costumeData = costumeDataList.get(0);
		getCostumeFragment().setSelectedCostumeData(costumeData);
		String md5ImageFile = Utils.md5Checksum(imageFile);
		//		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("wrong number of costumedatas", 3, costumeDataList.size());
		assertTrue("new added image has been deleted", tempImageFile.exists());
		assertEquals("wrong number of checksum references of sunnglasses picture", 1, projectManager
				.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testCostumeNames() {
		goToCostumesTab();
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
		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.sleep(5000);
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

		getCostumeFragment().startActivityForResult(intent, CostumeFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.sleep(5000);
		expectedCostumeName = defaultCostumeName + "4";
		assertEquals("costume not renamed correctly", expectedCostumeName, costumeDataList.get(5).getCostumeName());
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImageFile));
	}

	public void testResolutionWhenEditedAndCroppedWithPaintroid() {
		goToCostumesTab();

		CostumeData costumeData = costumeDataList.get(0);
		getCostumeFragment().setSelectedCostumeData(costumeData);

		String pathToImageFile = imageFile.getAbsolutePath();
		int[] fileResolutionBeforeCrop = costumeData.getResolution();
		int[] displayedResolutionBeforeCrop = getDisplayedResolution(costumeData);

		int sampleSize = 2;

		solo.sleep(1000);
		try {
			UiTestUtils.cropImage(pathToImageFile, sampleSize);
		} catch (FileNotFoundException e) {
			fail("Test failed because file was not found");
			e.printStackTrace();
		}

		UiTestUtils.clickOnUpActionBarButton(solo.getCurrentActivity());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		goToCostumesTab();

		int[] fileResolutionAfterCrop = costumeData.getResolution();
		int[] displayedResolutionAfterCrop = getDisplayedResolution(costumeData);

		assertTrue("Bitmap resolution in file was not cropped",
				fileResolutionAfterCrop[0] < fileResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < fileResolutionBeforeCrop[1]);
		assertTrue("Image resolution was not updated in costume fragment",
				displayedResolutionAfterCrop[0] < displayedResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < displayedResolutionBeforeCrop[1]);
	}

	private int[] getDisplayedResolution(CostumeData costume) {
		TextView resolutionTextView = (TextView) solo.getView(R.id.costume_res);
		String resolutionString = resolutionTextView.getText().toString();
		//resolution string has form "width x height"
		int dividingPosition = resolutionString.indexOf(' ');
		String widthString = resolutionString.substring(0, dividingPosition);
		String heightString = resolutionString.substring(dividingPosition + 3, resolutionString.length());
		int width = Integer.parseInt(widthString);
		int heigth = Integer.parseInt(heightString);

		int[] resolution = new int[2];
		resolution[0] = width;
		resolution[1] = heigth;
		return resolution;
	}

	private void renameCostume(String currentCostumeName, String newCostumeName) {
		solo.clickOnText(currentCostumeName);
		EditText editTextCostumeName = solo.getEditText(0);
		solo.clearEditText(editTextCostumeName);
		solo.enterText(editTextCostumeName, newCostumeName);
		String buttonOKText = solo.getCurrentActivity().getString(R.string.ok);
		solo.clickOnButton(buttonOKText);
	}

	private CostumeFragment getCostumeFragment() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		return (CostumeFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_COSTUMES);
	}

	private void goToCostumesTab() {
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(500);
	}
}
