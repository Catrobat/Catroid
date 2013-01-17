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

import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class ProjectActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final String TEST_SPRITE_NAME = "cat";
	private static final String FIRST_TEST_SPRITE_NAME = "testSprite1";
	private static final String SECOND_TEST_SPRITE_NAME = "testSprite2";

	private Solo solo;

	private String rename;
	private String renameDialogTitle;
	private String delete;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private ProjectManager projectManager;
	private List<Sprite> spriteList;

	public ProjectActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();

		projectManager = ProjectManager.getInstance();
		spriteList = projectManager.getCurrentProject().getSpriteList();

		spriteList.add(new Sprite(FIRST_TEST_SPRITE_NAME));
		spriteList.add(new Sprite(SECOND_TEST_SPRITE_NAME));

		solo = new Solo(getInstrumentation(), getActivity());

		rename = solo.getString(R.string.rename);
		renameDialogTitle = solo.getString(R.string.rename_sprite_dialog);
		delete = solo.getString(R.string.delete);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testBackgroundSprite() {
		String sometext = "something" + System.currentTimeMillis();

		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.waitForText(solo.getString(R.string.new_project_dialog_title));

		UiTestUtils.clickEnterClose(solo, 0, sometext);

		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		String spriteBackgroundLabel = solo.getString(R.string.background);
		assertTrue("Wrong name for background sprite!", solo.searchText(spriteBackgroundLabel));
		solo.clickLongOnText(spriteBackgroundLabel);
		assertFalse("Found delete option for background sprite", solo.searchText(solo.getString(R.string.delete)));
	}

	public void testAddNewSprite() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		int spriteToCheckIndex = 2;
		String spriteToCheckName = spriteList.get(spriteToCheckIndex).getName();

		assertEquals("Sprite at index " + spriteToCheckIndex + " is not '" + SECOND_TEST_SPRITE_NAME + "'",
				SECOND_TEST_SPRITE_NAME, spriteToCheckName);
		assertTrue("Sprite is not in current Project", projectManager.spriteExists(spriteToCheckName));

		final String addedSpriteName = "addedTestSprite";
		addNewSprite(addedSpriteName);

		spriteList = projectManager.getCurrentProject().getSpriteList();

		spriteToCheckIndex = 3;

		Sprite spriteToCheck = spriteList.get(spriteToCheckIndex);
		spriteToCheckName = spriteToCheck.getName();

		assertEquals("Sprite at index " + spriteToCheckIndex + " is not '" + addedSpriteName + "'", addedSpriteName,
				spriteToCheckName);
		assertTrue("Sprite is not in current Project", spriteList.contains(spriteToCheck));
		assertTrue("Sprite not shown in List", solo.searchText(spriteToCheckName));
	}

	public void testAddedSpriteVisibleOnLongList() {
		addSprite("dog");
		addSprite("mouse");
		addSprite("bear");
		addSprite("tiger");
		addSprite("lion");
		addSprite("eagle");

		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		assertTrue("Sprite cat is first in list - should be visible on initial start without scrolling",
				solo.searchText("cat", 0, false));

		String addedSpriteName = "addedTestSprite";
		addNewSprite(addedSpriteName);

		solo.waitForText(addedSpriteName, 1, 2000);
		assertTrue("Sprite '" + addedSpriteName + "' was not found - List did not move to last added sprite",
				solo.searchText(addedSpriteName, 0, false));
	}

	public void testOrientation() throws NameNotFoundException {
		/// Method 1: Assert it is currently in portrait mode.
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);
		assertEquals("ProjectActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, solo
				.getCurrentActivity().getResources().getConfiguration().orientation);

		/// Method 2: Retreive info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = solo.getCurrentActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(solo.getCurrentActivity().getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);

		assertEquals(ProjectActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}

	public void testContextMenu() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		// Rename sprite
		final String renamedSpriteName = "renamedTestSpriteName";

		clickOnContextMenuItem(FIRST_TEST_SPRITE_NAME, rename);
		solo.waitForText(solo.getString(R.string.rename_sprite_dialog));
		solo.sleep(50);

		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, renamedSpriteName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		int spriteToRenameIndex = 1;

		Sprite renamedSprite = spriteList.get(spriteToRenameIndex);
		assertEquals("Sprite on position " + spriteToRenameIndex + " wasn't renamed correctly", renamedSpriteName,
				renamedSprite.getName());

		// Delete sprite
		int expectedNumberOfSpritesAfterDelete = spriteList.size() - 1;
		clickOnContextMenuItem(renamedSpriteName, delete);

		// Dialog is handled asynchronously, so we need to wait a while for it to finish
		solo.sleep(300);
		spriteList = projectManager.getCurrentProject().getSpriteList();

		assertEquals("Size of sprite list has not changed accordingly", expectedNumberOfSpritesAfterDelete,
				spriteList.size());
		assertFalse("Sprite is still shown in sprite list", solo.searchText(renamedSpriteName));
		assertFalse("Sprite is still in Project", spriteList.contains(renamedSprite));

		Sprite notDeletedSprite = spriteList.get(1);
		assertEquals("Subsequent sprite was not moved up after predecessor's deletion", SECOND_TEST_SPRITE_NAME,
				notDeletedSprite.getName());
	}

	public void testMainMenuButton() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Main menu is not displayed", MainMenuActivity.class);
	}

	public void testCheckMaxTextLines() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		int expectedLineCount = 1;
		String spriteName = "poor poor poor poor poor poor poor poor me me me me me me";

		addNewSprite(spriteName);

		TextView textView = solo.getText(4);
		assertEquals("linecount is wrong - ellipsize failed", expectedLineCount, textView.getLineCount());
	}

	public void testNewSpriteDialog() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		String addedTestSpriteName = "addedTestSprite";

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		UiTestUtils.clickEnterClose(solo, 0, addedTestSpriteName);
		solo.sleep(200);

		assertTrue("Sprite not successfully added", projectManager.spriteExists(addedTestSpriteName));
	}

	public void testNewSpriteDialogErrorMessages() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		String spriteName = "spriteError";

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		UiTestUtils.clickEnterClose(solo, 0, spriteName);
		solo.sleep(200);
		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName));

		// Add sprite which already exists
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		UiTestUtils.clickEnterClose(solo, 0, spriteName);
		solo.sleep(200);

		String errorMessageText = solo.getString(R.string.spritename_already_exists);
		String buttonCloseText = solo.getString(R.string.close);
		solo.sleep(200);

		assertTrue("ErrorMessage not visible", solo.searchText(errorMessageText));

		solo.clickOnButton(buttonCloseText);
		solo.sleep(200);

		solo.sendKey(Solo.ENTER);
		assertTrue("ErrorMessage not visible", solo.searchText(errorMessageText));
		solo.sleep(200);
		solo.clickOnButton(buttonCloseText);

		// Check if button deactivated when adding sprite without name ""
		UiTestUtils.enterText(solo, 0, "");
		solo.sleep(200);

		String okButtonText = solo.getString(R.string.ok);
		boolean okButtonEnabled = solo.getButton(okButtonText).isEnabled();
		assertFalse("'" + okButtonText + "' button not deactivated", okButtonEnabled);
	}

	public void testRenameSpriteDialog() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		// Rename sprite to name that already exists
		//------------ OK Button:
		String buttonCloseText = solo.getString(R.string.close);
		String errorSpriteAlreadyExists = solo.getString(R.string.spritename_already_exists);
		String dialogRenameSpriteText = solo.getString(R.string.rename_sprite_dialog);

		clickOnContextMenuItem(FIRST_TEST_SPRITE_NAME, rename);

		UiTestUtils.enterText(solo, 0, SECOND_TEST_SPRITE_NAME);
		solo.sendKey(Solo.ENTER);

		solo.sleep(200);
		assertTrue("Error message not visible", solo.searchText(errorSpriteAlreadyExists));
		solo.clickOnButton(buttonCloseText);
		assertTrue("RenameSpriteDialog not visible", solo.searchText(dialogRenameSpriteText));

		//------------ Enter Key:
		solo.clickOnEditText(0);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(errorSpriteAlreadyExists));
		solo.clickOnButton(buttonCloseText);

		// Check if button deactivated when renaming sprite to name ""
		UiTestUtils.enterText(solo, 0, "");
		solo.sleep(200);

		String okButtonText = solo.getString(R.string.ok);
		boolean okButtonEnabled = solo.getButton(okButtonText).isEnabled();
		assertFalse("'" + okButtonText + "' button not deactivated", okButtonEnabled);
	}

	public void testDivider() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		ListView listView = solo.getCurrentListViews().get(0);

		assertTrue("ListView divider should be null", listView.getDivider() == null);
		assertTrue("Listview dividerheight should be 0", listView.getDividerHeight() == 0);

		int currentViewID;
		int pixelColor;
		int colorDivider;

		Bitmap viewBitmap;
		boolean isBackground = true;

		for (View viewToTest : solo.getCurrentViews()) {
			currentViewID = viewToTest.getId();

			if (currentViewID == R.id.sprite_divider) {
				viewToTest.buildDrawingCache();
				viewBitmap = viewToTest.getDrawingCache();

				if (isBackground) {
					pixelColor = viewBitmap.getPixel(1, 3);
					viewToTest.destroyDrawingCache();
					assertTrue("Background divider should have 4px height", viewToTest.getHeight() == 4);
					colorDivider = solo.getCurrentActivity().getResources().getColor(R.color.gray);
					assertEquals("Divider color for background should be gray", pixelColor, colorDivider);
					isBackground = false;
				} else {
					pixelColor = viewBitmap.getPixel(1, 1);
					viewToTest.destroyDrawingCache();
					assertTrue("Normal Sprite divider should have 2px height", viewToTest.getHeight() == 2);
					colorDivider = solo.getCurrentActivity().getResources().getColor(R.color.egg_yellow);
					assertEquals("Divider color for normal sprite should be eggyellow", pixelColor, colorDivider);
				}
			}
		}
	}

	public void testSpriteListDetails() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		int overFlowMenuIndex = 0;

		String showDetailsText = solo.getString(R.string.show_details);
		String hideDetailsText = solo.getString(R.string.hide_details);

		TextView tvScriptCount = ((TextView) solo.getView(R.id.textView_number_of_scripts));
		TextView tvBrickCount = ((TextView) solo.getView(R.id.textView_number_of_bricks));
		TextView tvCostumeCount = ((TextView) solo.getView(R.id.textView_number_of_costumes));
		TextView tvSoundCount = ((TextView) solo.getView(R.id.textView_number_of_sounds));

		// Hide details if shown
		solo.clickOnImageButton(overFlowMenuIndex);
		if (solo.waitForText(hideDetailsText, 0, 200)) {
			solo.clickOnText(hideDetailsText);
			solo.sleep(300);
		} else {
			solo.goBack();
		}

		checkVisibilityOfViews(tvScriptCount, tvBrickCount, tvCostumeCount, tvSoundCount, false);

		solo.clickOnMenuItem(showDetailsText);
		solo.sleep(300);

		checkVisibilityOfViews(tvScriptCount, tvBrickCount, tvCostumeCount, tvSoundCount, true);

		solo.clickOnImageButton(overFlowMenuIndex);
		assertTrue("Hide details should be shown!", solo.waitForText(hideDetailsText));
		solo.goBack();

		solo.clickOnText(TEST_SPRITE_NAME);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.goBack();

		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		checkVisibilityOfViews(tvScriptCount, tvBrickCount, tvCostumeCount, tvSoundCount, true);

		solo.clickOnMenuItem(hideDetailsText);
		solo.sleep(300);

		assertFalse("Scripts should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_scripts), 0, 100, false, true));
		assertFalse("Costumes should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_costumes), 0, 100, false, true));
		assertFalse("Bricks should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_bricks), 0, 100, false, true));
		assertFalse("Sounds should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_sounds), 0, 100, false, true));

		solo.clickOnImageButton(overFlowMenuIndex);
		assertTrue("Show details should be shown!", solo.waitForText(showDetailsText));
		solo.goBack();

		Sprite currentSprite = projectManager.getCurrentSprite();
		int scriptCount = currentSprite.getNumberOfScripts();
		int brickCount = currentSprite.getNumberOfBricks();
		int costumeCount = currentSprite.getCostumeDataList().size();
		int soundCount = currentSprite.getSoundList().size();

		String scriptCountString = tvScriptCount.getText().toString();
		String brickCountString = tvBrickCount.getText().toString();
		String costumeCountString = tvCostumeCount.getText().toString();
		String soundCountString = tvSoundCount.getText().toString();

		int scriptCountActual = Integer.parseInt(scriptCountString.substring(scriptCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of scripts", scriptCount, scriptCountActual);

		int brickCountActual = Integer.parseInt(brickCountString.substring(brickCountString.lastIndexOf(' ') + 1));
		int brickCountExpected = scriptCount + brickCount;
		assertEquals("Displayed the wrong number of bricks", brickCountExpected, brickCountActual);

		int costumeCountActual = Integer
				.parseInt(costumeCountString.substring(costumeCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of costumes", costumeCount, costumeCountActual);

		int soundCountActual = Integer.parseInt(soundCountString.substring(soundCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of sound", soundCount, soundCountActual);
	}

	public void testBottomBarOnActionModes() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		LinearLayout bottomBarLayout = (LinearLayout) solo.getView(R.id.bottom_bar);
		LinearLayout addButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_add);
		LinearLayout playButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_play);

		int timeToWait = 300;
		String addDialogTitle = solo.getString(R.string.new_sprite_dialog_title);

		assertTrue("Add button not clickable", addButton.isClickable());
		assertTrue("Play button not clickable", playButton.isClickable());

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0);
		solo.waitForText(rename, 1, timeToWait, false, true);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(solo.getString(R.string.sprites), 1, timeToWait, false, true);

		assertTrue("Add button not clickable after ActionMode", addButton.isClickable());
		assertTrue("Play button not clickable after ActionMode", playButton.isClickable());

		// Test on delete ActionMode
		UiTestUtils.clickOnActionBar(solo, R.id.delete);
		solo.waitForText(delete, 1, timeToWait, false, true);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(solo.getString(R.string.sprites), 1, timeToWait, false, true);
	}

	public void testDeleteActionModeCheckingAndTitle() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, null, R.id.delete);

		int timeToWaitForTitle = 300;

		String sprite = solo.getString(R.string.sprite);
		String sprites = solo.getString(R.string.sprites);
		String delete = solo.getString(R.string.delete);

		assertFalse("Sprite should not be displayed in title", solo.waitForText(sprite, 3, 300, false, true));

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		int expectedNumberOfSelectedSprites = 1;
		String expectedTitle = delete + " " + expectedNumberOfSelectedSprites + " " + sprite;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSprites = 2;
		expectedTitle = delete + " " + expectedNumberOfSelectedSprites + " " + sprites;

		solo.clickOnCheckBox(1);
		// Check if multiple-selection is possible
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSprites = 1;
		expectedTitle = delete + " " + expectedNumberOfSelectedSprites + " " + sprite;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testDeleteActionModeIfNothingSelected() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		int expectedNumberOfSprites = getCurrentNumberOfSprites();

		UiTestUtils.openActionMode(solo, null, R.id.delete);

		int timeToWait = 300;

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);

		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, timeToWait));

		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);
	}

	public void testDeleteActionModeIfSelectedAndPressingBack() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		int expectedNumberOfSprites = getCurrentNumberOfSprites();

		int timeToWait = 300;

		UiTestUtils.openActionMode(solo, null, R.id.delete);
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, timeToWait));

		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);
	}

	public void testDeleteActionMode() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		Sprite firstSprite = projectManager.getCurrentProject().getSpriteList().get(1);
		Sprite secondSprite = projectManager.getCurrentProject().getSpriteList().get(2);

		int expectedNumberOfSprites = getCurrentNumberOfSprites() - 1;

		UiTestUtils.openActionMode(solo, null, R.id.delete);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, 300));

		checkIfNumberOfSpritesIsEqual(expectedNumberOfSprites);

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();

		assertTrue("Unselected sprite '" + firstSprite.getName() + "' has been deleted!",
				spriteList.contains(firstSprite));

		String deletedSpriteName = secondSprite.getName();

		assertFalse("Selected sprite '" + deletedSpriteName + "' was not deleted!", spriteList.contains(secondSprite));

		assertFalse("Sprite '" + deletedSpriteName + "' has been deleted but is still showing!",
				solo.waitForText(deletedSpriteName, 0, 200, false, false));
	}

	public void testRenameActionModeChecking() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.openActionMode(solo, rename, 0);

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);

		solo.clickOnCheckBox(1);
		// Check if only single-selection is possible
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
	}

	public void testRenameActionModeIfNothingSelected() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.openActionMode(solo, rename, 0);

		int timeToWait = 200;

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);

		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, timeToWait));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, timeToWait));
	}

	public void testRenameActionModeIfSelectedAndPressingBack() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.openActionMode(solo, rename, 0);

		int timeToWait = 200;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, timeToWait));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, timeToWait));
	}

	public void testRenameActionMode() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		String renamedSpriteName = "renamedSprite";

		solo.clickOnMenuItem(rename);
		assertTrue("ActionMode title is not set correctly!", solo.searchText(rename));

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clearEditText(0);
		solo.enterText(0, renamedSpriteName);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(100);

		assertTrue("The second sprite was not renamed!", projectManager.getCurrentProject().getSpriteList().get(2)
				.getName().equalsIgnoreCase(renamedSpriteName));
	}

	public void testOverFlowMenuSettings() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		solo.clickOnMenuItem(solo.getString(R.string.main_menu_settings));
		solo.assertCurrentActivity("Not in SettingsActivity", SettingsActivity.class);
	}

	private void addNewSprite(String spriteName) {
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(solo.getString(R.string.new_sprite_dialog_title));

		EditText addNewSpriteEditText = solo.getEditText(0);

		// Check if hint is set
		assertEquals("No proper hint set", solo.getString(R.string.new_sprite_dialog_default_sprite_name),
				addNewSpriteEditText.getHint());
		assertEquals("There should no text be set", "", addNewSpriteEditText.getText().toString());

		solo.enterText(0, spriteName);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(200);
	}

	private void addSprite(String spriteName) {
		Project project = projectManager.getCurrentProject();

		project.addSprite(new Sprite(spriteName));

		projectManager.saveProject();
		projectManager.setProject(project);
	}

	private void checkVisibilityOfViews(TextView tvScriptCount, TextView tvBrickCount, TextView tvCostumeCount,
			TextView tvSoundCount, boolean visible) {
		int visibility = View.GONE;

		String assertMessageAffix = "not gone";

		if (visible) {
			visibility = View.VISIBLE;
			assertMessageAffix = "not visible";
		}

		assertTrue("Script count " + assertMessageAffix, tvSoundCount.getVisibility() == visibility);
		assertTrue("Brick count " + assertMessageAffix, tvBrickCount.getVisibility() == visibility);
		assertTrue("Costume count " + assertMessageAffix, tvCostumeCount.getVisibility() == visibility);
		assertTrue("Sound count " + assertMessageAffix, tvSoundCount.getVisibility() == visibility);
	}

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(300);
		firstCheckBox = solo.getCurrentCheckBoxes().get(1);
		secondCheckBox = solo.getCurrentCheckBoxes().get(2);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private void checkIfNumberOfSpritesIsEqual(int expectedNumber) {
		assertEquals("Number of sprites is not as expected", expectedNumber, getCurrentNumberOfSprites());
	}

	private int getCurrentNumberOfSprites() {
		return projectManager.getCurrentProject().getSpriteList().size();
	}

	private void clickOnContextMenuItem(String spriteName, String itemName) {
		solo.clickLongOnText(spriteName);
		solo.waitForText(itemName);
		solo.clickOnText(itemName);
	}
}
