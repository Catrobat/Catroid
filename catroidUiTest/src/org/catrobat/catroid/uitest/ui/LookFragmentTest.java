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
package org.catrobat.catroid.uitest.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.LookAdapter;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class LookFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int RESOURCE_IMAGE = org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses;
	private static final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;
	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final int TIME_TO_WAIT = 50;

	private String lookName = "lookNametest";

	private File imageFile;
	private File imageFile2;
	private File paintroidImageFile;

	private ArrayList<LookData> lookDataList;

	private ProjectManager projectManager;

	private Solo solo;

	public LookFragmentTest() {
		super(MainMenuActivity.class);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();

		projectManager = ProjectManager.getInstance();
		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);

		paintroidImageFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/testFile.png",
				R.drawable.catroid_banzai, getActivity());

		LookData lookData = new LookData();
		lookData.setLookFilename(imageFile.getName());
		lookData.setLookName(lookName);
		lookDataList.add(lookData);

		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		lookData = new LookData();
		lookData.setLookFilename(imageFile2.getName());
		lookData.setLookName("lookNameTest2");
		lookDataList.add(lookData);

		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();

		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoLooksFromMainMenu(solo, true);

		if (getLookAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		paintroidImageFile.delete();
		super.tearDown();
		solo = null;
	}

	public void testInitialLayout() {
		assertFalse("Initially showing details", getLookAdapter().getShowDetails());
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testAddNewLookDialog() {
		String addLookFromCameraText = solo.getString(R.string.add_look_from_camera);
		String addLookFromGalleryText = solo.getString(R.string.add_look_from_gallery);

		assertFalse("Menu to add look from camera should not be visible", solo.searchText(addLookFromCameraText));
		assertFalse("Menu to add look from gallery should not be visible", solo.searchText(addLookFromGalleryText));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertTrue("Entry to add look from camera not visible", solo.searchText(addLookFromCameraText));
		assertTrue("Entry to add look from gallery not visible", solo.searchText(addLookFromGalleryText));
	}

	public void testCopyLook() {
		solo.clickOnText(solo.getString(R.string.copy_look), 1);
		if (solo.searchText(lookName + "_" + solo.getString(R.string.copy_look_addition), 1, true)) {
			assertEquals("the copy of the look wasn't added to the lookDataList in the sprite", 3, lookDataList.size());
		} else {
			fail("copy look didn't work");
		}
	}

	public void testDeleteLook() {
		ListAdapter adapter = getLookFragment().getListAdapter();

		int oldCount = adapter.getCount();
		solo.clickOnButton(solo.getString(R.string.delete_lowercase));
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(300);
		int newCount = adapter.getCount();
		assertEquals("the old count was not right", 2, oldCount);
		assertEquals("the new count is not right - all looks should be deleted", 1, newCount);
		assertEquals("the count of the lookDataList is not right", 1, lookDataList.size());
	}

	public void testDeleteLookFile() {
		Sprite firstSprite = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0);
		LookData lookToDelete = firstSprite.getLookDataList().get(1);

		solo.clickOnText(solo.getString(R.string.delete_lowercase), 2);
		String buttonPositive = solo.getString(R.string.ok);
		solo.clickOnText(buttonPositive);

		File deletedFile = new File(lookToDelete.getAbsolutePath());
		assertFalse("File should be deleted", deletedFile.exists());
	}

	public void testRenameLook() {
		String newName = "newName";
		solo.clickOnView(solo.getView(R.id.look_name));
		solo.sleep(200);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		assertEquals("look is not renamed in LookList", newName, lookDataList.get(0).getLookName());
		if (!solo.searchText(newName)) {
			fail("look not renamed in actual view");
		}
	}

	public void testRenameLookMixedCase() {
		solo.clickOnView(solo.getView(R.id.look_name));
		solo.sleep(300);
		solo.clearEditText(0);
		String newNameMixedCase = "coSTuMeNamEtESt";
		solo.enterText(0, newNameMixedCase);
		solo.sendKey(Solo.ENTER);
		solo.sleep(100);
		assertEquals("Look is not renamed to Mixed Case", newNameMixedCase, lookDataList.get(0).getLookName());
		if (!solo.searchText(newNameMixedCase)) {
			fail("look not renamed in actual view");
		}
	}

	public void testGetImageFromPaintroid() {
		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumPaintroidImageFile));

		boolean isInLookDataList = false;
		for (LookData lookData : projectManager.getCurrentSprite().getLookDataList()) {
			if (lookData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		if (!isInLookDataList) {
			fail("File not added in LookDataList");
		}
	}

	public void testEditImageWithPaintroid() {
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String md5PaintroidImage = Utils.md5Checksum(paintroidImageFile);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);

		solo.sleep(5000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertNotSame("Picture was not changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())),
				md5PaintroidImage);

		boolean isInLookDataListPaintroidImage = false;
		boolean isInLookDataListSunnglasses = false;
		for (LookData lookDatas : projectManager.getCurrentSprite().getLookDataList()) {
			if (lookDatas.getChecksum().equalsIgnoreCase(md5PaintroidImage)) {
				isInLookDataListPaintroidImage = true;
			}
			if (lookDatas.getChecksum().equalsIgnoreCase(md5ImageFile)) {
				isInLookDataListSunnglasses = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataListPaintroidImage);
		assertFalse("File not deleted from LookDataList", isInLookDataListSunnglasses);
	}

	public void testEditImageWithPaintroidNoChanges() {
		int numberOfLookDatas = lookDataList.size();
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ImageFile);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", numberOfLookDatas, newNumberOfLookDatas);

		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testEditImageWithPaintroidNoPath() {
		int numberOfLookDatas = lookDataList.size();
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ImageFile);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", numberOfLookDatas, newNumberOfLookDatas);
		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testGetImageFromPaintroidNoPath() {
		LookData lookData = lookDataList.get(0);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int numberOfLookDatas = lookDataList.size();
		assertEquals("wrong size of lookdatalist", 2, numberOfLookDatas);
		assertEquals("Picture changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ImageFile);
	}

	public void testGetImageFromGallery() {
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));

		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumPaintroidImageFile));

		boolean isInLookDataList = false;
		for (LookData lookData : projectManager.getCurrentSprite().getLookDataList()) {
			if (lookData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		if (!isInLookDataList) {
			fail("File not added in LookDataList");
		}
	}

	public void testGetImageFromGalleryNullData() {
		lookDataList = ProjectManager.INSTANCE.getCurrentSprite().getLookDataList();
		int numberOfLookDatasBeforeIntent = lookDataList.size();
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		bundleForGallery.putBoolean("returnNullData", true);
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(2000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName(), 2000);
		solo.assertCurrentActivity("Test should not fail - should be in ScriptActivity",
				ScriptActivity.class.getSimpleName());
		lookDataList = ProjectManager.INSTANCE.getCurrentSprite().getLookDataList();
		int numberOfLookDatasAfterReturning = lookDataList.size();
		assertEquals("wrong size of lookdatalist", numberOfLookDatasBeforeIntent, numberOfLookDatasAfterReturning);
	}

	public void testEditImagePaintroidToSomethingWhichIsAlreadyUsed() throws IOException {
		int numberOfLookDatas = lookDataList.size();
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);
		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ImageFile);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", numberOfLookDatas, newNumberOfLookDatas);
		assertEquals("too many references for checksum", 0,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
		assertEquals("not the right number of checksum references", 2, projectManager.getFileChecksumContainer()
				.getUsage(md5PaintroidImageFile));
	}

	public void testEditImageWhichIsAlreadyUsed() {
		File tempImageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				"catroid_sunglasses2.png", RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		LookData lookDataToAdd = new LookData();
		lookDataToAdd.setLookFilename(tempImageFile.getName());
		lookDataToAdd.setLookName("justforthistest");
		lookDataList.add(lookDataToAdd);
		projectManager.getFileChecksumContainer().addChecksum(lookDataToAdd.getChecksum(),
				lookDataToAdd.getAbsolutePath());

		solo.sleep(200);

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);
		//		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertEquals("wrong number of lookdatas", 3, lookDataList.size());
		assertTrue("new added image has been deleted", tempImageFile.exists());
		assertEquals("wrong number of checksum references of sunnglasses picture", 1, projectManager
				.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testLookNames() {
		String buttonCopyLookText = solo.getString(R.string.copy_look);
		solo.clickOnText(buttonCopyLookText);
		solo.scrollToTop();

		String defaultLookName = solo.getString(R.string.default_look_name);
		String expectedLookName = "";

		renameLook(lookName, defaultLookName);
		renameLook("lookNameTest2", defaultLookName);
		expectedLookName = defaultLookName + "1";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(1).getLookName());
		renameLook("lookNametest_", defaultLookName);
		expectedLookName = defaultLookName + "2";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(2).getLookName());

		renameLook(defaultLookName + "1", "a");
		solo.scrollToTop();
		solo.clickOnText(solo.getString(R.string.copy_look));
		renameLook(defaultLookName + "_", defaultLookName);
		expectedLookName = defaultLookName + "1";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(3).getLookName());

		// test that Image from paintroid is correctly renamed
		String fileName = defaultLookName;
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
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(5000);
		expectedLookName = defaultLookName + "3";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(4).getLookName());
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImageFile));

		// test that Image from gallery is correctly renamed
		fileName = defaultLookName;
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
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(5000);
		expectedLookName = defaultLookName + "4";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(5).getLookName());
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImageFile));
	}

	public void testResolutionWhenEditedAndCroppedWithPaintroid() {
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String pathToImageFile = imageFile.getAbsolutePath();
		int[] fileResolutionBeforeCrop = lookData.getResolution();
		int[] displayedResolutionBeforeCrop = getDisplayedResolution(lookData);

		int sampleSize = 2;

		solo.sleep(1000);
		try {
			UiTestUtils.cropImage(pathToImageFile, sampleSize);
		} catch (FileNotFoundException e) {
			fail("Test failed because file was not found");
			e.printStackTrace();
		}

		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		//		goToLooksTab();

		int[] fileResolutionAfterCrop = lookData.getResolution();
		int[] displayedResolutionAfterCrop = getDisplayedResolution(lookData);

		assertTrue("Bitmap resolution in file was not cropped",
				fileResolutionAfterCrop[0] < fileResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < fileResolutionBeforeCrop[1]);
		assertTrue("Image resolution was not updated in look fragment",
				displayedResolutionAfterCrop[0] < displayedResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < displayedResolutionBeforeCrop[1]);
	}

	private int[] getDisplayedResolution(LookData look) {
		TextView resolutionTextView = (TextView) solo.getView(R.id.look_resolution);
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

	private void renameLook(String currentLookName, String newLookName) {
		solo.clickOnText(currentLookName);
		EditText editTextLookName = solo.getEditText(0);
		solo.clearEditText(editTextLookName);
		solo.enterText(editTextLookName, newLookName);
		String buttonOKText = solo.getCurrentActivity().getString(R.string.ok);
		solo.clickOnButton(buttonOKText);
	}

	private LookFragment getLookFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (LookFragment) activity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
	}

	private LookAdapter getLookAdapter() {
		return (LookAdapter) getLookFragment().getListAdapter();
	}

	private void checkVisibilityOfViews(int imageVisibility, int lookNameVisibility, int lookDetailsVisibility,
			int checkBoxVisibility) {
		assertTrue("Look image " + getAssertMessageAffix(imageVisibility), solo.getView(R.id.look_image)
				.getVisibility() == imageVisibility);
		assertTrue("Look name " + getAssertMessageAffix(lookNameVisibility), solo.getView(R.id.look_name)
				.getVisibility() == lookNameVisibility);
		assertTrue("Look details " + getAssertMessageAffix(lookDetailsVisibility), solo.getView(R.id.look_details)
				.getVisibility() == lookDetailsVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility), solo.getView(R.id.look_checkbox)
				.getVisibility() == checkBoxVisibility);
	}

	private String getAssertMessageAffix(int visibility) {
		String assertMessageAffix = "";
		switch (visibility) {
			case View.VISIBLE:
				assertMessageAffix = "not visible";
				break;
			case View.GONE:
				assertMessageAffix = "not gone";
				break;
			default:
				break;
		}
		return assertMessageAffix;
	}
}
