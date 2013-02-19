package org.catrobat.catroid.uitest.content.brick;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class BrickValueParameterTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private static final TextView TextView = null;

	public BrickValueParameterTest() {
		super(MainMenuActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		getIntoActivity();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testifEditTextIsTheSameAsInValueFileForMotion() {
		String categoryMotionText = solo.getString(R.string.category_motion);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryMotionText);

		TextView PlaceAtX = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_x);
		String XPosition = PlaceAtX.getText().toString();
		String XPositionValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals(XPositionValue, XPosition);

		TextView PlaceAtY = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_y);
		String YPosition = PlaceAtY.getText().toString();
		String YPositionValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals(YPositionValue, YPosition);

		TextView SetXTo = (TextView) solo.getView(R.id.brick_set_x_prototype_text_view);
		String SetXtoValue = SetXTo.getText().toString();
		String XPositionValueSetX = Integer.toString(BrickValues.X_POSITION);
		assertEquals(XPositionValueSetX, SetXtoValue);

		TextView SetYTo = (TextView) solo.getView(R.id.brick_set_y_prototype_text_view);
		String SetYtoValue = SetYTo.getText().toString();
		String YPositionValueSetY = Integer.toString(BrickValues.Y_POSITION);
		assertEquals(YPositionValueSetY, SetYtoValue);

		TextView ChangeXBy = (TextView) solo.getView(R.id.brick_change_x_prototype_text_view);
		String ValueChangeXBy = ChangeXBy.getText().toString();
		String XPositionValueChangeXBy = Integer.toString(BrickValues.CHANGE_X_BY);
		assertEquals(XPositionValueChangeXBy, ValueChangeXBy);

		TextView ChangeYBy = (TextView) solo.getView(R.id.brick_change_y_prototype_text_view);
		String ValueChangeYBy = ChangeYBy.getText().toString();
		String XPositionValueChangeYBy = Integer.toString(BrickValues.CHANGE_Y_BY);
		assertEquals(XPositionValueChangeYBy, ValueChangeYBy);

		TextView MoveNSteps = (TextView) solo.getView(R.id.brick_move_n_steps_prototype_text_view);
		String ValueMoveNSteps = MoveNSteps.getText().toString();
		String StepsValue = Float.toString(BrickValues.Move_Steps_Value);
		assertEquals(StepsValue, ValueMoveNSteps);

		solo.scrollDown();

		TextView TurnLeft = (TextView) solo.getView(R.id.brick_turn_left_prototype_text_view);
		String ValueTurnLeft = TurnLeft.getText().toString();
		String TurnLeftDegreesValue = Float.toString(BrickValues.TURN_REIGTH);
		assertEquals(TurnLeftDegreesValue, ValueTurnLeft);

		TextView TurnRight = (TextView) solo.getView(R.id.brick_turn_right_prototype_text_view);
		String ValueTurnRight = TurnRight.getText().toString();
		String TurnRightDegreesValue = Float.toString(BrickValues.TURN_REIGTH);
		assertEquals(TurnRightDegreesValue, ValueTurnRight);

		TextView PointInDirection = (TextView) solo.getView(R.id.brick_point_in_direction_prototype_text_view);
		String ValuePointInDirection = PointInDirection.getText().toString();
		String PointInDirectionValue = Float.toString(BrickValues.POINT_IN_DIRECTION);
		assertEquals(PointInDirectionValue, ValuePointInDirection);

		TextView GlideSeconds = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_duration);
		String ValueGlideSeconds = GlideSeconds.getText().toString();
		int seconds = BrickValues.GLIDE_SECONDS / 1000;
		String GlideSecondsValue = Integer.toString(seconds);
		assertEquals(GlideSecondsValue, ValueGlideSeconds);

		TextView GlideX = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_x);
		String ValueGlideX = GlideX.getText().toString();
		String GlideXValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals(GlideXValue, ValueGlideX);

		TextView GlideY = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_y);
		String ValueGlideY = GlideY.getText().toString();
		String GlideYValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals(GlideYValue, ValueGlideY);

		solo.scrollUp();
		solo.goBack();
	}

	private void createProject() {
		// TODO Auto-generated method stub

	}

	private void getIntoActivity() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createEmptyProject();

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

}
