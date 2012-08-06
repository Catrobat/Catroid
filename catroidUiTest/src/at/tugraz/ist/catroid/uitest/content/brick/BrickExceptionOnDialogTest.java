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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeVolumeByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;
import at.tugraz.ist.catroid.content.bricks.NoteBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick.Direction;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetVolumeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.SpeakBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class BrickExceptionOnDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private Project project;
	private Sprite sprite;
	private Script script;
	private final String spriteName = "cat";

	public BrickExceptionOnDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		createProject();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testBroadCastBrick() {
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		script.addBrick(broadcastBrick);
		String buttonNewText = solo.getString(R.string.new_broadcast_message);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(buttonNewText);
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(buttonNewText);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testBroadcastWaitBrick() {
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		script.addBrick(broadcastWaitBrick);
		String buttonNewText = solo.getString(R.string.new_broadcast_message);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(buttonNewText);
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(buttonNewText);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testChangeBrightnessBrick() {
		ChangeBrightnessBrick brightnessBrick = new ChangeBrightnessBrick(sprite, 40);
		script.addBrick(brightnessBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeGhostEffectBrick() {
		ChangeGhostEffectBrick ghostBrick = new ChangeGhostEffectBrick(sprite, 40);
		script.addBrick(ghostBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeSizeByNBrick() {
		ChangeSizeByNBrick sizeByNBrick = new ChangeSizeByNBrick(sprite, 40);
		script.addBrick(sizeByNBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeVolumeByBrick() {
		ChangeVolumeByBrick changeVolumeBrick = new ChangeVolumeByBrick(sprite, 40);
		script.addBrick(changeVolumeBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeXByBrick() {
		ChangeXByBrick changeXByBrick = new ChangeXByBrick(sprite, 40);
		script.addBrick(changeXByBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeYByBrick() {
		ChangeYByBrick changeYByBrick = new ChangeYByBrick(sprite, 40);
		script.addBrick(changeYByBrick);

		clickEditTextGoBackAndClickAgain();
	}

	//TODO test glideToBrick

	public void testGoNStepsBackBrick() {
		GoNStepsBackBrick nStepsBackBrick = new GoNStepsBackBrick(sprite, 40);
		script.addBrick(nStepsBackBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testMoveNStepsBrick() {
		MoveNStepsBrick nStepsBrick = new MoveNStepsBrick(sprite, 40);
		script.addBrick(nStepsBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testNoteBrick() {
		NoteBrick noteBrick = new NoteBrick(sprite);
		script.addBrick(noteBrick);

		clickEditTextGoBackAndClickAgain();
	}

	//TODO test nxt
	public void testPlaceAtBrick() {
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, 40, 40);
		script.addBrick(placeAtBrick);

		clickEditTextGoBackAndClickAgain();

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnEditText(1);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testPlaySoundBrick() {
		String spinnerNothingSelectedText = solo.getString(R.string.broadcast_nothing_selected);
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(sprite);
		script.addBrick(playSoundBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(spinnerNothingSelectedText);
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(spinnerNothingSelectedText);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testSetCostumeBrick() {
		String spinnerNothingSelectedText = solo.getString(R.string.broadcast_nothing_selected);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		script.addBrick(setCostumeBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(spinnerNothingSelectedText);
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(spinnerNothingSelectedText);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testPointInDirectionBrick() {
		PointInDirectionBrick directionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT);
		script.addBrick(directionBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText("90");
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText("90");
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testRepeatBrick() {
		RepeatBrick repeatbrick = new RepeatBrick(sprite, 3);
		script.addBrick(repeatbrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetBrightnessBrick() {
		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, 4);
		script.addBrick(setBrightnessBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetGhostEffectBrick() {
		SetGhostEffectBrick ghostEffectBrick = new SetGhostEffectBrick(sprite, 4);
		script.addBrick(ghostEffectBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetSizeToBrick() {
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(sprite, 4);
		script.addBrick(setSizeToBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetVolumeToBrick() {
		SetVolumeToBrick setVolumeToBrick = new SetVolumeToBrick(sprite, 4);
		script.addBrick(setVolumeToBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetXBrick() {
		SetXBrick setXBrick = new SetXBrick(sprite, 4);
		script.addBrick(setXBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetYBrick() {
		SetYBrick setYBrick = new SetYBrick(sprite, 4);
		script.addBrick(setYBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSpeakBrick() {
		SpeakBrick speakBrick = new SpeakBrick(sprite, "I say lol");
		script.addBrick(speakBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testWaitBrick() {
		WaitBrick waitBrick = new WaitBrick(sprite, 500);
		script.addBrick(waitBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void clickEditTextGoBackAndClickAgain() {
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(spriteName);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.clickOnEditText(0);
		solo.sleep(100);
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.clickOnEditText(0);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void createProject() {
		sprite = new Sprite(spriteName);
		script = new StartScript(sprite);
		sprite.addScript(script);
		ProjectManager manager = ProjectManager.getInstance();
		manager.setFileChecksumContainer(new FileChecksumContainer());
		manager.setProject(project);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		project = UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteList, getActivity());
	}
}
