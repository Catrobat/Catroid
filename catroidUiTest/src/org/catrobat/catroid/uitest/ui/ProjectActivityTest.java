/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class ProjectActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private static final String TEST_SPRITE_NAME = "cat";

	public ProjectActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.createEmptyProject();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
		ProjectManager.getInstance().deleteCurrentProject();
	}

	private void addNewSprite(String spriteName) {
		solo.sleep(500);
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		solo.waitForText(solo.getString(R.string.new_sprite_dialog_title));

		EditText addNewSpriteEditText = solo.getEditText(0);
		//check if hint is set
		assertEquals("Not the proper hint set", solo.getString(R.string.new_sprite_dialog_default_sprite_name),
				addNewSpriteEditText.getHint());
		assertEquals("There should no text be set", "", addNewSpriteEditText.getText().toString());
		solo.enterText(0, spriteName);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(200);
	}

	public void testBackgroundSprite() {
		String sometext = "something" + System.currentTimeMillis();
		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.waitForText(solo.getString(R.string.new_project_dialog_title));
		solo.clearEditText(0);
		solo.enterText(0, sometext);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		String spriteBackgroundLabel = solo.getString(R.string.background);
		assertTrue("Wrong name for background sprite!", solo.searchText(spriteBackgroundLabel));
		solo.clickLongOnText(spriteBackgroundLabel);
		assertFalse("Found delete option for background sprite", solo.searchText(solo.getString(R.string.delete)));
	}

	public void testAddNewSprite() {
		final String spriteName = "testSprite";
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite(spriteName);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite secondSprite = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite at index 1 is not " + spriteName, spriteName, secondSprite.getName());
		assertTrue("Sprite is not in current Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
				.contains(secondSprite));

		final String spriteName2 = "anotherTestSprite";
		addNewSprite(spriteName2);
		spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite thirdSprite = (Sprite) spritesList.getItemAtPosition(2);
		assertEquals("Sprite at index 2 is not " + spriteName2, spriteName2, thirdSprite.getName());
		assertTrue("Sprite is not in current Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
				.contains(thirdSprite));
		assertTrue("Sprite not shown in Adapter", solo.searchText(spriteName2));
	}

	public void testAddNewSpriteUnderList() {
		String spriteName = "testSprite";
		String newSpriteDialogTitle = solo.getString(R.string.new_sprite_dialog_title);
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		solo.clickOnView(solo.getView(R.id.spritelist_footerview));
		solo.waitForText(newSpriteDialogTitle, 0, 1000);
		assertTrue("New Sprite dialog did not appear", solo.searchText(newSpriteDialogTitle));

		EditText addNewSpriteEditText = solo.getEditText(0);
		assertEquals("Not the proper hint set", solo.getString(R.string.new_sprite_dialog_default_sprite_name),
				addNewSpriteEditText.getHint());
		assertEquals("There should no text be set", "", addNewSpriteEditText.getText().toString());
		solo.clearEditText(0);
		solo.enterText(0, spriteName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(300);

		ListView spriteList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		assertEquals("Sprite was not added", 3, spriteList.getCount());

		spriteName = "testSprite2";
		solo.clickOnView(solo.getView(R.id.spritelist_footerview_add_image));
		solo.waitForText(newSpriteDialogTitle, 0, 1000);
		assertTrue("New Sprite dialog did not appear", solo.searchText(newSpriteDialogTitle));
		solo.clearEditText(0);
		solo.enterText(0, spriteName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(300);
		spriteList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		assertEquals("Sprite was not added", 4, spriteList.getCount());

		spriteName = "testSprite3";
		solo.clickOnView(solo.getView(R.id.view_below_spritelist_non_scrollable));
		solo.waitForText(newSpriteDialogTitle, 0, 1000);
		assertTrue("New Sprite dialog did not appear", solo.searchText(newSpriteDialogTitle));
		solo.clearEditText(0);
		solo.enterText(0, spriteName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(300);
		spriteList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		assertEquals("Sprite was not added", 5, spriteList.getCount());
	}

	public void testAddedSpriteVisibleOnLongList() {
		Project project = ProjectManager.INSTANCE.getCurrentProject();
		addSprite("dog", project);
		addSprite("mouse", project);
		addSprite("bear", project);
		addSprite("tiger", project);
		addSprite("lion", project);
		addSprite("eagle", project);
		addSprite("leopard", project);
		addSprite("snake", project);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		assertTrue("Sprite cat is first in list - should be visible on initial start without scrolling",
				solo.searchText("cat", 0, false));

		String newSpriteName = "Koala";
		addNewSprite(newSpriteName);
		solo.waitForText(newSpriteName, 0, 2000);
		assertTrue("Sprite Koala was not found - List did not move to last added sprite",
				solo.searchText(newSpriteName, 0, false));
	}

	public void testOrientation() throws NameNotFoundException {
		/// Method 1: Assert it is currently in portrait mode.
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
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

		assertEquals(ProjectActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}

	public void testContextMenu() {
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		// Create sprites manually so we're able to check for equality
		final String spriteName = "foo";
		final String spriteName2 = "bar";

		addNewSprite(spriteName);
		addNewSprite(spriteName2);

		// Rename sprite
		final String newSpriteName = "baz";
		solo.clickLongOnText(spriteName);
		solo.clickOnText(solo.getString(R.string.rename));
		solo.sleep(50);

		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, newSpriteName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite sprite = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite on position wasn't renamed correctly", newSpriteName, sprite.getName());

		// Delete sprite
		solo.clickLongOnText(newSpriteName);
		solo.clickOnText(solo.getString(R.string.delete));

		// Dialog is handled asynchronously, so we need to wait a while for it to finish
		solo.sleep(300);

		assertFalse("Sprite is still in Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
				.contains(sprite));
		assertFalse("Sprite is still in Project", solo.searchText(newSpriteName));

		spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite sprite2 = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Subsequent sprite was not moved up after predecessor's deletion", spriteName2, sprite2.getName());
	}

	public void testMainMenuButton() {
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnUpActionBarButton(solo.getCurrentActivity());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		assertTrue("Clicking on main menu button did not cause main menu to be displayed",
				solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	public void testCheckMaxTextLines() {
		String spriteName = "poor poor poor poor poor poor poor poor me me me me me me";
		int expectedLineCount = 1;
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite(spriteName);
		TextView textView = solo.getText(2);
		assertEquals("linecount is wrong - ellipsize failed", expectedLineCount, textView.getLineCount());
	}

	public void testNewSpriteDialog() {
		ProjectManager projectManager = ProjectManager.getInstance();
		String spriteName1 = "sprite1";
		String spriteName2 = "sprite2";
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName1);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName1));

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName2);
		sendKeys(KeyEvent.KEYCODE_ENTER);
		solo.sleep(200);
		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName2));
	}

	public void testNewSpriteDialogErrorMessages() {
		ProjectManager projectManager = ProjectManager.getInstance();
		String spriteName = "spriteError";
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName);
		solo.clickOnButton(0);
		solo.sleep(200);
		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName));

		//trying to add sprite which already exists:
		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);

		String errorSpriteAlreadyExists = solo.getString(R.string.spritename_already_exists);
		String buttonCloseText = solo.getString(R.string.close);
		solo.sleep(100);
		assertTrue("ErrorMessage not visible", solo.searchText(errorSpriteAlreadyExists));
		solo.clickOnButton(buttonCloseText);
		solo.sleep(200);

		sendKeys(KeyEvent.KEYCODE_ENTER);
		assertTrue("ErrorMessage not visible", solo.searchText(errorSpriteAlreadyExists));
		solo.sleep(200);
		solo.clickOnButton(buttonCloseText);

		//trying to add sprite without name ("")
		UiTestUtils.enterText(solo, 0, "");
		sendKeys(KeyEvent.KEYCODE_ENTER);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(solo.getString(R.string.spritename_invalid)));
		solo.clickOnButton(buttonCloseText);

		solo.sleep(100);
		solo.clickOnButton(0);
		solo.sleep(200);
		assertTrue("not in NewSpriteDialog", solo.searchText(solo.getString(R.string.new_sprite_dialog_title)));
	}

	public void testRenameSpriteDialog() {
		String spriteName = "spriteRename";
		String spriteName2 = "spriteRename2";
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite(spriteName);
		addNewSprite(spriteName2);

		//trying to rename sprite to name which already exists:
		//------------ OK Button:
		String buttonCloseText = solo.getString(R.string.close);
		String errorSpriteAlreadyExists = solo.getString(R.string.spritename_already_exists);
		String dialogRenameSpriteText = solo.getString(R.string.rename_sprite_dialog);
		openRenameSpriteDialog(spriteName);
		UiTestUtils.enterText(solo, 0, spriteName2);
		solo.sleep(200);
		sendKeys(KeyEvent.KEYCODE_ENTER);

		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(errorSpriteAlreadyExists));
		solo.clickOnButton(buttonCloseText);
		assertTrue("RenameSpriteDialog not visible", solo.searchText(dialogRenameSpriteText));

		//------------ Enter Key:
		solo.sleep(100);
		sendKeys(KeyEvent.KEYCODE_ENTER);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(errorSpriteAlreadyExists));
		solo.clickOnButton(buttonCloseText);
		solo.sleep(100);

		//trying to rename sprite to ""
		//------------ OK Button:
		UiTestUtils.enterText(solo, 0, "");
		sendKeys(KeyEvent.KEYCODE_ENTER);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible", solo.searchText(solo.getString(R.string.spritename_invalid)));
		solo.clickOnButton(buttonCloseText);
		solo.clickOnButton(0);
		assertTrue("not in RenameSpriteDialog", solo.searchText(dialogRenameSpriteText));
	}

	public void testDivider() {
		String spriteName = "Sprite1";
		String spriteName2 = "Sprite2";
		String spriteName3 = "Sprite3";
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite(spriteName);
		addNewSprite(spriteName2);
		addNewSprite(spriteName3);

		assertTrue("ListView divider should be null", solo.getCurrentListViews().get(0).getDivider() == null);
		assertTrue("Listview dividerheight should be 0", solo.getCurrentListViews().get(0).getDividerHeight() == 0);

		int dividerID = R.id.sprite_divider;
		int currentViewID;
		boolean isBackground = true;
		Bitmap viewBitmap;
		int pixelColor;
		int colorDivider;

		for (View viewToTest : solo.getCurrentViews()) {
			currentViewID = viewToTest.getId();
			if (dividerID == currentViewID) {
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
		createProject();
		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite("testSprite");

		TextView tvScriptCount = ((TextView) solo.getView(R.id.textView_number_of_scripts));
		TextView tvBrickCount = ((TextView) solo.getView(R.id.textView_number_of_bricks));
		TextView tvCostumeCount = ((TextView) solo.getView(R.id.textView_number_of_costumes));
		TextView tvSoundCount = ((TextView) solo.getView(R.id.textView_number_of_sounds));

		String scriptCountString = tvScriptCount.getText().toString();
		String brickCountString = tvBrickCount.getText().toString();
		String costumeCountString = tvCostumeCount.getText().toString();
		String soundCountString = tvSoundCount.getText().toString();

		boolean scriptCountShowing = tvScriptCount.getVisibility() == View.GONE ? false : true;
		boolean brickCountShowing = tvBrickCount.getVisibility() == View.GONE ? false : true;
		boolean costumeCountShowing = tvCostumeCount.getVisibility() == View.GONE ? false : true;
		boolean soundCountShowing = tvSoundCount.getVisibility() == View.GONE ? false : true;

		String showDetailsText = solo.getString(R.string.show_details);
		String hideDetailsText = solo.getString(R.string.hide_details);

		if (scriptCountShowing || brickCountShowing || costumeCountShowing || soundCountShowing) {
			solo.clickOnMenuItem(hideDetailsText);
			solo.sleep(300);

			scriptCountShowing = tvScriptCount.getVisibility() == View.GONE ? false : true;
			brickCountShowing = tvBrickCount.getVisibility() == View.GONE ? false : true;
			costumeCountShowing = tvCostumeCount.getVisibility() == View.GONE ? false : true;
			soundCountShowing = tvSoundCount.getVisibility() == View.GONE ? false : true;
		}

		assertFalse("Details are not hidden!", scriptCountShowing || brickCountShowing || costumeCountShowing
				|| soundCountShowing);

		solo.clickOnMenuItem(showDetailsText);
		solo.sleep(300);

		scriptCountShowing = tvScriptCount.getVisibility() == View.GONE ? false : true;
		brickCountShowing = tvBrickCount.getVisibility() == View.GONE ? false : true;
		costumeCountShowing = tvCostumeCount.getVisibility() == View.GONE ? false : true;
		soundCountShowing = tvSoundCount.getVisibility() == View.GONE ? false : true;

		assertTrue("Details are not showing after being enabled!", scriptCountShowing && brickCountShowing
				&& costumeCountShowing && soundCountShowing);

		solo.clickOnImageButton(0);
		assertTrue("Hide details should be shown!", solo.waitForText(hideDetailsText));
		solo.goBack();

		solo.clickOnText(TEST_SPRITE_NAME);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.goBack();

		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fr_sprites_list);

		solo.clickOnImageButton(0);
		assertTrue("Hide details should be shown!", solo.waitForText(hideDetailsText));
		solo.goBack();

		scriptCountShowing = tvScriptCount.getVisibility() == View.GONE ? false : true;
		brickCountShowing = tvBrickCount.getVisibility() == View.GONE ? false : true;
		costumeCountShowing = tvCostumeCount.getVisibility() == View.GONE ? false : true;
		soundCountShowing = tvSoundCount.getVisibility() == View.GONE ? false : true;

		assertTrue("Details are not showing!", scriptCountShowing && brickCountShowing && costumeCountShowing
				&& soundCountShowing);

		solo.clickOnMenuItem(hideDetailsText);

		assertFalse("Scripts not shown", solo.searchText(solo.getString(R.string.number_of_scripts), true));
		assertFalse("Costumes not shown", solo.searchText(solo.getString(R.string.number_of_costumes), true));
		assertFalse("Bricks not shown", solo.searchText(solo.getString(R.string.number_of_bricks), true));
		assertFalse("Sounds not shown", solo.searchText(solo.getString(R.string.number_of_sounds), true));

		solo.clickOnImageButton(0);
		assertTrue("Show details should be shown!", solo.waitForText(showDetailsText));
		solo.goBack();

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		int scriptCount = sprite.getNumberOfScripts();
		int brickCount = sprite.getNumberOfBricks();
		int costumeCount = sprite.getCostumeDataList().size();
		int soundCount = sprite.getSoundList().size();

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

	public void testOverFlowMenuDelete() {
		createProject();
		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite("sprite1");
		addNewSprite("sprite2");
		addNewSprite("sprite3");
		addNewSprite("sprite4");

		Sprite spriteOne = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(1);
		Sprite spriteTwo = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(2);
		Sprite spriteThree = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(3);
		Sprite spriteFour = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(4);

		solo.clickOnMenuItem(solo.getString(R.string.delete));
		assertTrue("ActionMode title is not set correctly!", solo.searchText(solo.getString(R.string.delete)));

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);
		solo.goBack();
		solo.sleep(200);

		assertTrue("Unselected sprites have been deleted!", ProjectManager.INSTANCE.getCurrentProject().getSpriteList()
				.contains(spriteOne)
				&& ProjectManager.INSTANCE.getCurrentProject().getSpriteList().contains(spriteFour));

		assertFalse("Selected sprites were not deleted!", ProjectManager.INSTANCE.getCurrentProject().getSpriteList()
				.contains(spriteTwo)
				|| ProjectManager.INSTANCE.getCurrentProject().getSpriteList().contains(spriteThree));

		assertFalse("sprite2 and sprite3 have been deleted but are still showing!",
				solo.searchText("sprite2") || solo.searchText("sprite3"));
	}

	public void testOverFlowMenuRename() {
		createProject();
		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite("sprite1");
		addNewSprite("sprite2");

		solo.clickOnMenuItem(solo.getString(R.string.rename));
		assertTrue("ActionMode title is not set correctly!", solo.searchText(solo.getString(R.string.rename)));

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);

		solo.goBack();
		solo.clearEditText(0);
		solo.enterText(0, "renamed");
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(100);
		assertTrue("sprite2 was not renamed!", ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(2)
				.getName().equalsIgnoreCase("renamed"));

	}

	public void testOverFlowMenuSettings() {
		createProject();
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnMenuItem(solo.getString(R.string.main_menu_settings));
		solo.assertCurrentActivity("Not in SettingsActivity", SettingsActivity.class);
	}

	private void openNewSpriteDialog() {
		solo.sleep(200);
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		solo.sleep(50);
	}

	private void openRenameSpriteDialog(String spriteName) {
		solo.sleep(200);
		solo.clickLongOnText(spriteName);
		solo.sleep(250);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(50);
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.PROJECTNAME1);

		Sprite spriteCat = new Sprite(TEST_SPRITE_NAME);
		Script startScriptCat = new StartScript(spriteCat);
		Script scriptTappedCat = new WhenScript(spriteCat);
		Brick setXBrick = new SetXBrick(spriteCat, 50);
		Brick setYBrick = new SetYBrick(spriteCat, 50);
		Brick changeXBrick = new ChangeXByNBrick(spriteCat, 50);
		startScriptCat.addBrick(setYBrick);
		startScriptCat.addBrick(setXBrick);
		scriptTappedCat.addBrick(changeXBrick);

		spriteCat.addScript(startScriptCat);
		spriteCat.addScript(scriptTappedCat);
		project.addSprite(spriteCat);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(spriteCat);
		ProjectManager.getInstance().setCurrentScript(startScriptCat);

		File imageFile = UiTestUtils.saveFileToProject(project.getName(), "catroid_sunglasses.png",
				org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses, getActivity(), UiTestUtils.FileTypes.IMAGE);

		ProjectManager projectManager = ProjectManager.getInstance();
		ArrayList<CostumeData> costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName("Catroid sun");
		costumeDataList.add(costumeData);
		projectManager.getFileChecksumContainer().addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());

		File soundFile = UiTestUtils.saveFileToProject(project.getName(), "longsound.mp3",
				org.catrobat.catroid.uitest.R.raw.longsound, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle("longsound");

		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
	}

	private void addSprite(String spriteName, Project project) {
		Sprite spriteToAdd = new Sprite(spriteName);
		project.addSprite(spriteToAdd);
		ProjectManager.INSTANCE.saveProject();
		ProjectManager.INSTANCE.setProject(project);
	}
}
