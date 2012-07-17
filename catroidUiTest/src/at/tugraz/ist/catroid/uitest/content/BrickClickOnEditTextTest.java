package at.tugraz.ist.catroid.uitest.content;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class BrickClickOnEditTextTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public BrickClickOnEditTextTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {

		String settings = getActivity().getString(R.string.settings);
		String prefMsBricks = getActivity().getString(R.string.pref_enable_ms_bricks);

		while (!solo.searchText(solo.getString(R.string.home))) {
			solo.goBack();
		}

		solo.clickOnText(solo.getString(R.string.home));
		solo.clickOnText(settings);
		solo.clickOnText(prefMsBricks);

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();

		super.tearDown();
	}

	public void testIfEditTextAreVisibleAndClickOnTextSetXandYInAddBrickDialog() {
		// clicks on spriteName needed to get focus on listview for solo without adding hovering brick

		String settings = getActivity().getString(R.string.settings);
		String prefMsBricks = getActivity().getString(R.string.pref_enable_ms_bricks);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//disable mindstorm bricks, if enabled at start
		if (!prefs.getBoolean("setting_mindstorm_bricks", false)) {
			solo.clickOnText(solo.getString(R.string.home));
			solo.clickOnText(settings);
			solo.clickOnText(prefMsBricks);
			solo.goBack();
			solo.clickOnText(solo.getString(R.string.current_project_button));
			UiTestUtils.createEmptyProject();

			solo.clickOnText("cat");
		}

		String spriteName = solo.getString(R.string.sprite_name);
		int categoryStringId = 0;
		float screenWidth = 0;
		float getTextViewXPosition = 0;

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_x);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_motion));
		ArrayList<Integer> listOfYPosition = UiTestUtils.getListItemYPositions(solo);
		screenWidth = solo.getCurrentActivity().getResources().getDisplayMetrics().widthPixels;

		getTextViewXPosition = (float) ((screenWidth / 2.0) * 0.75);

		solo.clickOnScreen(getTextViewXPosition, listOfYPosition.get(1));
		solo.clickOnText(spriteName);

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 1, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_y);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_motion));
		listOfYPosition = UiTestUtils.getListItemYPositions(solo);
		screenWidth = solo.getCurrentActivity().getResources().getDisplayMetrics().widthPixels;
		getTextViewXPosition = (float) ((screenWidth / 2.0) * 0.75);

		solo.clickOnScreen(getTextViewXPosition, listOfYPosition.get(2));

		solo.clickOnText(spriteName);

		brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 2, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetYBrick", brickListToCheck.get(0) instanceof SetYBrick);
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(1) instanceof SetXBrick);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);

		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_x);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_motion));

		ArrayList<EditText> editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();

		solo.goBack();

		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_size_to);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_looks));

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();

		solo.goBack();

		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_stop_all_sounds);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_sound));

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();

		solo.goBack();

		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_when_started);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_control));

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();

		solo.goBack();

		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_motor_action);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_lego_nxt));

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();

		solo.goBack();

	}
}
