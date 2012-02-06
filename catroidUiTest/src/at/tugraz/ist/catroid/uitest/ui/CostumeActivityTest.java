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
import android.widget.ListAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class CostumeActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private ProjectManager projectManager = ProjectManager.getInstance();
	private Solo solo;
	private String costumeName = "costumeNametest";
	private File imageFile;
	private File imageFile2;
	private File paintroidImageFile;
	private ArrayList<CostumeData> costumeDataList;
	private final int RESOURCE_IMAGE = R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;

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

		paintroidImageFile = UiTestUtils.createTestMediaFile(Consts.DEFAULT_ROOT + "/testFile.png",
				R.drawable.catroid_banzai, getActivity());

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile2.getName());
		costumeData.setCostumeName("costumeNameTest2");
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();
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
		UiTestUtils.clearAllUtilTestProjects();
		paintroidImageFile.delete();
		super.tearDown();
	}

	public void testCopyCostume() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(2000);
		solo.clickOnButton(2);
		if (solo.searchText(costumeName + "_" + getActivity().getString(R.string.copy_costume_addition))) {
			assertEquals("the copy of the costume wasn't added to the costumeDataList in the sprite", 3,
					costumeDataList.size());
		} else {
			fail("copy costume didn't work");
		}
	}

	public void testDeleteCostume() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(700);
		ListAdapter adapter = ((CostumeActivity) solo.getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		solo.sleep(1000);
		int newCount = adapter.getCount();
		assertEquals("the old count was not right", 2, oldCount);
		assertEquals("the new count is not right - all costumes should be deleted", 1, newCount);
		assertEquals("the count of the costumeDataList is not right", 1, costumeDataList.size());
	}

	public void testRenameCostume() {
		String newName = "newName";
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.sound_rename));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);

		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnButton(0);
		solo.sleep(100);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		assertEquals("costume is not renamed in CostumeList", newName, costumeDataList.get(0).getCostumeName());
		if (!solo.searchText(newName)) {
			fail("costume not renamed in actual view");
		}
	}

	public void testMainMenuButton() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(500);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo = new Solo(getInstrumentation(), getActivity());

		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
	}

	public void testDialogsOnChangeOrientation() {
		String newName = "newTestName";
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.sound_rename));
		assertTrue("Dialog is not visible", solo.searchText(getActivity().getString(R.string.ok)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		assertTrue("Dialog is not visible", solo.searchText(getActivity().getString(R.string.ok)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(100);
		assertTrue("Costume wasnt renamed", solo.searchText(newName));
	}

	public void testGetImageFromPaintroid() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(500);

		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(getActivity().getString(R.string.extra_picture_path_paintroid), paintroidImageFile
				.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);
		solo.sleep(200);

		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));
		assertTrue("Checksum not in checksumcontainer", projectManager.fileChecksumContainer
				.containsChecksum(checksumPaintroidImageFile));

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
		solo.sleep(800);

		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5PaintroidImage = Utils.md5Checksum(paintroidImageFile);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(getActivity().getString(R.string.extra_picture_path_paintroid), imageFile
				.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(5000);

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
		solo.sleep(800);

		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(getActivity().getString(R.string.extra_picture_path_paintroid), imageFile
				.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(200);

		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);

		assertEquals("too many references for checksum", 1, projectManager.fileChecksumContainer.getUsage(md5ImageFile));
	}

	public void testEditImageWithPaintroidNoPath() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(800);

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
		solo.sleep(200);

		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);
		assertEquals("too many references for checksum", 1, projectManager.fileChecksumContainer.getUsage(md5ImageFile));
	}

	public void testGetImageFromPaintroidNoPath() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(800);

		CostumeData costumeData = costumeDataList.get(0);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);
		solo.sleep(200);

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int numberOfCostumeDatas = costumeDataList.size();
		assertEquals("wrong size of costumedatalist", 2, numberOfCostumeDatas);
		assertEquals("Picture changed", Utils.md5Checksum(new File(costumeData.getAbsolutePath())), md5ImageFile);
	}

	public void testGetImageFromGallery() {
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(800);

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));

		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);
		assertTrue("Checksum not in checksumcontainer", projectManager.fileChecksumContainer
				.containsChecksum(checksumPaintroidImageFile));

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
		solo.sleep(900);

		int numberOfCostumeDatas = costumeDataList.size();
		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5ImageFile = Utils.md5Checksum(imageFile);
		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(getActivity().getString(R.string.extra_picture_path_paintroid), imageFile
				.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(costumeData.getAbsolutePath())),
				md5ImageFile);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int newNumberOfCostumeDatas = costumeDataList.size();
		assertEquals("CostumeData was added", numberOfCostumeDatas, newNumberOfCostumeDatas);
		assertEquals("too many references for checksum", 0, projectManager.fileChecksumContainer.getUsage(md5ImageFile));
		assertEquals("not the right number of checksum references", 2, projectManager.fileChecksumContainer
				.getUsage(md5PaintroidImageFile));
	}

	public void testEditImageWhichIsAlreadyUsed() {
		File tempImageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				"catroid_sunglasses2.png", RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		CostumeData costumeDataToAdd = new CostumeData();
		costumeDataToAdd.setCostumeFilename(tempImageFile.getName());
		costumeDataToAdd.setCostumeName("justforthistest");
		costumeDataList.add(costumeDataToAdd);
		projectManager.fileChecksumContainer.addChecksum(costumeDataToAdd.getChecksum(), costumeDataToAdd
				.getAbsolutePath());

		solo.sleep(900);
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(900);

		CostumeData costumeData = costumeDataList.get(0);
		(getActivity()).selectedCostumeData = costumeData;
		String md5ImageFile = Utils.md5Checksum(imageFile);
		//		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(getActivity().getString(R.string.extra_picture_path_paintroid), imageFile
				.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				at.tugraz.ist.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getActivity().getCurrentActivity().startActivityForResult(intent, CostumeActivity.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);

		assertEquals("wrong number of costumedatas", 3, costumeDataList.size());
		assertTrue("new added image has been deleted", tempImageFile.exists());
		assertEquals("wrong number of checksum references of sunnglasses picture", 1,
				projectManager.fileChecksumContainer.getUsage(md5ImageFile));

	}
}
