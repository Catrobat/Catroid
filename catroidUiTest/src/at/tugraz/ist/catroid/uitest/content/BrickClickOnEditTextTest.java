package at.tugraz.ist.catroid.uitest.content;

import java.util.ArrayList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
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

	public void testClickOnTextSetXandYInAddBrickDialog() {
		// clicks on spriteName needed to get focus on listview for solo without adding hovering brick
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

	}
}
