package org.catrobat.catroid.uitest.content;

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class BrickEditFormulaTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private List<Brick> brickList;

	public BrickEditFormulaTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		brickList = UiTestUtils.createTestProjectWithEveryBrick();
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	private void checkBrick(int brickName) {
		solo.clickOnText(solo.getString(brickName));
		solo.clickOnMenuItem(solo.getString(R.string.brick_context_dialog_formula_edit_brick));
		assertTrue("Formula Editor don't opened!", solo.waitForView(solo.getView(R.id.formula_editor_brick_space)));
		solo.goBack();

	}

	public void testClickOnBrickItemEditFormula() {
		checkBrick(R.string.brick_change_brightness);
		checkBrick(R.string.brick_change_ghost_effect);
		checkBrick(R.string.brick_change_size_by);
		checkBrick(R.string.brick_change_volume_by);
		checkBrick(R.string.brick_change_variable);
		checkBrick(R.string.brick_change_x_by);
		checkBrick(R.string.brick_change_y_by);
		checkBrick(R.string.brick_glide);
		checkBrick(R.string.brick_go_back);
		checkBrick(R.string.brick_move);
		checkBrick(R.string.brick_place_at);
		checkBrick(R.string.brick_point_in_direction);
		checkBrick(R.string.brick_set_brightness);
		checkBrick(R.string.brick_set_ghost_effect);
		checkBrick(R.string.brick_set_size_to);
		checkBrick(R.string.brick_set_variable);
		checkBrick(R.string.brick_set_volume_to);
		checkBrick(R.string.brick_set_x);
		checkBrick(R.string.brick_set_y);
		checkBrick(R.string.brick_turn_left);
		checkBrick(R.string.brick_turn_right);

	}
}
