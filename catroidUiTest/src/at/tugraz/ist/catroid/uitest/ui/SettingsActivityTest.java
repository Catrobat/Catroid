package at.tugraz.ist.catroid.uitest.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SettingsActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public SettingsActivityTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
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
		super.tearDown();
	}

	public void testToggleMindstormBricks() {
		String currentProject = getActivity().getString(R.string.current_project_button);
		String background = getActivity().getString(R.string.background);
		String settings = getActivity().getString(R.string.settings);
		String prefMsBricks = getActivity().getString(R.string.pref_enable_ms_bricks);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//disable mindstorm bricks, if enabled at start
		if (prefs.getBoolean("setting_mindstorm_bricks", false)) {
			solo.clickOnText(settings);
			solo.clickOnText(prefMsBricks);
			solo.goBack();
		}

		solo.clickOnText(currentProject);
		solo.clickOnText(background);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_add_sprite);
		assertFalse("Lego brick category is showing!",
				solo.searchText(getActivity().getString(R.string.category_lego_nxt)));
		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.clickOnText(settings);
		solo.clickOnText(prefMsBricks);
		solo.goBack();
		solo.clickOnText(currentProject);
		solo.clickOnText(background);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_add_sprite);
		assertTrue("Lego brick category is not showing!",
				solo.searchText(getActivity().getString(R.string.category_lego_nxt)));

	}
}
