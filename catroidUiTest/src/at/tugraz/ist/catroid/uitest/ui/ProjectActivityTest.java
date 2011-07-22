/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.ui;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ProjectActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public ProjectActivityTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.createEmptyProject();
	}

	@Override
	public void tearDown() throws Exception {
		ProjectManager.getInstance().deleteCurrentProject(getActivity());

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private void addNewSprite(String spriteName) {
		solo.sleep(50);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_add_sprite);

		solo.sleep(50);
		UiTestUtils.enterText(solo, 0, spriteName);

		solo.clickOnButton(getActivity().getString(R.string.new_sprite_dialog_button));
		solo.sleep(50);
	}
	
	public void testAddNewSprite() {
		final String spriteName = "testSprite";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
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
	}
	
	public void testAddNewSpriteErrors() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		addNewSprite("");
		assertTrue("No error message was displayed upon creating a sprite with an empty name.",
				solo.searchText(getActivity().getString(R.string.error_no_name_entered)));
		solo.clickOnButton(0);
		solo.goBack();

		final String spriteName = "testSprite";
		addNewSprite(spriteName);
		addNewSprite(spriteName);

		assertTrue("No error message was displayed upon creating a sprite with the same name twice.",
				solo.searchText(getActivity().getString(R.string.error_sprite_exists)));
	}

	public void testContextMenu() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		// Create sprites manually so we're able to check for equality
		final String spriteName = "foo";
		final String spriteName2 = "bar";

		addNewSprite(spriteName);
		addNewSprite(spriteName2);

		solo.sleep(500);

		// Rename sprite
		final String newSpriteName = "baz";
		solo.clickLongOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(50);

		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, newSpriteName);
		solo.clickOnButton(getActivity().getString(R.string.rename_button));
		solo.sleep(50);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite sprite = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite on position wasn't renamed correctly", newSpriteName, sprite.getName());

		// Delete sprite
		solo.clickLongOnText(newSpriteName);
		solo.clickOnText(getActivity().getString(R.string.delete));

		// Dialog is handled asynchronously, so we need to wait a while for it to finish
		solo.sleep(1000);

		assertFalse("Sprite is still in Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
				.contains(sprite));
		assertFalse("Sprite is still in Project", solo.searchText(newSpriteName));

		spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite sprite2 = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Subsequent sprite was not moved up after predecessor's deletion", spriteName2, sprite2.getName());
	}

	public void testMainMenuButton() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_home);

		solo.sleep(50);

		List<ImageButton> btnList = solo.getCurrentImageButtons();
		boolean buttonFound = false;
		for (ImageButton button : btnList) {
			if (button.getId() == R.id.btn_home) {
				buttonFound = true;
				break;
			}
		}
		assertTrue("Main menu is not visible", buttonFound);
	}

	/**
	 * This is a test that confirms that Catroid doesn't dump the core if we change the orientation while running this
	 * activity
	 */
	public void testChangeOrientation() {
		String spriteName = "testSprite";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.setActivityOrientation(Solo.PORTRAIT);

		solo.sleep(500);

		addNewSprite(spriteName);
		solo.clickLongOnText(spriteName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("Sprite name not visible after changing orientation", solo.searchText(spriteName));
	}

	public void testCheckMaxTextLines() {
		String spriteName = "poor poor poor poor poor poor poor poor me me me me me me";
		int expectedLineCount = 2;
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		addNewSprite(spriteName);
		TextView textView = solo.getText(2);
		assertEquals("linecount is wrong - ellipsize failed", expectedLineCount, textView.getLineCount());
		solo.clickLongOnText(spriteName);
		TextView textView2 = solo.getText(1);
		assertEquals("linecount is wrong", expectedLineCount + 1, textView2.getLineCount());
	}

	public void testNewSpriteDialog() {

		ProjectManager projectManager = ProjectManager.getInstance();
		String spriteName1 = "sprite1";
		String spriteName2 = "sprite2";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName1);
		solo.clickOnButton(getActivity().getString(R.string.ok));

		solo.sleep(800); //WARGLWARGLWARGL!!! damned random sleep -.-

		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName1));

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName2);
		sendKeys(KeyEvent.KEYCODE_ENTER);

		solo.sleep(800);

		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName2));

	}

	public void testNewSpriteDialogErrorMessages() {
		ProjectManager projectManager = ProjectManager.getInstance();
		String spriteName = "spriteError";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName);
		solo.clickOnButton(getActivity().getString(R.string.ok));

		solo.sleep(800);

		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName));

		//trying to add sprite which already exists:
		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName);
		solo.clickOnButton(getActivity().getString(R.string.ok));

		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));

		solo.sleep(200);

		sendKeys(KeyEvent.KEYCODE_ENTER);
		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.close));

		//trying to add sprite without name ("")

		UiTestUtils.enterText(solo, 0, "");
		sendKeys(KeyEvent.KEYCODE_ENTER);
		assertTrue("ErrorMessage not visible", solo.searchText(getActivity().getString(R.string.spritename_invalid)));
		solo.clickOnButton(getActivity().getString(R.string.close));

		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("not in NewSpriteDialog", solo.searchText(getActivity().getString(R.string.new_sprite_dialog_title)));
	}

	public void testRenameSpriteDialog() {
		String spriteName = "spriteRename";
		String spriteName2 = "spriteRename2";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		addNewSprite(spriteName);
		addNewSprite(spriteName2);

		//trying to rename sprite to name which already exists:
		//------------ OK Button:
		openRenameSpriteDialog(spriteName);
		UiTestUtils.enterText(solo, 0, spriteName2);
		solo.clickOnButton(getActivity().getString(R.string.ok));

		solo.sleep(200);
		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));
		assertTrue("RenameSpriteDialog not visible",
				solo.searchText(getActivity().getString(R.string.rename_sprite_dialog)));

		//------------ Enter Key:
		solo.sleep(200);
		sendKeys(KeyEvent.KEYCODE_ENTER);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));
		solo.sleep(100);

		//trying to rename sprite to ""
		//------------ OK Button:
		UiTestUtils.enterText(solo, 0, "");
		sendKeys(KeyEvent.KEYCODE_ENTER);
		assertTrue("ErrorMessage not visible", solo.searchText(getActivity().getString(R.string.spritename_invalid)));
		solo.clickOnButton(getActivity().getString(R.string.close));

		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		assertTrue("not in RenameSpriteDialog", solo.searchText(getActivity().getString(R.string.rename_sprite_dialog)));

	}

	private void addNewSprite(String spriteName) {
		solo.sleep(50);
		List<ImageButton> btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_add_sprite) {
				solo.clickOnImageButton(i);
			}
		}
		solo.sleep(50);
		UiTestUtils.enterText(solo, 0, spriteName);

		solo.clickOnButton(getActivity().getString(R.string.new_sprite_dialog_button));
		solo.sleep(50);
	}

	private void openNewSpriteDialog() {
		solo.sleep(200);
		List<ImageButton> btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_add_sprite) {
				solo.clickOnImageButton(i);
			}
		}
		solo.sleep(50);
	}

	private void openRenameSpriteDialog(String spriteName) {
		solo.sleep(200);
		solo.clickLongOnText(spriteName);
		solo.sleep(2500);
		solo.clickInList(1);
		//solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(50);
	}
}
