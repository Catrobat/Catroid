package org.catrobat.catroid.uitest.ui.fragment;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class AddBrickFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private static final String KEY_SETTINGS_MINDSTORM_BRICKS = "setting_mindstorm_bricks";

	public AddBrickFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		// disable mindstorm bricks, if enabled in test
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false).commit();
		}

		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testBrickCategories() {
		goToAddBrickFromMainMenu();
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		checkActionBarInACatagory(solo.getString(R.string.category_sound), "sound");
		checkActionBarInACatagory(solo.getString(R.string.category_lego_nxt), "lego nxt");
		checkActionBarInACatagory(solo.getString(R.string.category_control), "control");
		checkActionBarInACatagory(solo.getString(R.string.category_looks), "looks");
		checkActionBarInACatagory(solo.getString(R.string.category_motion), "motion");

	}

	public void testCorrectReturnToScriptFragment() {
		goToAddBrickFromMainMenu();
		assertTrue("Script text in action bar not found before adding a brick",
				solo.waitForText(solo.getString(R.string.scripts), 0, 2000));

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.sleep(2000);

		assertTrue("Script text in action bar not found after adding a brick",
				solo.waitForText(solo.getString(R.string.scripts), 0, 2000));
		solo.goBack();

	}

	public void testCorrectReturnToCategoriesFragment() {
		goToAddBrickFromMainMenu();
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertTrue("Categories text in action bar not found before selecting a category",
				solo.waitForText(solo.getString(R.string.categories), 0, 2000));

		solo.clickOnText(solo.getString(R.string.category_control));
		solo.goBack();

		assertTrue("Categories text in action bar not found after selecting a category",
				solo.waitForText(solo.getString(R.string.categories), 0, 2000));

	}

	private void checkActionBarInACatagory(String categoryID, String category) {
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

		if (!sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, true).commit();
		}

	}
}