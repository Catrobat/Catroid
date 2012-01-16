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

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SoundActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptTabActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public ScriptTabActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
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
		super.tearDown();
	}

	public void testMainMenuButton() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		assertTrue("Clicking on main menu button did not cause main menu to be displayed",
				solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	public void testScriptTab() {
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.backgrounds));
		solo.clickOnText("Script");
		solo.sleep(100);
		assertTrue("Clicking on Script Tab did not cause ScriptActivity to be displayed",
				solo.getCurrentActivity() instanceof ScriptActivity);
	}

	public void testCostumesTab() {
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.backgrounds));
		solo.sleep(100);
		assertTrue("Clicking on Costumes Tab did not cause CostumeActivity to be displayed",
				solo.getCurrentActivity() instanceof CostumeActivity);
	}

	public void testSoundsTab() {
		solo.clickOnText("Sounds");
		solo.sleep(100);
		assertTrue("Clicking on Sounds Tab did not cause SoundActivity to be displayed",
				solo.getCurrentActivity() instanceof SoundActivity);
	}

	public void testTabCostumeOrBackgroundLabel() {
		String spriteDog = "dog";
		String spriteBear = "bear";
		String spriteFrog = "frog";
		String spriteToTest = "";

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		addNewSprite(spriteDog);
		addNewSprite(spriteBear);
		addNewSprite(spriteFrog);

		ArrayList<ListView> listViews = solo.getCurrentListViews();
		ListView spriteList = listViews.get(0);
		spriteToTest = spriteList.getItemAtPosition(0).toString();
		solo.clickOnText(spriteToTest);
		solo.sleep(100);
		assertTrue("Wrong label - Tab should be named \"Backgrounds\"",
				solo.searchText(getActivity().getString(R.string.backgrounds)));
		solo.clickOnText(getActivity().getString(R.string.backgrounds));
		solo.sleep(100);
		assertTrue("Wrong label - Tab should be named \"Backgrounds\"",
				solo.searchText(getActivity().getString(R.string.backgrounds)));

		for (int i = 1; i < 3; i++) {
			solo.goBack();
			solo.sleep(100);
			spriteToTest = spriteList.getItemAtPosition(i).toString();
			solo.clickOnText(spriteToTest);
			solo.sleep(100);
			assertTrue("Wrong label - Tab should be named \"Costumes\"",
					solo.searchText(getActivity().getString(R.string.costumes)));
		}
	}

	private void addNewSprite(String spriteName) {
		solo.sleep(300);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_sprite);
		solo.sleep(200);
		solo.enterText(0, spriteName);
		solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(100);
	}
}
