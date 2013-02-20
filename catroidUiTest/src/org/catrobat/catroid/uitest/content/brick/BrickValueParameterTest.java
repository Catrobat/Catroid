package org.catrobat.catroid.uitest.content.brick;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class BrickValueParameterTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;

	//private static final TextView TextView = null;

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

	@Smoke
	public void testifEditMotionEqualBrickValue() {
		String categoryMotionText = solo.getString(R.string.category_motion);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryMotionText);

		TextView PlaceAtX = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_x);
		String XPositionPrototype = PlaceAtX.getText().toString();
		String XPositionValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick PlaceAtX are not correct", XPositionValue, XPositionPrototype);

		TextView PlaceAtY = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_y);
		String YPositionPrototype = PlaceAtY.getText().toString();
		String YPositionValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick PlaceAtY are not correct", YPositionValue, YPositionPrototype);

		TextView SetXTo = (TextView) solo.getView(R.id.brick_set_x_prototype_text_view);
		String SetXtoPrototype = SetXTo.getText().toString();
		String XPositionValueSetX = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick SetXTo are not correct", XPositionValueSetX, SetXtoPrototype);

		TextView SetYTo = (TextView) solo.getView(R.id.brick_set_y_prototype_text_view);
		String SetYtoPrototype = SetYTo.getText().toString();
		String YPositionValueSetY = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick SetYTo are not correct", YPositionValueSetY, SetYtoPrototype);

		TextView ChangeXBy = (TextView) solo.getView(R.id.brick_change_x_prototype_text_view);
		String ChangeXByPrototype = ChangeXBy.getText().toString();
		String XPositionValueChangeXBy = Integer.toString(BrickValues.CHANGE_X_BY);
		assertEquals("Value in Brick ChangeXBy are not correct", XPositionValueChangeXBy, ChangeXByPrototype);

		TextView ChangeYBy = (TextView) solo.getView(R.id.brick_change_y_prototype_text_view);
		String ChangeYByPrototype = ChangeYBy.getText().toString();
		String XPositionValueChangeYBy = Integer.toString(BrickValues.CHANGE_Y_BY);
		assertEquals("Value in Brick ChangeYBy are not correct", XPositionValueChangeYBy, ChangeYByPrototype);

		TextView MoveNSteps = (TextView) solo.getView(R.id.brick_move_n_steps_prototype_text_view);
		String MoveNStepsPrototype = MoveNSteps.getText().toString();
		String StepsValue = Float.toString(BrickValues.Move_Steps_Value);
		assertEquals("Value in Brick MoveNSteps are not correct", StepsValue, MoveNStepsPrototype);

		solo.scrollDown();

		TextView TurnLeft = (TextView) solo.getView(R.id.brick_turn_left_prototype_text_view);
		String TurnLeftPrototype = TurnLeft.getText().toString();
		String TurnLeftDegreesValue = Float.toString(BrickValues.TURN_REIGTH);
		assertEquals("Value in Brick TurnLeft are not correct", TurnLeftDegreesValue, TurnLeftPrototype);

		TextView TurnRight = (TextView) solo.getView(R.id.brick_turn_right_prototype_text_view);
		String TurnRightPrototype = TurnRight.getText().toString();
		String TurnRightDegreesValue = Float.toString(BrickValues.TURN_REIGTH);
		assertEquals("Value in Brick TurnRight are not correct", TurnRightDegreesValue, TurnRightPrototype);

		TextView PointInDirection = (TextView) solo.getView(R.id.brick_point_in_direction_prototype_text_view);
		String PointInDirectionPrototype = PointInDirection.getText().toString();
		String PointInDirectionValue = Float.toString(BrickValues.POINT_IN_DIRECTION);
		assertEquals("Value in Brick PointInDirection are not correct", PointInDirectionValue,
				PointInDirectionPrototype);

		TextView GlideSeconds = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_duration);
		String GlideSecondsPrototype = GlideSeconds.getText().toString();
		int seconds = BrickValues.GLIDE_SECONDS / 1000;
		String GlideSecondsValue = Integer.toString(seconds);
		assertEquals("Value in Brick GlideSeconds are not correct", GlideSecondsValue, GlideSecondsPrototype);

		TextView GlideX = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_x);
		String GlideXPrototype = GlideX.getText().toString();
		String GlideXValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick GlideX are not correct", GlideXValue, GlideXPrototype);

		TextView GlideY = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_y);
		String GlideYPrototype = GlideY.getText().toString();
		String GlideYValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick GlideY are not correct", GlideYValue, GlideYPrototype);

		solo.goBack();
	}

	@Smoke
	public void testifEditTextLooksEqualBrickValue() {

		String categoryLooksText = solo.getString(R.string.category_looks);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooksText);

		TextView SetSizeTo = (TextView) solo.getView(R.id.brick_set_size_to_prototype_text_view);
		String SetSizeToPrototype = SetSizeTo.getText().toString();
		String SetSizeToValue = Float.toString(BrickValues.SET_SIZE_TO);
		assertEquals("Value in Brick SetSizeTo are not correct", SetSizeToValue, SetSizeToPrototype);

		TextView ChangeSizeBy = (TextView) solo.getView(R.id.brick_change_size_by_prototype_text_view);
		String ChangeSizeByPrototype = ChangeSizeBy.getText().toString();
		String ChangeSizeByValue = Float.toString(BrickValues.CHANGE_SIZE_BY);
		assertEquals("Value in Brick ChangeSizeBy are not correct", ChangeSizeByValue, ChangeSizeByPrototype);

		solo.scrollDown();

		TextView SetGhostEffect = (TextView) solo.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		String SetGhostEffectPrototype = SetGhostEffect.getText().toString();
		String SetGhostEffectValue = Float.toString(BrickValues.SET_GHOST_EFFECT);
		assertEquals("Value in Brick SetGhostEffect are not correct", SetGhostEffectValue, SetGhostEffectPrototype);

		TextView ChangeGhostEffect = (TextView) solo.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		String ChangeGhostEffectPrototype = ChangeGhostEffect.getText().toString();
		String ChngeGhostEffectValue = Float.toString(BrickValues.SET_GHOST_EFFECT);
		assertEquals("Value in Brick SetGhostEffect are not correct", ChngeGhostEffectValue, ChangeGhostEffectPrototype);

		TextView SetBrightness = (TextView) solo.getView(R.id.brick_set_brightness_prototype_text_view);
		String SetBrightnessPrototype = SetBrightness.getText().toString();
		String SetBrightnessValue = Float.toString(BrickValues.SET_BRIGHTNESS_TO);
		assertEquals("Value in Brick SetBrightness are not correct", SetBrightnessValue, SetBrightnessPrototype);

		TextView ChangeBrightness = (TextView) solo.getView(R.id.brick_change_brightness_prototype_text_view);
		String ChangeBrightnessPrototype = ChangeBrightness.getText().toString();
		String ChangeBrightnessValue = Float.toString(BrickValues.CHANGE_BRITHNESS_BY);
		assertEquals("Value in Brick ChangeBrightness are not correct", ChangeBrightnessValue,
				ChangeBrightnessPrototype);

		solo.goBack();

	}

	@Smoke
	public void testifEditTextSoundEqualBrickValue() {

		String categorySoundText = solo.getString(R.string.category_sound);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categorySoundText);

		TextView SetVolumenTo = (TextView) solo.getView(R.id.brick_set_volume_to_prototype_text_view);
		String SetVolumenToPrototype = SetVolumenTo.getText().toString();
		String SetVolumenToValue = Float.toString(BrickValues.SET_VOLUMEN_TO);
		assertEquals("Value in Brick SetVolumenTo are not correct", SetVolumenToValue, SetVolumenToPrototype);

		TextView ChangeVolumenTo = (TextView) solo.getView(R.id.brick_change_volume_by_prototype_text_view);
		String ChangeVolumenToPrototype = ChangeVolumenTo.getText().toString();
		String ChangeVolumenToValue = Float.toString(BrickValues.CHANGE_VOLUMEN_BY);
		assertEquals("Value in Brick SetVolumenTo are not correct", ChangeVolumenToValue, ChangeVolumenToPrototype);

		TextView Speak = (TextView) solo.getView(R.id.brick_speak_prototype_text_view);
		String SpeakPrototype = Speak.getText().toString();
		String SpeakValue = BrickValues.SPEAK;
		assertEquals("Value in Brick Speak are not correct", SpeakValue, SpeakPrototype);

		solo.goBack();

	}

	@Smoke
	public void testifEditTextControlEqualBrickValue() {

		String categoryControlText = solo.getString(R.string.category_control);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryControlText);

		TextView Wait = (TextView) solo.getView(R.id.brick_wait_prototype_text_view);
		String WaitPrototype = Wait.getText().toString();
		String WaitValue = Integer.toString(BrickValues.WAIT);
		assertEquals("Value in Brick Wait are not correct", WaitValue, WaitPrototype);

		solo.scrollDown();

		TextView Note = (TextView) solo.getView(R.id.brick_note_prototype_text_view);
		String NotePrototype = Note.getText().toString();
		String NoteValue = BrickValues.NOTE;
		assertEquals("Value in Note Speak are not correct", NoteValue, NotePrototype);

		TextView Repeate = (TextView) solo.getView(R.id.brick_repeat_prototype_text_view);
		String RepeatePrototype = Repeate.getText().toString();
		String RepeateValue = Integer.toString(BrickValues.REPEAT);
		assertEquals("Value in Repeate Wait are not correct", RepeateValue, RepeatePrototype);

		solo.goBack();

	}

	//private void createProject() {
	//	// TODO Auto-generated method stub

	//}

	private void getIntoActivity() {
		//SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createEmptyProject();

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

}
