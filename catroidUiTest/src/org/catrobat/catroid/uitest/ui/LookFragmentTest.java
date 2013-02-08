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
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.LookAdapter;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class LookFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int RESOURCE_IMAGE = org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses;
	private static final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;
	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;
	private static final int ACTION_MODE_COPY = 0;
	private static final int ACTION_MODE_DELETE = 1;
	private static final int ACTION_MODE_RENAME = 2;

	private static final int TIME_TO_WAIT = 50;

	private static final String FIRST_TEST_LOOK_NAME = "lookNameTest";
	private static final String SECOND_TEST_LOOK_NAME = "lookNameTest2";

	private String copy;
	private String rename;
	private String renameDialogTitle;
	private String delete;
	private String deleteDialogTitle;

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
		lookData.setLookName(FIRST_TEST_LOOK_NAME);
		lookDataList.add(lookData);

		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		lookData = new LookData();
		lookData.setLookFilename(imageFile2.getName());
		lookData.setLookName(SECOND_TEST_LOOK_NAME);
		lookDataList.add(lookData);

		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();

		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoLooksFromMainMenu(solo, true);

		copy = solo.getString(R.string.copy);
		rename = solo.getString(R.string.rename);
		renameDialogTitle = solo.getString(R.string.rename_look_dialog);
		delete = solo.getString(R.string.delete);
		deleteDialogTitle = solo.getString(R.string.delete_look_dialog);

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

		assertFalse("Entry to add look from camera should not be visible", solo.searchText(addLookFromCameraText));
		assertFalse("Entry to add look from gallery should not be visible", solo.searchText(addLookFromGalleryText));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertTrue("Entry to add look from camera not visible", solo.searchText(addLookFromCameraText));
		assertTrue("Entry to add look from gallery not visible", solo.searchText(addLookFromGalleryText));
	}

	public void testCopyLookContextMenu() {
		String testLookName = SECOND_TEST_LOOK_NAME;

		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(testLookName, copy);
		solo.sleep(50);

		int newCount = adapter.getCount();

		if (solo.searchText(testLookName + "_" + solo.getString(R.string.copy_look_addition), 1, true)) {
			assertEquals("Old count was not correct", 2, oldCount);
			assertEquals("New count is not correct - copy should be added", 3, newCount);
			assertEquals("Count of the lookDataList is not correct", newCount, lookDataList.size());
		} else {
			fail("Copy look didn't work");
		}
	}

	public void testDeleteLookContextMenu() {
		Sprite firstSprite = projectManager.getCurrentProject().getSpriteList().get(0);
		LookData lookToDelete = firstSprite.getLookDataList().get(1);

		Log.d("TEST", "Look to delete: " + lookToDelete.getLookName());

		String testLookName = SECOND_TEST_LOOK_NAME;
		assertEquals(testLookName, lookToDelete.getLookName());

		LookAdapter adapter = getLookAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(testLookName, solo.getString(R.string.delete));
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(50);

		int newCount = adapter.getCount();

		assertEquals("Old count was not correct", 2, oldCount);
		assertEquals("New count is not correct - one look should be deleted", 1, newCount);
		assertEquals("Count of the lookDataList is not correct", newCount, lookDataList.size());

		File deletedFile = new File(lookToDelete.getAbsolutePath());
		assertFalse("File should be deleted", deletedFile.exists());
	}

	public void testRenameLookContextMenu() {
		String newLookName = "coSTuMeNamEtESt1";

		renameLook(FIRST_TEST_LOOK_NAME, newLookName);
		solo.sleep(50);

		assertEquals("Look not renamed in LookDataList", newLookName, getLookName(0));
		assertTrue("Look not renamed in actual view", solo.searchText(newLookName));
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

		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		boolean isInLookDataList = false;
		for (LookData lookData : lookDataList) {
			if (lookData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataList);
	}

	public void testGetImageFromGalleryNullData() {
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
		solo.assertCurrentActivity("Should be in ScriptActivity", ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int numberOfLookDatasAfterReturning = lookDataList.size();
		assertEquals("Wrong size of lookDataList", numberOfLookDatasBeforeIntent, numberOfLookDatasAfterReturning);
	}

	public void testGetImageFromPaintroid() {
		String md5ChecksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

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
				projectManager.getFileChecksumContainer().containsChecksum(md5ChecksumPaintroidImageFile));

		lookDataList = projectManager.getCurrentSprite().getLookDataList();

		boolean isInLookDataList = false;
		for (LookData lookData : lookDataList) {
			if (lookData.getChecksum().equalsIgnoreCase(md5ChecksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataList);
	}

	public void testGetImageFromPaintroidNoPath() {
		String md5ChecksumImageFileBeforeIntent = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		LookData lookData = lookDataList.get(0);
		String md5ChecksumImageFileAfterIntent = Utils.md5Checksum(new File(lookData.getAbsolutePath()));

		assertEquals("Wrong size of lookDataList", 2, lookDataList.size());
		assertEquals("Picture changed", md5ChecksumImageFileBeforeIntent, md5ChecksumImageFileAfterIntent);
	}

	public void testEditImageWithPaintroid() {
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String md5ChecksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);

		solo.sleep(5000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(lookData.getAbsolutePath())),
				md5ChecksumPaintroidImageFile);

		boolean isInLookDataListPaintroidImage = false;
		boolean isInLookDataListSunnglasses = false;
		for (LookData lookDatas : lookDataList) {
			if (lookDatas.getChecksum().equalsIgnoreCase(md5ChecksumPaintroidImageFile)) {
				isInLookDataListPaintroidImage = true;
			}
			if (lookDatas.getChecksum().equalsIgnoreCase(md5ChecksumImageFile)) {
				isInLookDataListSunnglasses = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataListPaintroidImage);
		assertFalse("File not deleted from LookDataList", isInLookDataListSunnglasses);
	}

	public void testEditImageWithPaintroidNoChanges() {
		int oldNumberOfLookDatas = lookDataList.size();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);

		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		assertEquals("Picture did change, although it shouldn't change",
				Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ChecksumImageFile);

		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("Size of lookDataList has changed", oldNumberOfLookDatas, newNumberOfLookDatas);
		assertEquals("Too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
	}

	public void testEditImageWithPaintroidNoPath() {
		int oldNumberOfLookDatas = lookDataList.size();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);

		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		assertEquals("Picture changed, although it shouldn't change",
				Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ChecksumImageFile);

		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", oldNumberOfLookDatas, newNumberOfLookDatas);
		assertEquals("Too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
	}

	public void testEditImageWithPaintroidToSomethingAlreadyUsed() throws IOException {
		int oldNumberOfLookDatas = lookDataList.size();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);
		String md5ChecksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		lookData = lookDataList.get(0);

		int newNumberOfLookDatas = lookDataList.size();

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(lookData.getAbsolutePath())),
				md5ChecksumImageFile);
		assertEquals("LookData was added, although this shouldn't be possible", oldNumberOfLookDatas,
				newNumberOfLookDatas);
		assertEquals("Too many references for checksum", 0,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
		assertEquals("Incorrect number of checksum references", 2,
				projectManager.getFileChecksumContainer().getUsage(md5ChecksumPaintroidImageFile));
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
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		assertEquals("Wrong number of lookDatas", 3, lookDataList.size());
		assertTrue("New added image has been deleted", tempImageFile.exists());
		assertEquals("Wrong number of checksum references of sunnglasses picture", 1, projectManager
				.getFileChecksumContainer().getUsage(md5ChecksumImageFile));
	}

	public void testEqualLookNames() {
		final String assertMessageText = "Look not renamed correctly";

		String defaultLookName = solo.getString(R.string.default_look_name);
		String newLookName = defaultLookName;
		String copyAdditionString = solo.getString(R.string.copy_look_addition);

		clickOnContextMenuItem(FIRST_TEST_LOOK_NAME, copy);

		renameLook(FIRST_TEST_LOOK_NAME, defaultLookName);
		renameLook(SECOND_TEST_LOOK_NAME, defaultLookName);

		String expectedLookName = defaultLookName + "1";
		assertEquals(assertMessageText, expectedLookName, getLookName(1));

		String copiedLookName = FIRST_TEST_LOOK_NAME + "_" + copyAdditionString;
		renameLook(copiedLookName, defaultLookName);

		expectedLookName = defaultLookName + "2";
		assertEquals(assertMessageText, expectedLookName, getLookName(2));

		expectedLookName = defaultLookName + "1";
		newLookName = "x";
		renameLook(expectedLookName, newLookName);

		solo.scrollToTop();
		clickOnContextMenuItem(newLookName, copy);

		copiedLookName = newLookName + "_" + copyAdditionString;
		renameLook(copiedLookName, defaultLookName);

		assertEquals(assertMessageText, expectedLookName, getLookName(3));

		// Test that Image from Paintroid is correctly renamed
		String fileName = defaultLookName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE2, getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image was not created");
		}
		String md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(5000);

		expectedLookName = defaultLookName + "3";
		assertEquals(assertMessageText, expectedLookName, getLookName(4));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(md5ChecksumImageFile));

		// Test that Image from gallery is correctly renamed
		fileName = defaultLookName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE, getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image was not created");
		}
		md5ChecksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", imageFile.getAbsolutePath());
		intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(5000);

		expectedLookName = defaultLookName + "4";
		assertEquals(assertMessageText, expectedLookName, getLookName(5));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(md5ChecksumImageFile));
	}

	public void testBottomBarAndContextMenuOnActionModes() {
		LinearLayout bottomBarLayout = (LinearLayout) solo.getView(R.id.bottom_bar);
		LinearLayout addButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_add);
		LinearLayout playButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_play);

		int timeToWait = 300;
		String addDialogTitle = solo.getString(R.string.new_look_dialog_title);
		String lookSpinnerItemText = solo.getString(R.string.looks);

		assertTrue("Add button not clickable", addButton.isClickable());
		assertTrue("Play button not clickable", playButton.isClickable());

		checkIfContextMenuAppears(true, ACTION_MODE_RENAME);

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0);
		solo.waitForText(rename, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, ACTION_MODE_RENAME);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(lookSpinnerItemText, 1, timeToWait, false, true);

		checkIfContextMenuAppears(true, ACTION_MODE_RENAME);

		assertTrue("Add button not clickable after ActionMode", addButton.isClickable());
		assertTrue("Play button not clickable after ActionMode", playButton.isClickable());

		// Test on delete ActionMode
		UiTestUtils.openActionMode(solo, null, R.id.delete);
		solo.waitForText(delete, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, ACTION_MODE_DELETE);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(lookSpinnerItemText, 1, timeToWait, false, true);

		checkIfContextMenuAppears(true, ACTION_MODE_DELETE);

		assertTrue("Add button not clickable after ActionMode", addButton.isClickable());
		assertTrue("Play button not clickable after ActionMode", playButton.isClickable());

		// Test on copy ActionMode
		UiTestUtils.openActionMode(solo, null, R.id.copy);
		solo.waitForText(copy, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, ACTION_MODE_COPY);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(lookSpinnerItemText, 1, timeToWait, false, true);

		checkIfContextMenuAppears(true, ACTION_MODE_COPY);
	}

	public void testResolutionWhenCroppedWithPaintroid() {
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(200);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

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
		UiTestUtils.getIntoLooksFromMainMenu(solo, true);

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

		int startIndex = "Resolution: ".length();

		// Resolution string has form "Resolution: width x height"
		int dividingPosition = resolutionString.indexOf(' ', startIndex);

		String widthString = resolutionString.substring(startIndex, dividingPosition);
		String heightString = resolutionString.substring(dividingPosition + 3, resolutionString.length());

		int width = Integer.parseInt(widthString);
		int heigth = Integer.parseInt(heightString);

		int[] resolution = new int[2];
		resolution[0] = width;
		resolution[1] = heigth;

		return resolution;
	}

	private void renameLook(String lookToRename, String newLookName) {
		clickOnContextMenuItem(lookToRename, solo.getString(R.string.rename));
		assertTrue("Wrong title of dialog", solo.searchText(renameDialogTitle));
		assertTrue("No EditText with actual look name", solo.searchEditText(lookToRename));

		UiTestUtils.enterText(solo, 0, newLookName);
		solo.sendKey(Solo.ENTER);
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

	private void clickOnContextMenuItem(String lookName, String menuItemName) {
		solo.clickLongOnText(lookName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	private String getLookName(int lookIndex) {
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		return lookDataList.get(lookIndex).getLookName();
	}

	private void checkIfContextMenuAppears(boolean contextMenuShouldAppear, int actionModeType) {
		solo.clickLongOnText(FIRST_TEST_LOOK_NAME);

		int timeToWait = 200;
		String assertMessageAffix = "";

		if (contextMenuShouldAppear) {
			assertMessageAffix = "should appear";

			assertTrue("Context menu with title '" + FIRST_TEST_LOOK_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_LOOK_NAME, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + copy + "' " + assertMessageAffix,
					solo.waitForText(copy, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, 1, timeToWait, false, true));

			solo.goBack();
		} else {
			assertMessageAffix = "should not appear";

			int minimumMatchesCopy = 1;
			int minimumMatchesDelete = 1;
			int minimumMatchesRename = 1;

			switch (actionModeType) {
				case ACTION_MODE_COPY:
					minimumMatchesCopy = 2;
					break;
				case ACTION_MODE_DELETE:
					minimumMatchesDelete = 2;
					break;
				case ACTION_MODE_RENAME:
					minimumMatchesRename = 2;
					break;
			}
			assertFalse("Context menu with title '" + FIRST_TEST_LOOK_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_LOOK_NAME, 3, timeToWait, false, true));
			assertFalse("Context menu item '" + copy + "' " + assertMessageAffix,
					solo.waitForText(copy, minimumMatchesCopy, timeToWait, false, true));
			assertFalse("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, minimumMatchesDelete, timeToWait, false, true));
			assertFalse("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, minimumMatchesRename, timeToWait, false, true));
		}
	}
}
