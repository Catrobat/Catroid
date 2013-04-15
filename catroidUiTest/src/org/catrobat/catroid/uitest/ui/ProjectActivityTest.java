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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class ProjectActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final String TEST_SPRITE_NAME = "cat";
	private static final String FIRST_TEST_SPRITE_NAME = "test1";
	private static final String SECOND_TEST_SPRITE_NAME = "test2";
	private static final String THIRD_TEST_SPRITE_NAME = "test3";
	private static final String FOURTH_TEST_SPRITE_NAME = "test4";

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
		UiTestUtils.prepareStageForTest();

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();

		projectManager = ProjectManager.getInstance();
		spriteList = projectManager.getCurrentProject().getSpriteList();

		spriteList.add(new Sprite(FIRST_TEST_SPRITE_NAME));
		spriteList.add(new Sprite(SECOND_TEST_SPRITE_NAME));
		spriteList.add(new Sprite(THIRD_TEST_SPRITE_NAME));
		spriteList.add(new Sprite(FOURTH_TEST_SPRITE_NAME));

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

	public void testCopySpriteWithUserVariables() {
		Project project = new Project(null, "testProject");

		Sprite firstSprite = new Sprite("firstSprite");
		Sprite secondSprite = new Sprite("Catroid");
		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(secondSprite);

		ProjectManager.getInstance().getCurrentProject().getUserVariables().addSpriteUserVariable("p", 0d);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().addSpriteUserVariable("q", 0d);

		Double setVariable1ToValue = Double.valueOf(3d);
		Double setVariable2ToValue = Double.valueOf(8d);

		SetVariableBrick setVariableBrick1 = new SetVariableBrick(secondSprite, new Formula(setVariable1ToValue),
				ProjectManager.getInstance().getCurrentProject().getUserVariables().getUserVariable("p", secondSprite));

		SetVariableBrick setVariableBrick2 = new SetVariableBrick(secondSprite, new Formula(setVariable2ToValue),
				ProjectManager.getInstance().getCurrentProject().getUserVariables().getUserVariable("q", secondSprite));

		Script startScript1 = new StartScript(secondSprite);
		secondSprite.addScript(startScript1);
		startScript1.addBrick(setVariableBrick1);
		startScript1.addBrick(setVariableBrick2);

		solo.clickOnButton(0);

		solo.sleep(200);
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_catroid_name));
		solo.sleep(200);
		assertEquals("Copy is not in context menu!", true, solo.searchText(getActivity().getString(R.string.copy)));
		solo.clickOnText(getActivity().getString(R.string.copy));
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_catroid_name));
		Sprite copiedSprite = project.getSpriteList().get(2);
		ProjectManager.getInstance().setCurrentSprite(copiedSprite);

		double q = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable("q", copiedSprite).getValue();

		double p = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable("p", copiedSprite).getValue();

		Log.e("CATROID", "q hat den Wert: " + q);
		Log.e("CATROID", "p hat den Wert: " + p);

		assertEquals("The local uservariable q does not exist after copying the sprite!", 0.0, q);
		assertEquals("The local uservariable p does not exist after copying the sprite!", 0.0, p);
	}

	public void testCopySpriteWithNameTaken() {
		try {
			StandardProjectHandler.createAndSaveStandardProject(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Standard Project not created");
		}

		Sprite sprite = new Sprite(solo.getString(R.string.default_project_sprites_catroid_name)
				+ solo.getString(R.string.copy_sprite_name_suffix));

		ProjectManager.getInstance().getCurrentProject().addSprite(sprite);

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.clickOnTextInList(solo, solo.getString(R.string.default_project_name));
		solo.sleep(200);
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_catroid_name));
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.copy));
		solo.sleep(1000);

		assertTrue(
				"Copied Sprite name should have 1 as suffix!",
				solo.searchText((solo.getString(R.string.default_project_sprites_catroid_name)
						+ solo.getString(R.string.copy_sprite_name_suffix) + "1")));
	}

	public void testCopySprite() {
		UiTestUtils.createProjectForCopySprite(UiTestUtils.PROJECTNAME1, getActivity());

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		UiTestUtils.clickOnTextInList(solo, UiTestUtils.PROJECTNAME1);
		solo.sleep(200);
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_catroid_name));
		solo.sleep(200);
		assertEquals("Copy is not in context menu!", true, solo.searchText(getActivity().getString(R.string.copy)));
		solo.clickOnText(getActivity().getString(R.string.copy));
		solo.sleep(1000);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite copiedSprite = ((Sprite) spritesList.getItemAtPosition(3));
		Sprite firstSprite = ((Sprite) spritesList.getItemAtPosition(1));

		checkNumberOfElements(firstSprite, copiedSprite);
		checkSpecialBricks(firstSprite, copiedSprite);
		int brickCounter = checkIds(firstSprite, copiedSprite);

		solo.goBack();
		solo.sleep(500);
		solo.clickLongOnText(solo.getString(R.string.default_project_sprites_catroid_name));
		solo.clickOnText(getActivity().getString(R.string.delete));
		solo.sleep(500);
		solo.sendKey(Solo.ENTER);
		solo.sleep(500);
		solo.clickOnText(solo.getString(R.string.default_project_sprites_catroid_name)
				+ solo.getString(R.string.copy_sprite_name_suffix));
		solo.sleep(500);

		assertEquals("The number of Bricks differs!", ProjectManager.getInstance().getCurrentSprite().getScript(0)
				.getBrickList().size(), brickCounter);
	}

	public void testBackgroundSprite() {
		String sometext = "something" + System.currentTimeMillis();

		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.waitForText(solo.getString(R.string.new_project_dialog_title));

		enterTextAndCloseDialog(sometext);

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

		spriteToCheckIndex = 5;

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
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
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

	public void testRenameSpriteContextMenu() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		final String spriteToRename = "renamedTestSpriteName";

		clickOnContextMenuItem(FIRST_TEST_SPRITE_NAME, rename);
		solo.waitForText(solo.getString(R.string.rename_sprite_dialog));
		solo.sleep(50);

		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, spriteToRename);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		int spriteToRenameIndex = 1;

		Sprite renamedSprite = spriteList.get(spriteToRenameIndex);
		assertEquals("Sprite on position " + spriteToRenameIndex + " wasn't renamed correctly", spriteToRename,
				renamedSprite.getName());
	}

	public void testDeleteSpriteContextMenu() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		final String spriteToDelete = FIRST_TEST_SPRITE_NAME;

		// Delete sprite
		int expectedNumberOfSpritesAfterDelete = spriteList.size() - 1;
		clickOnContextMenuItem(spriteToDelete, delete);

		// Dialog is handled asynchronously, so we need to wait a while for it to finish
		solo.sleep(300);
		spriteList = projectManager.getCurrentProject().getSpriteList();

		assertEquals("Size of sprite list has not changed accordingly", expectedNumberOfSpritesAfterDelete,
				spriteList.size());
		assertFalse("Sprite is still shown in sprite list", solo.searchText(spriteToDelete));
		assertFalse("Sprite is still in Project", projectManager.spriteExists(spriteToDelete));

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

		enterTextAndCloseDialog(addedTestSpriteName);

		assertTrue("Sprite not successfully added", projectManager.spriteExists(addedTestSpriteName));
	}

	public void testNewSpriteDialogErrorMessages() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		String spriteName = "spriteError";

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		enterTextAndCloseDialog(spriteName);
		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName));

		// Add sprite which already exists
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		enterTextAndCloseDialog(spriteName);

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

		//Check if button deactivated when adding sprite without name ""
		UiTestUtils.enterText(solo, 0, "");
		solo.sleep(200);

		String okButtonText = solo.getString(R.string.ok);
		boolean okButtonEnabled = solo.getButton(okButtonText).isEnabled();
		assertFalse("'" + okButtonText + "' button not deactivated", okButtonEnabled);

		// Test to add sprite without name ("") with ENTER key
		solo.clickOnEditText(0);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		assertTrue("ErrorMessage not visible", solo.searchText(solo.getString(R.string.spritename_invalid)));
		solo.clickOnButton(buttonCloseText);
		solo.sleep(200);
		assertTrue("not in NewSpriteDialog", solo.searchText(solo.getString(R.string.new_sprite_dialog_title)));
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

		// Test renaming sprite to ("") with ENTER key
		solo.clickOnEditText(0);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		assertTrue("ErrorMessage not visible", solo.searchText(solo.getString(R.string.spritename_invalid)));
		solo.clickOnButton(buttonCloseText);
		solo.sleep(200);
		assertTrue("not in NewSpriteDialog", solo.searchText(dialogRenameSpriteText));
	}

	public void testHeadlinesInList() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		ListView listView = solo.getCurrentListViews().get(0);

		View listItemView = listView.getAdapter().getView(0, null, null);

		View backgroundHeadline = listItemView.findViewById(R.id.spritelist_background_headline);

		assertEquals("Background headline should be visible above background sprite!",
				backgroundHeadline.getVisibility(), View.VISIBLE);

		View objectsHeadline = listItemView.findViewById(R.id.spritelist_objects_headline);

		assertEquals("Objects headline should be visible under background sprite!", objectsHeadline.getVisibility(),
				View.VISIBLE);

		listItemView = listView.getAdapter().getView(1, null, null);

		backgroundHeadline = listItemView.findViewById(R.id.spritelist_background_headline);

		assertEquals("Background headline should not be visible for sprite " + FIRST_TEST_SPRITE_NAME + "!",
				backgroundHeadline.getVisibility(), View.GONE);

		objectsHeadline = listItemView.findViewById(R.id.spritelist_objects_headline);

		assertEquals("Objects headline should not be visible for sprite " + FIRST_TEST_SPRITE_NAME + "!",
				objectsHeadline.getVisibility(), View.GONE);
	}

	public void testClickOnHeadlines() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		String backgroundHeadline = solo.getString(R.string.spritelist_background_headline);
		solo.clickOnText(backgroundHeadline);
		solo.assertCurrentActivity("Click on background headline switched activity!", ProjectActivity.class);

		String objectsHeadline = solo.getString(R.string.spritelist_objects_headline);
		solo.clickOnText(objectsHeadline);
		solo.assertCurrentActivity("Click on objects headline switched activity!", ProjectActivity.class);
	}

	public void testSpriteListDetails() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		String showDetailsText = solo.getString(R.string.show_details);
		String hideDetailsText = solo.getString(R.string.hide_details);

		TextView tvScriptCount = ((TextView) solo.getView(R.id.textView_number_of_scripts));
		TextView tvBrickCount = ((TextView) solo.getView(R.id.textView_number_of_bricks));
		TextView tvLookCount = ((TextView) solo.getView(R.id.textView_number_of_looks));
		TextView tvSoundCount = ((TextView) solo.getView(R.id.textView_number_of_sounds));

		// Hide details if shown
		UiTestUtils.openOptionsMenu(solo);

		if (solo.waitForText(hideDetailsText, 0, 300)) {
			solo.clickOnText(hideDetailsText);
			solo.sleep(300);
		} else {
			solo.goBack();
		}

		checkVisibilityOfViews(tvScriptCount, tvBrickCount, tvLookCount, tvSoundCount, false);

		solo.clickOnMenuItem(showDetailsText);
		solo.sleep(300);

		checkVisibilityOfViews(tvScriptCount, tvBrickCount, tvLookCount, tvSoundCount, true);

		UiTestUtils.openOptionsMenu(solo);
		assertTrue("Hide details should be shown!", solo.waitForText(hideDetailsText));
		solo.goBack();

		solo.clickOnText(TEST_SPRITE_NAME);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.goBack();

		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_sprites_list);

		checkVisibilityOfViews(tvScriptCount, tvBrickCount, tvLookCount, tvSoundCount, true);

		solo.clickOnMenuItem(hideDetailsText);
		solo.sleep(300);

		assertFalse("Scripts should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_scripts), 0, 100, false, true));
		assertFalse("Looks should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_looks), 0, 100, false, true));
		assertFalse("Bricks should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_bricks), 0, 100, false, true));
		assertFalse("Sounds should be hidden",
				solo.waitForText(solo.getString(R.string.number_of_sounds), 0, 100, false, true));

		UiTestUtils.openOptionsMenu(solo);
		assertTrue("Show details should be shown!", solo.waitForText(showDetailsText));
		solo.goBack();

		Sprite currentSprite = projectManager.getCurrentSprite();
		int scriptCount = currentSprite.getNumberOfScripts();
		int brickCount = currentSprite.getNumberOfBricks();
		int lookCount = currentSprite.getLookDataList().size();
		int soundCount = currentSprite.getSoundList().size();

		String scriptCountString = tvScriptCount.getText().toString();
		String brickCountString = tvBrickCount.getText().toString();
		String lookCountString = tvLookCount.getText().toString();
		String soundCountString = tvSoundCount.getText().toString();

		int scriptCountActual = Integer.parseInt(scriptCountString.substring(scriptCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of scripts", scriptCount, scriptCountActual);

		int brickCountActual = Integer.parseInt(brickCountString.substring(brickCountString.lastIndexOf(' ') + 1));
		int brickCountExpected = scriptCount + brickCount;
		assertEquals("Displayed the wrong number of bricks", brickCountExpected, brickCountActual);

		int lookCountActual = Integer.parseInt(lookCountString.substring(lookCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of looks", lookCount, lookCountActual);

		int soundCountActual = Integer.parseInt(soundCountString.substring(soundCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of sound", soundCount, soundCountActual);
	}

	public void testBottomBarAndContextMenuOnActionModes() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		LinearLayout bottomBarLayout = (LinearLayout) solo.getView(R.id.bottom_bar);
		LinearLayout addButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_add);
		LinearLayout playButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_play);

		int timeToWait = 300;
		String addDialogTitle = solo.getString(R.string.new_sprite_dialog_title);

		assertTrue("Add button not clickable", addButton.isClickable());
		assertTrue("Play button not clickable", playButton.isClickable());

		checkIfContextMenuAppears(true, false);

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0);
		solo.waitForText(rename, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, false);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(solo.getString(R.string.sprites), 1, timeToWait, false, true);

		checkIfContextMenuAppears(true, false);

		assertTrue("Add button not clickable after ActionMode", addButton.isClickable());
		assertTrue("Play button not clickable after ActionMode", playButton.isClickable());

		// Test on delete ActionMode
		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.waitForText(delete, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, true);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(solo.getString(R.string.sprites), 1, timeToWait, false, true);

		checkIfContextMenuAppears(true, true);
	}

	public void testDeleteActionModeCheckingAndTitle() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);

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

		UiTestUtils.openActionMode(solo, delete, R.id.delete);

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

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
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

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
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

	public void testDeleteMultipleSprites() {
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		solo.scrollListToBottom(0);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);
		solo.clickOnCheckBox(3);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, 300));

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();

		assertEquals("First sprite should be " + TEST_SPRITE_NAME, spriteList.get(0).getName(), TEST_SPRITE_NAME);
		assertEquals("Second sprite should be " + FIRST_TEST_SPRITE_NAME, spriteList.get(1).getName(),
				FIRST_TEST_SPRITE_NAME);

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

	private void checkVisibilityOfViews(TextView tvScriptCount, TextView tvBrickCount, TextView tvLookCount,
			TextView tvSoundCount, boolean visible) {
		int visibility = View.GONE;

		String assertMessageAffix = "not gone";

		if (visible) {
			visibility = View.VISIBLE;
			assertMessageAffix = "not visible";
		}

		assertTrue("Script count " + assertMessageAffix, tvSoundCount.getVisibility() == visibility);
		assertTrue("Brick count " + assertMessageAffix, tvBrickCount.getVisibility() == visibility);
		assertTrue("Look count " + assertMessageAffix, tvLookCount.getVisibility() == visibility);
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

	private void checkIfContextMenuAppears(boolean contextMenuShouldAppear, boolean isDeleteActionMode) {
		solo.clickLongOnText(FIRST_TEST_SPRITE_NAME);

		int timeToWait = 200;
		String assertMessageAffix = "";

		if (contextMenuShouldAppear) {
			assertMessageAffix = "should appear";

			assertTrue("Context menu with title '" + FIRST_TEST_SPRITE_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_SPRITE_NAME, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, 1, timeToWait, false, true));

			solo.goBack();
		} else {
			assertMessageAffix = "should not appear";

			int minimumMatchesDelete = 1;
			int minimumMatchesRename = 1;

			if (isDeleteActionMode) {
				minimumMatchesDelete = 2;
			} else {
				minimumMatchesRename = 2;
			}
			assertFalse("Context menu with title '" + FIRST_TEST_SPRITE_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_SPRITE_NAME, 2, timeToWait, false, true));
			assertFalse("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, minimumMatchesDelete, timeToWait, false, true));
			assertFalse("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, minimumMatchesRename, timeToWait, false, true));
		}
	}

	private void enterTextAndCloseDialog(String text) {
		// Don't use UiTestUtils.clickEnterClose(solo, 0, "text")
		solo.clearEditText(0);
		solo.enterText(0, text);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(200);
	}

	private int checkNumberOfElements(Sprite firstSprite, Sprite copiedSprite) {

		ArrayList<SoundInfo> copiedSoundList = copiedSprite.getSoundList();
		ArrayList<SoundInfo> firstSoundList = firstSprite.getSoundList();
		assertEquals("The number of sounds differs!", firstSoundList.size(), copiedSoundList.size());

		ArrayList<LookData> copiedCustomeList = copiedSprite.getLookDataList();
		ArrayList<LookData> firstCustomeList = firstSprite.getLookDataList();
		assertEquals("The number of customes differs!", firstCustomeList.size(), copiedCustomeList.size());

		assertEquals(
				"The first sprite is NOT copied!",
				copiedSprite.getName(),
				solo.getString(R.string.default_project_sprites_catroid_name)
						+ solo.getString(R.string.copy_sprite_name_suffix));
		assertEquals("The first sprite has a new name!", firstSprite.getName(),
				solo.getString(R.string.default_project_sprites_catroid_name));

		ArrayList<Brick> brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		ArrayList<Brick> brickListFirstSprite = firstSprite.getScript(0).getBrickList();

		assertEquals("The number of Scripts differs!", copiedSprite.getNumberOfScripts(),
				firstSprite.getNumberOfScripts());
		assertEquals("The number of Bricks differs!", brickListCopiedSprite.size(), brickListFirstSprite.size());

		int brickCounter = 0;
		for (Brick element : brickListCopiedSprite) {
			assertEquals("Brick classes are different!", element.getClass(), brickListFirstSprite.get(brickCounter)
					.getClass());
			brickCounter++;
		}
		return brickCounter;
	}

	private void checkSpecialBricks(Sprite firstSprite, Sprite copiedSprite) {

		assertEquals("Message of BroadcastReceiver Brick is not right!",
				((BroadcastScript) (firstSprite.getScript(1))).getBroadcastMessage(),
				((BroadcastScript) (copiedSprite.getScript(1))).getBroadcastMessage());

		ArrayList<Brick> brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		ArrayList<Brick> brickListFirstSprite = firstSprite.getScript(0).getBrickList();

		LoopBeginBrick firstLoopBrick = (LoopBeginBrick) brickListFirstSprite.get(32);
		LoopBeginBrick copiedLoopBrick = (LoopBeginBrick) brickListCopiedSprite.get(32);
		LoopEndBrick firstEndBrick = firstLoopBrick.getLoopEndBrick();
		LoopEndBrick copiedEndBrick = copiedLoopBrick.getLoopEndBrick();
		assertNotSame("Loop Brick is not copied right!", firstEndBrick, copiedEndBrick);
		assertNotSame("Loop Brick is not copied right!", firstEndBrick.getLoopBeginBrick(),
				copiedEndBrick.getLoopBeginBrick());
		assertEquals("Loop Brick is not copied right!", firstEndBrick.getLoopBeginBrick(), firstLoopBrick);
		assertEquals("Loop Brick is not copied right!", copiedEndBrick.getLoopBeginBrick(), copiedLoopBrick);
		assertEquals("Loop Brick is not copied right!", firstLoopBrick.getLoopEndBrick(), firstEndBrick);
		assertEquals("Loop Brick is not copied right!", copiedLoopBrick.getLoopEndBrick(), copiedEndBrick);
	}

	private int checkIds(Sprite firstSprite, Sprite copiedSprite) {

		ArrayList<Brick> brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		ArrayList<Brick> brickListFirstSprite = firstSprite.getScript(0).getBrickList();

		assertNotSame("Sprite is not copied!", firstSprite, copiedSprite);
		assertNotSame("CustomDataList is not copied!", firstSprite.getLookDataList(), copiedSprite.getLookDataList());
		assertNotSame("Script is no copied!", firstSprite.getScript(0), copiedSprite.getScript(0));
		assertNotSame("Script is no copied!", firstSprite.getScript(1), copiedSprite.getScript(1));
		assertNotSame("Soundlist is no copied!", firstSprite.getSoundList(), copiedSprite.getSoundList());

		brickListFirstSprite = firstSprite.getScript(0).getBrickList();
		brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		assertNotSame("Script is not copied!", brickListFirstSprite, brickListCopiedSprite);

		int loopCounter = 0;
		for (Brick element : brickListFirstSprite) {
			assertNotSame("Brick is not copied!", element, brickListCopiedSprite.get(loopCounter));
			loopCounter++;
		}

		solo.clickOnText(solo.getString(R.string.default_project_sprites_catroid_name)
				+ solo.getString(R.string.copy_sprite_name_suffix));
		solo.sleep(1000);

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		Script scriptCopied = currentSprite.getScript(0);

		Script scriptOriginal = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getScript(0);

		scriptCopied.addBrick(new SetXBrick(currentSprite, 10));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size() - 1, scriptOriginal
				.getBrickList().size());

		scriptOriginal.addBrick(new SetXBrick(currentSprite, 10));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size(), scriptOriginal.getBrickList()
				.size());

		scriptCopied.removeBrick(scriptCopied.getBrickList().get(5));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size() + 1, scriptOriginal
				.getBrickList().size());

		scriptOriginal.removeBrick(scriptOriginal.getBrickList().get(6));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size(), scriptOriginal.getBrickList()
				.size());

		return scriptCopied.getBrickList().size();
	}
}
