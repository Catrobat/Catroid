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

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
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

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		boolean buttonFound = solo.getView(R.id.btn_home) != null ? true : false;
		assertTrue("Main menu is not visible", buttonFound);

		//assertTrue("Current project is not visible", solo.searchText(getActivity().getString(R.string.current_project)));
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
}
