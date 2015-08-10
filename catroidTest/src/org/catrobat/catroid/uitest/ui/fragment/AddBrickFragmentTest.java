/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class AddBrickFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public AddBrickFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void tearDown() throws Exception {
		// disable mindstorms bricks, if enabled in test
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false).commit();
		}
		super.tearDown();
	}

	public void testBrickCategories() {
		goToAddBrickFromMainMenu();
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		checkActionBarInACategory(solo.getString(R.string.category_control), "control");
		checkActionBarInACategory(solo.getString(R.string.category_motion), "motion");
		checkActionBarInACategory(solo.getString(R.string.category_sound), "sound");
		checkActionBarInACategory(solo.getString(R.string.category_looks), "looks");
		//searchText just to get focus
		solo.searchText(solo.getString(R.string.categories));
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		checkActionBarInACategory(solo.getString(R.string.category_data), "data");
		checkActionBarInACategory(solo.getString(R.string.category_lego_nxt), "lego nxt");
	}

	public void testCorrectReturnToScriptFragment() {
		goToAddBrickFromMainMenu();

		String currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
		assertEquals("Current sprite name is not shown as actionbar title or is wrong before adding a brick", "cat",
				currentSprite);

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.waitForActivity(ScriptActivity.class);
		solo.waitForFragmentById(R.id.fragment_script);

		assertEquals("Current sprite name is not shown as actionbar title or is wrong before adding a brick", "cat",
				currentSprite);
		solo.goBack();
	}

	public void testCorrectReturnToCategoriesFragment() {
		goToAddBrickFromMainMenu();
		String categoriesString = solo.getString(R.string.categories);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertTrue("Categories text in action bar not found before selecting a category",
				solo.waitForText(categoriesString, 0, 2000));

		solo.clickOnText(solo.getString(R.string.category_control));
		solo.goBack();

		assertTrue("Categories text in action bar not found after selecting a category",
				solo.waitForText(categoriesString, 0, 2000));

		String selectSoundCatogory = solo.getString(R.string.category_sound);
		solo.clickOnText(selectSoundCatogory);
		String selectPlaySound = solo.getString(R.string.brick_play_sound);
		solo.clickOnText(selectPlaySound);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);
		String selectNewSound = solo.getString(R.string.new_broadcast_message);
		solo.clickOnText(selectNewSound);
		solo.clickOnText(selectNewSound);
		solo.clickOnText(solo.getString(R.string.add_sound_from_recorder));
		solo.clickOnImageButton(0);
		solo.clickOnImageButton(0);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		assertTrue("Categories text in action bar not found after selecting a category",
				solo.waitForText(categoriesString, 0, 2000));
	}

	private void checkActionBarInACategory(String categoryID, String category) {
		String showDetails = solo.getString(R.string.show_details);

		solo.clickOnText(categoryID);
		assertTrue("The Actionbar wasn't correctly setted after opening category " + category,
				solo.waitForText(categoryID, 0, 2000));
		UiTestUtils.openOptionsMenu(solo);
		assertFalse("Found menu item '" + showDetails + "'", solo.waitForText(showDetails, 1, 200, false, true));
		solo.goBack();
		solo.goBack();
	}

	private void goToAddBrickFromMainMenu() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		if (!sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true).commit();
		}
	}
}
