/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.uitest.ui.fragment;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.NfcTagAdapter;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilUi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NfcTagFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final int TIME_TO_WAIT = 100;

	private static final String FIRST_TEST_TAG_NAME = "tagNameTest";
	private static final String FIRST_TEST_TAG_ID = "111";

	private static final String SECOND_TEST_TAG_NAME = "tagNameTest2";
	private static final String SECOND_TEST_TAG_ID = "222";

	private static final String THIRD_TEST_TAG_NAME = "tagNameTest3";
	private static final String THIRD_TEST_TAG_ID = "333";

	private String copy;
	private String rename;
	private String renameDialogTitle;
	private String delete;
	private String deleteDialogTitle;

	private NfcTagData tagData;
	private NfcTagData tagData2;
	private NfcTagData tagData3;

	private List<NfcTagData> tagDataList;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private ProjectManager projectManager;

	public NfcTagFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.enableNfcBricks(getActivity().getApplicationContext());
		UiTestUtils.createTestProject();
		UiTestUtils.prepareStageForTest();

		projectManager = ProjectManager.getInstance();
		tagDataList = projectManager.getCurrentSprite().getNfcTagList();

		tagData = new NfcTagData();
		tagData.setNfcTagName(FIRST_TEST_TAG_NAME);
		tagData.setNfcTagUid(FIRST_TEST_TAG_ID);
		tagDataList.add(tagData);

		tagData2 = new NfcTagData();
		tagData2.setNfcTagName(SECOND_TEST_TAG_NAME);
		tagData2.setNfcTagUid(SECOND_TEST_TAG_ID);
		tagDataList.add(tagData2);

		tagData3 = new NfcTagData();
		tagData3.setNfcTagName(THIRD_TEST_TAG_NAME);
		tagData3.setNfcTagUid(THIRD_TEST_TAG_ID);
		tagDataList.add(tagData3);

		UtilUi.updateScreenWidthAndHeight(solo.getCurrentActivity());
		projectManager.getCurrentProject().getXmlHeader().virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		projectManager.getCurrentProject().getXmlHeader().virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;

		UiTestUtils.getIntoNfcTagsFromMainMenu(solo, true);

		copy = solo.getString(R.string.copy);
		rename = solo.getString(R.string.rename);
		renameDialogTitle = solo.getString(R.string.rename_nfctag_dialog);
		delete = solo.getString(R.string.delete);
		deleteDialogTitle = solo.getString(R.string.dialog_confirm_delete_nfctag_title);

		if (getNfcTagAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	public void testScanTag() {
		NfcTagAdapter adapter = getNfcTagAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		UiTestUtils.fakeNfcTag(solo, "1234", null, null);

		solo.sleep(500);

		int newCount = adapter.getCount();

		assertEquals("Tag not added!", oldCount + 1, newCount);
		assertEquals("Tag added but not visible!", solo.searchText(solo.getString(R.string.default_tag_name), 1), true);
	}

	public void testInitialLayout() {
		assertFalse("Initially showing details", getNfcTagAdapter().getShowDetails());
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testEmptyView() {
		TextView emptyViewHeading = (TextView) solo.getCurrentActivity().findViewById(R.id.fragment_nfctags_text_heading);
		TextView emptyViewDescription = (TextView) solo.getCurrentActivity().findViewById(
				R.id.fragment_nfctags_text_description);

		// The Views are gone, we can still make assumptions about them
		assertEquals("Empty View heading is not correct", solo.getString(R.string.nfctags), emptyViewHeading
				.getText().toString());
		assertEquals("Empty View description is not correct",
				solo.getString(R.string.fragment_nfctag_text_description), emptyViewDescription.getText()
						.toString());

		assertEquals("Empty View shown although there are items in the list!", View.GONE,
				solo.getView(android.R.id.empty).getVisibility());

		projectManager.addSprite(new SingleSprite("test"));
		solo.goBack();
		solo.goBack();
		solo.clickInList(2);
		solo.clickOnText(solo.getString(R.string.nfctag));
		solo.sleep(400);

		emptyViewHeading = (TextView) solo.getCurrentActivity().findViewById(R.id.fragment_nfctags_text_heading);
		emptyViewDescription = (TextView) solo.getCurrentActivity().findViewById(R.id.fragment_nfctags_text_description);

		assertEquals("Empty View heading is not correct", solo.getString(R.string.nfctags), emptyViewHeading.getText()
				.toString());
		assertEquals("Empty View description is not correct", solo.getString(R.string.fragment_nfctag_text_description),
				emptyViewDescription.getText().toString());

		assertEquals("Empty View not shown although there are items in the list!", View.VISIBLE,
				solo.getView(android.R.id.empty).getVisibility());
	}

	public void testCopyTagContextMenu() {
		String testTagName = SECOND_TEST_TAG_NAME;

		NfcTagAdapter adapter = getNfcTagAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(testTagName, copy, R.string.copy);
		solo.sleep(300);

		int newCount = adapter.getCount();

		if (solo.searchText(testTagName + "1", 1, true)) {
			assertEquals("Old count was not correct", 3, oldCount);
			assertEquals("New count is not correct - copy should be added", 4, newCount);
			assertEquals("Count of the tagDataList is not correct", newCount, tagDataList.size());
		} else {
			fail("Copy tag didn't work");
		}
	}

	public void testDeleteTagContextMenu() {
		Sprite firstSprite = projectManager.getCurrentProject().getDefaultScene().getSpriteList().get(0);
		NfcTagData tagToDelete = firstSprite.getNfcTagList().get(1);

		Log.d("TEST", "Tag to delete: " + tagToDelete.getNfcTagName());

		String testTagName = SECOND_TEST_TAG_NAME;
		assertEquals("The two names should be equal", testTagName, tagToDelete.getNfcTagName());

		NfcTagAdapter adapter = getNfcTagAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(testTagName, solo.getString(R.string.delete), R.string.delete);
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(50);

		int newCount = adapter.getCount();

		assertEquals("Old count was not correct", 3, oldCount);
		assertEquals("New count is not correct - one tag should be deleted", 2, newCount);
		assertEquals("Count of the tagDataList is not correct", newCount, tagDataList.size());
	}

	public void testRenameTagContextMenu() {
		String newTagName = "taGNamEtESt1";

		renameTag(FIRST_TEST_TAG_NAME, newTagName);
		solo.sleep(50);

		assertEquals("Tag not renamed in tagDataList", newTagName, getTagName(0));
		assertTrue("Tag not renamed in actual view", solo.searchText(newTagName));
	}

	public void testShowAndHideDetails() {
		int timeToWait = 300;

		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.nfctags));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testEqualTagNames() {
		final String assertMessageText = "Tag not renamed correctly";

		String defaultTagName = solo.getString(R.string.default_tag_name);
		String newTagName = defaultTagName;
		String copyAdditionString = "1";

		clickOnContextMenuItem(FIRST_TEST_TAG_NAME, copy, R.string.copy);

		renameTag(FIRST_TEST_TAG_NAME, defaultTagName);
		renameTag(SECOND_TEST_TAG_NAME, defaultTagName);

		solo.sleep(50);

		String expectedTagName = defaultTagName + copyAdditionString;
		assertEquals(assertMessageText, expectedTagName, getTagName(1));

		String copiedTagName = FIRST_TEST_TAG_NAME + "1";
		renameTag(copiedTagName, defaultTagName);

		solo.sleep(50);

		expectedTagName = defaultTagName + "2";
		assertEquals(assertMessageText, expectedTagName, getTagName(3));

		expectedTagName = defaultTagName + "1";
		newTagName = "x";
		renameTag(expectedTagName, newTagName);

		solo.scrollToTop();
		clickOnContextMenuItem(newTagName, copy, R.string.copy);

		copiedTagName = newTagName + "1";
		renameTag(copiedTagName, defaultTagName);

		solo.sleep(50);

		assertEquals(assertMessageText, expectedTagName, getTagName(4));

		expectedTagName = THIRD_TEST_TAG_NAME;
		assertEquals(assertMessageText, expectedTagName, getTagName(2));
	}

	public void testBottomBarAndContextMenuOnActionModes() {
		if (!getNfcTagAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.show_details), true);
			solo.sleep(TIME_TO_WAIT);
		}

		LinearLayout bottomBarLayout = (LinearLayout) solo.getView(R.id.bottom_bar);
		ImageButton playButton = (ImageButton) bottomBarLayout.findViewById(R.id.button_play);

		int timeToWait = 300;
		String tagIdPrefixText = solo.getString(R.string.uid);

		assertTrue("ID prefix not visible", solo.searchText(tagIdPrefixText, true));

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(rename, 1, timeToWait, false, true);

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		assertTrue("ID prefix not visible after ActionMode", solo.searchText(tagIdPrefixText, true));

		// Test on delete ActionMode
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(delete, 1, timeToWait, false, true);

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		assertTrue("ID prefix not visible after ActionMode", solo.searchText(tagIdPrefixText, true));

		// Test on copy ActionMode
		UiTestUtils.openActionMode(solo, copy, R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(copy, 1, timeToWait, false, true);

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		assertTrue("ID prefix not visible after ActionMode", solo.searchText(tagIdPrefixText, true));
	}

	public void testRenameActionModeChecking() {
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		UiTestUtils.openActionMode(solo, rename, 0);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);

		// Check if only single-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
	}

	public void testRenameActionModeIfNothingSelected() {
		UiTestUtils.openActionMode(solo, rename, 0);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openActionMode(solo, rename, 0);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeEqualTagNames() {
		UiTestUtils.openActionMode(solo, rename, 0);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int checkboxIndex = 1;

		// Rename second look to the name of the first
		String newTagName = FIRST_TEST_TAG_NAME;

		solo.clickOnCheckBox(checkboxIndex);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForText(renameDialogTitle);
		assertTrue("Rename dialog didn't show up", solo.searchText(renameDialogTitle, true));
		assertTrue("No EditText with actual tag name", solo.searchEditText(SECOND_TEST_TAG_NAME));

		UiTestUtils.enterText(solo, 0, newTagName);
		solo.sendKey(Solo.ENTER);

		// If an already existing name was entered a counter should be appended
		String expectedNewTagName = newTagName + "1";
		solo.sleep(300);
		tagDataList = projectManager.getCurrentSprite().getNfcTagList();
		assertEquals("Tag is not correctly renamed in lookDataList (1 should be appended)", expectedNewTagName,
				tagDataList.get(checkboxIndex).getNfcTagName());
		assertTrue("Tag not renamed in actual view", solo.searchText(expectedNewTagName, true));
	}

	public void testDeleteActionModeCheckingAndTitle() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String tag = solo.getString(R.string.nfctag);
		String tags = solo.getString(R.string.nfctags);

		assertFalse("Tag should not be displayed in title", solo.waitForText(tag, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		int expectedNumberOfSelectedTags = 1;
		String expectedTitle = delete + " " + expectedNumberOfSelectedTags + " " + tag;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedTags = 2;
		expectedTitle = delete + " " + expectedNumberOfSelectedTags + " " + tags;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedTags = 1;
		expectedTitle = delete + " " + expectedNumberOfSelectedTags + " " + tag;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testDeleteActionModeIfNothingSelected() {
		int expectedNumberOfTags = tagDataList.size();

		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if delete ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfTagsIsEqual(expectedNumberOfTags);
	}

	public void testDeleteActionModeIfSomethingSelectedAndPressingBack() {
		int expectedNumberOfTags = tagDataList.size();

		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		// Check if delete ActionMode disappears if back was pressed
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfTagsIsEqual(expectedNumberOfTags);
	}

	public void testDeleteActionMode() {
		int currentNumberOfTags = tagDataList.size();
		int expectedNumberOfTags = currentNumberOfTags - 1;

		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfTagsIsEqual(expectedNumberOfTags);

		assertTrue("Unselected tag '" + FIRST_TEST_TAG_NAME + "' has been deleted!", tagDataList.contains(tagData));

		assertFalse("Selected tag '" + SECOND_TEST_TAG_NAME + "' was not deleted!", tagDataList.contains(tagData2));

		assertFalse("Tag '" + SECOND_TEST_TAG_NAME + "' has been deleted but is still showing!",
				solo.waitForText(SECOND_TEST_TAG_NAME, 0, 200, false, false));
	}

	public void testDeleteSelectAll() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.waitForActivity("ScriptActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		checkAllCheckboxes();
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		String yes = solo.getString(R.string.yes);
		solo.waitForText(yes);
		solo.clickOnText(yes);

		assertFalse("Tag was not Deleted!", solo.waitForText(FIRST_TEST_TAG_NAME, 1, 200));
		assertFalse("Tag was not Deleted!", solo.waitForText(SECOND_TEST_TAG_NAME, 1, 200));
	}

	public void testItemClick() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.clickInList(2);
		solo.sleep(TIME_TO_WAIT);

		List<CheckBox> checkBoxList = solo.getCurrentViews(CheckBox.class);
		int index = checkBoxList.size() > projectManager.getCurrentSprite().getNfcTagList().size() ? 2 : 1;
		assertTrue("CheckBox not checked", checkBoxList.get(index).isChecked());

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertTrue("default project not visible", solo.searchText(solo.getString(R.string.yes)));
		solo.clickOnButton(solo.getString(R.string.yes));

		assertFalse("Tag not deleted", solo.waitForText(SECOND_TEST_TAG_NAME, 0, 200));
	}

	public void testDeleteAndCopyActionMode() {
		UiTestUtils.openActionMode(solo, copy, R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		solo.sleep(300);
		clickOnContextMenuItem(FIRST_TEST_TAG_NAME, copy, R.string.copy);
		solo.sleep(300);

		tagDataList = projectManager.getCurrentSprite().getNfcTagList();

		int currentNumberOfTags = tagDataList.size();
		assertEquals("Wrong number of tags", 6, currentNumberOfTags);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int[] checkboxIndicesToCheck = { solo.getCurrentViews(CheckBox.class).size() - 1, 0, 2 };
		int expectedNumberOfTags = currentNumberOfTags - checkboxIndicesToCheck.length;

		solo.scrollDown();
		solo.clickOnCheckBox(checkboxIndicesToCheck[0]);
		// Note: We don't actually click the first checkbox on lower resolution devices because
		//       solo won't perform, any sort of scrolling after a checkBox-click at the moment.
		//       But we delete 3 sounds anyways, so the test succeeds.
		solo.scrollToTop();
		solo.clickOnCheckBox(checkboxIndicesToCheck[1]);
		solo.clickOnCheckBox(checkboxIndicesToCheck[2]);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfTagsIsEqual(expectedNumberOfTags);
	}

	public void testCopyActionModeCheckingAndTitle() {
		UiTestUtils.openActionMode(solo, copy, R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String tag = solo.getString(R.string.nfctag);
		String tags = solo.getString(R.string.nfctags);

		assertFalse("Tag should not be displayed in title", solo.waitForText(tag, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		int expectedNumberOfSelectedTags = 1;
		String expectedTitle = copy + " " + expectedNumberOfSelectedTags + " " + tag;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedTags = 2;
		expectedTitle = copy + " " + expectedNumberOfSelectedTags + " " + tags;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedTags = 1;
		expectedTitle = copy + " " + expectedNumberOfSelectedTags + " " + tag;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = copy;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testCopyActionModeIfNothingSelected() {
		int expectedNumberOfTags = tagDataList.size();

		UiTestUtils.openActionMode(solo, copy, R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if copy ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		checkIfNumberOfTagsIsEqual(expectedNumberOfTags);
	}

	public void testCopyActionModeIfSomethingSelectedAndPressingBack() {
		int expectedNumberOfTags = tagDataList.size();

		UiTestUtils.openActionMode(solo, copy, R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		// Check if copy ActionMode disappears if back was pressed
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		checkIfNumberOfTagsIsEqual(expectedNumberOfTags);
	}

	public void testCopyActionMode() {
		int currentNumberOfTags = tagDataList.size();
		int expectedNumberOfTags = currentNumberOfTags + 2;

		String copiedTagAddition = "1";
		solo.sleep(500);

		UiTestUtils.openActionMode(solo, copy, R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(copy, 0, TIME_TO_WAIT));

		solo.sleep(TIME_TO_WAIT);

		checkIfNumberOfTagsIsEqual(expectedNumberOfTags);

		assertTrue("Selected tag '" + FIRST_TEST_TAG_NAME + "' was not copied!",
				solo.searchText(FIRST_TEST_TAG_NAME, 4) && solo.searchText(FIRST_TEST_TAG_NAME + copiedTagAddition));

		assertTrue(
				"Selected tag '" + SECOND_TEST_TAG_NAME + "' was not copied!",
				solo.searchText(SECOND_TEST_TAG_NAME, 2)
						&& solo.searchText(SECOND_TEST_TAG_NAME + copiedTagAddition));
	}

	public void testCopySelectAll() {
		int currentNumberOfTags = tagDataList.size();
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.waitForActivity("ScriptActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		checkAllCheckboxes();
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);

		checkIfNumberOfTagsIsEqual(currentNumberOfTags * 2);
	}

	public void testBottomBarElementsVisibility() {
		assertTrue("BottomBar is not visible", solo.getView(R.id.button_play).getVisibility() == View.VISIBLE);
		assertFalse("Add button is visible", solo.getView(R.id.button_add).getVisibility() == View.VISIBLE);
		assertTrue("Play button is not visible", solo.getView(R.id.button_play).getVisibility() == View.VISIBLE);
		assertFalse("BottomBar separator is visible",
				solo.getView(R.id.bottom_bar_separator).getVisibility() == View.VISIBLE);
	}

	public void testSelectAllActionModeButton() {
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.waitForActivity("ScriptActivity");
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		UiTestUtils.clickOnCheckBox(solo, 1);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.waitForActivity("ScriptActivity");
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		UiTestUtils.clickOnCheckBox(solo, 1);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());
	}

	public void testDragAndDropUp() {
		for (int i = 0; i < 2; i++) {
			addNFCTagWithName("TestNFC" + i);
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.nfctags));

		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(0).getNfcTagName(), FIRST_TEST_TAG_NAME);
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(1).getNfcTagName(), SECOND_TEST_TAG_NAME);
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(2).getNfcTagName(), THIRD_TEST_TAG_NAME);
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(3).getNfcTagName(), "TestNFC0");
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(4).getNfcTagName(), "TestNFC1");

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(1) - 100, 20);

		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(0).getNfcTagName(), FIRST_TEST_TAG_NAME);
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(1).getNfcTagName(), SECOND_TEST_TAG_NAME);
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(2).getNfcTagName(), "TestNFC1");
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(3).getNfcTagName(), THIRD_TEST_TAG_NAME);
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(4).getNfcTagName(), "TestNFC0");
	}

	public void testDragAndDropDown() {
		for (int i = 0; i < 2; i++) {
			addNFCTagWithName("TestNFC" + i);
		}

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.nfctags));

		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(0).getNfcTagName(), FIRST_TEST_TAG_NAME);
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(1).getNfcTagName(), SECOND_TEST_TAG_NAME);
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(2).getNfcTagName(), THIRD_TEST_TAG_NAME);
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(3).getNfcTagName(), "TestNFC0");
		assertEquals("Wrong List before DragAndDropTest", tagDataList.get(4).getNfcTagName(), "TestNFC1");

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(1), 10, yPositionList.get(4) + 100, 20);

		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(0).getNfcTagName(), FIRST_TEST_TAG_NAME);
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(1).getNfcTagName(), THIRD_TEST_TAG_NAME);
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(2).getNfcTagName(), "TestNFC0");
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(3).getNfcTagName(), SECOND_TEST_TAG_NAME);
		assertEquals("Wrong List after DragAndDropTest", tagDataList.get(4).getNfcTagName(), "TestNFC1");
	}

	private void renameTag(String tagToRename, String newTagName) {
		clickOnContextMenuItem(tagToRename, solo.getString(R.string.rename), R.string.rename);
		assertTrue("Wrong title of dialog", solo.searchText(renameDialogTitle));
		assertTrue("No EditText with actual tag name", solo.searchEditText(tagToRename));

		UiTestUtils.enterText(solo, 0, newTagName);
		solo.sendKey(Solo.ENTER);
	}

	private NfcTagFragment getNfcTagFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (NfcTagFragment) activity.getFragment(ScriptActivity.FRAGMENT_NFCTAGS);
	}

	private NfcTagAdapter getNfcTagAdapter() {
		return (NfcTagAdapter) getNfcTagFragment().getListAdapter();
	}

	private void checkVisibilityOfViews(int imageVisibility, int tagNameVisibility, int tagDetailsVisibility, int checkBoxVisibility) {
		assertTrue("Tag image " + getAssertMessageAffix(imageVisibility),
				solo.getView(R.id.fragment_nfctag_item_image_button).getVisibility() == imageVisibility);
		assertTrue("Tag name " + getAssertMessageAffix(tagNameVisibility),
				solo.getView(R.id.fragment_nfctag_item_title_text_view).getVisibility() == tagNameVisibility);
		assertTrue("Tag details " + getAssertMessageAffix(tagDetailsVisibility),
				solo.getView(R.id.fragment_nfctag_item_detail_linear_layout).getVisibility() == tagDetailsVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility),
				solo.getView(R.id.fragment_nfctag_item_checkbox).getVisibility() == checkBoxVisibility);
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

	private void clickOnContextMenuItem(String tagName, String menuItemName, int menuItem) {
		UiTestUtils.openActionMode(solo, menuItemName, menuItem);
		solo.clickOnText(tagName);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(TIME_TO_WAIT);
	}

	private String getTagName(int tagIndex) {
		tagDataList = projectManager.getCurrentSprite().getNfcTagList();
		return tagDataList.get(tagIndex).getNfcTagName();
	}

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(300);
		int startIndex = 0;
		if (solo.getCurrentViews(CheckBox.class).size() > projectManager.getCurrentSprite().getNfcTagList().size()) {
			startIndex = 1;
		}
		firstCheckBox = solo.getCurrentViews(CheckBox.class).get(startIndex);
		secondCheckBox = solo.getCurrentViews(CheckBox.class).get(startIndex + 1);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private void checkAllCheckboxes() {
		boolean skipFirst = solo.getCurrentViews(CheckBox.class).size() > projectManager.getCurrentSprite().getNfcTagList().size();
		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			if (skipFirst) {
				continue;
			}
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
	}

	private void checkIfNumberOfTagsIsEqual(int expectedNumber) {
		tagDataList = projectManager.getCurrentSprite().getNfcTagList();
		assertEquals("Number of looks is not as expected", expectedNumber, tagDataList.size());
	}

	private void addNFCTagWithName(String tagName) {
		NfcTagData tagDataToAdd = tagData.clone();
		tagDataToAdd.setNfcTagName(tagName);
		tagDataList.add(tagDataToAdd);
	}
}
